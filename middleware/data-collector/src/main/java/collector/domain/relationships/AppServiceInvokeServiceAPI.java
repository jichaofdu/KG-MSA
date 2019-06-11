package collector.domain.relationships;

import collector.domain.entities.AppService;
import collector.domain.entities.ServiceAPI;

public class AppServiceInvokeServiceAPI {

    private String id;

    private AppService appService;

    private ServiceAPI serviceAPI;

    private String relation;

    private int count;

    private String className = this.getClass().toString();

    public AppServiceInvokeServiceAPI() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AppService getAppService() {
        return appService;
    }

    public void setAppService(AppService appService) {
        this.appService = appService;
    }

    public ServiceAPI getServiceAPI() {
        return serviceAPI;
    }

    public void setServiceAPI(ServiceAPI serviceAPI) {
        this.serviceAPI = serviceAPI;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
