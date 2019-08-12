package graphapp.services;

import graphapp.domain.entities.PodMetric;
import graphapp.domain.entities.ServiceApiMetric;
import graphapp.repositories.MetricOfPodRepository;
import graphapp.repositories.MetricOfServiceApiRepository;
import graphapp.repositories.PodRepository;
import graphapp.repositories.ServiceApiRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

@Service
public class GraphAppServices {

    private final PodRepository podRepository;

    private final ServiceApiRepository serviceApiRepository;

    private final MetricOfPodRepository metricOfPodRepository;

    private final MetricOfServiceApiRepository metricOfServiceApiRepository;

    private static final double DEFAULT_ABNORMALITY = 0.1;

    public GraphAppServices(PodRepository podRepository,
                        ServiceApiRepository serviceApiRepository,
                            MetricOfPodRepository metricOfPodRepository,
                            MetricOfServiceApiRepository metricOfServiceApiRepository){
        this.podRepository = podRepository;
        this.serviceApiRepository = serviceApiRepository;
        this.metricOfPodRepository = metricOfPodRepository;
        this.metricOfServiceApiRepository = metricOfServiceApiRepository;
    }

    public String updateAbnormalityOfPods(){
        ArrayList<PodMetric> podMetricList = metricOfPodRepository.findAllMetrics();
        for(PodMetric podMetric : podMetricList){
            double abnormality = updateSingleAbnormalityOfPods(podMetric);
            podMetric.setAbnormality(abnormality);
            metricOfPodRepository.save(podMetric);
        }
        System.out.println("[更新PodMetric Abnormality] 数量:" + podMetricList.size());
        return "Success";
    }

    public String updateAbnomalityOfServiceApis(){
        ArrayList<ServiceApiMetric> serviceApiMetricList = metricOfServiceApiRepository.findAllMetrics();
        for(ServiceApiMetric serviceApiMetric : serviceApiMetricList){
            double abnormality = updateSingleAbnormalityOfServiceApis(serviceApiMetric);
            serviceApiMetric.setAbnormality(abnormality);
            metricOfServiceApiRepository.save(serviceApiMetric);
        }
        System.out.println("[更新ServiceAPIMetric Abnormality] 数量:" + serviceApiMetricList.size());
        return "Success";
    }

    public double updateSingleAbnormalityOfPods(PodMetric podMetric){
        ArrayList<Double> values = podMetric.getHistoryValues();
        if(values.size() <= 3){
            return 0.0;
        }else{
            return threeSigmaAbnormality(values, podMetric.getValue());
        }

    }

    public double updateSingleAbnormalityOfServiceApis(ServiceApiMetric serviceApiMetric){
        ArrayList<Double> values = serviceApiMetric.getValues();
        if(values.size() <= 3){
            return 0.0;
        }else{
            return threeSigmaAbnormality(values, values.get(values.size()-1));
        }

    }

    public double threeSigmaAbnormality(ArrayList<Double> historyValue, double latestValue){
        int arrLen = historyValue.size();
        double latestAvg = (latestValue + historyValue.get(arrLen-1) + historyValue.get(arrLen-2)) / 3.0;

        double totalAvg = getAverage(historyValue);
        double totalSd = getStandardDiviation(historyValue, totalAvg);

        double abnormality;
        if(totalSd == 0){
            abnormality = DEFAULT_ABNORMALITY;
        }else{
            abnormality = Math.abs((latestAvg - totalAvg) / totalSd);
        }

        System.out.println("[ThreeSigmaAbnormality]" +
                " LatestAvg:" + latestAvg +
                " TotalAvg:" + totalAvg +
                " TotalSd:" + totalSd +
                " Abnormality:" + abnormality);

        return abnormality;
    }

    public double getAverage(ArrayList<Double> x){
        int m = x.size();
        double sum=0;
        for(int i = 0; i < m; i++){//求和
            sum += x.get(i);
        }
        return sum / m;
    }

    public double getStandardDiviation(ArrayList<Double> x, double dAve) {
        double dVar = 0;
        for(int i = 0; i < x.size(); i++){//求方差
            dVar += (x.get(i) - dAve) * (x.get(i) - dAve);
        }
        return Math.sqrt(dVar / x.size());
    }

}
