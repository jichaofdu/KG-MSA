package collector.controller;

import collector.domain.apicontainer.ContainerList;
import collector.domain.apinode.NodeList;
import collector.domain.apipod.PodList;
import collector.domain.apiservice.AppServiceList;
import collector.domain.prom.ExpressionQueriesVectorResponse;
import collector.service.DataCollectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
public class DataCollectorController {

    @Autowired
    DataCollectorService dataCollectorService;

    @GetMapping("/node")
    public NodeList getNodeList(){
        return dataCollectorService.getNodeList();
    }

    @GetMapping("/container")
    public ContainerList getContainerList(){
        return dataCollectorService.getContainerList();
    }

    @GetMapping("/pod")
    public PodList getPodList() {
        return dataCollectorService.getPodList();
    }

    @GetMapping("/service")
    public AppServiceList getAppServiceList(){
        return dataCollectorService.getAppServiceList();
    }

    @GetMapping("/buildFramework")
    public String buildFrameWork(){
        return dataCollectorService.createRawFrameworkToKnowledgeGraph();
    }



}
