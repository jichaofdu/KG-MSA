package collector.domain.relationships;

import collector.domain.entities.Pod;
import collector.domain.entities.PodMetric;
import java.util.Objects;

public class PodAndMetric extends BasicRelationship  {

    private PodMetric podMetric;

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
