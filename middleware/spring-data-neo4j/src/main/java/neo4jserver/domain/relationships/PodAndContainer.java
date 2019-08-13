package neo4jserver.domain.relationships;

import neo4jserver.domain.entities.Container;
import neo4jserver.domain.entities.Pod;
import org.neo4j.ogm.annotation.*;

import java.util.Objects;

@RelationshipEntity(type = "PodAndContainer")
public class PodAndContainer {

    @Id
    private String id;

    @StartNode
    private Container container;

    @EndNode
    private Pod pod;

    @Property(name="relation")
    private String relation;

    @Property(name="className")
    private String className = this.getClass().toString();

    public PodAndContainer() {
    }

    public PodAndContainer(Container container, Pod pod, String relation) {
        this.container = container;
        this.pod = pod;
        this.relation = relation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        if (!(o instanceof PodAndContainer)) return false;
        PodAndContainer that = (PodAndContainer) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(container, that.container) &&
                Objects.equals(pod, that.pod) &&
                Objects.equals(relation, that.relation) &&
                Objects.equals(className, that.className);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, container, pod, relation, className);
    }
}
