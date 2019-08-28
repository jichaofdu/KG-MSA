package graphapp.domain.relationships;

import graphapp.domain.entities.Pod;
import graphapp.domain.entities.ServiceAPI;
import org.neo4j.ogm.annotation.*;

import java.util.HashSet;
import java.util.Objects;

@RelationshipEntity(type = "TraceInvokePodToApi")
public class TraceInvokePodToApi extends BasicRelationship  {

    @StartNode
    private Pod pod;

    @EndNode
    private ServiceAPI serviceAPI;

    @Property(name="traceIdSpanId")
    private HashSet<String> traceIdAndSpanIds;

    public TraceInvokePodToApi() {
        super();
    }

    public Pod getPod() {
        return pod;
    }

    public void setPod(Pod pod) {
        this.pod = pod;
    }

    public ServiceAPI getServiceAPI() {
        return serviceAPI;
    }

    public void setServiceAPI(ServiceAPI serviceAPI) {
        this.serviceAPI = serviceAPI;
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
        if (!(o instanceof TraceInvokePodToApi)) return false;
        if (!super.equals(o)) return false;
        TraceInvokePodToApi that = (TraceInvokePodToApi) o;
        return Objects.equals(pod, that.pod) &&
                Objects.equals(serviceAPI, that.serviceAPI) &&
                Objects.equals(traceIdAndSpanIds, that.traceIdAndSpanIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), pod, serviceAPI, traceIdAndSpanIds);
    }
}
