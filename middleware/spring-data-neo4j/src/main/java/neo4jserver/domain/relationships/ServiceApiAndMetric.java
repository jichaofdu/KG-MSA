package neo4jserver.domain.relationships;

import neo4jserver.domain.entities.ServiceAPI;
import neo4jserver.domain.entities.ServiceApiMetric;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.util.Objects;

@RelationshipEntity(type = "ServiceApiAndMetric")
public class ServiceApiAndMetric extends BasicRelationship  {

    @StartNode
    private ServiceApiMetric apiMetric;

    @EndNode
    private ServiceAPI serviceAPI;

    public ServiceApiAndMetric() {
        super();
    }

    public ServiceApiMetric getApiMetric() {
        return apiMetric;
    }

    public void setApiMetric(ServiceApiMetric apiMetric) {
        this.apiMetric = apiMetric;
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
        if (!(o instanceof ServiceApiAndMetric)) return false;
        if (!super.equals(o)) return false;
        ServiceApiAndMetric that = (ServiceApiAndMetric) o;
        return Objects.equals(apiMetric, that.apiMetric) &&
                Objects.equals(serviceAPI, that.serviceAPI);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), apiMetric, serviceAPI);
    }
}
