package collector.domain.relationships;

import collector.domain.entities.AppService;
import collector.domain.entities.Pod;
import java.util.Objects;

public class AppServiceAndPod extends BasicRelationship {

    private Pod pod;

    private AppService appService;

    public AppServiceAndPod() {
        super();
    }

    public Pod getPod() {
        return pod;
    }

    public void setPod(Pod pod) {
        this.pod = pod;
    }

    public AppService getAppService() {
        return appService;
    }

    public void setAppService(AppService appService) {
        this.appService = appService;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppServiceAndPod)) return false;
        if (!super.equals(o)) return false;
        AppServiceAndPod that = (AppServiceAndPod) o;
        return Objects.equals(pod, that.pod) &&
                Objects.equals(appService, that.appService);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), pod, appService);
    }
}

