package graphapp.domain.relationships;

import graphapp.domain.entities.AppService;
import graphapp.domain.entities.Pod;
import org.neo4j.ogm.annotation.*;
import java.util.Objects;


@RelationshipEntity(type = "AppServiceAndPod")
public class AppServiceAndPod extends BasicRelationship {

    @StartNode
    private Pod pod;

    @EndNode
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

