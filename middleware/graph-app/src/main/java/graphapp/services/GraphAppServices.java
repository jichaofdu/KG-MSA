package graphapp.services;

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

    public double updateSingleAbnormalityOfPods(PodMetric podMetric){
        ArrayList<Double> values = podMetric.getHistoryValues();
        if(values.size() <= 3){
            return 0.0;
        }else{
            return threeSigmaAbnormality(values, podMetric.getValue());
        }

    }

    public double updateSingleAbnormalityOfServiceApis(ServiceApiMetric serviceApiMetric){
        ArrayList<Double> values = serviceApiMetric.getValues();
        if(values.size() <= 3){
            return 0.0;
        }else{
            return threeSigmaAbnormality(values, values.get(values.size()-1));
        }

    }

    public double threeSigmaAbnormality(ArrayList<Double> historyValue, double latestValue){
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

    public double getAverage(ArrayList<Double> x){
        int m = x.size();
        double sum=0;
        for(int i = 0; i < m; i++){//求和
            sum += x.get(i);
        }
        return sum / m;
    }

    public double getStandardDiviation(ArrayList<Double> x, double dAve) {
        double dVar = 0;
        for(int i = 0; i < x.size(); i++){//求方差
            dVar += (x.get(i) - dAve) * (x.get(i) - dAve);
        }
        return Math.sqrt(dVar / x.size());
    }



    @Transactional(readOnly = true)
    public Map<String, Set> getOneTracePath(String traceId){
        Map<String, Set> retMap = new HashMap<>();
        //cql语句
        String cql =
                "MATCH (n)-[r:TraceInvokeApiToPod|TraceInvokePodToApi]->(m)-[rs:AppServiceAndPod|AppServiceHostServiceAPI|VirtualMachineAndPod]->(s) " +
                        " WHERE" +
                        "    ANY(x IN r.traceIdSpanId WHERE x =~ '" + traceId + "-.*') " +
                        " RETURN n,r,m,rs,s ";
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

        neo4jUtil.getList(cql, podSet,
                serviceAPISet,
                appServiceSet,
                virtualMachineSet,
                traceInvokeApiToPodSet,
                traceInvokePodToApiSet,
                appServiceAndPodSet,
                virtualMachineAndPodSet,
                appServiceHostServiceAPISet);

        System.out.println("[TraceInfo] Trace ID:" + traceId);
        System.out.println("Pod:" + podSet.size());
        System.out.println("ServiceAPI:" + serviceAPISet.size());
        System.out.println("AppService:" + appServiceSet.size());
        System.out.println("VirtualMachine:" + virtualMachineSet.size());

        System.out.println("TraceInvokeApiToPod:" + traceInvokeApiToPodSet.size());
        System.out.println("TraceInvokePodToApi:" + traceInvokePodToApiSet.size());
        System.out.println("AppServiceAndPod:" + appServiceAndPodSet.size());
        System.out.println("VirtualMachineAndPod:" + virtualMachineAndPodSet.size());
        System.out.println("AppServiceHostServiceAPI:" + appServiceHostServiceAPISet.size());

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

}
