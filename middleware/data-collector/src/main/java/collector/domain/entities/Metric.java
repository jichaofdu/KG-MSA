package collector.domain.entities;

public class Metric extends GraphNode{

    private String value;

    private String time;

    public Metric() {
        super();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
