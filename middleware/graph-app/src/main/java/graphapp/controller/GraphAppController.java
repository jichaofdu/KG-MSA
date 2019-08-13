package graphapp.controller;

import graphapp.services.GraphAppServices;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/")
public class GraphAppController {

    private final GraphAppServices graphAppServices;


    public GraphAppController(GraphAppServices graphAppServices) {
        this.graphAppServices = graphAppServices;
    }

    @GetMapping("/abnormality/pods")
    public String updateAbnomalityOfPods(){
        return graphAppServices.updateAbnormalityOfPods();
    }

    @GetMapping("/abnormality/serviceApi")
    public String updateAbnomalityOfServiceApis(){
        return graphAppServices.updateAbnomalityOfServiceApis();
    }

    @GetMapping("/getShortPath/{traceId}")
    public Map<String, Set> getShortPath(@PathVariable String traceId){
        return graphAppServices.getOneTracePath(traceId);
    }

}
