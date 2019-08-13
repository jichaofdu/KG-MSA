package neo4jserver.domain.relationships;

import neo4jserver.domain.entities.ServiceAPI;
import neo4jserver.domain.entities.ServiceApiMetric;
import org.neo4j.ogm.annotation.*;

import java.util.Objects;

@RelationshipEntity(type = "ServiceApiAndMetric")
public class ServiceApiAndMetric {

    @Id
    private String id;

    @StartNode
    private ServiceApiMetric apiMetric;

    @EndNode
    private ServiceAPI serviceAPI;

    @Property(name="relation")
    private String relation;

    @Property(name="className")
    private String className = this.getClass().toString();

    public ServiceApiAndMetric() {
    }

    public ServiceApiAndMetric(ServiceApiMetric apiMetric, ServiceAPI serviceAPI, String relation) {
        this.apiMetric = apiMetric;
        this.serviceAPI = serviceAPI;
        this.relation = relation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        if (!(o instanceof ServiceApiAndMetric)) return false;
        ServiceApiAndMetric that = (ServiceApiAndMetric) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(apiMetric, that.apiMetric) &&
                Objects.equals(serviceAPI, that.serviceAPI) &&
                Objects.equals(relation, that.relation) &&
                Objects.equals(className, that.className);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, apiMetric, serviceAPI, relation, className);
    }
}
