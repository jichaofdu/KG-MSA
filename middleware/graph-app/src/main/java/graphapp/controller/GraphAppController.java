package graphapp.controller;

import graphapp.services.GraphAppServices;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
