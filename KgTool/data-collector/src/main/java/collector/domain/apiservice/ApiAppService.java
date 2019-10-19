package collector.domain.apiservice;

public class ApiAppService {

    private AppServiceMetadata metadata;

    private AppServiceSpec spec;

    public ApiAppService() {
    }

    public AppServiceMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(AppServiceMetadata metadata) {
        this.metadata = metadata;
    }

    public AppServiceSpec getSpec() {
        return spec;
    }

    public void setSpec(AppServiceSpec spec) {
        this.spec = spec;
    }
}
