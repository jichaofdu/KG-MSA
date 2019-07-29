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
    public void ongoing() {
        logger.info("[Demo-Service-4] 进行中");

    }

    public String sayHelloToAll(){
        return "[Demo-Service-4]Hello;";
    }











}
