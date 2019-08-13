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


    public void getList(String cql,
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
                for (String index : r.keys()) {
                    Map<String, Object> map = new HashMap<>();
                    //关系上设置的属性
                    map.putAll(r.get(index).asMap());
                    String fullClassName = (String)map.get("className");
                    String rawClassName = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
                    switch (rawClassName){
                        case "Pod":
                            podSet.add(getNode(Pod.class, map));
                            break;
                        case "VirtualMachine":
                            virtualMachineSet.add(getNode(VirtualMachine.class, map));
                            break;
                        case "AppService":
                            appServiceSet.add(getNode(AppService.class, map));
                            break;
                        case "ServiceAPI":
                            serviceAPISet.add(getNode(ServiceAPI.class, map));
                            break;
                        case "TraceInvokeApiToPod":
                            traceInvokeApiToPodSet.add(getNode(TraceInvokeApiToPod.class, map));
                            break;
                        case "TraceInvokePodToApi":
                            traceInvokePodToApiSet.add(getNode(TraceInvokePodToApi.class, map));
                            break;
                        case "AppServiceAndPod":
                            appServiceAndPodSet.add(getNode(AppServiceAndPod.class, map));
                            break;
                        case "VirtualMachineAndPod":
                            virtualMachineAndPodSet.add(getNode(VirtualMachineAndPod.class, map));
                            break;
                        case "AppServiceHostServiceAPI":
                            appServiceHostServiceAPISet.add(getNode(AppServiceHostServiceAPI.class, map));
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
