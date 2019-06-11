package collector.service;

import collector.domain.apicontainer.ApiContainer;
import collector.domain.apicontainer.ContainerList;
import collector.domain.apinode.ApiNode;
import collector.domain.apinode.NodeList;
import collector.domain.apipod.ApiPod;
import collector.domain.apipod.PodList;
import collector.domain.apiservice.ApiAppService;
import collector.domain.apiservice.AppServiceList;
import collector.domain.entities.*;
import collector.domain.prom.ExpressionQueriesVectorResponse;
import collector.domain.relationships.*;
import collector.domain.trace.BinaryAnnotation;
import collector.domain.trace.Span;
import collector.util.MatcherUrlRouterUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DataCollectorService {

    //集群master机器的地址
    private static final String masterIP =
            "http://10.141.211.162:8082/";

    //neo4j的api服务器的地址
    private static final String neo4jDaoIP =
            "http://localhost:19872";

    //promethsus的查询地址
    private static final String promethsusQuery =
            "http://10.141.211.162:31999/api/v1/query";

    //zipkin的查询地址
    private static final String zipkinQuery =
            "http://10.141.211.162:31879/zipkin/api/v1/traces?limit=100";

    //集群全部机器的ip地址
    private static final String[] clusterIPs = {
            "http://10.141.212.24",
            "http://10.141.212.25",
            "http://10.141.211.162",
            "http://10.141.212.133",
            "http://10.141.212.136",
    };

    //需要查询的容器的metric指标名称
    private static final String[] containerMetricsNameVector = {
            "container_memory_usage_bytes",
            "container_fs_usage_bytes",
    };

    //时间戳 在图谱更新的时候会附加在节点上
    //在图谱重整的时候此值将会被刷新
    //在更新Metric的时候这个值不会刷新
    private static String currTimestampString = "Not Set Yet";

    //当前的实体列表
    private static HashMap<String, VirtualMachine> vms = new HashMap<>();
    private static HashMap<String, AppService> svcs = new HashMap<>();
    private static HashMap<String, Pod> pods = new HashMap<>();
    private static HashMap<String, Container> containers = new HashMap<>();
    private static HashMap<String, ServiceAPI> apis = new HashMap<>();

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private Gson gson;


    @Scheduled(initialDelay=5000, fixedDelay =100000)
    public void updateFrameworkPeriodly() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        System.out.println("定期刷新应用骨架 现在时间：" + dateFormat.format(new Date()));
        createRawFrameworkToKnowledgeGraph();
    }

    @Scheduled(initialDelay=100000, fixedDelay =50000)
    public void updateTracePeriodly() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        System.out.println("定期刷新调用关系 现在时间：" + dateFormat.format(new Date()));
        uploadApiSvcRelations();
    }


