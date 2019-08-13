package neo4jserver.domain.entities;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import java.util.Objects;

@NodeEntity(label="Container")
public class Container extends GraphNode {

    @Property(name="image")
    private String image;

    @Property(name="command")
    private String command;

    @Property(name="created")
    private String created;

    @Property(name="state")
    private String state;

    @Property(name="status")
    private String status;

    @Property(name="abnormality")
    private double abnormality;

    public Container() {
        super();
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getAbnormality() {
        return abnormality;
    }

    public void setAbnormality(double abnormality) {
        this.abnormality = abnormality;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Container)) return false;
        if (!super.equals(o)) return false;
        Container container = (Container) o;
        return Double.compare(container.abnormality, abnormality) == 0 &&
                Objects.equals(image, container.image) &&
                Objects.equals(command, container.command) &&
                Objects.equals(created, container.created) &&
                Objects.equals(state, container.state) &&
                Objects.equals(status, container.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), image, command, created, state, status, abnormality);
    }
}
