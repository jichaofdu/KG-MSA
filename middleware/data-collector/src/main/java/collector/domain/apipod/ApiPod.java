package collector.domain.apipod;

public class ApiPod {

    private PodMetadata metadata;

    private PodSpec spec;

    private PodStatus status;

    public ApiPod() {
    }

    public PodMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(PodMetadata metadata) {
        this.metadata = metadata;
    }

    public PodSpec getSpec() {
        return spec;
    }

    public void setSpec(PodSpec spec) {
        this.spec = spec;
    }

    public PodStatus getStatus() {
        return status;
    }

    public void setStatus(PodStatus status) {
        this.status = status;
    }
}
