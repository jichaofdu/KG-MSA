package collector.domain.relationships;

import collector.domain.entities.Container;
import collector.domain.entities.Pod;

public class PodAndContainer {

    private String id;

    private Container container;

    private Pod pod;

    private String relation;

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
}
