package collector.controller;

import collector.domain.apicontainer.ContainerList;
import collector.domain.apinode.NodeList;
import collector.domain.apipod.PodList;
import collector.domain.apiservice.AppServiceList;
import collector.domain.entities.Metric;
import collector.domain.relationships.AppServiceHostServiceAPI;
import collector.domain.relationships.AppServiceInvokeServiceAPI;
import collector.domain.trace.Span;
import collector.service.DataCollectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;


@RestController
public class DataCollectorController {

    @Autowired
    DataCollectorService dataCollectorService;

    @GetMapping("/getCurrTime")
    public String getCurrTime(){
        return dataCollectorService.getCurrentTimestamp();
    }

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

    @GetMapping("/updateMetrics")
    public ArrayList<Metric> updateMetrics(){
        return dataCollectorService.updateMetrics();
    }

    @GetMapping("/buildFramework")
    public String buildFrameWork(){
        return dataCollectorService.createRawFrameworkToKnowledgeGraph();
    }

    @GetMapping("/trace")
    public ArrayList<AppServiceHostServiceAPI> getTraces(){


        return dataCollectorService.uploadApiSvcRelations();
    }

}
