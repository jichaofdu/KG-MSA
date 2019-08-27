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


    public HashMap<GraphNode, HashSet<BasicRelationship>> getWholeGraphByAdjacentList(){
        HashMap<GraphNode, HashSet<BasicRelationship>> graphAdjacentList = new HashMap<>();

        String cql =
                "MATCH (from)-[relationship]->(to) " +
                "RETURN relationship, from, to";
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
                switch (rawClassName){
                    case "AppServiceAndPod":
                        AppServiceAndPod r1 = getNode(AppServiceAndPod.class, relationshipMap);
                        r1.setPod(getNode(Pod.class, fromMap));
                        r1.setAppService(getNode(AppService.class, toMap));

                        GraphNode r1From = r1.getPod();
                        HashSet<BasicRelationship> r1Set = graphAdjacentList.getOrDefault(r1From, new HashSet<>());
                        r1Set.add(r1);
                        graphAdjacentList.put(r1From, r1Set);

                        break;
                    case "AppServiceHostServiceAPI":
                        AppServiceHostServiceAPI r2 = getNode(AppServiceHostServiceAPI.class, relationshipMap);
                        r2.setServiceAPI(getNode(ServiceAPI.class, fromMap));
                        r2.setAppService(getNode(AppService.class, toMap));

                        GraphNode r2From = r2.getServiceAPI();
                        HashSet<BasicRelationship> r2Set = graphAdjacentList.getOrDefault(r2From, new HashSet<>());
                        r2Set.add(r2);
                        graphAdjacentList.put(r2From, r2Set);

                        break;
                    case "AppServiceInvokeServiceAPI":
                        AppServiceInvokeServiceAPI r3 = getNode(AppServiceInvokeServiceAPI.class, relationshipMap);
                        r3.setAppService(getNode(AppService.class, fromMap));
                        r3.setServiceAPI(getNode(ServiceAPI.class, toMap));

                        GraphNode r3From = r3.getAppService();
                        HashSet<BasicRelationship> r3Set = graphAdjacentList.getOrDefault(r3From, new HashSet<>());
                        r3Set.add(r3);
                        graphAdjacentList.put(r3From, r3Set);

                        break;
                    case "MetricAndContainer":
                        MetricAndContainer r4 = getNode(MetricAndContainer.class, relationshipMap);
                        r4.setMetric(getNode(Metric.class, fromMap));
                        r4.setContainer(getNode(Container.class, toMap));

                        GraphNode r4From = r4.getMetric();
                        HashSet<BasicRelationship> r4Set = graphAdjacentList.getOrDefault(r4From, new HashSet<>());
                        r4Set.add(r4);
                        graphAdjacentList.put(r4From, r4Set);

                        break;
                    case "PodAndContainer":
                        PodAndContainer r5 = getNode(PodAndContainer.class, relationshipMap);
                        r5.setContainer(getNode(Container.class, fromMap));
                        r5.setPod(getNode(Pod.class, toMap));

                        GraphNode r5From = r5.getContainer();
                        HashSet<BasicRelationship> r5Set = graphAdjacentList.getOrDefault(r5From, new HashSet<>());
                        r5Set.add(r5);
                        graphAdjacentList.put(r5From, r5Set);

                        break;
                    case "PodAndMetric":
                        PodAndMetric r6 = getNode(PodAndMetric.class, relationshipMap);
                        r6.setPodMetric(getNode(PodMetric.class, fromMap));
                        r6.setPod(getNode(Pod.class, toMap));

                        GraphNode r6From = r6.getPodMetric();
                        HashSet<BasicRelationship> r6Set = graphAdjacentList.getOrDefault(r6From, new HashSet<>());
                        r6Set.add(r6);
                        graphAdjacentList.put(r6From, r6Set);

                        break;
                    case "ServiceApiAndMetric":
                        ServiceApiAndMetric r7 = getNode(ServiceApiAndMetric.class, relationshipMap);
                        r7.setApiMetric(getNode(ServiceApiMetric.class, fromMap));
                        r7.setServiceAPI(getNode(ServiceAPI.class, toMap));

                        GraphNode r7From = r7.getApiMetric();
                        HashSet<BasicRelationship> r7Set = graphAdjacentList.getOrDefault(r7From, new HashSet<>());
                        r7Set.add(r7);
                        graphAdjacentList.put(r7From, r7Set);

                        break;
                    case "TraceInvokeApiToPod":
                        TraceInvokeApiToPod r8 = getNode(TraceInvokeApiToPod.class, relationshipMap);
                        r8.setServiceAPI(getNode(ServiceAPI.class, fromMap));
                        r8.setPod(getNode(Pod.class, toMap));

                        GraphNode r8From = r8.getServiceAPI();
                        HashSet<BasicRelationship> r8Set = graphAdjacentList.getOrDefault(r8From, new HashSet<>());
                        r8Set.add(r8);
                        graphAdjacentList.put(r8From, r8Set);

                        break;
                    case "TraceInvokePodToApi":
                        TraceInvokePodToApi r9 = getNode(TraceInvokePodToApi.class, relationshipMap);
                        r9.setPod(getNode(Pod.class, fromMap));
                        r9.setServiceAPI(getNode(ServiceAPI.class, toMap));

                        GraphNode r9From = r9.getPod();
                        HashSet<BasicRelationship> r9Set = graphAdjacentList.getOrDefault(r9From, new HashSet<>());
                        r9Set.add(r9);
                        graphAdjacentList.put(r9From, r9Set);

                        break;
                    case "VirtualMachineAndPod":
                        VirtualMachineAndPod r10 = getNode(VirtualMachineAndPod.class, relationshipMap);
                        r10.setPod(getNode(Pod.class, fromMap));
                        r10.setVirtualMachine(getNode(VirtualMachine.class, toMap));

                        GraphNode r10From = r10.getPod();
                        HashSet<BasicRelationship> r10Set = graphAdjacentList.getOrDefault(r10From, new HashSet<>());
                        r10Set.add(r10);
                        graphAdjacentList.put(r10From, r10Set);

                        break;
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return graphAdjacentList;

    }


    public void getTraceMetricComponentList(String cql,
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
                            break;
                        case "ServiceApiMetric":
                            ServiceApiMetric serviceApiMetric = getNode(ServiceApiMetric.class, map);
                            recordGraphNodeMap.put(index, serviceApiMetric);
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
                        case "Metric":

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
