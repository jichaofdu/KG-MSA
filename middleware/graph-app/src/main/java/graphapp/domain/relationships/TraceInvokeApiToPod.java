package graphapp.domain.relationships;

import graphapp.domain.entities.Pod;
import graphapp.domain.entities.ServiceAPI;
import org.neo4j.ogm.annotation.*;

import java.util.HashSet;
import java.util.Objects;

//TraceInvokeApiToPod和TraceInvokePodToApi的区别在于
// Pod A -> Api -> PodB
// TraceInvokeApiToPod 负责从Api到Pod-B的一段
// TraceInvokePodToApi 负责Pod-A到API的一段
@RelationshipEntity(type = "TraceInvokeApiToPod")
public class TraceInvokeApiToPod {

    @Id
    private String id;

    @StartNode
    private ServiceAPI serviceAPI;

    @EndNode
    private Pod pod;

    @Property(name="relation")
    private String relation;

    @Property(name="className")
    private String className = this.getClass().toString();

    @Property(name="traceIdSpanId")
    private HashSet<String> traceIdAndSpanIds;

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

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public HashSet<String> getTraceIdAndSpanIds() {
        return traceIdAndSpanIds;
    }

    public void setTraceIdAndSpanIds(HashSet<String> traceIdAndSpanIds) {
        this.traceIdAndSpanIds = traceIdAndSpanIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TraceInvokeApiToPod)) return false;
        TraceInvokeApiToPod that = (TraceInvokeApiToPod) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(serviceAPI, that.serviceAPI) &&
                Objects.equals(pod, that.pod) &&
                Objects.equals(relation, that.relation) &&
                Objects.equals(className, that.className) &&
                Objects.equals(traceIdAndSpanIds, that.traceIdAndSpanIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, serviceAPI, pod, relation, className, traceIdAndSpanIds);
    }
}
