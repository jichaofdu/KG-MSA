package graphapp.services;

import com.sun.tools.javac.code.Scope;
import graphapp.domain.UnitGraphNode;
import graphapp.domain.entities.*;
import graphapp.domain.relationships.*;
import graphapp.repositories.MetricOfPodRepository;
import graphapp.repositories.MetricOfServiceApiRepository;
import graphapp.repositories.PodRepository;
import graphapp.repositories.ServiceApiRepository;
import graphapp.utils.Neo4jUtil;
import graphapp.utils.RemoteExecuteCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import java.text.DecimalFormat;
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

    /****************************基础算法：读取和打印全图数据：开始***********************************/

    public HashMap<GraphNode, HashMap<String, HashSet<BasicRelationship>>> getTotalGraph(){

        HashMap<GraphNode, HashMap<String, HashSet<BasicRelationship>>> ret =
                neo4jUtil.getWholeGraphByAdjacentList();

        printTotalGraph(ret);

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

    /****************************基础算法：读取和打印全图数据：结束***********************************/


    /****************************扩缩容优化算法：微服务间流量分析：开始***********************************/
    //返回数据格式: 起点微服务-重点微服务-服务间流量
    public HashMap<String, HashMap<String, Integer>> extractLoadRelationAmongMicroservice(){
        //API从属Map API:SvcName
        HashMap<String, String> apiBelongMap = new HashMap<>();
        //服务调用API Map  SvcName:Relation
        HashMap<String, HashSet<AppServiceInvokeServiceAPI>> apiInvokeByMap = new HashMap<>();

        //第一步：确定API和微服务的从属关系
        HashMap<GraphNode, HashMap<String, HashSet<BasicRelationship>>> apiBelongMicrosvc =
                neo4jUtil.getNodeAndRelationInfoWithRelationTag("AppServiceHostServiceAPI");
        for(Map.Entry<GraphNode, HashMap<String, HashSet<BasicRelationship>>> entry: apiBelongMicrosvc.entrySet()){
            ServiceAPI api = (ServiceAPI)entry.getKey();
            HashSet<BasicRelationship> apiHostRelation = entry.getValue().get("AppServiceHostServiceAPI");
            AppServiceHostServiceAPI relation = (AppServiceHostServiceAPI)apiHostRelation.iterator().next();
            AppService appService = relation.getAppService();
            apiBelongMap.put(api.getName(), appService.getName());
            System.out.println("API属于关系:" + api.getName() + " 属于" + appService.getName());
        }


        //第二步：确定微服务与API的负载关系
        HashMap<GraphNode, HashMap<String, HashSet<BasicRelationship>>> microsvcInvokeApi =
                neo4jUtil.getNodeAndRelationInfoWithRelationTag("AppServiceInvokeServiceAPI");
        for(Map.Entry<GraphNode, HashMap<String, HashSet<BasicRelationship>>> entry : microsvcInvokeApi.entrySet()){
            AppService appService = (AppService) entry.getKey();
            String svcName = appService.getName();
            HashSet<BasicRelationship> invokeRelations = entry.getValue().get("AppServiceInvokeServiceAPI");
            for(BasicRelationship rawRelation: invokeRelations){
                AppServiceInvokeServiceAPI relation = (AppServiceInvokeServiceAPI)rawRelation;
                HashSet<AppServiceInvokeServiceAPI> apis = apiInvokeByMap.getOrDefault(svcName, new HashSet<>());
                apis.add(relation);
                apiInvokeByMap.put(appService.getName(), apis);
                System.out.println("API调用关系:" + relation.getServiceAPI().getName() + " 被调用" + appService.getName());
            }
        }

        //第三步：整合API与微服务的负载关系
        HashMap<String, HashMap<String, Integer>> flowRecord = new HashMap<>();
        for(Map.Entry<String, HashSet<AppServiceInvokeServiceAPI>> entry : apiInvokeByMap.entrySet()) {
            String fromSvcName = entry.getKey();
            for(AppServiceInvokeServiceAPI apiRelation : entry.getValue()){
                String apiName = apiRelation.getServiceAPI().getName();
                String toSvcName = apiBelongMap.getOrDefault(apiName, "defaultSvc");
                int count = apiRelation.getCount();
                HashMap<String, Integer> targets = flowRecord.getOrDefault(fromSvcName, new HashMap<>());
                int totalCount = targets.getOrDefault(toSvcName, 0) + count;
                targets.put(toSvcName, totalCount);
                flowRecord.put(fromSvcName, targets);
            }
        }

        //第四步：打印数据
        for(Map.Entry<String, HashMap<String, Integer>> entry : flowRecord.entrySet()){
            String fromSvcName = entry.getKey();
            HashMap<String, Integer> map = entry.getValue();
            for(Map.Entry<String, Integer> dataEntry : map.entrySet()){
                String toSvcName = dataEntry.getKey();
                int number = dataEntry.getValue();
                System.out.println("微服务" + fromSvcName + "调用了" + toSvcName + " " + number + "次");
            }
        }

        //第五步：返回数据
        return flowRecord;
    }

    /****************************扩缩容优化算法：微服务间流量分析：结束***********************************/

    /****************************扩缩容优化方法：微服务间流量变化计算：开始********************************/
    //输出数据是变化后的流量数据
    //格式： 起点微服务-重点微服务-新的流量数据
    public HashMap<String, Double> extractLoadRelationAmongMicroservice(
            HashMap<String, HashMap<String, Integer>> oldLoadData, String staringSvcName, int newLoad){

        //1.先把旧的数据Copy一份到新的数据
        HashMap<String, HashMap<String, Integer>> newLoadData = new HashMap<>();
        for(Map.Entry<String, HashMap<String, Integer>> oldOuterEntry: oldLoadData.entrySet()){
            String fromSvcName = oldOuterEntry.getKey();
            for(Map.Entry<String, Integer> oldInnerEntry : oldOuterEntry.getValue().entrySet()){
                String toSvcName = oldInnerEntry.getKey();
                int count = oldInnerEntry.getValue();
                //将旧数据抄录进去
                HashMap<String, Integer> newInnerEntry = newLoadData.getOrDefault(fromSvcName, new HashMap<>());
                newInnerEntry.put(toSvcName, count);
                newLoadData.put(fromSvcName, newInnerEntry);
            }
        }
        //2.统计旧时刻每隔微服务的负载
        HashMap<String, Integer> oldMvcPayloadMap = new HashMap<>();
        for(Map.Entry<String, HashMap<String, Integer>> oldOuterEntry: oldLoadData.entrySet()){
            for(Map.Entry<String, Integer> oldInnerEntry : oldOuterEntry.getValue().entrySet()){
                String toSvcName = oldInnerEntry.getKey();
                int oldMvcPayLoad = oldMvcPayloadMap.getOrDefault(toSvcName, 0);
                oldMvcPayLoad += oldInnerEntry.getValue();
                oldMvcPayloadMap.put(toSvcName, oldMvcPayLoad);
            }
        }
        //3.计算微服务间负载变化
        HashMap<String, Integer> newMvcPayloadMap = new HashMap<>(oldMvcPayloadMap);
        newMvcPayloadMap.put(staringSvcName, newLoad);

        LinkedList<String> propagateSvcNameStack = new LinkedList<>();
        propagateSvcNameStack.offer(staringSvcName);

        while(!propagateSvcNameStack.isEmpty()){
            String nextSvcName = propagateSvcNameStack.poll();
            int thisSvcOldPayload = oldMvcPayloadMap.get(nextSvcName);
            int thisSvcNewPayload = newMvcPayloadMap.get(nextSvcName);
            double proportionScale = (double)thisSvcNewPayload / (double)thisSvcOldPayload;

            HashMap<String, Integer> invokeSvcMap = newLoadData.get(nextSvcName);
            if(invokeSvcMap == null){
                continue;
            }
            for(Map.Entry<String, Integer> invokeEntry : invokeSvcMap.entrySet()){
                String toSvc = invokeEntry.getKey();
                int toPayload = invokeEntry.getValue();
                int newToPayload = (int)(toPayload * proportionScale);
                invokeSvcMap.put(toSvc, newToPayload);

                int toSvcOldPayload = newMvcPayloadMap.get(toSvc);
                int toSvcNewPayload = toSvcOldPayload - toPayload + newToPayload;
                newMvcPayloadMap.put(toSvc, toSvcNewPayload);

                propagateSvcNameStack.offer(toSvc);
            }
            newLoadData.put(nextSvcName, invokeSvcMap);

        }

        //4.输出结果
        HashMap<String, Double> resultMap = new HashMap<>();
        for(String svcName : oldMvcPayloadMap.keySet()){
            int oldPayLoad = oldMvcPayloadMap.get(svcName);
            int newPayLoad = newMvcPayloadMap.get(svcName);
            double portion = (double)newPayLoad / (double)oldPayLoad;
            resultMap.put(svcName, portion);
            System.out.println("微服务" + svcName + "流量变化:" + portion);
        }

        //5.返回结果
        return resultMap;
    }
    /****************************扩缩容优化方法：微服务间流量变化计算：结束********************************/


    /****************************扩缩容优化方法：计算新时刻各个微服务的应有数量：开始********************************/
    public  HashMap<String, Integer> calculateMvcReplicaInNewEra(HashMap<String, Double> svcPayloadChangePortion){
        //1.登记每个微服务目前包含多少个副本
        HashMap<String, Integer> oldSvcReplicaNumber = new HashMap<>();
        HashMap<GraphNode, HashMap<String, HashSet<BasicRelationship>>> podBelongToSvc =
                neo4jUtil.getNodeAndRelationInfoWithRelationTag("AppServiceAndPod");
        for(Map.Entry<GraphNode, HashMap<String, HashSet<BasicRelationship>>> entry: podBelongToSvc.entrySet()){
            Pod pod = (Pod)entry.getKey();
            HashSet<BasicRelationship> apiHostRelation = entry.getValue().get("AppServiceAndPod");
            AppServiceAndPod relation = (AppServiceAndPod)apiHostRelation.iterator().next();
            AppService svc = relation.getAppService();
            String svcName = svc.getName();

            int existReplica = oldSvcReplicaNumber.getOrDefault(svcName, 0);
            existReplica += 1;
            oldSvcReplicaNumber.put(svcName, existReplica);
        }

        //2.根据Payload的比例关系记录新的微服务实例数量数据
        HashMap<String, Integer> newSvcReplicaNumber = new HashMap<>();
        for(String svcName : svcPayloadChangePortion.keySet()){
            double portion = svcPayloadChangePortion.get(svcName);
            int oldReplica = oldSvcReplicaNumber.get(svcName);
            int newReplica = (int)Math.ceil((double)oldReplica * portion);
            newSvcReplicaNumber.put(svcName, newReplica);
        }

        //3.打印结果
        for(String svcName : newSvcReplicaNumber.keySet()){
            int oldReplica = oldSvcReplicaNumber.get(svcName);
            int newReplica = newSvcReplicaNumber.get(svcName);
            System.out.println("微服务" + svcName + "副本数由" + oldReplica + "变为" + newReplica);
        }

        //4.返回结果
        return newSvcReplicaNumber;
    }
    /****************************扩缩容优化方法：计算新时刻各个微服务的应有数量：结束********************************/


    /****************************扩缩容优化方法：执行扩缩容：开始********************************/
    public void doScaling(HashMap<String, Integer> scalingDemands){

        String ip = "10.141.211.162";
        String user = "root";
        String passwd = "FlHy355g@rA#grhV";
        RemoteExecuteCommand rec = new RemoteExecuteCommand(ip, user, passwd);
        System.out.println(rec.login());

        for(String svcName : scalingDemands.keySet()){
            int replca = scalingDemands.get(svcName);
            System.out.println(
                    rec.execute("export KUBECONFIG=/etc/kubernetes/admin.conf; " +
                            "kubectl scale deployment.v1.apps/" + svcName + " --replicas=" + replca)
            );
        }

    }
    /****************************扩缩容优化方法：执行扩缩容：结束********************************/


    /****************************故障定位算法：开始***********************************/

    public String diagnosisTrace(String traceId){

        Map<String, Set> traceMetricsSet = getOneTraceMetrics(traceId);
        Set<PodMetric> podMetricSet = traceMetricsSet.get("PodMetric");
        Set<ServiceApiMetric> serviceApiMetricSet = traceMetricsSet.get("ServiceApiMetric");

        ArrayList<BasicMetric> startings = new ArrayList<>();
        startings.addAll(serviceApiMetricSet);

        HashMap<GraphNode, HashMap<String, HashSet<BasicRelationship>>> ret = getTotalGraph();

        layerTraverseGraphFromOneNode(startings, ret);

        //TODO 起点需要重新确认
//        ArrayList<GraphNode> startings = new ArrayList<>();
//        Iterator<GraphNode> iter = ret.keySet().iterator();
//        GraphNode gn1 = iter.next();
//        gn1.setScore(100);
//        startings.add(gn1);
//        startings.add(gn1);
//        layerTraverseGraphFromOneNode(startings, ret);

        return "";
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
    private void layerTraverseGraphFromOneNode(ArrayList<BasicMetric> startings,
                                               HashMap<GraphNode, HashMap<String, HashSet<BasicRelationship>>>
                                                       graphAdjacentMap){

        HashMap<GraphNode, GraphNodeInfo> nodeAndInfo = new HashMap<>();


        for(BasicMetric starting : startings){

            System.out.println("==================On Processing==================");

            System.out.println("Startings:" + starting.getName() + " Abnormality:" +starting.getAbnormality());

            //下面这两个是用于一次源节点值的传播的
            //已经被访问过的节点
            HashMap<GraphNode, GraphNodeInfo> recordVisited = new HashMap<>();
            //这个Queue用于广度优先遍历
            LinkedList<GraphNode> queue = new LinkedList<>();

            //起始点设置
            GraphNodeInfo initNode = new GraphNodeInfo(starting);
            initNode.score = starting.getAbnormality();
            recordVisited.put(starting, initNode);
            queue.offer(starting);

            int layerIndex = 0;
            while(!queue.isEmpty()){
                int layerSize = queue.size();
                ArrayList<GraphNode> layerNodes = new ArrayList<>();
//                System.out.println("本层大小" + layerSize);
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
//                        System.out.println(tempNode.getName() + " 没有指向新节点");
                        continue;
                    }
                    for(String key : neighborsMap.keySet()){
                        HashSet<BasicRelationship> neighborsSet = neighborsMap.get(key);
//                        System.out.println(key + " - " + neighborsSet.size());
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
//                                System.out.println("在第" + layerIndex + "层新发现:" + gni.node.getName());
                            }else{
                                gni = recordVisited.get(to);
//                                System.out.println("更新已发现节点GraphNodeInfo");
                            }
                            gni.parents.add(tempNode);
                            gni.propagateScores.add(fromNodeScore);
                            gni.score = calcualteScore(gni);
                            gni.toRelations.add(br);
//                            System.out.println(gni.node.getName() + "分数为:" + gni.score);
                            recordVisited.put(to, gni);
                            nodeAndInfo.put(to, gni);
                        }
                    }
                }
                layerIndex++;
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

        ArrayList<KeyHigher> keys = new ArrayList<>();

        for(String key : recordTypeScore.keySet()){
            ArrayList<GraphNodeInfo> graphNodeInfos = recordTypeScore.get(key);

            double total = 0.0001;

            //前面是按照累积的异常度排序 接下来要把这些异常度归一化
            for(GraphNodeInfo graphNodeInfo : graphNodeInfos){
                total += graphNodeInfo.score;
            }

            double average = total / graphNodeInfos.size();
            double maxHigherThanAverage = graphNodeInfos.get(0).score / average;
            keys.add(new KeyHigher(key, maxHigherThanAverage));

            for(GraphNodeInfo graphNodeInfo : graphNodeInfos){
                graphNodeInfo.score = graphNodeInfo.score / average;
//                System.out.println(graphNodeInfo.node.getName() + "    " + (graphNodeInfo.score * 100) + "%");
            }
        }

        Collections.sort(keys, new Comparator<KeyHigher>() {
            @Override
            public int compare(KeyHigher o1, KeyHigher o2) {
                return Double.compare(o1.value, o2.value);
            }
        });

        System.out.println("==========End Diagnosis==========");

        for(KeyHigher kh : keys){
            String key = kh.key;
            ArrayList<GraphNodeInfo> graphNodeInfos = recordTypeScore.get(key);
            Collections.sort(graphNodeInfos, new Comparator<GraphNodeInfo>() {
                @Override
                public int compare(GraphNodeInfo o1, GraphNodeInfo o2) {
                    return 0 - Double.compare(o1.score, o2.score);
                }
            });
            System.out.println("====" + key + "====");

            int count = 0;
            for(GraphNodeInfo graphNodeInfo : graphNodeInfos){
                if(graphNodeInfo.score > 1.0){
                    DecimalFormat df = new DecimalFormat("0.000");

                    System.out.println(graphNodeInfo.node.getName() + "    " + df.format(graphNodeInfo.score));
                }else{
                    break;
                }
            }
        }
    }

    //用来输出结果的时候决定先输出哪一个key的
    class KeyHigher {
        String key;
        double value;

        KeyHigher(String key, double value){
            this.key = key;
            this.value = value;
        }
    }

    //GraphNodeInfo是在故障诊断时图遍历过程中所使用到的节点
    //其中包含其他节点传来的异常度的值 这里根据定义的算法综合这些传来的异常度输出一个最终的异常度
    private double calcualteScore(GraphNodeInfo info){
        ArrayList<Double> scores = info.propagateScores;
        int size = scores.size();
        if(size == 0){
            return info.score;
        } else if(size == 1){
            return 0.8 * scores.get(0);
        }else{
            double total = 0;
            int count = 0;
            for(int i = 0; i < size; i++){
                total += scores.get(i);
            }
            total /= scores.size();

            return total;
        }
    }

    /****************************故障定位算法：结束***********************************/


    /****************************图谱数据更新，异常度计算：开始***********************************/

    //更新所有PodMetric的异常度
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

    //更新一组PodMetric的异常度
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

    //更新一组ServiceApiMetric的异常度
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

    //更新所有ServiceApiMetric的异常度
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

    //返回某一个Pod的异常度
    private double updateSingleAbnormalityOfPods(PodMetric podMetric){
        ArrayList<Double> values = podMetric.getHistoryValues();
        if(values.size() <= 3){
            return 0.0;
        }else{
            return threeSigmaAbnormality(values, podMetric.getValue());
        }

    }

    //返回某一个ApiMetirc的异常度
    private double updateSingleAbnormalityOfServiceApis(ServiceApiMetric serviceApiMetric){
        ArrayList<Double> values = serviceApiMetric.getHistoryValues();
        if(values.size() <= 3){
            return 0.0;
        }else{
            return threeSigmaAbnormality(values,serviceApiMetric.getValue());
        }
    }

    //给出一组历史数据，给出一个当前值，输出当前值相比过去的异常度
    private double threeSigmaAbnormality(ArrayList<Double> historyValue, double latestValue){
        int arrLen = historyValue.size();
        double latestAvg = (latestValue + historyValue.get(arrLen-1) + historyValue.get(arrLen-2)) / 3.0;

        //抛掉最大值 最大值一般出现于请求第一次调用,此时很多应用尚未初始化
        ArrayList<Double> arrangedArr = new ArrayList<>();
        arrangedArr.addAll(historyValue);
        Collections.sort(arrangedArr, Collections.reverseOrder());
        arrangedArr.remove(0);

        double totalAvg = getAverage(arrangedArr);
        double totalSd = getStandardDiviation(arrangedArr, totalAvg);

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

    /****************************图谱数据更新，异常度计算：结束***********************************/


    /****************************图谱数据花式查询：开始***********************************/

    //获取两条Trace的交集
    @Transactional(readOnly = true)
    public Map<String, Set> getCrossOfTwoTrace(String traceA, String traceB){
        Map<String, Set> crossComponent = getCrossComponentOfTwoTrace(traceA, traceB);
        Map<String, Set> crossMetrics = getCrossMetricsOfTwoTrace(traceA, traceB);

        Map<String, Set> cross = new HashMap<>();
        cross.putAll(crossComponent);
        cross.putAll(crossMetrics);

        return cross;
    }

    //获取一条Trace的Metric信息
    @Transactional(readOnly = true)
    public Map<String, Set> getOneTraceMetrics(String traceId){
        Map<String, Set> retMap = new HashMap<>();
        String cql =
                "MATCH (n)-[r:TraceInvokeApiToPod|TraceInvokePodToApi]->(m)<-[rm:PodAndMetric|ServiceApiAndMetric]-(metrics) " +
                        "WHERE " +
                        "    ANY(x IN r.traceIdSpanId WHERE x =~ '" + traceId + "-.*') " +
                        "WITH n,m,r,metrics,rm " +
                        "RETURN m,metrics,rm";
        Set<PodMetric> podMetricSet = new HashSet<>();
        Set<ServiceApiMetric> serviceApiMetricSet = new HashSet<>();
        Set<PodAndMetric> podAndMetricSet = new HashSet<>();
        Set<ServiceApiAndMetric> serviceApiAndMetricSet = new HashSet<>();

        neo4jUtil.getTraceMetricComponentList(cql,
                podMetricSet,
                serviceApiMetricSet,
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

    //获取两条Trace交集Metirc信息
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

    //获得一条Trace除了Metric以外的信息
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

    //获取一条Trace中的Abnormality的排序
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

    /****************************图谱数据花式查询：结束***********************************/

}
