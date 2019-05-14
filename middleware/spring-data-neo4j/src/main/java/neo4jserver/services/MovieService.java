package neo4jserver.services;

import java.util.*;
import neo4jserver.domain.*;
import neo4jserver.domain.entities.*;
import neo4jserver.domain.relationships.AppServiceAndPod;
import neo4jserver.domain.relationships.PodAndContainer;
import neo4jserver.domain.relationships.VirtualMachineAndPod;
import neo4jserver.repositories.*;
import neo4jserver.utils.Neo4jUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovieService {

//	@Autowired
//	private Neo4jUtil neo4jUtil;

	private final ContainerRepository containerRepository;

	private final AppServiceRepository appServiceRepository;

	private final PodRepository podRepository;

	private final VirtualMachineRepository virtualMachineRepository;

	private final VirtualMachineAndPodRepository virtualMachineAndPodRepository;

	private final AppServiceAndPodRepository appServiceAndPodRepository;

	private final PodAndContainerRepository podAndContainerRepository;

	public MovieService(PodRepository podRepository,
						ContainerRepository containerRepository,
						AppServiceRepository appServiceRepository,
						VirtualMachineRepository virtualMachineRepository,
						VirtualMachineAndPodRepository virtualMachineAndPodRepository,
						AppServiceAndPodRepository appServiceAndPodRepository,
						PodAndContainerRepository podAndContainerRepository) {
		this.podRepository = podRepository;
		this.virtualMachineRepository = virtualMachineRepository;
		this.virtualMachineAndPodRepository = virtualMachineAndPodRepository;
		this.containerRepository = containerRepository;
		this.appServiceRepository = appServiceRepository;
		this.appServiceAndPodRepository = appServiceAndPodRepository;
		this.podAndContainerRepository = podAndContainerRepository;
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
