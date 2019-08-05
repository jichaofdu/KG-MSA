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

	private static final int METRIC_MAX_TIME_WINDOW_SIZE = 200;

//	@Autowired
//	private Neo4jUtil neo4jUtil;

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
						MetricAndServiceApiRepository metricAndServiceApiRepository) {
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
	}

	@Transactional(readOnly = true)
	public ArrayList<ServiceApiAndMetric> postMetricsOfServiceApi(ArrayList<ServiceApiAndMetric> relations){

		ArrayList<ServiceApiAndMetric> result = new ArrayList<>();
		for(ServiceApiAndMetric relation : relations){
			ServiceApiMetric metric = relation.getApiMetric();
			System.out.println("上传ServiceApiMetric=================");
			Optional<ServiceApiMetric> metricOptional = metricOfServiceApiRepository.findById(metric.getId());
			if(metricOptional.isPresent()){
				ServiceApiMetric oldMetric = metricOptional.get();
				oldMetric.getValues().addAll(metric.getValues());
				relation.setApiMetric(oldMetric);
			}

			ServiceApiAndMetric savedRelation = metricAndServiceApiRepository.save(relation);
			result.add(savedRelation);
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

			if(savedRelationOptional.isPresent()){
				TraceInvokeApiToPod savedRelation = savedRelationOptional.get();
				savedRelation.getTraceIdAndSpanIds().addAll(relation.getTraceIdAndSpanIds());
				relation = savedRelation;
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
			if(savedRelationOptional.isPresent()){
				TraceInvokePodToApi savedRelation = savedRelationOptional.get();
				savedRelation.getTraceIdAndSpanIds().addAll(relation.getTraceIdAndSpanIds());
				relation = savedRelation;
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
			metricOldEntity.setTime(metric.getTime());
			metricOldEntity.setValue(metric.getValue());
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


//	@Transactional(readOnly = true)
//	public VirtualMachineAndPod saveDeploy(){
//		VirtualMachine vm = new VirtualMachine("1-vm1",1024,2.5);
//
//		Pod pod = new Pod("1-pod1",5);
//
//		VirtualMachine vm2 = new VirtualMachine("1-vm2",1024,2.5);
//
//		vm = virtualMachineRepository.save(vm);
//		vm2 = virtualMachineRepository.save(vm2);
//		pod = podRepository.save(pod);
//
//		VirtualMachineAndPod virtualMachineAndPod = new VirtualMachineAndPod(pod,"1-virtualMachineAndPod",vm);
//		virtualMachineAndPod = virtualMachineAndPodRepository.save(virtualMachineAndPod);
//
//		VirtualMachineAndPod virtualMachineAndPod2 = new VirtualMachineAndPod(pod,"1-virtualMachineAndPod2",vm2);
//		virtualMachineAndPod2 = virtualMachineAndPodRepository.save(virtualMachineAndPod2);
//
//
//		System.out.println("========VirtualMachineAndPod ID:" + virtualMachineAndPod.getId() + "=====");
//		System.out.println("========VirtualMachineAndPod ID:" + virtualMachineAndPod2.getId() + "=====");
//		return virtualMachineAndPod;
//	}
//
//	@Transactional(readOnly = true)
//	public Map<String, Object> getShortPath(){
//		Map<String, Object> retMap = new HashMap<>();
//		//cql语句
//		String cql = "match l=shortestPath(({name:'1-vm1'})-[*]-({name:'1-vm2'})) return l";
//		//待返回的值，与cql return后的值顺序对应
//		Set<Map<String ,Object>> nodeList = new HashSet<>();
//		Set<Map<String ,Object>> edgeList = new HashSet<>();
//		neo4jUtil.getPathList(cql,nodeList,edgeList);
//		System.out.println("=======");
//
//		retMap.put("edgeList",edgeList);
//
//		Set<GraphNode> retNodeSet = new HashSet<>();
//		for(Map<String ,Object> tempMap: nodeList){
//			String fullClassName = (String)tempMap.get("className");
//			System.out.println("=====" + fullClassName);
//			GraphNode temp = MapToObjUtil.toGraphNodeBean(fullClassName, tempMap);
//			retNodeSet.add(temp);
//			System.out.println("ResultClass" + temp.getClass().toString());
//		}
//		retMap.put("nodeList",retNodeSet);
//
//		return retMap;
//	}

}
