package neo4jserver.domain.relationships;

import neo4jserver.domain.entities.Pod;
import neo4jserver.domain.entities.PodMetric;
import org.neo4j.ogm.annotation.*;

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
}
