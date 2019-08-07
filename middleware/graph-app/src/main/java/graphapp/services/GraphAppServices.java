package graphapp.services;

import graphapp.domain.entities.Pod;
import graphapp.domain.entities.PodMetric;
import graphapp.domain.entities.ServiceAPI;
import graphapp.domain.entities.ServiceApiMetric;
import graphapp.repositories.PodRepository;
import graphapp.repositories.ServiceApiRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;

@Service
public class GraphAppServices {

    private final PodRepository podRepository;

    private final ServiceApiRepository serviceApiRepository;

    public GraphAppServices(PodRepository podRepository,
                        ServiceApiRepository serviceApiRepository){
        this.podRepository = podRepository;
        this.serviceApiRepository = serviceApiRepository;
    }

    public String updateAbnormalityOfPods(){
        ArrayList<Pod> podList = podRepository.findAllPods();
        for(Pod pod : podList){
            //1.找到Pod相关联的PodMetric
            HashSet<PodMetric> localMetrics = null;

            //2.逐个计算Pod的Abnormality
            double abnormality = updateSingleAbnormalityOfPods(pod, localMetrics);
            //3.将计算好的Abnormality存塞进去并存回数据库
            pod.setAbnormality(abnormality);
            podRepository.save(pod);
        }
        return "";
    }

    public String updateAbnomalityOfServiceApis(){
        ArrayList<ServiceAPI> serviceAPIList = serviceApiRepository.findAllAppService();
        for(ServiceAPI api : serviceAPIList){
            //1.找到ServiceAPI相关联的ServiceApiMetric
            HashSet<ServiceApiMetric> localMetrics = null;

            //2.逐个计算ServiceAPI的Abnormality
            double abnormality = updateSingleAbnormalityOfServiceApis(api, localMetrics);
            //3.将计算好的Abnormality存塞进去并存回数据库
            api.setAbnormality(abnormality);
            serviceApiRepository.save(api);
        }
        return "";
    }


    private double updateSingleAbnormalityOfPods(Pod pod,
                                               HashSet<PodMetric> podMetrics){
        return 0.99;
    }

    private double updateSingleAbnormalityOfServiceApis(ServiceAPI serviceAPI,
                                                      HashSet<ServiceApiMetric> serviceApiMetrics){
        return 0.99;
    }

}
