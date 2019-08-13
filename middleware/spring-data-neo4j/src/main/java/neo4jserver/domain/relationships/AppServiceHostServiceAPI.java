package neo4jserver.domain.relationships;


import neo4jserver.domain.entities.AppService;
import neo4jserver.domain.entities.ServiceAPI;
import org.neo4j.ogm.annotation.*;

import java.util.Objects;

@RelationshipEntity(type = "AppServiceHostServiceAPI")
public class AppServiceHostServiceAPI {

    @Id
    private String id;

    @EndNode
    private AppService appService;

    @StartNode
    private ServiceAPI serviceAPI;

    @Property(name="relation")
    private String relation;

    @Property(name="className")
    private String className = this.getClass().toString();

    public AppServiceHostServiceAPI() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppServiceHostServiceAPI)) return false;
        AppServiceHostServiceAPI that = (AppServiceHostServiceAPI) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(appService, that.appService) &&
                Objects.equals(serviceAPI, that.serviceAPI) &&
                Objects.equals(relation, that.relation) &&
                Objects.equals(className, that.className);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, appService, serviceAPI, relation, className);
    }
}
