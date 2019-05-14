package neo4jserver.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import neo4jserver.domain.entities.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class MapToObjUtil {

    @Autowired
    private static Gson gson;

    public static final GraphNode toGraphNodeBean(String className, Map<String, ? extends Object> map){
        int index = className.lastIndexOf(".");
        String rawClassName = className.substring(index+1);
        switch (rawClassName){
            case "Pod":
                return getPodByMap(map);
            case "VirtualMachine":
                return getVirtualMachineByMap(map);
            case "GraphNode":
                return getGraphNode(map);
            case "AppSerivice":
                return getAppServiceByMap(map);
            case "Container":
                return getContainer(map);
            default:
                return null;
        }
    }

    public static Container getContainer(Map<String, ? extends Object> map) {
        Container container = new Container();
        try{
            JsonElement jsonElement = gson.toJsonTree(map);
            container = gson.fromJson(jsonElement, Container.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return container;
    }

    public static AppService getAppServiceByMap(Map<String, ? extends Object> map){
        AppService appService = new AppService();
        try{
            JsonElement jsonElement = gson.toJsonTree(map);
            appService = gson.fromJson(jsonElement, AppService.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return appService;
    }

    public static Pod getPodByMap(Map<String, ? extends Object> map){
        Pod pod = new Pod();
        try{
            JsonElement jsonElement = gson.toJsonTree(map);
            pod = gson.fromJson(jsonElement, Pod.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return pod;
    }

    public static VirtualMachine getVirtualMachineByMap(Map<String, ? extends Object> map){
        VirtualMachine virtualMachine = new VirtualMachine();
        try{
            JsonElement jsonElement = gson.toJsonTree(map);
            virtualMachine = gson.fromJson(jsonElement, VirtualMachine.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return virtualMachine;
    }

    public static GraphNode getGraphNode(Map<String, ? extends Object> map){
        GraphNode graphNode = new GraphNode();
        try{
            JsonElement jsonElement = gson.toJsonTree(map);
            graphNode = gson.fromJson(jsonElement, GraphNode.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return graphNode;
    }


}
