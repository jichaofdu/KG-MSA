package demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DemoService {

    @Autowired
    private RestTemplate restTemplate;

    public String sayHello(){

        String echo = restTemplate.getForObject(
                "http://demo-service-2:18082/hello", String.class);

        echo += "[Demo-Service-1]SayHello;";

        return echo;
    }












}
