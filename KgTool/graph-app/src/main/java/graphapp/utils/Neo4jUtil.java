package graphapp.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import graphapp.domain.entities.*;
import graphapp.domain.relationships.*;

import java.util.*;

/**
 * 通用的neo4j调用类
 */
@Component
public class Neo4jUtil {

    private static Driver driver;

    private Gson gson = new Gson();

    @Autowired
    public Neo4jUtil(Driver driver) {
        Neo4jUtil.driver = driver;
    }

    //提取API相关的关系和节点
    public HashMap<GraphNode, HashMap<String, HashSet<BasicRelationship>>> getNodeAndRelationInfoWithRelationTag(String relationTag){
        HashMap<GraphNode, HashMap<String, HashSet<BasicRelationship>>> graphAdjacentList = new HashMap<>();
        String cql = "MATCH (from)-[relationship:" + relationTag + "]->(to) RETURN relationship, from, to";
        try {
            Session session = driver.session();
            StatementResult result = session.run(cql);
            List<Record> list = result.list();
            for (Record r : list) {
                //拿到关系本身 起点 重点
                Map<String, Object> relationshipMap
                        = new HashMap<>(r.get("relationship").asMap());
                Map<String, Object> fromMap
                        = new HashMap<>(r.get("from").asMap());
                Map<String, Object> toMap
                        = new HashMap<>(r.get("to").asMap());

                String fullClassName = (String)relationshipMap.get("className");
                String rawClassName = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);

                BasicRelationship br = null;
                GraphNode from =  null;
                HashMap<String, HashSet<BasicRelationship>> fromAdjacentMap = null;
                HashSet<BasicRelationship> fromAdjacentSet = null;

                switch (rawClassName){
                    case "AppServiceAndPod":
                        AppServiceAndPod r1 = getNode(AppServiceAndPod.class, relationshipMap);
                        r1.setPod(getNode(Pod.class, fromMap));
                        r1.setAppService(getNode(AppService.class, toMap));

                        br = r1;
                        from = r1.getPod();

                        break;
                    case "AppServiceHostServiceAPI":
                        AppServiceHostServiceAPI r2 = getNode(AppServiceHostServiceAPI.class, relationshipMap);
                        r2.setServiceAPI(getNode(ServiceAPI.class, fromMap));
                        r2.setAppService(getNode(AppService.class, toMap));

                        br = r2;
                        from = r2.getServiceAPI();

                        break;
                    case "AppServiceInvokeServiceAPI":
                        AppServiceInvokeServiceAPI r3 = getNode(AppServiceInvokeServiceAPI.class, relationshipMap);
                        r3.setAppService(getNode(AppService.class, fromMap));
                        r3.setServiceAPI(getNode(ServiceAPI.class, toMap));

                        br = r3;
                        from = r3.getAppService();

                        break;
                    case "MetricAndContainer":
                        MetricAndContainer r4 = getNode(MetricAndContainer.class, relationshipMap);
                        r4.setMetric(getNode(Metric.class, fromMap));
                        r4.setContainer(getNode(Container.class, toMap));

                        br = r4;
                        from = r4.getMetric();

                        break;
                    case "PodAndContainer":
                        PodAndContainer r5 = getNode(PodAndContainer.class, relationshipMap);
                        r5.setContainer(getNode(Container.class, fromMap));
                        r5.setPod(getNode(Pod.class, toMap));

                        br = r5;
                        from = r5.getContainer();

                        break;
                    case "PodAndMetric":
                        PodAndMetric r6 = getNode(PodAndMetric.class, relationshipMap);
                        r6.setPodMetric(getNode(PodMetric.class, fromMap));
                        r6.setPod(getNode(Pod.class, toMap));

                        br = r6;
                        from = r6.getPodMetric();

                        break;
                    case "ServiceApiAndMetric":
                        ServiceApiAndMetric r7 = getNode(ServiceApiAndMetric.class, relationshipMap);
                        r7.setApiMetric(getNode(ServiceApiMetric.class, fromMap));
                        r7.setServiceAPI(getNode(ServiceAPI.class, toMap));

                        br = r7;
                        from = r7.getApiMetric();

                        break;
                    case "TraceInvokeApiToPod":
                        TraceInvokeApiToPod r8 = getNode(TraceInvokeApiToPod.class, relationshipMap);
                        r8.setServiceAPI(getNode(ServiceAPI.class, fromMap));
                        r8.setPod(getNode(Pod.class, toMap));

                        br = r8;
                        from = r8.getServiceAPI();

                        break;
                    case "TraceInvokePodToApi":
                        TraceInvokePodToApi r9 = getNode(TraceInvokePodToApi.class, relationshipMap);
                        r9.setPod(getNode(Pod.class, fromMap));
                        r9.setServiceAPI(getNode(ServiceAPI.class, toMap));

                        br = r9;
                        from = r9.getPod();

                        break;
                    case "VirtualMachineAndPod":
                        VirtualMachineAndPod r10 = getNode(VirtualMachineAndPod.class, relationshipMap);
                        r10.setPod(getNode(Pod.class, fromMap));
                        r10.setVirtualMachine(getNode(VirtualMachine.class, toMap));

                        br = r10;
                        from = r10.getPod();

                        break;
                }

                fromAdjacentMap =
                        graphAdjacentList.getOrDefault(from, new HashMap<>());
                fromAdjacentSet = fromAdjacentMap.getOrDefault(rawClassName, new HashSet<>());
                fromAdjacentSet.add(br);
                fromAdjacentMap.put(rawClassName, fromAdjacentSet);
                graphAdjacentList.put(from, fromAdjacentMap);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return graphAdjacentList;
    }


    //下面这个方法是拿邻接矩阵的
    //检索图中的每一条关系 以关系的起点作为Key 而Value是按照关系类型分类的以Key为起点的各种关系
    //todo
    //还需要搞一个以to为Key的 value是以key为终点的按照关系类型分类的各种关系
    public HashMap<GraphNode, HashMap<String, HashSet<BasicRelationship>>> getWholeGraphByAdjacentList(){
        HashMap<GraphNode, HashMap<String, HashSet<BasicRelationship>>> graphAdjacentList = new HashMap<>();

        String cql =
                "MATCH (from)-[relationship]->(to) " +
                "RETURN relationship, from, to";
        try {
            Session session = driver.session();
            StatementResult result = session.run(cql);
            List<Record> list = result.list();
            for (Record r : list) {
                //拿到关系本身 起点 终点
                Map<String, Object> relationshipMap
                        = new HashMap<>(r.get("relationship").asMap());
                Map<String, Object> fromMap
                        = new HashMap<>(r.get("from").asMap());
                Map<String, Object> toMap
                        = new HashMap<>(r.get("to").asMap());

                String fullClassName = (String)relationshipMap.get("className");
                String rawClassName = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);

                BasicRelationship br = null;
                GraphNode from =  null;
                HashMap<String, HashSet<BasicRelationship>> fromAdjacentMap = null;
                HashSet<BasicRelationship> fromAdjacentSet = null;

                switch (rawClassName){
                    case "AppServiceAndPod":
                        AppServiceAndPod r1 = getNode(AppServiceAndPod.class, relationshipMap);
                        r1.setPod(getNode(Pod.class, fromMap));
                        r1.setAppService(getNode(AppService.class, toMap));

                        br = r1;
                        from = r1.getPod();

                        break;
                    case "AppServiceHostServiceAPI":
                        AppServiceHostServiceAPI r2 = getNode(AppServiceHostServiceAPI.class, relationshipMap);
                        r2.setServiceAPI(getNode(ServiceAPI.class, fromMap));
                        r2.setAppService(getNode(AppService.class, toMap));

                        br = r2;
                        from = r2.getServiceAPI();

                        break;
                    case "AppServiceInvokeServiceAPI":
                        AppServiceInvokeServiceAPI r3 = getNode(AppServiceInvokeServiceAPI.class, relationshipMap);
                        r3.setAppService(getNode(AppService.class, fromMap));
                        r3.setServiceAPI(getNode(ServiceAPI.class, toMap));

                        br = r3;
                        from = r3.getAppService();

                        break;
                    case "MetricAndContainer":
                        MetricAndContainer r4 = getNode(MetricAndContainer.class, relationshipMap);
                        r4.setMetric(getNode(Metric.class, fromMap));
                        r4.setContainer(getNode(Container.class, toMap));

                        br = r4;
                        from = r4.getMetric();

                        break;
                    case "PodAndContainer":
                        PodAndContainer r5 = getNode(PodAndContainer.class, relationshipMap);
                        r5.setContainer(getNode(Container.class, fromMap));
                        r5.setPod(getNode(Pod.class, toMap));

                        br = r5;
                        from = r5.getContainer();

                        break;
                    case "PodAndMetric":
                        PodAndMetric r6 = getNode(PodAndMetric.class, relationshipMap);
                        r6.setPodMetric(getNode(PodMetric.class, fromMap));
                        r6.setPod(getNode(Pod.class, toMap));

                        br = r6;
                        from = r6.getPodMetric();

                        break;
                    case "ServiceApiAndMetric":
                        ServiceApiAndMetric r7 = getNode(ServiceApiAndMetric.class, relationshipMap);
                        r7.setApiMetric(getNode(ServiceApiMetric.class, fromMap));
                        r7.setServiceAPI(getNode(ServiceAPI.class, toMap));

                        br = r7;
                        from = r7.getApiMetric();

                        break;
                    case "TraceInvokeApiToPod":
                        TraceInvokeApiToPod r8 = getNode(TraceInvokeApiToPod.class, relationshipMap);
                        r8.setServiceAPI(getNode(ServiceAPI.class, fromMap));
                        r8.setPod(getNode(Pod.class, toMap));

                        br = r8;
                        from = r8.getServiceAPI();

                        break;
                    case "TraceInvokePodToApi":
                        TraceInvokePodToApi r9 = getNode(TraceInvokePodToApi.class, relationshipMap);
                        r9.setPod(getNode(Pod.class, fromMap));
                        r9.setServiceAPI(getNode(ServiceAPI.class, toMap));

                        br = r9;
                        from = r9.getPod();

                        break;
                    case "VirtualMachineAndPod":
                        VirtualMachineAndPod r10 = getNode(VirtualMachineAndPod.class, relationshipMap);
                        r10.setPod(getNode(Pod.class, fromMap));
                        r10.setVirtualMachine(getNode(VirtualMachine.class, toMap));

                        br = r10;
                        from = r10.getPod();

                        break;
                }

                fromAdjacentMap =
                        graphAdjacentList.getOrDefault(from, new HashMap<>());
                fromAdjacentSet = fromAdjacentMap.getOrDefault(rawClassName, new HashSet<>());
                fromAdjacentSet.add(br);
                fromAdjacentMap.put(rawClassName, fromAdjacentSet);
                graphAdjacentList.put(from, fromAdjacentMap);

            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return graphAdjacentList;

    }


    public void getTraceMetricComponentList(String cql,
                                            Set<PodMetric> podMetricSet,
                                            Set<ServiceApiMetric> serviceApiMetricSet,
                                            Set<PodAndMetric> podAndMetricSet,
                                            Set<ServiceApiAndMetric> serviceApiAndMetricSet){
        try {
            Session session = driver.session();
            StatementResult result = session.run(cql);
            List<Record> list = result.list();
            for (Record r : list) {
                Map<String, GraphNode> recordGraphNodeMap = new HashMap<>();
                for (String index : r.keys()) {
                    System.out.println(index);
                    Map<String, Object> map = new HashMap<>(r.get(index).asMap());
                    String fullClassName = (String)map.get("className");
                    String rawClassName = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
                    switch (rawClassName){
                        case "Pod":
                            Pod pod = getNode(Pod.class, map);
                            recordGraphNodeMap.put(index, pod);
                            break;
                        case "ServiceAPI":
                            ServiceAPI api = getNode(ServiceAPI.class, map);
                            recordGraphNodeMap.put(index, api);
                            break;
                        case "PodMetric":
                            PodMetric podMetric = getNode(PodMetric.class, map);
                            recordGraphNodeMap.put(index, podMetric);
                            podMetricSet.add(podMetric);
                            break;
                        case "ServiceApiMetric":
                            ServiceApiMetric serviceApiMetric = getNode(ServiceApiMetric.class, map);
                            recordGraphNodeMap.put(index, serviceApiMetric);
                            serviceApiMetricSet.add(serviceApiMetric);
                            break;
                        case "PodAndMetric":
                            PodAndMetric podAndMetric = getNode(PodAndMetric.class, map);
                            podAndMetric.setPod((Pod) recordGraphNodeMap.get("m"));
                            podAndMetric.setPodMetric((PodMetric) recordGraphNodeMap.get("metrics"));
                            podAndMetricSet.add(podAndMetric);
                            break;
                        case "ServiceApiAndMetric":
                            ServiceApiAndMetric serviceApiAndMetric = getNode(ServiceApiAndMetric.class, map);
                            serviceApiAndMetric.setServiceAPI((ServiceAPI) recordGraphNodeMap.get("m"));
                            serviceApiAndMetric.setApiMetric((ServiceApiMetric) recordGraphNodeMap.get("metrics"));
                            serviceApiAndMetricSet.add(serviceApiAndMetric);
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getTraceComponentList(String cql,
                        Set<Pod> podSet,
                        Set<ServiceAPI> serviceAPISet,
                        Set<AppService> appServiceSet,
                        Set<VirtualMachine> virtualMachineSet,
                        Set<TraceInvokeApiToPod> traceInvokeApiToPodSet,
                        Set<TraceInvokePodToApi> traceInvokePodToApiSet,
                        Set<AppServiceAndPod> appServiceAndPodSet,
                        Set<VirtualMachineAndPod> virtualMachineAndPodSet,
                        Set<AppServiceHostServiceAPI> appServiceHostServiceAPISet) {
        try {
            Session session = driver.session();
            StatementResult result = session.run(cql);
            List<Record> list = result.list();
            for (Record r : list) {
                Map<String, GraphNode> recordGraphNodeMap = new HashMap<>();
                for (String index : r.keys()) {
                    Map<String, Object> map = new HashMap<>();
                    //关系上设置的属性
                    map.putAll(r.get(index).asMap());
                    String fullClassName = (String)map.get("className");
                    String rawClassName = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
                    switch (rawClassName){
                        case "Pod":
                            Pod pod = getNode(Pod.class, map);
                            recordGraphNodeMap.put(index, pod);
                            podSet.add(pod);
                            break;
                        case "VirtualMachine":
                            VirtualMachine vm = getNode(VirtualMachine.class, map);
                            recordGraphNodeMap.put(index, vm);
                            virtualMachineSet.add(vm);
                            break;
                        case "AppService":
                            AppService svc = getNode(AppService.class, map);
                            recordGraphNodeMap.put(index, svc);
                            appServiceSet.add(svc);
                            break;
                        case "ServiceAPI":
                            ServiceAPI api = getNode(ServiceAPI.class, map);
                            recordGraphNodeMap.put(index, api);
                            serviceAPISet.add(api);
                            break;
                        case "TraceInvokeApiToPod":
                            TraceInvokeApiToPod traceInvokeApiToPod = getNode(TraceInvokeApiToPod.class, map);
                            traceInvokeApiToPod.setServiceAPI((ServiceAPI) recordGraphNodeMap.get("n"));
                            traceInvokeApiToPod.setPod((Pod)recordGraphNodeMap.get("m"));
                            traceInvokeApiToPodSet.add(traceInvokeApiToPod);
                            break;
                        case "TraceInvokePodToApi":
                            TraceInvokePodToApi traceInvokePodToApi = getNode(TraceInvokePodToApi.class, map);
                            traceInvokePodToApi.setPod((Pod)recordGraphNodeMap.get("n"));
                            traceInvokePodToApi.setServiceAPI((ServiceAPI) recordGraphNodeMap.get("m"));
                            traceInvokePodToApiSet.add(traceInvokePodToApi);
                            break;
                        case "AppServiceAndPod":
                            AppServiceAndPod appServiceAndPod = getNode(AppServiceAndPod.class, map);
                            appServiceAndPod.setPod((Pod) recordGraphNodeMap.get("m"));
                            appServiceAndPod.setAppService((AppService)recordGraphNodeMap.get("s"));
                            appServiceAndPodSet.add(appServiceAndPod);
                            break;
                        case "VirtualMachineAndPod":
                            VirtualMachineAndPod virtualMachineAndPod = getNode(VirtualMachineAndPod.class, map);
                            virtualMachineAndPod.setPod((Pod) recordGraphNodeMap.get("m"));
                            virtualMachineAndPod.setVirtualMachine((VirtualMachine) recordGraphNodeMap.get("s"));
                            virtualMachineAndPodSet.add(virtualMachineAndPod);
                            break;
                        case "AppServiceHostServiceAPI":
                            AppServiceHostServiceAPI appServiceHostServiceAPI = getNode(AppServiceHostServiceAPI.class, map);
                            appServiceHostServiceAPI.setServiceAPI((ServiceAPI) recordGraphNodeMap.get("m"));
                            appServiceHostServiceAPI.setAppService((AppService)recordGraphNodeMap.get("s"));
                            appServiceHostServiceAPISet.add(appServiceHostServiceAPI);
                            break;
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> T getNode(Class<T> c, Map<String, ? extends Object> map){
        JsonElement jsonElement = gson.toJsonTree(map);
        return gson.fromJson(jsonElement, c);
    }
}
