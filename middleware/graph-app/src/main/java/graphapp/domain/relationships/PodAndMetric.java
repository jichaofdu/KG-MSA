package graphapp.domain.relationships;

import graphapp.domain.entities.Pod;
import graphapp.domain.entities.PodMetric;
import org.neo4j.ogm.annotation.*;

import java.util.Objects;

@RelationshipEntity(type = "PodAndMetric")
public class PodAndMetric {

    @Id
    private String id;

    @StartNode
    private PodMetric podMetric;

    @EndNode
    private Pod pod;

    @Property(name="relation")
    private String relation;

    @Property(name="className")
    private String className = this.getClass().toString();

    public PodAndMetric() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PodMetric getPodMetric() {
        return podMetric;
    }

    public void setPodMetric(PodMetric podMetric) {
        this.podMetric = podMetric;
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
        if (!(o instanceof PodAndMetric)) return false;
        PodAndMetric that = (PodAndMetric) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(podMetric, that.podMetric) &&
                Objects.equals(pod, that.pod) &&
                Objects.equals(relation, that.relation) &&
                Objects.equals(className, that.className);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, podMetric, pod, relation, className);
    }
}
