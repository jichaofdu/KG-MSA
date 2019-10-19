package neo4jserver.domain.relationships;

import neo4jserver.domain.entities.AppService;
import neo4jserver.domain.entities.ServiceAPI;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.util.Objects;

@RelationshipEntity(type = "AppServiceInvokeServiceAPI")
public class AppServiceInvokeServiceAPI extends BasicRelationship {

    @StartNode
    private AppService appService;

    @EndNode
    private ServiceAPI serviceAPI;

    @Property(name="count")
    private int count;

    public AppServiceInvokeServiceAPI() {
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppServiceInvokeServiceAPI)) return false;
        if (!super.equals(o)) return false;
        AppServiceInvokeServiceAPI that = (AppServiceInvokeServiceAPI) o;
        return count == that.count &&
                Objects.equals(appService, that.appService) &&
                Objects.equals(serviceAPI, that.serviceAPI);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), appService, serviceAPI, count);
    }
}
