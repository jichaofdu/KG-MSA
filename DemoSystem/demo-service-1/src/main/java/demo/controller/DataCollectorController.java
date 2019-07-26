package demo.controller;

import demo.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataCollectorController {

    @Autowired
    DemoService demoService;


    @GetMapping("/hello")
    public String getCurrTime(){
        return demoService.sayHello();
    }


}
