package graphapp.controller;

import graphapp.domain.UnitGraphNode;
import graphapp.domain.entities.GraphNode;
import graphapp.domain.relationships.BasicRelationship;
import graphapp.services.GraphAppServices;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/")
public class GraphAppController {

    private final GraphAppServices graphAppServices;


    public GraphAppController(GraphAppServices graphAppServices) {
        this.graphAppServices = graphAppServices;
    }

    @GetMapping("/total")
    public HashMap<GraphNode, HashMap<String, HashSet<BasicRelationship>>> getTotalGraph(){
        return graphAppServices.getTotalGraph();
    }

    @GetMapping("/invoke")
    public String getInvoke(){
        graphAppServices.extractLoadRelationAmongMicroservice();
        return "结束";
    }

    //http://localhost:17632/calculate/ts-execute-service/100
    @GetMapping("/calculate/{svcName}/{newPayload}")
    public String getCalculate(@PathVariable String svcName, @PathVariable int newPayload){

        System.out.println("微服务" + svcName + "的新流量" + newPayload);

        HashMap<String, HashMap<String, Integer>> svcAmongData = graphAppServices.extractLoadRelationAmongMicroservice();
        graphAppServices.extractLoadRelationAmongMicroservice(svcAmongData, svcName, newPayload);
        return "结束";
    }

    //http://localhost:17632/calculate/ts-execute-service/100
    @GetMapping("/calculateReplica/{svcName}/{newPayload}")
    public String getCalculateReplica(@PathVariable String svcName, @PathVariable int newPayload){

        System.out.println("微服务" + svcName + "的新流量" + newPayload);

        HashMap<String, HashMap<String, Integer>> svcAmongData = graphAppServices.extractLoadRelationAmongMicroservice();
        HashMap<String, Double> portionChangeResult = graphAppServices.extractLoadRelationAmongMicroservice(
                svcAmongData, svcName, newPayload);
        graphAppServices.calculateMvcReplicaInNewEra(portionChangeResult);
        return "结束";
    }

    //http://localhost:17632/calculate/ts-execute-service/100
    @GetMapping("/doScaling/{svcName}/{newPayload}")
    public String doScaling(@PathVariable String svcName, @PathVariable int newPayload){

        System.out.println("微服务" + svcName + "的新流量" + newPayload);

        HashMap<String, HashMap<String, Integer>> svcAmongData = graphAppServices.extractLoadRelationAmongMicroservice();
        HashMap<String, Double> portionChangeResult = graphAppServices.extractLoadRelationAmongMicroservice(
                svcAmongData, svcName, newPayload);
        HashMap<String, Integer> demands = graphAppServices.calculateMvcReplicaInNewEra(portionChangeResult);
        HashMap<String, Integer> oldReplicaCount = graphAppServices.getNowSvcReplicaNumber();
        graphAppServices.doScaling(oldReplicaCount, demands);
        return "结束";
    }

    @GetMapping("/doNormalScaling/{svcName}/{newPayload}")
    public String doNormalScaling(@PathVariable String svcName, @PathVariable int newPayload){

        System.out.println("微服务" + svcName + "的新流量" + newPayload);

        Date start = new Date();

        HashMap<String, HashMap<String, Integer>> svcAmongData = graphAppServices.extractLoadRelationAmongMicroservice();
        HashMap<String, Double> portionChangeResult = graphAppServices.extractLoadRelationAmongMicroservice(
                svcAmongData, svcName, newPayload);


        HashMap<String, Integer> demands = graphAppServices.calculateMvcReplicaInNewEra(portionChangeResult);
        HashMap<String, Integer> oldReplicaCount = graphAppServices.getNowSvcReplicaNumber();
        graphAppServices.doNormalScaling(oldReplicaCount,demands);

        Date end = new Date();
        System.out.println("优化扩缩容 - 开始时间：" + start.toString());
        System.out.println("优化扩缩容 - 结束时间：" + end.toString());
        return "结束";
    }

