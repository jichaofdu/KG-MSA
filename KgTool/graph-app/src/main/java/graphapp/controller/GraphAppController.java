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
