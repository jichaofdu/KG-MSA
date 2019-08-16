package graphapp.services;

import graphapp.domain.UnitGraphNode;
import graphapp.domain.entities.*;
import graphapp.domain.relationships.*;
import graphapp.repositories.MetricOfPodRepository;
import graphapp.repositories.MetricOfServiceApiRepository;
import graphapp.repositories.PodRepository;
import graphapp.repositories.ServiceApiRepository;
import graphapp.utils.Neo4jUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class GraphAppServices {

    @Autowired
    private Neo4jUtil neo4jUtil;

    private final PodRepository podRepository;

    private final ServiceApiRepository serviceApiRepository;

    private final MetricOfPodRepository metricOfPodRepository;

    private final MetricOfServiceApiRepository metricOfServiceApiRepository;

    private static final double DEFAULT_ABNORMALITY = 0.1;

    public GraphAppServices(PodRepository podRepository,
                        ServiceApiRepository serviceApiRepository,
                            MetricOfPodRepository metricOfPodRepository,
                            MetricOfServiceApiRepository metricOfServiceApiRepository){
        this.podRepository = podRepository;
        this.serviceApiRepository = serviceApiRepository;
        this.metricOfPodRepository = metricOfPodRepository;
        this.metricOfServiceApiRepository = metricOfServiceApiRepository;
    }

    public String updateAbnormalityOfPods(){
        ArrayList<PodMetric> podMetricList = metricOfPodRepository.findAllMetrics();
        for(PodMetric podMetric : podMetricList){
            double abnormality = updateSingleAbnormalityOfPods(podMetric);
            podMetric.setAbnormality(abnormality);
            metricOfPodRepository.save(podMetric);
        }
        System.out.println("[更新PodMetric Abnormality] 数量:" + podMetricList.size());
        return "Success";
    }

    public String updateAbnomalityOfServiceApis(){
        ArrayList<ServiceApiMetric> serviceApiMetricList = metricOfServiceApiRepository.findAllMetrics();
        for(ServiceApiMetric serviceApiMetric : serviceApiMetricList){
            double abnormality = updateSingleAbnormalityOfServiceApis(serviceApiMetric);
            serviceApiMetric.setAbnormality(abnormality);
            metricOfServiceApiRepository.save(serviceApiMetric);
        }
        System.out.println("[更新ServiceAPIMetric Abnormality] 数量:" + serviceApiMetricList.size());
        return "Success";
    }

    private double updateSingleAbnormalityOfPods(PodMetric podMetric){
        ArrayList<Double> values = podMetric.getHistoryValues();
        if(values.size() <= 3){
            return 0.0;
        }else{
            return threeSigmaAbnormality(values, podMetric.getValue());
        }

    }

    private double updateSingleAbnormalityOfServiceApis(ServiceApiMetric serviceApiMetric){
        ArrayList<Double> values = serviceApiMetric.getValues();
        if(values.size() <= 3){
            return 0.0;
        }else{
            return threeSigmaAbnormality(values, values.get(values.size()-1));
        }

    }

    private double threeSigmaAbnormality(ArrayList<Double> historyValue, double latestValue){
        int arrLen = historyValue.size();
        double latestAvg = (latestValue + historyValue.get(arrLen-1) + historyValue.get(arrLen-2)) / 3.0;

        double totalAvg = getAverage(historyValue);
        double totalSd = getStandardDiviation(historyValue, totalAvg);

        double abnormality;
        if(totalSd == 0){
            abnormality = DEFAULT_ABNORMALITY;
        }else{
            abnormality = Math.abs((latestAvg - totalAvg) / totalSd);
        }

        System.out.println("[ThreeSigmaAbnormality]" +
                " LatestAvg:" + latestAvg +
                " TotalAvg:" + totalAvg +
                " TotalSd:" + totalSd +
                " Abnormality:" + abnormality);

        return abnormality;
    }

    private double getAverage(ArrayList<Double> x){
        int m = x.size();
        double sum=0;
        for(int i = 0; i < m; i++){//求和
            sum += x.get(i);
        }
        return sum / m;
    }

    private double getStandardDiviation(ArrayList<Double> x, double dAve) {
        double dVar = 0;
        for(int i = 0; i < x.size(); i++){//求方差
            dVar += (x.get(i) - dAve) * (x.get(i) - dAve);
        }
        return Math.sqrt(dVar / x.size());
    }

    @Transactional(readOnly = true)
    public Map<String, Set> getCrossOfTwoTrace(String traceA, String traceB){
        Map<String, Set> crossComponent = getCrossComponentOfTwoTrace(traceA, traceB);
        Map<String, Set> crossMetrics = getCrossMetricsOfTwoTrace(traceA, traceB);

        Map<String, Set> cross = new HashMap<>();
        cross.putAll(crossComponent);
        cross.putAll(crossMetrics);

        return cross;
    }

    @Transactional(readOnly = true)
    public Map<String, Set> getOneTraceMetrics(String traceId){
        Map<String, Set> retMap = new HashMap<>();
        String cql =
                "MATCH (n)-[r:TraceInvokeApiToPod|TraceInvokePodToApi]->(m)<-[rm:PodAndMetric|ServiceApiAndMetric]-(metrics) " +
                        "WHERE " +
                        "    ANY(x IN r.traceIdSpanId WHERE x =~ '75c1d44834925763c082bf6cf7863e53-.*') " +
                        "WITH n,m,r,metrics,rm " +
                        "RETURN m,metrics,rm";
        Set<PodMetric> podMetricSet = new HashSet<>();
        Set<ServiceApiMetric> serviceApiMetricSet = new HashSet<>();
        Set<PodAndMetric> podAndMetricSet = new HashSet<>();
        Set<ServiceApiAndMetric> serviceApiAndMetricSet = new HashSet<>();

        neo4jUtil.getTraceMetricComponentList(cql,
                podAndMetricSet,
                serviceApiAndMetricSet);

        System.out.println("[TraceMetricInfo] Trace ID:" + traceId);
        System.out.println("PodMetricSet:" + podMetricSet.size());
        System.out.println("ServiceApiMetricSet:" + serviceApiMetricSet.size());
        System.out.println("PodAndMetricSet:" + podAndMetricSet.size());
        System.out.println("ServiceApiAndMetricSet:" + serviceApiAndMetricSet.size());

        retMap.put("PodMetric", podMetricSet);
        retMap.put("ServiceApiMetric", serviceApiMetricSet);
        retMap.put("PodAndMetric", podAndMetricSet);
        retMap.put("ServiceApiAndMetric", serviceApiAndMetricSet);

        return retMap;
    }

    @Transactional(readOnly = true)
    public Map<String, Set> getCrossMetricsOfTwoTrace(String traceA, String traceB){
        //下面这部分是获得的两个Trace的Metric交集
        Map<String, Set> traceAMap = getOneTraceMetrics(traceA);
        Map<String, Set> traceBMap = getOneTraceMetrics(traceB);

        Map<String, Set> crossingMap = new HashMap<>();

        System.out.println("[Metrics Crossing Set] Trace-A:" + traceA + " Trace-B:" + traceB);

        for(String key : traceAMap.keySet()){
            Set traceASet = traceAMap.get(key);
            Set traceBSet = traceBMap.get(key);

            Set crossingSet = new HashSet(traceASet);
            crossingSet.retainAll(traceBSet);

            crossingMap.put(key, crossingSet);

            System.out.println(key + ":" + crossingSet.size());
        }

        return crossingMap;
    }


    @Transactional(readOnly = true)
    public Map<String, Set> getOneTracePath(String traceId){
        Map<String, Set> retMap = new HashMap<>();
        //cql语句
        String cql =
                "MATCH (n)-[r:TraceInvokeApiToPod|TraceInvokePodToApi]->(m)-[rs:AppServiceAndPod|AppServiceHostServiceAPI|VirtualMachineAndPod]->(s) " +
                        " WHERE" +
                        "    ANY(x IN r.traceIdSpanId WHERE x =~ '" + traceId + "-.*') " +
                        " RETURN n,m,r,s,rs";
        //待返回的值，与cql return后的值顺序对应
        Set<Pod> podSet = new HashSet<>();
        Set<ServiceAPI> serviceAPISet = new HashSet<>();
        Set<AppService> appServiceSet = new HashSet<>();
        Set<VirtualMachine> virtualMachineSet = new HashSet<>();

        Set<TraceInvokeApiToPod> traceInvokeApiToPodSet = new HashSet<>();
        Set<TraceInvokePodToApi> traceInvokePodToApiSet = new HashSet<>();
        Set<AppServiceAndPod> appServiceAndPodSet = new HashSet<>();
        Set<VirtualMachineAndPod> virtualMachineAndPodSet = new HashSet<>();
        Set<AppServiceHostServiceAPI> appServiceHostServiceAPISet = new HashSet<>();

        neo4jUtil.getTraceComponentList(cql, podSet,
                serviceAPISet,
                appServiceSet,
                virtualMachineSet,
                traceInvokeApiToPodSet,
                traceInvokePodToApiSet,
                appServiceAndPodSet,
                virtualMachineAndPodSet,
                appServiceHostServiceAPISet);

        System.out.println("[TraceInfo] Trace ID:" + traceId);
        System.out.println("PodSet:" + podSet.size());
        System.out.println("ServiceAPISet:" + serviceAPISet.size());
        System.out.println("AppServiceSet:" + appServiceSet.size());
        System.out.println("VirtualMachineSet:" + virtualMachineSet.size());

        System.out.println("TraceInvokeApiToPodSet:" + traceInvokeApiToPodSet.size());
        System.out.println("TraceInvokePodToApiSet:" + traceInvokePodToApiSet.size());
        System.out.println("AppServiceAndPodSet:" + appServiceAndPodSet.size());
        System.out.println("VirtualMachineAndPodSet:" + virtualMachineAndPodSet.size());
        System.out.println("AppServiceHostServiceAPISet:" + appServiceHostServiceAPISet.size());

        retMap.put("PodSet", podSet);
        retMap.put("ServiceAPISet", serviceAPISet);
        retMap.put("AppServiceSet", appServiceSet);
        retMap.put("VirtualMachineSet", virtualMachineSet);
        retMap.put("TraceInvokeApiToPodSet", traceInvokeApiToPodSet);
        retMap.put("TraceInvokePodToApiSet", traceInvokePodToApiSet);
        retMap.put("AppServiceAndPodSet", appServiceAndPodSet);
        retMap.put("VirtualMachineAndPodSet", virtualMachineAndPodSet);
        retMap.put("AppServiceHostServiceAPISet", appServiceHostServiceAPISet);

        return retMap;
    }


    @Transactional(readOnly = true)
    public Map<String, Set> getCrossComponentOfTwoTrace(String traceA, String traceB){
        //下面这部分是获得的两个Trace的交集
        Map<String, Set> traceAMap = getOneTracePath(traceA);
        Map<String, Set> traceBMap = getOneTracePath(traceB);

        Map<String, Set> crossingMap = new HashMap<>();

        System.out.println("[Crossing Set] Trace-A:" + traceA + " Trace-B:" + traceB);

        for(String key : traceAMap.keySet()){
            Set traceASet = traceAMap.get(key);
            Set traceBSet = traceBMap.get(key);

            Set crossingSet = new HashSet(traceASet);
            crossingSet.retainAll(traceBSet);

            crossingMap.put(key, crossingSet);

            System.out.println(key + ":" + crossingSet.size());
        }

        return crossingMap;
    }

    public ArrayList<UnitGraphNode> getSortedGraphNode(String traceId){
        Map<String, Set> metricMap = getOneTraceMetrics(traceId);

        Set<ServiceApiAndMetric> serviceApiAndMetricSet = metricMap.get("ServiceApiAndMetric");
        Set<PodAndMetric> podAndMetricSet = metricMap.get("PodAndMetric");

        ArrayList<UnitGraphNode> list = new ArrayList<>();
        for(PodAndMetric podAndMetric : podAndMetricSet){
            double rv = podAndMetric.getPodMetric().getAbnormality();
            GraphNode gn = podAndMetric.getPod();
            UnitGraphNode ung = new UnitGraphNode(rv, gn);
            list.add(ung);
        }
        for(ServiceApiAndMetric serviceApiAndMetric : serviceApiAndMetricSet) {
            double rv = serviceApiAndMetric.getApiMetric().getAbnormality();
            GraphNode gn = serviceApiAndMetric.getServiceAPI();
            UnitGraphNode ung = new UnitGraphNode(rv, gn);
            list.add(ung);
        }

        sortByCalculateValue(list);

        return list;
    }


    private void sortByCalculateValue(ArrayList<UnitGraphNode> nodeList){
        nodeList.sort((UnitGraphNode o1, UnitGraphNode o2) ->
            {
                if(o1.resultValue - o2.resultValue < 0){
                    return 1;
                }else if(o1.resultValue - o2.resultValue == 0){
                    return 0;
                }else{
                    return -1;
                }
            });
    }

}
