package neo4jserver.domain.entities;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@NodeEntity(label="Container")
public class Container extends GraphNode {

    @Property(name="image")
    private String image;

    public Container() {
        super();
    }

    public Container(String name, String image) {
        super(name);
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