//    @Scheduled(initialDelay=100000, fixedDelay =15000)
//    public void updateMetricsPeriodly() {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
//        System.out.println("定期刷新应用指标数据 现在时间：" + dateFormat.format(new Date()));
//        updateMetrics();
//    }

    public String getCurrentTimestamp(){
        return currTimestampString;
    }

    //读取和记录zipkin的trace
    public ArrayList<ArrayList<Span>> getAndParseTrace(){
        String list = restTemplate.getForObject(zipkinQuery, String.class);
        Type founderListType = new TypeToken<ArrayList<ArrayList<Span>>>(){}.getType();
        return gson.fromJson(list, founderListType);
    }


    public ArrayList<AppServiceHostServiceAPI> uploadApiSvcRelations(){
        ArrayList<AppServiceHostServiceAPI> svcApiRelations = new ArrayList<>();
        ArrayList<AppServiceInvokeServiceAPI> svcInvokeApiRelations = new ArrayList<>();
        getServiceHostApiAndServiceInvokeApi(svcApiRelations, svcInvokeApiRelations);
        //向对面提交一堆并处理结果
        ArrayList<AppServiceHostServiceAPI> updatedSvcApiRelations = restTemplate.postForObject(
                neo4jDaoIP + "/apiHostService", svcApiRelations, svcApiRelations.getClass());
        ArrayList<AppServiceInvokeServiceAPI> updatedSvcInvokeApiRelations = restTemplate.postForObject(
                neo4jDaoIP + "/apiInvokeService", svcInvokeApiRelations, svcInvokeApiRelations.getClass());
        System.out.println("API数量:" + apis.size());
        return updatedSvcApiRelations;

    }

    public void getServiceHostApiAndServiceInvokeApi(ArrayList<AppServiceHostServiceAPI> svcHostApi,
                                                     ArrayList<AppServiceInvokeServiceAPI> svcInvokeApi){
        //获取trace
        ArrayList<ArrayList<Span>> traces = getAndParseTrace();
        //遍历每一个trace
        for(ArrayList<Span> trace : traces){
            //遍历一个trace的每一个span
            for(Span span : trace){
                //istio的输出信息不是我们需要的 忽略
                if(span.getName().contains("istio-policy")){
                    continue;
                }
                // Client-Send或者Client-Receive才是我们需要的
                if(!span.getAnnotations().get(0).getValue().equals("cr") &&
                        !span.getAnnotations().get(0).getValue().equals("cs")){
                    continue;
                }
                //开始处理我们需要的内容
                //找到key=http.url的那个binary-annotation
                for(BinaryAnnotation bn : span.getBinaryAnnotations()){
                    //不是http.url就跳过吧
                    if(!"http.url".equals(bn.getKey())){
                        continue;
                    }
                    String invokeSource = bn.getEndpoint().getServiceName();
                    String totalInvokeAddress = bn.getValue();
                    //解析出API所在的服务名,调用API的服务名以及API本身的名称
                    String invokeService = getSvcNameFromTotalName(invokeSource);
                    String hostService = getHostFromLink(totalInvokeAddress);
                    String api = getApiFromLink(totalInvokeAddress);
                    //自己调自己的不要 开头是ip的不要
                    if(/**invokeService.equals(hostService) ||**/ hostService.contains("10.")){
                        continue;
                    }
                    //看下API在吗，不在的话重组一个
                    System.out.println("发现API " + api);
                    ServiceAPI serviceApi;
                    if(apis.get(api) != null){
                        serviceApi = apis.get(api);
                    }else{
                        serviceApi = new ServiceAPI();
                        serviceApi.setHostName(hostService);
                        serviceApi.setName(api);
                        //API的ID就是API的名字
                        serviceApi.setId(api);
                        serviceApi.setLatestUpdateTimestamp(currTimestampString);
                        serviceApi.setCreationTimestamp("" + new Date().getTime() / 1000);
                        apis.put(serviceApi.getName(), serviceApi);
                    }

                    //看看host serivice在吗 不在的话就不管了 在的话组装一下relation
                    System.out.println("发现API的所属关系 " + hostService);
                    if(svcs.get(hostService)!= null){
                       AppService hostSvc = svcs.get(hostService);
                       AppServiceHostServiceAPI relationHost = new AppServiceHostServiceAPI();
                       relationHost.setAppService(hostSvc);
                       relationHost.setServiceAPI(serviceApi);
                       relationHost.setId(serviceApi.getId() + "ApiSvc" + hostSvc.getId());
                       relationHost.setRelation("API_HOST_ON");
                       svcHostApi.add(relationHost);
                    }

                    //看看invoke service在吗 不在的话就不管了 在的话组装一下relation
                    System.out.println("发现API的被调关系 " + invokeService);
                    if(svcs.get(invokeService)!= null){
                        AppService invokeSvc = svcs.get(invokeService);
                        AppServiceInvokeServiceAPI relationInvoke = new AppServiceInvokeServiceAPI();
                        relationInvoke.setAppService(invokeSvc);
                        relationInvoke.setServiceAPI(serviceApi);
                        relationInvoke.setCount(1);
                        relationInvoke.setId(serviceApi.getId() + "ApiSvc" + invokeSvc.getId());
                        relationInvoke.setRelation("API_INVOKE_BY");
                        svcInvokeApi.add(relationInvoke);
                    }
                }
            }
        }
    }

    private String getApiFromLink(String url){
        return MatcherUrlRouterUtil.matcherPattern(url);
    }

    private String getHostFromLink(String url){
        String api = MatcherUrlRouterUtil.matcherPattern(url);
        int index1 = url.indexOf("http://");
        int index2 = url.indexOf(api);
        String svc = url.substring(index1, index2).substring("http://".length());
        int index3 = svc.indexOf(":");
        svc = svc.substring(0, index3);
        return svc;
    }

    private String getSvcNameFromTotalName(String s){
        int index = s.indexOf(".");
        return s.substring(0, index);
    }



    //更新所有metrics
    //更新依据是所有记录在案的container
    //如果要依据最新的container需要先更新containers
    public ArrayList<Metric> updateMetrics(){
        ArrayList<Metric> newMetrics = new ArrayList<>();
        for(String containerMetricName : containerMetricsNameVector){
            for(Container container : containers.values()){
                String containerName = container.getName();
                if(containerName.startsWith("/")){
                    containerName = containerName.substring(1);
                }
                try{
                    ExpressionQueriesVectorResponse res = getMetric(containerMetricName, containerName);
                    Metric metric = getMetricFromExpressionQueriesVectorResponse(res,
                            containerMetricName, containerName);
                    metric.setLatestUpdateTimestamp(currTimestampString);
                    newMetrics.add(metric);

                }catch (Exception e){
                    System.out.println("Metric未查到 容器名称:" + containerName + " Metric名称:" +containerMetricName);
                }
            }
        }
        ArrayList<Metric> updatedMetrics = restTemplate.postForObject(
                neo4jDaoIP + "/updateMetrics", newMetrics, newMetrics.getClass());
        return updatedMetrics;
    }

    //container_memory_usage_bytes{name="k8s_ts-order-service_ts-order-service-68d9c9b878-vgzhl_default_ff88298e-777c-11e9-bb23-005056a4ea84_26"}
    private ExpressionQueriesVectorResponse getMetric(String metricName, String containerName){

        String queryStr = metricName + "{" + "name=" + "\"" + containerName + "\"" + "}";

        MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<>();
        postParameters.add("query", queryStr);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity<MultiValueMap<String, Object>> r = new HttpEntity<>(postParameters, headers);

        String str = restTemplate.postForObject(promethsusQuery,r,String.class);

        ExpressionQueriesVectorResponse res = gson.fromJson(str,ExpressionQueriesVectorResponse.class);

        return res;
    }

    public ContainerList getContainerList(){
        ArrayList<ApiContainer> containers = new ArrayList<>();
        for(String clusterIP : clusterIPs) {
            String list = restTemplate.getForObject(clusterIP + ":5678/containers/json", String.class);

            //Json的解析类对象
            JsonParser parser = new JsonParser();
            //将JSON的String 转成一个JsonArray对象
            JsonArray jsonArray = parser.parse(list).getAsJsonArray();

            Gson gson = new Gson();
            ArrayList<ApiContainer> tempList = new ArrayList<>();

            //加强for循环遍历JsonArray
            for (JsonElement user : jsonArray) {
                //使用GSON，直接转成Bean对象
                ApiContainer apiContainer = gson.fromJson(user, ApiContainer.class);
                tempList.add(apiContainer);
            }

            containers.addAll(tempList);
        }
        ContainerList containerList = new ContainerList();
        containerList.setItems(containers);
        return containerList;
    }

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

    private void clearAllInfo(){
        vms.clear();
        svcs.clear();
        pods.clear();
        containers.clear();
    }

    //构建一个基础的知识图谱 - 包括pod node svc
    public String createRawFrameworkToKnowledgeGraph(){
        //记录当前时间
        currTimestampString = "" + new Date().getTime() / 1000;
        System.out.println("CurrTimestampString" + currTimestampString);
        //清空环境
        clearAllInfo();
        System.out.println("Clear");
        //第一步: 获取所有的node,pod,service
        ArrayList<ApiNode> apiNodeList = getNodeList().getItems();
        System.out.println("Get Nodes");
        ArrayList<ApiPod> apiPodList = getPodList().getItems();
        System.out.println("Get POD");
        ArrayList<ApiAppService> apiServiceList = getAppServiceList().getItems();
        System.out.println("Get Service");
        ArrayList<ApiContainer> apiContainerList = getContainerList().getItems();
        System.out.println("Get Container");
        //第二步: 构建关系
        ArrayList<VirtualMachineAndPod> vmPodRelations = constructVmPodRelation(apiNodeList,apiPodList);
        System.out.println("vmPodRelations");
        ArrayList<AppServiceAndPod> appServiceAndPodRelations = constructAppServicePodRelation(apiServiceList, apiPodList);
        System.out.println("appServiceAndPodRelations");
        ArrayList<PodAndContainer> podAndContainerRelations = constructPodAndContainerRelation(apiPodList,apiContainerList);
        System.out.println("podAndContainerRelations");
//        ArrayList<MetricAndContainer> metricAndContainerRelations = constructMetricAndContainer(apiContainerList);
//        System.out.println("metricAndContainerRelations");
        //第三步: 上传关系(无需额外上传entity, 关系中包含entity, 对面会自动处理)
        ArrayList<VirtualMachineAndPod> vmPodRelationsResult = new ArrayList<>();
        vmPodRelationsResult = postVmAndPodList(vmPodRelations);
//        for(VirtualMachineAndPod vmAndPod : vmPodRelations){
//            //添加更新时间戳以区分新旧时间
//            vmAndPod.getVirtualMachine().setLatestUpdateTimestamp(currTimestampString);
//            vmAndPod.getPod().setLatestUpdateTimestamp(currTimestampString);
//            //上传
//            VirtualMachineAndPod newVmAndPod = postVmAndPod(vmAndPod);
//            vmPodRelationsResult.add(newVmAndPod);
//        }
        System.out.println("完成上传VirtualMachineAndPod:" + vmPodRelationsResult.size());
        ArrayList<AppServiceAndPod> appServiceAndPodsResult = new ArrayList<>();
        appServiceAndPodsResult = postSvcAndPodList(appServiceAndPodRelations);
//        for(AppServiceAndPod svcAndPod : appServiceAndPodRelations){
//            //添加更新时间戳以区分新旧时间
//            svcAndPod.getPod().setLatestUpdateTimestamp(currTimestampString);
//            svcAndPod.getAppService().setLatestUpdateTimestamp(currTimestampString);
//            //上传
//            AppServiceAndPod newSvcAndPod = postSvcAndPod(svcAndPod);
//            appServiceAndPodsResult.add(newSvcAndPod);
//        }
        System.out.println("完成上传AppServiceAndPod:" + appServiceAndPodsResult.size());
        ArrayList<PodAndContainer> podAndContainerResult = new ArrayList<>();
        podAndContainerResult = postPodAndContainerList(podAndContainerRelations);
//        for(PodAndContainer podAndContainer : podAndContainerRelations){
//            //添加更新时间戳以区分新旧时间
//            podAndContainer.getPod().setLatestUpdateTimestamp(currTimestampString);
//            podAndContainer.getContainer().setLatestUpdateTimestamp(currTimestampString);
//            //上传
//            PodAndContainer newPodAndContainer = postPodAndContainer(podAndContainer);
//            podAndContainerResult.add(newPodAndContainer);
//        }
        System.out.println("完成上传PodAndContainer:" + podAndContainerResult.size());
//        ArrayList<MetricAndContainer> metricAndContainerResult = new ArrayList<>();
//        metricAndContainerResult = postMetricAndContainerList(metricAndContainerRelations);
//        for(MetricAndContainer metricAndContainer : metricAndContainerRelations){
//            //添加更新时间戳以区分新旧时间
//            metricAndContainer.getContainer().setLatestUpdateTimestamp(currTimestampString);
//            metricAndContainer.getMetric().setLatestUpdateTimestamp(currTimestampString);
//            //上传
//            MetricAndContainer newMetricAndContainer = postMetricAndContainer(metricAndContainer);
//            metricAndContainerResult.add(newMetricAndContainer);
//        }
//        System.out.println("完成上传MetricAndContainer:" + metricAndContainerResult.size());
        System.out.println("虚拟机数量:" + vms.size());
        System.out.print("服务数量:" + svcs.size());
        System.out.println("Pod数量:" + pods.size());
        System.out.println("容器数量:" + containers.size());
        return "";
    }

    private VirtualMachineAndPod postVmAndPod(VirtualMachineAndPod vmAndPod){
        VirtualMachineAndPod newObj =
                restTemplate.postForObject(neo4jDaoIP + "/virtualMachineAndPod",vmAndPod,VirtualMachineAndPod.class);
        vms.put(newObj.getVirtualMachine().getName(), newObj.getVirtualMachine());
        pods.put(newObj.getPod().getName(), newObj.getPod());
        return newObj;
    }

    private ArrayList<VirtualMachineAndPod> postVmAndPodList(ArrayList<VirtualMachineAndPod> relations){
//        ArrayList<VirtualMachineAndPod> result =
//            restTemplate.postForObject(neo4jDaoIP + "/virtualMachineAndPodRelations",relations,relations.getClass());
        String str = restTemplate.postForObject(neo4jDaoIP + "/virtualMachineAndPodRelations",relations,String.class);
        Type founderListType = new TypeToken<ArrayList<VirtualMachineAndPod>>(){}.getType();
        ArrayList<VirtualMachineAndPod> result = gson.fromJson(str, founderListType);
        System.out.println("postVmAndPodList传输完毕");
        for(VirtualMachineAndPod relation : result){
            vms.put(relation.getVirtualMachine().getName(), relation.getVirtualMachine());
            pods.put(relation.getPod().getName(), relation.getPod());
        }
        return result;
    }

    private AppServiceAndPod postSvcAndPod(AppServiceAndPod svcAndPod){
        AppServiceAndPod newObj =
                restTemplate.postForObject(neo4jDaoIP + "/appServiceAndPod", svcAndPod, AppServiceAndPod.class);
        svcs.put(newObj.getAppService().getName(), newObj.getAppService());
        pods.put(newObj.getPod().getName(), newObj.getPod());
        return newObj;
    }

    private ArrayList<AppServiceAndPod> postSvcAndPodList(ArrayList<AppServiceAndPod> relations){
//        ArrayList<AppServiceAndPod> result =
//                restTemplate.postForObject(neo4jDaoIP + "/appServiceAndPodRelations",relations,relations.getClass());
        String str = restTemplate.postForObject(neo4jDaoIP + "/appServiceAndPodRelations",relations,String.class);
        Type founderListType = new TypeToken<ArrayList<AppServiceAndPod>>(){}.getType();
        ArrayList<AppServiceAndPod> result = gson.fromJson(str, founderListType);
        System.out.println("postSvcAndPodList传输完毕");
        for(AppServiceAndPod relation : result){
            svcs.put(relation.getAppService().getName(), relation.getAppService());
            pods.put(relation.getPod().getName(), relation.getPod());
        }
        return result;
    }

    private PodAndContainer postPodAndContainer(PodAndContainer podAndContainer){
        PodAndContainer newObj =
                restTemplate.postForObject(neo4jDaoIP + "/podAndContainer", podAndContainer, PodAndContainer.class);
        pods.put(newObj.getPod().getName(), newObj.getPod());
        containers.put(newObj.getContainer().getName(), newObj.getContainer());
        return newObj;
    }


    private ArrayList<PodAndContainer> postPodAndContainerList(ArrayList<PodAndContainer> relations){
//        ArrayList<PodAndContainer> result =
//                restTemplate.postForObject(neo4jDaoIP + "/podAndContainerRelations",relations,relations.getClass());
        String str = restTemplate.postForObject(neo4jDaoIP + "/podAndContainerRelations",relations,String.class);
        Type founderListType = new TypeToken<ArrayList<PodAndContainer>>(){}.getType();
        ArrayList<PodAndContainer> result = gson.fromJson(str, founderListType);

        System.out.println("postPodAndContainerList传输完毕");
        for(PodAndContainer relation : result){
            pods.put(relation.getPod().getName(), relation.getPod());
            containers.put(relation.getContainer().getName(), relation.getContainer());
        }
        return result;
    }

    private MetricAndContainer postMetricAndContainer(MetricAndContainer metricAndContainer){
        MetricAndContainer newObj =
                restTemplate.postForObject(neo4jDaoIP + "/metricAndContainer", metricAndContainer, MetricAndContainer.class);
        return newObj;
    }

    private ArrayList<MetricAndContainer> postMetricAndContainerList(ArrayList<MetricAndContainer> relations){
//        ArrayList<MetricAndContainer> result =
//                restTemplate.postForObject(neo4jDaoIP + "/metricAndContainerRelations",relations,relations.getClass());
        String str = restTemplate.postForObject(neo4jDaoIP + "/metricAndContainerRelations",relations,String.class);
        Type founderListType = new TypeToken<ArrayList<MetricAndContainer>>(){}.getType();
        ArrayList<MetricAndContainer> result = gson.fromJson(str, founderListType);

        System.out.println("postMetricAndContainerList传输完毕");
        return result;
    }

    //使用抽取到的apiContainer并用apicontainer抽取
    public ArrayList<MetricAndContainer> constructMetricAndContainer(ArrayList<ApiContainer> apiContainers){
        ArrayList<MetricAndContainer> relations = new ArrayList<>();
        for(ApiContainer apiContainer : apiContainers){
            Container container = converApiContainerToContainer(apiContainer);
            String containerName = container.getName();
            if(containerName.startsWith("/")){
                containerName = containerName.substring(1);
            }
            //根据containernNamec抽取各种Metric
            MetricAndContainer relation;
                    //抽取container_memory_usage_bytes
            relation = asemblyMetricAndContainer("container_memory_usage_bytes",
                    containerName, container);
            relations.add(relation);
            //抽取container_fs_usage_bytes
            relation = asemblyMetricAndContainer("container_fs_usage_bytes",
                    containerName, container);
            relations.add(relation);
        }
        return relations;
    }

    //Assembly Metrics
    private MetricAndContainer asemblyMetricAndContainer(String metricName, String containerName, Container container){
        ExpressionQueriesVectorResponse res = getMetric(metricName, containerName);
        Metric metric = getMetricFromExpressionQueriesVectorResponse(res, metricName, containerName);
        MetricAndContainer relation = new MetricAndContainer();
        relation.setContainer(container);
        relation.setMetric(metric);
        relation.setId(metric.getId() + "MetricAndContainer" + container.getId());
        relation.setRelation("RUNTIME_INFO");
        relation.getContainer().setLatestUpdateTimestamp(currTimestampString);
        relation.getMetric().setLatestUpdateTimestamp(currTimestampString);

        return relation;
    }

    private Metric getMetricFromExpressionQueriesVectorResponse(ExpressionQueriesVectorResponse res,
                                                                String metricName, String containerName){
        Metric metric = new Metric();
        metric.setTime(res.getData().getResult().get(0).getValue().get(0));
        metric.setValue(res.getData().getResult().get(0).getValue().get(1));
        //metric的ID为container的名字与metric的名字
        metric.setId(containerName + "_" + metricName);
        metric.setName(containerName + "_" + metricName);
        metric.setCreationTimestamp("" + new Date().getTime() / 1000);

        return metric;
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
                    relation.getVirtualMachine().setLatestUpdateTimestamp(currTimestampString);
                    relation.getPod().setLatestUpdateTimestamp(currTimestampString);
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
                    relation.getAppService().setLatestUpdateTimestamp(currTimestampString);
                    relation.getPod().setLatestUpdateTimestamp(currTimestampString);
                    relations.add(relation);
                }
            }
        }
        return relations;
    }

    private ArrayList<PodAndContainer> constructPodAndContainerRelation(ArrayList<ApiPod> apiPods,
                                                                        ArrayList<ApiContainer> apiContainers){
        ArrayList<PodAndContainer> relations = new ArrayList<>();
        for(ApiPod apiPod : apiPods){
            //当前这个node叫什么名字
            String podName = apiPod.getMetadata().getName();
            Pod pod = convertApiPodToPod(apiPod);
            for(ApiContainer apiContainer : apiContainers){
                //找到容器的pod-name和之前pod名字一样的哪些container
                if(podName.equals(apiContainer.getLabels().get("io.kubernetes.pod.name"))){
                    Container container = converApiContainerToContainer(apiContainer);
                    PodAndContainer relation = new PodAndContainer();
                    relation.setContainer(container);
                    relation.setPod(pod);
                    relation.setRelation("DEPLOY-IN");
                    relation.setId(container.getId() + "ContainerPod" + pod.getId());
                    relation.getContainer().setLatestUpdateTimestamp(currTimestampString);
                    relation.getPod().setLatestUpdateTimestamp(currTimestampString);
                    relations.add(relation);
                }
            }
        }

        return relations;
    }

    //转换ApiNode到Virtual Machine
    private VirtualMachine convertApiNodeToVm(ApiNode node){

        VirtualMachine vm = new VirtualMachine();
        //虚拟机的ID就是虚拟机的名字
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
        vm.setCreationTimestamp(convertTime(node.getMetadata().getCreationTimestamp()));

        return vm;
    }

    //转换ApiPod到Pod
    private Pod convertApiPodToPod(ApiPod apiPod){

        Pod pod = new Pod();
        //Pod的ID就是Pod的名字
        pod.setId(apiPod.getMetadata().getName());
        pod.setDnsPolicy(apiPod.getSpec().getDnsPolicy());
        pod.setNamespace(apiPod.getMetadata().getNamespace());
        pod.setPhase(apiPod.getStatus().getPhase());
        pod.setPodIP(apiPod.getStatus().getPodIP());
        pod.setQosClass(apiPod.getStatus().getQosClass());
        pod.setName(apiPod.getMetadata().getName());
        pod.setRestartPolicy(apiPod.getSpec().getRestartPolicy());
        pod.setSelflink(apiPod.getMetadata().getSelfLink());
        pod.setTerminationGracePeriodSeconds(apiPod.getSpec().getTerminationGracePeriodSeconds());
        pod.setCreationTimestamp(convertTime(apiPod.getMetadata().getCreationTimestamp()));

        return pod;
    }

    //转换ApiService到Service
    private AppService convertApiAppServiceToAppService(ApiAppService apiAppService){

        AppService appService = new AppService();
        //AppService的ID就是服务的名字
        appService.setId(apiAppService.getMetadata().getName());
        appService.setClusterIP(apiAppService.getSpec().getClusterIP());
        appService.setNamespace(apiAppService.getMetadata().getNamespace());
        appService.setName(apiAppService.getMetadata().getName());
        appService.setPort(apiAppService.getSpec().getPorts().get(0).getPort());
        appService.setSelflink(apiAppService.getMetadata().getSelfLink());
        appService.setType(apiAppService.getSpec().getType());
        appService.setCreationTimestamp(convertTime(apiAppService.getMetadata().getCreationTimestamp()));

        return appService;
    }

    private Container converApiContainerToContainer(ApiContainer apiContainer){

        Container container = new Container();
        container.setCommand(apiContainer.getCommand());
        container.setState(apiContainer.getState());
        container.setStatus(apiContainer.getStatus());
        container.setCreated(apiContainer.getCreated());
        container.setImage(apiContainer.getImage());
        //使用真正的ID而不是自定义ID
        container.setId(apiContainer.getId());
        container.setName(apiContainer.getNames().get(0));
        container.setCreationTimestamp(apiContainer.getCreated());
        return container;
    }

    private String convertTime(String timeStr){
        String utcTime = "2018-01-31T14:32:19Z";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        //设置时区UTC
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        //格式化，转当地时区时间
        try{
            Date after = df.parse(timeStr);
            return "" + after.getTime() / 1000;
        }catch (Exception e){
            e.printStackTrace();
            return "ConvertTimeFailure";
        }
        //Wed Jan 31 22:32:19 GMT+08:00 2018
    }
}
