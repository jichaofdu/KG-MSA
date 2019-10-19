package collector.domain.relationships;

import collector.domain.entities.Pod;
import collector.domain.entities.ServiceAPI;
import java.util.HashSet;
import java.util.Objects;

//TraceInvokeApiToPod和TraceInvokePodToApi的区别在于
// Pod A -> Api -> PodB
// TraceInvokeApiToPod 负责从Api到Pod-B的一段
// TraceInvokePodToApi 负责Pod-A到API的一段
public class TraceInvokeApiToPod extends BasicRelationship  {

    private ServiceAPI serviceAPI;

    private Pod pod;

    private HashSet<String> traceIdAndSpanIds;

    public TraceInvokeApiToPod() {
        super();
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
        if (!super.equals(o)) return false;
        TraceInvokeApiToPod that = (TraceInvokeApiToPod) o;
        return Objects.equals(serviceAPI, that.serviceAPI) &&
                Objects.equals(pod, that.pod) &&
                Objects.equals(traceIdAndSpanIds, that.traceIdAndSpanIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), serviceAPI, pod, traceIdAndSpanIds);
    }
}
