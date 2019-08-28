package neo4jserver.domain.relationships;

import neo4jserver.domain.entities.Container;
import neo4jserver.domain.entities.Metric;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.util.Objects;

@RelationshipEntity(type = "MetricAndContainer")
public class MetricAndContainer extends BasicRelationship {

    @StartNode
    private Metric metric;

    @EndNode
    private Container container;

    public MetricAndContainer() {
        super();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetricAndContainer)) return false;
        if (!super.equals(o)) return false;
        MetricAndContainer that = (MetricAndContainer) o;
        return Objects.equals(metric, that.metric) &&
                Objects.equals(container, that.container);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), metric, container);
    }
}
