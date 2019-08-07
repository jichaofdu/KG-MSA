package graphapp.domain.relationships;

import graphapp.domain.entities.AppService;
import graphapp.domain.entities.ServiceAPI;
import org.neo4j.ogm.annotation.*;

@RelationshipEntity(type = "AppServiceInvokeServiceAPI")
public class AppServiceInvokeServiceAPI {

    @Id
    private String id;

    @StartNode
    private AppService appService;

    @EndNode
    private ServiceAPI serviceAPI;

    @Property(name="relation")
    private String relation;

    @Property(name="count")
    private int count;

    @Property(name="className")
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
