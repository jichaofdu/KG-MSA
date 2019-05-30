package collector.domain.trace;

import java.util.ArrayList;

public class Span {

    private String traceId;

    private String id;

    private String name;

    private String parentId;

    private String timestamp;

    private String duration;

    private ArrayList<Annotation> annotations;

    private ArrayList<BinaryAnnotation> binaryAnnotations;

    public Span() {
        //Empty Constructor
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public ArrayList<Annotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(ArrayList<Annotation> annotations) {
        this.annotations = annotations;
    }

    public ArrayList<BinaryAnnotation> getBinaryAnnotations() {
        return binaryAnnotations;
    }

    public void setBinaryAnnotations(ArrayList<BinaryAnnotation> binaryAnnotations) {
        this.binaryAnnotations = binaryAnnotations;
    }
}
