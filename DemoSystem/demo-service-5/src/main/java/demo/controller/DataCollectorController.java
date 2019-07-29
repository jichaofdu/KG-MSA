package demo.controller;

import demo.service.DemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataCollectorController {

    @Autowired
    DemoService demoService;


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Scheduled(initialDelay=5000, fixedDelay =3000)
    public void ongoing() {
        logger.info("[Demo-Service-4] 进行中");

    }

    @GetMapping("/hello")
    public String getCurrTime(){
        return demoService.sayHelloToAll();
    }

}
