package graphapp.domain.relationships;

import graphapp.domain.entities.Pod;
import graphapp.domain.entities.PodMetric;
import org.neo4j.ogm.annotation.*;

import java.util.Objects;

@RelationshipEntity(type = "PodAndMetric")
public class PodAndMetric extends BasicRelationship  {

    @StartNode
    private PodMetric podMetric;

    @EndNode
    private Pod pod;

    public PodAndMetric() {
        super();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PodAndMetric)) return false;
        if (!super.equals(o)) return false;
        PodAndMetric that = (PodAndMetric) o;
        return Objects.equals(podMetric, that.podMetric) &&
                Objects.equals(pod, that.pod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), podMetric, pod);
    }
}
