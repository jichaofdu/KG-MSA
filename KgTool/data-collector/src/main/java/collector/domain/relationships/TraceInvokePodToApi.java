package collector.domain.relationships;

import collector.domain.entities.Pod;
import collector.domain.entities.ServiceAPI;
import java.util.HashSet;
import java.util.Objects;

public class TraceInvokePodToApi extends BasicRelationship  {

    private Pod pod;

    private ServiceAPI serviceAPI;

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
