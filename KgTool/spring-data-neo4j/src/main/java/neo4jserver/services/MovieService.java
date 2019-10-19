package neo4jserver.services;

import java.util.*;
import neo4jserver.domain.*;
import neo4jserver.domain.entities.*;
import neo4jserver.domain.relationships.*;
import neo4jserver.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovieService {

	private static final int METRIC_MAX_TIME_WINDOW_SIZE = 30;

	private final ContainerRepository containerRepository;

	private final AppServiceRepository appServiceRepository;

	private final PodRepository podRepository;

	private final VirtualMachineRepository virtualMachineRepository;

	private final VirtualMachineAndPodRepository virtualMachineAndPodRepository;

	private final AppServiceAndPodRepository appServiceAndPodRepository;

	private final PodAndContainerRepository podAndContainerRepository;

	private final MetricRepository metricRepository;

	private final MetricAndContainerRepository metricAndContainerRepository;

	private final ServiceApiRepository serviceApiRepository;

	private final AppServiceHostApiRepository appServiceHostApiRepository;

	private final AppServiceInvokeApiRepository appServiceInvokeApiRepository;

	private final TraceInvokeApiToPodRepository traceInvokeApiToPodRepository;

	private final TraceInvokePodToApiRepository traceInvokePodToApiRepository;

	private final MetricOfServiceApiRepository metricOfServiceApiRepository;

	private final MetricAndServiceApiRepository metricAndServiceApiRepository;

	private final MetricAndPodRepository metricAndPodRepository;

	private final MetricOfPodRepository metricOfPodRepository;

	public MovieService(PodRepository podRepository,
						ContainerRepository containerRepository,
						AppServiceRepository appServiceRepository,
						VirtualMachineRepository virtualMachineRepository,
						VirtualMachineAndPodRepository virtualMachineAndPodRepository,
						AppServiceAndPodRepository appServiceAndPodRepository,
						PodAndContainerRepository podAndContainerRepository,
						MetricRepository metricRepository,
						MetricAndContainerRepository metricAndContainerRepository,
						ServiceApiRepository serviceApiRepository,
						AppServiceHostApiRepository appServiceHostApiRepository,
						AppServiceInvokeApiRepository appServiceInvokeApiRepository,
						TraceInvokeApiToPodRepository traceInvokeApiToPodRepository,
						TraceInvokePodToApiRepository traceInvokePodToApiRepository,
						MetricOfServiceApiRepository metricOfServiceApiRepository,
						MetricAndServiceApiRepository metricAndServiceApiRepository,
						MetricAndPodRepository metricAndPodRepository,
						MetricOfPodRepository metricOfPodRepository) {
		this.podRepository = podRepository;
		this.virtualMachineRepository = virtualMachineRepository;
		this.virtualMachineAndPodRepository = virtualMachineAndPodRepository;
		this.containerRepository = containerRepository;
		this.appServiceRepository = appServiceRepository;
		this.appServiceAndPodRepository = appServiceAndPodRepository;
		this.podAndContainerRepository = podAndContainerRepository;
		this.metricRepository = metricRepository;
		this.metricAndContainerRepository = metricAndContainerRepository;
		this.serviceApiRepository = serviceApiRepository;
		this.appServiceHostApiRepository = appServiceHostApiRepository;
		this.appServiceInvokeApiRepository = appServiceInvokeApiRepository;
		this.traceInvokeApiToPodRepository = traceInvokeApiToPodRepository;
		this.traceInvokePodToApiRepository = traceInvokePodToApiRepository;
		this.metricOfServiceApiRepository = metricOfServiceApiRepository;
		this.metricAndServiceApiRepository = metricAndServiceApiRepository;
		this.metricAndPodRepository = metricAndPodRepository;
		this.metricOfPodRepository = metricOfPodRepository;
	}

	@Transactional(readOnly = true)
	public ArrayList<ServiceApiAndMetric> postMetricsOfServiceApi(ArrayList<ServiceApiAndMetric> relations){

		ArrayList<ServiceApiAndMetric> result = new ArrayList<>();
		for(ServiceApiAndMetric relation : relations){
			ServiceApiMetric metric = relation.getApiMetric();
			System.out.println("=================上传ServiceApiMetric=================");
			Optional<ServiceApiMetric> metricOptional = metricOfServiceApiRepository.findById(metric.getId());
			if(metricOptional.isPresent()){
				System.out.println("查找的ServiceApiMetric存在 -> " + metric.getId());
				ServiceApiMetric oldMetric = metricOptional.get();
				ArrayList<Long> timeList = new ArrayList<>();
				timeList.addAll(oldMetric.getHistoryTimestamps());
				timeList.add(oldMetric.getTime());
				timeList.addAll(metric.getHistoryTimestamps());
				long lastTime = timeList.remove(timeList.size() - 1);

				System.out.println("原有数据条目:" + oldMetric.getHistoryValues().size());

				System.out.println("新增数据条目:" + metric.getHistoryValues().size());
				ArrayList<Double> valueList = new ArrayList<>();
				valueList.addAll(oldMetric.getHistoryValues());
				valueList.add(oldMetric.getValue());
				valueList.addAll(metric.getHistoryValues());
				double lastValue = valueList.remove(valueList.size() - 1);

				oldMetric.setTime(lastTime);
				oldMetric.setHistoryTimestamps(timeList);
				oldMetric.setValue(lastValue);
				oldMetric.setHistoryValues(valueList);

				System.out.println("结束后数据条目:" + oldMetric.getHistoryValues().size());

				metricOfServiceApiRepository.save(oldMetric);

				relation.setApiMetric(oldMetric);

				result.add(relation);

			}else{

				ServiceApiMetric newMetric = relation.getApiMetric();
				ArrayList<Long> timeList = newMetric.getHistoryTimestamps();
				ArrayList<Double> valueList = newMetric.getHistoryValues();
				long lastTime = timeList.remove(timeList.size() - 1);
				double lastValue = valueList.remove(valueList.size() - 1);
				newMetric.setTime(lastTime);
				newMetric.setValue(lastValue);
				newMetric.setHistoryTimestamps(timeList);
				newMetric.setHistoryValues(valueList);


				System.out.println("查找的ServiceApiMetric【不】存在 -> " + metric.getId());
				ServiceApiAndMetric savedRelation = metricAndServiceApiRepository.save(relation);
				result.add(savedRelation);
			}


		}
		return result;
	}

	@Transactional(readOnly = true)
	public ArrayList<TraceInvokeApiToPod> postTraceApiToPod(ArrayList<TraceInvokeApiToPod> relations){
		ArrayList<TraceInvokeApiToPod> result = new ArrayList<>();
		for(TraceInvokeApiToPod relation : relations) {
			System.out.println("FIND RELATION ID " + relation.getId());
			Optional<TraceInvokeApiToPod> savedRelationOptional =
					traceInvokeApiToPodRepository.findById(relation.getId());

			if(!savedRelationOptional.isPresent()){
				relation.setPod(podRepository.save(relation.getPod()));
				relation.setServiceAPI(serviceApiRepository.save(relation.getServiceAPI()));
				System.out.println("不存在");
			}else{
				TraceInvokeApiToPod savedRelation = savedRelationOptional.get();
				savedRelation.getTraceIdAndSpanIds().addAll(relation.getTraceIdAndSpanIds());
				relation = savedRelation;
				System.out.println("存在");
			}
			relation = traceInvokeApiToPodRepository.save(relation);
			result.add(relation);
		}
		return result;
	}

	@Transactional(readOnly = true)
	public ArrayList<TraceInvokePodToApi> postTracePodToApi(ArrayList<TraceInvokePodToApi> relations){
		ArrayList<TraceInvokePodToApi> result = new ArrayList<>();
		for(TraceInvokePodToApi relation : relations) {
			Optional<TraceInvokePodToApi> savedRelationOptional =
					traceInvokePodToApiRepository.findById(relation.getId());
			if(!savedRelationOptional.isPresent()){
				relation.setPod(podRepository.save(relation.getPod()));
				relation.setServiceAPI(serviceApiRepository.save(relation.getServiceAPI()));
				System.out.println("不存在");
			}else{
				TraceInvokePodToApi savedRelation = savedRelationOptional.get();
				savedRelation.getTraceIdAndSpanIds().addAll(relation.getTraceIdAndSpanIds());
				relation = savedRelation;
				System.out.println("存在");
			}
			relation = traceInvokePodToApiRepository.save(relation);
			result.add(relation);
		}
		return result;
	}

	@Transactional(readOnly = true)
	public ArrayList<AppServiceHostServiceAPI> postListOfAppServiceAndServiceAPI(ArrayList<AppServiceHostServiceAPI> list){
		ArrayList<AppServiceHostServiceAPI> result = new ArrayList<>();
		for(AppServiceHostServiceAPI relation : list){
			AppService svc = relation.getAppService();
			ServiceAPI api = relation.getServiceAPI();
			svc = appServiceRepository.save(svc);
			api = serviceApiRepository.save(api);
			relation.setAppService(svc);
			relation.setServiceAPI(api);
			relation = appServiceHostApiRepository.save(relation);
			result.add(relation);
		}
		return result;
	}

	@Transactional(readOnly = true)
	public ArrayList<AppServiceInvokeServiceAPI> postListOfAppServiceInvokeServiceAPI(ArrayList<AppServiceInvokeServiceAPI> list){
		ArrayList<AppServiceInvokeServiceAPI> result = new ArrayList<>();
		for(AppServiceInvokeServiceAPI relation : list){

			Optional<AppServiceInvokeServiceAPI> relationFind =
					appServiceInvokeApiRepository.findById(relation.getId());
			if(!relationFind.isPresent()){
				relation.setAppService(appServiceRepository.save(relation.getAppService()));
				relation.setServiceAPI(serviceApiRepository.save(relation.getServiceAPI()));
			}else{
				relation = relationFind.get();
				relation.setCount(relation.getCount()+1);
				System.out.println("API次数:" + relation.getCount());
			}
			relation = appServiceInvokeApiRepository.save(relation);
			result.add(relation);
		}
		return result;
	}

	@Transactional(readOnly = true)
	public Metric findByMetricId(String id){
		Long idLong = Long.parseLong(id);
		MetricResult mr = metricRepository.getContainerWithLabels(idLong);
		Metric metric = mr.metric;
		metric.setLabels(new HashSet<>(mr.labels));
		return metric;
	}

	@Transactional(readOnly = true)
	public Metric postMetric(Metric metric) {
		Metric newMetric = metricRepository.save(metric);
		return metric;
	}

	@Transactional(readOnly = true)
	public ArrayList<PodAndMetric> postPodAndMetric(ArrayList<PodAndMetric> podAndMetrics){
		ArrayList<PodAndMetric> result = new ArrayList<>();

		for(PodAndMetric relation : podAndMetrics){
			Optional<PodMetric> podMetricOpt = metricOfPodRepository.findById(relation.getPodMetric().getId());
			if(podMetricOpt.isPresent()){
				PodMetric oldPodMetric = podMetricOpt.get();
				oldPodMetric.getHistoryTimestamps().add(oldPodMetric.getTime());
				oldPodMetric.getHistoryValues().add(oldPodMetric.getValue());
//				oldPodMetric.getHistoryAbnormality().add(oldPodMetric.getAbnormality());
				oldPodMetric.setTime(relation.getPodMetric().getTime());
				oldPodMetric.setValue(relation.getPodMetric().getValue());
//				oldPodMetric.setAbnormality(relation.getPodMetric().getAbnormality());
				oldPodMetric.setLatestUpdateTimestamp(relation.getPodMetric().getLatestUpdateTimestamp());
				//time frame是200 最多保留两百条数据
				//多余的数据拿掉（越靠前的数据越离谱）
				if(oldPodMetric.getHistoryValues() != null &&
						oldPodMetric.getHistoryValues().size() > METRIC_MAX_TIME_WINDOW_SIZE){
					oldPodMetric.getHistoryValues().remove(0);
					oldPodMetric.getHistoryTimestamps().remove(0);
					oldPodMetric.getHistoryAbnormality().remove(0);
				}
				oldPodMetric = metricOfPodRepository.save(oldPodMetric);
				relation.setPodMetric(oldPodMetric);
				result.add(relation);
			}else{
				PodMetric podMetric = metricOfPodRepository.save(relation.getPodMetric());
				relation.setPodMetric(podMetric);
				relation = metricAndPodRepository.save(relation);
				result.add(relation);
			}
		}
		return result;
	}


	@Transactional(readOnly = true)
	public ArrayList<Metric> updateMetrics(ArrayList<Metric> metrics){
		ArrayList<Metric> returnMetrics = new ArrayList<>();
		for(Metric metric : metrics){
			String name = metric.getName();
			Optional<Metric> metricOld = metricRepository.findByName(name);
			if(!metricOld.isPresent()){
				continue;
			}
			Metric metricOldEntity = metricOld.get();
			metricOldEntity.getHistoryTimestamps().add(metricOldEntity.getTime());
			metricOldEntity.getHistoryValues().add(metricOldEntity.getValue());
//			metricOldEntity.getHistoryAbnormality().add(metricOldEntity.getAbnormality());
			metricOldEntity.setTime(metric.getTime());
			metricOldEntity.setValue(metric.getValue());
//			metricOldEntity.setAbnormality(metric.getAbnormality());
			metricOldEntity.setLatestUpdateTimestamp(metric.getLatestUpdateTimestamp());
			//time frame是200 最多保留两百条数据
			//多余的数据拿掉（越靠前的数据越离谱）
			if(metricOldEntity.getHistoryValues() != null &&
					metricOldEntity.getHistoryValues().size() > METRIC_MAX_TIME_WINDOW_SIZE){
				metricOldEntity.getHistoryValues().remove(0);
				metricOldEntity.getHistoryTimestamps().remove(0);
			}

			metricOldEntity = metricRepository.save(metricOldEntity);
			returnMetrics.add(metricOldEntity);
		}
		return returnMetrics;
	}

	@Transactional(readOnly = true)
	public ArrayList<Metric> findAllMetrics(){
		return metricRepository.findAllMetrics();
	}

	@Transactional(readOnly = true)
	public Container findByContainerId(String id){
		Long idLong = Long.parseLong(id);
		ContainerResult cr = containerRepository.getContainerWithLabels(idLong);
		Container container = cr.container;
		container.setLabels(new HashSet<>(cr.labels));
		return container;
	}

	@Transactional(readOnly = true)
	public Container postContainer(Container container){
		Container newContainer = containerRepository.save(container);
		return newContainer;
	}

	@Transactional(readOnly = true)
	public ArrayList<Container> findAllContainers(){
		return containerRepository.findAllContainers();
	}

	@Transactional(readOnly = true)
	public AppService findByAppServiceId(String id){
		Long idLong = Long.parseLong(id);
		AppServiceResult sr = appServiceRepository.getServiceWithLabels(idLong);
		AppService service = sr.appService;
		service.setLabels(new HashSet<>(sr.labels));
		return service;
	}

	@Transactional(readOnly = true)
	public AppService postAppService(AppService appService){
		AppService newAppService = appServiceRepository.save(appService);
		return newAppService;
	}

	@Transactional(readOnly = true)
	public ArrayList<AppService> findAllAppServices(){
		return appServiceRepository.findAllAppService();
	}

    @Transactional(readOnly = true)
	public Pod findByPodId(String id){
		Long idLong = Long.parseLong(id);
		PodResult pr = podRepository.getPodWithLabels(idLong);
		Pod pod = pr.node;
		pod.setLabels(new HashSet<>(pr.labels));
		return pod;
	}

	@Transactional(readOnly = true)
	public Pod postPod(Pod pod){
		Pod newPod = podRepository.save(pod);
		return newPod;
	}

	@Transactional(readOnly = true)
	public ArrayList<Pod> findAllPods(){
		return podRepository.findAllPods();
	}

	@Transactional(readOnly = true)
	public VirtualMachine findByVMId(String id){
		Long idLong = Long.parseLong(id);
		VirtualMachineResult vmr = virtualMachineRepository.getVitualMachineWithLabels(idLong);
		VirtualMachine vm = vmr.node;
		vm.setLabels(new HashSet<>(vmr.labels));
		return vm;
	}

	@Transactional(readOnly = true)
	public VirtualMachine postVirtualMachine(VirtualMachine vm){
		VirtualMachine newVM = virtualMachineRepository.save(vm);
		return newVM;
	}

	@Transactional(readOnly = true)
	public ArrayList<VirtualMachine> findAllVms(){
		return virtualMachineRepository.findAllVms();
	}


	@Transactional(readOnly = true)
	public AppServiceAndPod findByAppServiceAndPodId(String id){
		Optional<AppServiceAndPod> appServiceAndPod = appServiceAndPodRepository.findById(id);
		return appServiceAndPod.get();
	}

	@Transactional(readOnly = true)
	public AppServiceAndPod postAppServiceAndPod(AppServiceAndPod appServiceAndPod){

		AppService appService = appServiceAndPod.getAppService();
		Pod pod = appServiceAndPod.getPod();

		appService = appServiceRepository.save(appService);
		pod = podRepository.save(pod);

		appServiceAndPod.setAppService(appService);
		appServiceAndPod.setPod(pod);

		appServiceAndPod = appServiceAndPodRepository.save(appServiceAndPod);

		return appServiceAndPod;
	}

	@Transactional(readOnly = true)
	public ArrayList<AppServiceAndPod> postAppServiceAndPodList(ArrayList<AppServiceAndPod> relations){
		ArrayList<AppServiceAndPod> result = new ArrayList<>();
		for(AppServiceAndPod relation : relations){
			AppServiceAndPod newRelation = postAppServiceAndPod(relation);
			result.add(newRelation);
		}
		return result;
	}

	@Transactional(readOnly = true)
	public PodAndContainer findByPodAndContainerId(String id){
		Optional<PodAndContainer> podAndContainer = podAndContainerRepository.findById(id);
		return podAndContainer.get();
	}

	@Transactional(readOnly = true)
	public PodAndContainer postPodAndContainer(PodAndContainer podAndContainer){
		Pod pod = podAndContainer.getPod();
		Container container = podAndContainer.getContainer();

		pod = podRepository.save(pod);
		container = containerRepository.save(container);

		podAndContainer.setContainer(container);
		podAndContainer.setPod(pod);

		podAndContainer = podAndContainerRepository.save(podAndContainer);

		return podAndContainer;
	}

	@Transactional(readOnly = true)
	public ArrayList<PodAndContainer> postPodAndContainerList(ArrayList<PodAndContainer> relations){
		ArrayList<PodAndContainer> result = new ArrayList<>();
		for(PodAndContainer relation : relations){
			PodAndContainer newRelation = postPodAndContainer(relation);
			result.add(newRelation);
		}
		return result;
	}

	@Transactional(readOnly = true)
	public MetricAndContainer findByMetricAndContainerId(String id){
		Optional<MetricAndContainer> relation = metricAndContainerRepository.findById(id);
		return relation.get();
	}

	@Transactional(readOnly = true)
	public MetricAndContainer postMetricAndContainer(MetricAndContainer metricAndContainer) {

		Metric metric = metricAndContainer.getMetric();
		Container container = metricAndContainer.getContainer();

		if(!metricRepository.findByName(metric.getName()).isPresent() ||
				!containerRepository.findByName(container.getName()).isPresent()){
			metric = metricRepository.save(metric);
			container = containerRepository.save(container);
			metricAndContainer.setMetric(metric);
			metricAndContainer.setContainer(container);
			metricAndContainer = metricAndContainerRepository.save(metricAndContainer);
			System.out.println("刷新Metric与Container结构");
		}else{
			System.out.println("不刷新Metric与Container结构");
		}

		return metricAndContainer;
	}

	@Transactional(readOnly = true)
	public ArrayList<MetricAndContainer> postMetricAndContainerList(ArrayList<MetricAndContainer> relations){
		ArrayList<MetricAndContainer> result = new ArrayList<>();
		for(MetricAndContainer relation : relations){
			MetricAndContainer newRelation = postMetricAndContainer(relation);
			result.add(newRelation);
		}
		return result;
	}

	@Transactional(readOnly = true)
	public VirtualMachineAndPod findByVirtualMachineAndPodId(String id){
		Optional<VirtualMachineAndPod> deploy = virtualMachineAndPodRepository.findById(id);
		return deploy.get();
	}

	@Transactional(readOnly = true)
	public VirtualMachineAndPod postVirtualMachineAndPod(VirtualMachineAndPod virtualMachineAndPod){

		VirtualMachine vm = virtualMachineAndPod.getVirtualMachine();
		Pod pod = virtualMachineAndPod.getPod();

		vm = virtualMachineRepository.save(vm);
		pod = podRepository.save(pod);

		virtualMachineAndPod.setVirtualMachine(vm);
		virtualMachineAndPod.setPod(pod);

		virtualMachineAndPod = virtualMachineAndPodRepository.save(virtualMachineAndPod);

		return virtualMachineAndPod;
	}

	@Transactional(readOnly = true)
	public ArrayList<VirtualMachineAndPod> postVirtualMachineAndPodList(ArrayList<VirtualMachineAndPod> relations){
		ArrayList<VirtualMachineAndPod> result = new ArrayList<>();
		for(VirtualMachineAndPod relation : relations){
			VirtualMachineAndPod savedRelation = postVirtualMachineAndPod(relation);
			result.add(savedRelation);
		}
		return result;
	}


}
