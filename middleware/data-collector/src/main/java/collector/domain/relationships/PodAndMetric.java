package collector.domain.relationships;

import collector.domain.entities.Pod;
import collector.domain.entities.PodMetric;

public class PodAndMetric {

    private String id;

    private PodMetric podMetric;

    private Pod pod;

    private String relation;

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
