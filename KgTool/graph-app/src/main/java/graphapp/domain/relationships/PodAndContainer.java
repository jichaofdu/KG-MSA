package graphapp.domain.relationships;

import graphapp.domain.entities.Container;
import graphapp.domain.entities.Pod;
import org.neo4j.ogm.annotation.*;

import java.util.Objects;

@RelationshipEntity(type = "PodAndContainer")
public class PodAndContainer extends BasicRelationship {

    @StartNode
    private Container container;

    @EndNode
    private Pod pod;

    public PodAndContainer() {
        super();
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    public Pod getPod() {
        return pod;
    }

    public void setPod(Pod pod) {
        this.pod = pod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PodAndContainer)) return false;
        if (!super.equals(o)) return false;
        PodAndContainer that = (PodAndContainer) o;
        return Objects.equals(container, that.container) &&
                Objects.equals(pod, that.pod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), container, pod);
    }
}
