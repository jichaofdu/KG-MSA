package neo4jserver.controller;

import java.util.*;
import neo4jserver.domain.entities.*;
import neo4jserver.domain.relationships.*;
import neo4jserver.services.MovieService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class MovieController {

	private final MovieService movieService;
	
	public MovieController(MovieService movieService) {
		this.movieService = movieService;
	}

	@PostMapping("/serviceApiMetrics")
	public ArrayList<ServiceApiAndMetric> postMetricOfItsServiceApi(@RequestBody ArrayList<ServiceApiAndMetric> relations){
		return movieService.postMetricsOfServiceApi(relations);
	}

	@PostMapping("/traceApiToPod")
	public ArrayList<TraceInvokeApiToPod> postTraceApiToPod(@RequestBody ArrayList<TraceInvokeApiToPod> relations){
		return movieService.postTraceApiToPod(relations);
	}

	@PostMapping("/tracePodToApi")
	public ArrayList<TraceInvokePodToApi> postTracePodToApi(@RequestBody ArrayList<TraceInvokePodToApi> relations){
		return movieService.postTracePodToApi(relations);
	}

	@PostMapping("/apiHostService")
	public ArrayList<AppServiceHostServiceAPI> postAppServiceAndServiceApi(@RequestBody ArrayList<AppServiceHostServiceAPI> relations){
		return movieService.postListOfAppServiceAndServiceAPI(relations);
	}

	@PostMapping("/apiInvokeService")
	public ArrayList<AppServiceInvokeServiceAPI> postAppServiceAndInvokeApi(@RequestBody ArrayList<AppServiceInvokeServiceAPI> relations){
		return movieService.postListOfAppServiceInvokeServiceAPI(relations);
	}

	@PostMapping("/updateMetrics")
	public ArrayList<Metric> updateMetrics(@RequestBody ArrayList<Metric> metrics){
		return movieService.updateMetrics(metrics);
	}

	@GetMapping("/container/{id}")
	public Container getContainer(@PathVariable String id){
		return movieService.findByContainerId(id);
	}

	@PostMapping("/container")
	public Container postContainer(@RequestBody Container container){
		return movieService.postContainer(container);
	}

	@GetMapping("/containers")
	public ArrayList<Container> getContainers(){
		return movieService.findAllContainers();
	}

	@GetMapping("/appService/{id}")
	public AppService getAppService(@PathVariable String id){
		return movieService.findByAppServiceId(id);
	}

	@PostMapping("/appService")
	public AppService postAppService(@RequestBody AppService appService){
		return movieService.postAppService(appService);
	}

	@GetMapping("/appServices")
	public ArrayList<AppService> getAppServices(){
		return movieService.findAllAppServices();
	}

	@GetMapping("/pod/{id}")
	public Pod getPod(@PathVariable String id){
		return movieService.findByPodId(id);
	}

	@PostMapping("/pod")
	public Pod postPod(@RequestBody Pod pod){
		return movieService.postPod(pod);
	}

	@GetMapping("/pods")
	public ArrayList<Pod> getPods(){
		return movieService.findAllPods();
	}

	@GetMapping("/virtualMachine/{id}")
	public VirtualMachine getVirtualMachine(@PathVariable String id){
		return movieService.findByVMId(id);
	}

	@PostMapping("/virtualMachine")
	public VirtualMachine postVirtualMachine(@RequestBody VirtualMachine vm){
		return movieService.postVirtualMachine(vm);
	}

	@GetMapping("/virtualMachines")
	public ArrayList<VirtualMachine> getVirtualMachines(){
		return movieService.findAllVms();
	}

	@GetMapping("/appServiceAndPod/{id}")
	public AppServiceAndPod getAppServiceAndPod(@PathVariable String id){
		return movieService.findByAppServiceAndPodId(id);
	}

	@PostMapping("/appServiceAndPod")
	public AppServiceAndPod postAppServiceAndPod(@RequestBody AppServiceAndPod appServiceAndPod){
		return movieService.postAppServiceAndPod(appServiceAndPod);
	}

	@PostMapping("/appServiceAndPodRelations")
	public ArrayList<AppServiceAndPod> postAppServiceAndPodList(@RequestBody ArrayList<AppServiceAndPod>
																	  relations){
		return movieService.postAppServiceAndPodList(relations);
	}

	@GetMapping("/podAndContainer/{id}")
	public PodAndContainer getPodAndContainer(@PathVariable String id){
		return movieService.findByPodAndContainerId(id);
	}

	@PostMapping("/podAndContainer")
	public PodAndContainer postPodAndContainer(@RequestBody PodAndContainer podAndContainer){
		return movieService.postPodAndContainer(podAndContainer);
	}

	@PostMapping("/podAndContainerRelations")
	public ArrayList<PodAndContainer> postPodAndContainerList(@RequestBody ArrayList<PodAndContainer>
																				relations){
		return movieService.postPodAndContainerList(relations);
	}

	@GetMapping("/virtualMachineAndPod/{id}")
	public VirtualMachineAndPod getVirtualMachineAndPod(@PathVariable String id){
		return movieService.findByVirtualMachineAndPodId(id);
	}

	@PostMapping("/virtualMachineAndPod")
	public VirtualMachineAndPod postVirtualMachineAndPod(@RequestBody VirtualMachineAndPod virtualMachineAndPod){
		return movieService.postVirtualMachineAndPod(virtualMachineAndPod);
	}

	@PostMapping("/virtualMachineAndPodRelations")
	public ArrayList<VirtualMachineAndPod> postVirtualMachineAndPodList(@RequestBody ArrayList<VirtualMachineAndPod>
																					relations){
		return movieService.postVirtualMachineAndPodList(relations);
	}

	@GetMapping("/metricAndContainer/{id}")
	public MetricAndContainer getMetricAndContainer(@PathVariable String id){
		return movieService.findByMetricAndContainerId(id);
	}

	@PostMapping("/metricAndContainer")
	public MetricAndContainer postMetricAndContainer(@RequestBody MetricAndContainer metricAndContainer){
		return movieService.postMetricAndContainer(metricAndContainer);
	}

	@PostMapping("/metricAndContainerRelations")
	public ArrayList<MetricAndContainer> postMetricAndContainer(@RequestBody  ArrayList<MetricAndContainer> relations){
		return movieService.postMetricAndContainerList(relations);
	}


//	@GetMapping("/addDeploy")
//	public VirtualMachineAndPod addDeploy(){
//		return movieService.saveDeploy();
//	}
//
//	@GetMapping("/getShortPath")
//	public Map<String, Object> getShortPath(){
//		return movieService.getShortPath();
//	}

}
