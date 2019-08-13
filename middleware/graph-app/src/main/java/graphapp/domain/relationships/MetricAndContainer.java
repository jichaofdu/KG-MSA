package graphapp.domain.relationships;

import graphapp.domain.entities.Container;
import graphapp.domain.entities.Metric;
import org.neo4j.ogm.annotation.*;

import java.util.Objects;

@RelationshipEntity(type = "MetricAndContainer")
public class MetricAndContainer {

    @Id
    private String id;

    @StartNode
    private Metric metric;

    @EndNode
    private Container container;

    @Property(name="relation")
    private String relation;

    @Property(name="className")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetricAndContainer)) return false;
        MetricAndContainer that = (MetricAndContainer) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(metric, that.metric) &&
                Objects.equals(container, that.container) &&
                Objects.equals(relation, that.relation) &&
                Objects.equals(className, that.className);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, metric, container, relation, className);
    }
}
