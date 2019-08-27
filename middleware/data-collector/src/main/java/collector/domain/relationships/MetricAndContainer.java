package collector.domain.relationships;

import collector.domain.entities.Container;
import collector.domain.entities.Metric;

public class MetricAndContainer extends BasicRelationship {

    private String id;

    private Metric metric;

    private Container container;

    private String relation;

    private String className = this.getClass().toString();

    public MetricAndContainer() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Metric getMetric() {
        return metric;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
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
