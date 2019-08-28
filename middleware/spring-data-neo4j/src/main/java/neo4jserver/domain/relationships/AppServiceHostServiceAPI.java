package neo4jserver.domain.relationships;

import neo4jserver.domain.entities.AppService;
import neo4jserver.domain.entities.ServiceAPI;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.util.Objects;

@RelationshipEntity(type = "AppServiceHostServiceAPI")
public class AppServiceHostServiceAPI extends BasicRelationship {

    @EndNode
    private AppService appService;

    @StartNode
    private ServiceAPI serviceAPI;

    public AppServiceHostServiceAPI() {
        super();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppServiceHostServiceAPI)) return false;
        if (!super.equals(o)) return false;
        AppServiceHostServiceAPI that = (AppServiceHostServiceAPI) o;
        return Objects.equals(appService, that.appService) &&
                Objects.equals(serviceAPI, that.serviceAPI);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), appService, serviceAPI);
    }
}
