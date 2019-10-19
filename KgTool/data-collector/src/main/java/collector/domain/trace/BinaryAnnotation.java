package collector.domain.trace;


public class BinaryAnnotation {

    private String key;

    private String value;

    private EndPoint endpoint;

    public BinaryAnnotation() {
        //Empty Constructor
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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