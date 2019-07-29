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
        logger.info("[Demo-Service-2] 进行中");

    }

    public String sayHelloToAll(){
        String str3 = restTemplate.getForObject(
                "http://demo-service-3:18083/hello", String.class);
        String str4 = restTemplate.getForObject(
                "http://demo-service-4:18084/hello", String.class);
        String echo = str3 + str4;
        echo += "[Demo-Service-2]Hello;";
        return echo;
    }











}
