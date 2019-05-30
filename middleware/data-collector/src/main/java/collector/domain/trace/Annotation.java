package collector.domain.trace;

public class Annotation {

    private String timestamp;

    private String value;

    private EndPoint endpoint;

    public Annotation() {
        //Empty Constructor
    }


    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public EndPoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(EndPoint endpoint) {
        this.endpoint = endpoint;
    }
}