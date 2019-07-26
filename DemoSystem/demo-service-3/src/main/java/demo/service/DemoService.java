package demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DemoService {

    @Autowired
    private RestTemplate restTemplate;

    public String sayHelloToAll(){
        String str5 = restTemplate.getForObject(
                "http://demo-service-5:18085/hello", String.class);
        String echo = str5;
        echo += "[Demo-Service-3]Hello;";
        return echo;
    }











}
