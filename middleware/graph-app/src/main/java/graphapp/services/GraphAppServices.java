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
import org.springframework.web.bind.annotation.RequestBody;
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

    public HashMap<GraphNode, HashMap<String, HashSet<BasicRelationship>>> getTotalGraph(){

        HashMap<GraphNode, HashMap<String, HashSet<BasicRelationship>>> ret =
                neo4jUtil.getWholeGraphByAdjacentList();

        //TODO 起点需要重新确认
//        ArrayList<GraphNode> startings = new ArrayList<>();
//        Iterator<GraphNode> iter = ret.keySet().iterator();
//        GraphNode gn1 = iter.next();
//        gn1.setScore(100);
//        startings.add(gn1);
//        startings.add(gn1);
//        layerTraverseGraphFromOneNode(startings, ret);

        return ret;
    }

    private void printTotalGraph(HashMap<GraphNode, HashMap<String, HashSet<BasicRelationship>>> graphAdjacentMap){
        for(GraphNode gn : graphAdjacentMap.keySet()){
            System.out.println(gn.getName());
            HashMap<String, HashSet<BasicRelationship>> adjacentMap = graphAdjacentMap.get(gn);
            for(String key : adjacentMap.keySet()){
                System.out.println("    " + key);
                HashSet<BasicRelationship> brs = adjacentMap.get(key);
                for(BasicRelationship br : brs){
                    System.out.println("        " + br.getRelation() + " | " + br.getId());
                }
            }
        }
    }

    class GraphNodeInfo{
        GraphNode node;
        ArrayList<GraphNode> parents = new ArrayList<>();
        ArrayList<BasicRelationship> toRelations = new ArrayList<>();
        ArrayList<Double> propagateScores = new ArrayList<>();
        double score = -1.0;

        GraphNodeInfo(GraphNode node){
            this.node = node;
        }
    }

    //返回结果是整张图的每一个节点
    private ArrayList<ArrayList<GraphNode>> layerTraverseGraphFromOneNode(ArrayList<GraphNode> startings,
                                               HashMap<GraphNode, HashMap<String, HashSet<BasicRelationship>>>
                                                       graphAdjacentMap){

        ArrayList<ArrayList<GraphNode>> ret = new ArrayList<>();
        HashMap<GraphNode, GraphNodeInfo> nodeAndInfo = new HashMap<>();


        for(GraphNode starting : startings){
            System.out.println("===============================================");
            //下面这两个是用于一次源节点值的传播的
            //已经被访问过的节点
            HashMap<GraphNode, GraphNodeInfo> recordVisited = new HashMap<>();
            //这个Queue用于广度优先遍历
            LinkedList<GraphNode> queue = new LinkedList<>();

            //起始点设置
            GraphNodeInfo initNode = new GraphNodeInfo(starting);
            initNode.score = starting.getScore();
            recordVisited.put(starting, initNode);
            queue.offer(starting);

            while(!queue.isEmpty()){
                int layerSize = queue.size();
                ArrayList<GraphNode> layerNodes = new ArrayList<>();
                System.out.println("本层大小" + layerSize);
                //在队列里找一个起点出来 接着遍历
                for(int i = 0; i < layerSize; i++){
                    GraphNode tempNode = queue.poll();
                    GraphNodeInfo tempNodeInfo = recordVisited.get(tempNode);
                    double fromNodeScore = tempNodeInfo.score;

                    System.out.println(tempNode.getName());
                    layerNodes.add(tempNode);
                    HashMap<String, HashSet<BasicRelationship>> neighborsMap = graphAdjacentMap.get(tempNode);
                    //这个点的邻居们的遍历
                    //它的邻居有好几种relationship 挨个遍历一下
                    if(neighborsMap == null){
                        System.out.println(tempNode.getName() + " 没有指向新节点");
                        continue;
                    }
                    for(String key : neighborsMap.keySet()){
                        HashSet<BasicRelationship> neighborsSet = neighborsMap.get(key);
                        System.out.println(key + " - " + neighborsSet.size());
                        for(BasicRelationship br : neighborsSet){
                            GraphNode to = null;
                            switch (key){
                                case "AppServiceAndPod":
                                    AppServiceAndPod asap = (AppServiceAndPod)br;
                                    to = asap.getAppService();
                                    break;
                                case "AppServiceHostServiceAPI":
                                    AppServiceHostServiceAPI ashsa = (AppServiceHostServiceAPI)br;
                                    to = ashsa.getAppService();
                                    break;
                                case "AppServiceInvokeServiceAPI":
                                    AppServiceInvokeServiceAPI asisa = (AppServiceInvokeServiceAPI)br;
                                    to = asisa.getServiceAPI();
                                    break;
                                case "MetricAndContainer":
                                    MetricAndContainer mac = (MetricAndContainer)br;
                                    to = mac.getContainer();
                                    break;
                                case "PodAndContainer":
                                    PodAndContainer pac = (PodAndContainer)br;
                                    to = pac.getPod();
                                    break;
                                case "PodAndMetric":
                                    PodAndMetric pam = (PodAndMetric)br;
                                    to = pam.getPod();
                                    break;
                                case "ServiceApiAndMetric":
                                    ServiceApiAndMetric sasm = (ServiceApiAndMetric)br;
                                    to = sasm.getServiceAPI();
                                    break;
                                case "TraceInvokeApiToPod":
                                    TraceInvokeApiToPod tiatp = (TraceInvokeApiToPod)br;
                                    to = tiatp.getPod();
                                    break;
                                case "TraceInvokePodToApi":
                                    TraceInvokePodToApi tipta = (TraceInvokePodToApi)br;
                                    to = tipta.getServiceAPI();
                                    break;
                                case "VirtualMachineAndPod":
                                    VirtualMachineAndPod vmap = (VirtualMachineAndPod)br;
                                    to = vmap.getVirtualMachine();
                                    break;
                            }
                            //to被搜索过了吗 有的话把from的score加进去 没有就算了
                            GraphNodeInfo gni = null;
                            if(!recordVisited.containsKey(to)){
//                            if(nodeAndInfo.get(to) == null) {
                                queue.add(to);
                                if(nodeAndInfo.get(to) == null){
                                    gni = new GraphNodeInfo(to);
                                }else{
                                    gni = nodeAndInfo.get(to);
                                }
                                System.out.println("在第" + ret.size() + "层新发现:" + gni.node.getName());
                            }else{
                                gni = recordVisited.get(to);
                                System.out.println("更新已发现节点GraphNodeInfo");
                            }
                            gni.parents.add(tempNode);
                            gni.propagateScores.add(fromNodeScore);
                            gni.score = calcualteScore(gni);
                            gni.toRelations.add(br);
                            System.out.println(gni.node.getName() + "分数为:" + gni.score);
                            recordVisited.put(to, gni);
                            nodeAndInfo.put(to, gni);
                        }
                    }
                }
                ret.add(layerNodes);
            }

        }


        //查到的分类节点
        HashMap<String, ArrayList<GraphNodeInfo>> recordTypeScore = new HashMap<>();
        for(GraphNode gn : nodeAndInfo.keySet()){
                String className = gn.getClassName();
                ArrayList<GraphNodeInfo> graphNodeInfos = recordTypeScore.getOrDefault(className, new ArrayList<>());
                graphNodeInfos.add(nodeAndInfo.get(gn));
                recordTypeScore.put(className, graphNodeInfos);
        }

        for(String key : recordTypeScore.keySet()){
            ArrayList<GraphNodeInfo> graphNodeInfos = recordTypeScore.get(key);
            Collections.sort(graphNodeInfos, new Comparator<GraphNodeInfo>() {
                @Override
                public int compare(GraphNodeInfo o1, GraphNodeInfo o2) {
                    return 0 - Double.compare(o1.score, o2.score);
                }
            });
            System.out.println("====" + key + "====");
            for(GraphNodeInfo graphNodeInfo : graphNodeInfos){
                System.out.println(graphNodeInfo.node.getName() + "    " + graphNodeInfo.score);
            }
        }

        return ret;
    }


    private double calcualteScore(GraphNodeInfo info){
        ArrayList<Double> scores = info.propagateScores;
        int size = scores.size();
        if(size == 0){
            return info.score;
        } else if(size == 1){
            return 0.7 * scores.get(0);
        }else{
            double total = 0;
            for(int i = 0; i < size; i++){
                total += scores.get(i);
            }
            total /= scores.size();
            //TODO 计算有误
            return total * Math.log(size);
        }
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

    public String updatePodAbnormalityByList(ArrayList<String> podMetricIdList){
        for(String podMetricId : podMetricIdList){
            PodMetric podMetric = metricOfPodRepository.findById(podMetricId).get();
            double abnormality = updateSingleAbnormalityOfPods(podMetric);
            podMetric.getHistoryAbnormality().add(podMetric.getAbnormality());

            System.out.println("====updatePodAbnormalityByList");
            System.out.println(podMetric.getHistoryAbnormality());
            System.out.println(abnormality);

            podMetric.setAbnormality(abnormality);
            metricOfPodRepository.save(podMetric);
        }
        return "Success " + podMetricIdList.size();
    }

    public String updateApiAbnormalityByList(@RequestBody ArrayList<String> apiMetricIdList){
        for(String apiMetricId : apiMetricIdList){
            ServiceApiMetric serviceApiMetric = metricOfServiceApiRepository.findById(apiMetricId).get();
            double abnormality = updateSingleAbnormalityOfServiceApis(serviceApiMetric);
            serviceApiMetric.getHistoryAbnormality().add(serviceApiMetric.getAbnormality());
            serviceApiMetric.setAbnormality(abnormality);
            metricOfServiceApiRepository.save(serviceApiMetric);
        }

        return "Success" + apiMetricIdList.size();
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
        ArrayList<Double> values = serviceApiMetric.getHistoryValues();
        if(values.size() <= 3){
            return 0.0;
        }else{
            return threeSigmaAbnormality(values,serviceApiMetric.getValue());
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
                "\n LatestAvg:" + latestAvg +
                "\n TotalAvg:" + totalAvg +
                "\n TotalSd:" + totalSd +
                "\n Abnormality:" + abnormality);

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
