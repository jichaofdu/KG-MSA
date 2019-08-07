package graphapp.domain.relationships;

import graphapp.domain.entities.ServiceAPI;
import graphapp.domain.entities.ServiceApiMetric;
import org.neo4j.ogm.annotation.*;

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
}
