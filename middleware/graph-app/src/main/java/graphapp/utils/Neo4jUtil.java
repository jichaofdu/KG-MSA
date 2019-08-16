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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
