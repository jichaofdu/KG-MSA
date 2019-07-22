package neo4jserver.domain.relationships;

import neo4jserver.domain.entities.Pod;
import neo4jserver.domain.entities.ServiceAPI;
import org.neo4j.ogm.annotation.*;

//TraceInvokeApiToPod和TraceInvokePodToApi的区别在于
// Pod A -> Api -> PodB
// TraceInvokeApiToPod 负责从Api到Pod-B的一段
// TraceInvokePodToApi 负责Pod-A到API的一段
@RelationshipEntity(type = "Traces")
public class TraceInvokeApiToPod {

    @Id
    private String id;

    @StartNode
    private ServiceAPI serviceAPI;

    @EndNode
    private Pod pod;

    @Property(name="relation")
    private String relation;

    @Property(name="traceId")
    private String traceId;

    @Property(name="spanId")
    private String spanId;

    @Property(name="timestamp")
    private String timestamp;

    @Property(name="className")
    private String className = this.getClass().toString();

    public TraceInvokeApiToPod() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ServiceAPI getServiceAPI() {
        return serviceAPI;
    }

    public void setServiceAPI(ServiceAPI serviceAPI) {
        this.serviceAPI = serviceAPI;
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

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
