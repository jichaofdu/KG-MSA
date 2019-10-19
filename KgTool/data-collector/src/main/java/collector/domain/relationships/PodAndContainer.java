package collector.domain.relationships;

import collector.domain.entities.Container;
import collector.domain.entities.Pod;
import java.util.Objects;

public class PodAndContainer extends BasicRelationship {

    private Container container;

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
