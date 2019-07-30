package collector.domain.relationships;

import collector.domain.entities.ServiceAPI;
import collector.domain.entities.ServiceApiMetric;

public class ServiceApiAndMetric {

    private String id;

    private ServiceApiMetric apiMetric;

    private ServiceAPI serviceAPI;

    private String relation;

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