    @GetMapping("/doBetterScaling/{svcName}/{newPayload}")
    public String doBetterScaling(@PathVariable String svcName, @PathVariable int newPayload){

        System.out.println("微服务" + svcName + "的新流量" + newPayload);
        Date start = new Date();

        HashMap<String, HashMap<String, Integer>> svcAmongData = graphAppServices.extractLoadRelationAmongMicroservice();
        HashMap<String, Double> portionChangeResult = graphAppServices.extractLoadRelationAmongMicroservice(
                svcAmongData, svcName, newPayload);
        HashMap<String, Integer> demands = graphAppServices.calculateMvcReplicaInNewEra(portionChangeResult);
        HashMap<String, Integer> oldReplicaCount = graphAppServices.getNowSvcReplicaNumber();

        graphAppServices.doBetterScaling(oldReplicaCount, demands);

        Date end = new Date();
        System.out.println("优化扩缩容 - 开始时间：" + start.toString());
        System.out.println("优化扩缩容 - 结束时间：" + end.toString());
        return "结束";
    }

    @GetMapping("/doRecoverScaling/{svcName}/{newPayload}")
    public String doRecoverScaling(@PathVariable String svcName, @PathVariable int newPayload){

        System.out.println("微服务" + svcName + "的新流量" + newPayload);

        HashMap<String, HashMap<String, Integer>> svcAmongData = graphAppServices.extractLoadRelationAmongMicroservice();
        HashMap<String, Double> portionChangeResult = graphAppServices.extractLoadRelationAmongMicroservice(
                svcAmongData, svcName, newPayload);
        HashMap<String, Integer> demands = graphAppServices.calculateMvcReplicaInNewEra(portionChangeResult);
        graphAppServices.recoverScaling(demands);
        return "结束";
    }


    @GetMapping("/testcmd")
    public String testCmd(){
        graphAppServices.testRCM();
        return "结束";
    }

    @GetMapping("/diagnosis/{traceId}")
    public String diagnosisTraceId(@PathVariable String traceId){
        return graphAppServices.diagnosisTrace(traceId);
    }

    @GetMapping("/abnormality/pods")
    public String updateAbnomalityOfPods(){
        return graphAppServices.updateAbnormalityOfPods();
    }

    @GetMapping("/abnormality/serviceApi")
    public String updateAbnomalityOfServiceApis(){
        return graphAppServices.updateAbnomalityOfServiceApis();
    }

    @PostMapping("/abnormality/podList")
    public String updatePodAbnormalityByList(@RequestBody ArrayList<String> podMetricIdList){
        return graphAppServices.updatePodAbnormalityByList(podMetricIdList);
    }

    @PostMapping("/abnormality/apiList")
    public String updateApiAbnormalityByList(@RequestBody ArrayList<String> apiMetricIdList){
        return graphAppServices.updateApiAbnormalityByList(apiMetricIdList);
    }

    @GetMapping("/getTrace/{traceId}")
    public Map<String, Set> getTrace(@PathVariable String traceId){
        return graphAppServices.getOneTracePath(traceId);
    }

    @GetMapping("/getTraceMetrics/{traceId}")
    public Map<String, Set> getTraceMetrics(@PathVariable String traceId){
        return graphAppServices.getOneTraceMetrics(traceId);
    }

    @GetMapping("/getCrossComponentOfTwoTrace/{traceA}/{traceB}")
    public Map<String, Set> getCrossComponentOfTwoTrace(@PathVariable String traceA, @PathVariable String traceB){
        return graphAppServices.getCrossComponentOfTwoTrace(traceA, traceB);
    }

    @GetMapping("/getCrossMetricsOfTwoTrace/{traceA}/{traceB}")
    public Map<String, Set> getCrossMetricsOfTwoTrace(@PathVariable String traceA, @PathVariable String traceB){
        return graphAppServices.getCrossMetricsOfTwoTrace(traceA, traceB);
    }

    @GetMapping("/getCrossOfTwoTrace/{traceA}/{traceB}")
    public Map<String, Set>  getCrossOfTwoTrace(@PathVariable String traceA, @PathVariable String traceB) {
        return graphAppServices.getCrossOfTwoTrace(traceA, traceB);
    }

    @GetMapping("/getSortedGraphNodeByAbnormality/{traceId}")
    public ArrayList<UnitGraphNode> getSortedNode(@PathVariable String traceId){
        return graphAppServices.getSortedGraphNode(traceId);
    }

}
