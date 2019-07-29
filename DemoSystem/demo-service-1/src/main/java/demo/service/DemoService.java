package demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DemoService {

    @Autowired
    private RestTemplate restTemplate;


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Scheduled(initialDelay=5000, fixedDelay =3000)
    public void testMemoryPeriodly() {
        logger.info("[Demo-Service-1] 进行中");

    }

    public String sayHello(){

        String echo = restTemplate.getForObject(
                "http://demo-service-2:18082/hello", String.class);

        echo += "[Demo-Service-1]SayHello;";

        return echo;
    }












}
