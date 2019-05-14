package collector.service;

import collector.domain.apinode.ApiNode;
import collector.domain.apinode.NodeList;
import collector.domain.apipod.ApiPod;
import collector.domain.apipod.PodList;
import collector.domain.apiservice.ApiAppService;
import collector.domain.apiservice.AppServiceList;
import collector.domain.entities.AppService;
import collector.domain.entities.Pod;
import collector.domain.entities.VirtualMachine;
import collector.domain.relationships.AppServiceAndPod;
import collector.domain.relationships.VirtualMachineAndPod;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class DataCollectorService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Gson gson;

    private final String masterIP = "http://10.141.212.25:8080";

    private final String neo4jDaoIP = "http://localhost:19872";


    public NodeList getNodeList(){
        String list = restTemplate.getForObject( masterIP + "/api/v1/nodes", String.class);
        NodeList nodeList = gson.fromJson(list,NodeList.class);
        return nodeList;
    }

    public PodList getPodList(){
        String list = restTemplate.getForObject(masterIP + "/api/v1/pods", String.class);
        PodList podList = gson.fromJson(list, PodList.class);
        return podList;
    }

    public AppServiceList getAppServiceList(){
        String list = restTemplate.getForObject(masterIP + "/api/v1/services", String.class);
        AppServiceList appServiceList = gson.fromJson(list, AppServiceList.class);
        return appServiceList;
    }

    //构建一个基础的知识图谱 - 包括pod node svc
    public String createRawFrameworkToKnowledgeGraph(){

        //第一步: 获取所有的node,pod,service
        ArrayList<ApiNode> apiNodeList = getNodeList().getItems();
        ArrayList<ApiPod> apiPodList = getPodList().getItems();
        ArrayList<ApiAppService> apiServiceList = getAppServiceList().getItems();
        //第二步: 构建关系
        ArrayList<VirtualMachineAndPod> vmPodRelations = constructVmPodRelation(apiNodeList,apiPodList);
        ArrayList<AppServiceAndPod> appServiceAndPodRelations = constructAppServicePodRelation(apiServiceList, apiPodList);
        //第三步: 上传关系(无需额外上传entity, 关系中包含entity, 对面会自动处理)
        ArrayList<VirtualMachineAndPod> vmPodRelationsResult = new ArrayList<>();
        for(VirtualMachineAndPod vmAndPod : vmPodRelations){
            VirtualMachineAndPod newVmAndPod = postVmAndPod(vmAndPod);
            vmPodRelationsResult.add(newVmAndPod);
        }
        System.out.println("完成上传VirtualMachineAndPod:" + vmPodRelationsResult.size());
        ArrayList<AppServiceAndPod> appServiceAndPodsResult = new ArrayList<>();
        for(AppServiceAndPod svcAndPod : appServiceAndPodRelations){
            AppServiceAndPod newSvcAndPod = postSvcAndPod(svcAndPod);
            appServiceAndPodsResult.add(newSvcAndPod);
        }
        System.out.println("完成上传AppServiceAndPod:" + appServiceAndPodsResult.size());

        return "";
    }

    private VirtualMachineAndPod postVmAndPod(VirtualMachineAndPod vmAndPod){
        VirtualMachineAndPod newObj =
                restTemplate.postForObject(neo4jDaoIP + "/virtualMachineAndPod",vmAndPod,VirtualMachineAndPod.class);
        return newObj;
    }

    private AppServiceAndPod postSvcAndPod(AppServiceAndPod svcAndPod){
        AppServiceAndPod newObj =
                restTemplate.postForObject(neo4jDaoIP + "/appServiceAndPod", svcAndPod, AppServiceAndPod.class);
        return newObj;
    }

    //使用抽取到的apiNode和apiPod
    private ArrayList<VirtualMachineAndPod> constructVmPodRelation(ArrayList<ApiNode> apiNodes,
                                                                  ArrayList<ApiPod> apiPods){
        ArrayList<VirtualMachineAndPod> relations = new ArrayList<>();
        for(ApiNode apiNode : apiNodes){
            //当前这个node叫什么名字
            String nodeName = apiNode.getMetadata().getName();
            VirtualMachine vm = convertApiNodeToVm(apiNode);
            for(ApiPod apiPod : apiPods){
                //找到pod的nodename和之前node的名字一样的那些Pod
                if(apiPod.getSpec().getNodeName().equals(nodeName)){
                    Pod pod = convertApiPodToPod(apiPod);
                    VirtualMachineAndPod relation = new VirtualMachineAndPod();
                    relation.setPod(pod);
                    relation.setVirtualMachine(vm);
                    relation.setRelation("Deploy-On");
                    relation.setId(pod.getId() + "PodVm" + vm.getId());
                    relations.add(relation);
                }

            }
        }
        return relations;
    }

    //使用抽取到的apiPod和apiService
    private ArrayList<AppServiceAndPod> constructAppServicePodRelation(ArrayList<ApiAppService> apiAppServices,
                                                                      ArrayList<ApiPod> apiPods){
        ArrayList<AppServiceAndPod> relations = new ArrayList<>();
        for(ApiAppService apiAppService : apiAppServices){

            HashMap<String,String> svcSelector = apiAppService.getSpec().getSelector();
            if(svcSelector == null){
                continue;
            }

            AppService appService = convertApiAppServiceToAppService(apiAppService);
            for(ApiPod apiPod : apiPods) {
                HashMap<String,String> podLabel = apiPod.getMetadata().getLabels();
                boolean podIsSvc = false;
                for(Map.Entry<String,String> entry: podLabel.entrySet()){
                    if(svcSelector.containsKey(entry.getKey()) && svcSelector.get(entry.getKey()).equals(entry.getValue())){
                        podIsSvc = true;
                        break;
                    }
                }
                if(podIsSvc){
                    Pod pod = convertApiPodToPod(apiPod);
                    AppServiceAndPod relation = new AppServiceAndPod();
                    relation.setAppService(appService);
                    relation.setPod(pod);
                    relation.setRelation("BELONGS-TO");
                    relation.setId(pod.getId() + "PodSvc" + appService.getId());
                    relations.add(relation);
                }
            }
        }
        return relations;
    }




    //转换ApiNode到Virtual Machine
    private VirtualMachine convertApiNodeToVm(ApiNode node){

        VirtualMachine vm = new VirtualMachine();//id就用名字
        vm.setId(node.getMetadata().getName());
        vm.setAddress(node.getStatus().getAddresses().get(0).getAddress());
        vm.setArchitecture(node.getStatus().getNodeInfo().getArchitecture());
        vm.setContainerRuntimeVersion(node.getStatus().getNodeInfo().getContainerRuntimeVersion());
        vm.setCpu(node.getStatus().getCapacity().getCpu());
        vm.setKernelVersion(node.getStatus().getNodeInfo().getKernelVersion());
        vm.setMemory(node.getStatus().getCapacity().getMemory());
        vm.setOperatingSystem(node.getStatus().getNodeInfo().getOperatingSystem());
        vm.setOsImage(node.getStatus().getNodeInfo().getOsImage());
        vm.setSelflink(node.getMetadata().getSelfLink());
        vm.setName(node.getMetadata().getName());

        return vm;
    }

    //转换ApiPod到Pod
    private Pod convertApiPodToPod(ApiPod apiPod){

        Pod pod = new Pod();
        pod.setId(apiPod.getMetadata().getName());//id就用名字
        pod.setDnsPolicy(apiPod.getSpec().getDnsPolicy());
        pod.setNamespace(apiPod.getMetadata().getNamespace());
        pod.setPhase(apiPod.getStatus().getPhase());
        pod.setPodIP(apiPod.getStatus().getPodIP());
        pod.setQosClass(apiPod.getStatus().getQosClass());
        pod.setName(apiPod.getMetadata().getName());
        pod.setRestartPolicy(apiPod.getSpec().getRestartPolicy());
        pod.setSelflink(apiPod.getMetadata().getSelfLink());
        pod.setTerminationGracePeriodSeconds(apiPod.getSpec().getTerminationGracePeriodSeconds());

        return pod;
    }

    //转换ApiService到Service
    private AppService convertApiAppServiceToAppService(ApiAppService apiAppService){

        AppService appService = new AppService();
        appService.setId(apiAppService.getMetadata().getName());//id就用名字
        appService.setClusterIP(apiAppService.getSpec().getClusterIP());
        appService.setNamespace(apiAppService.getMetadata().getNamespace());
        appService.setName(apiAppService.getMetadata().getName());
        appService.setPort(apiAppService.getSpec().getPorts().get(0).getPort());
        appService.setSelflink(apiAppService.getMetadata().getSelfLink());
        appService.setType(apiAppService.getSpec().getType());

        return appService;
    }


}
