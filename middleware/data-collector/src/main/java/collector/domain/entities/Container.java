package collector.domain.entities;

public class Container extends GraphNode {

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
