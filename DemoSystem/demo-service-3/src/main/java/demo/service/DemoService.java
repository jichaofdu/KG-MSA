package demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class DemoService {

    @Autowired
    private RestTemplate restTemplate;

    @Scheduled(initialDelay=5000, fixedDelay =30000)
    public void testMemoryPeriodly() {
        memory();
    }

    private void memory() {
        List<int[]> list = new ArrayList<>();

        Runtime run = Runtime.getRuntime();
        int i = 1;
        while (true) {
            int[] arr = new int[1024 * 8];
            list.add(arr);

            try {
                Thread.sleep(5);
            }catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (i++ % 1000 == 0) {

                System.out.print(
                        "[Order Service]Max RAM=" + run.maxMemory() / 1024 / 1024 + "M,");
                System.out.print(
                        "[Order Service]Allocated RAM=" + run.totalMemory() / 1024 / 1024 + "M,");
                System.out.print(
                        "[Order Service]Rest RAM=" + run.freeMemory() / 1024 / 1024 + "M");
                System.out.println(
                        "[Order Service]Max available RAM=" + (run.maxMemory() - run.totalMemory() + run.freeMemory()) / 1024 / 1024 + "M");
            }
            if(i >= 6000){
                break;
            }
        }
    }

    public String sayHelloToAll(){
        String str5 = restTemplate.getForObject(
                "http://demo-service-5:18085/hello", String.class);
        String echo = str5;
        echo += "[Demo-Service-3]Hello;";
        return echo;
    }











}
