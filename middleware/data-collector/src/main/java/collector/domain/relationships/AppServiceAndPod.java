package collector.domain.relationships;

import collector.domain.entities.AppService;
import collector.domain.entities.Pod;

public class AppServiceAndPod {

    private String id;

    private Pod pod;

    private AppService appService;

    private String relation;

    private String className = this.getClass().toString();

    public AppServiceAndPod() {
    }

    public AppServiceAndPod(Pod pod, AppService appService, String relation) {
        this.pod = pod;
        this.appService = appService;
        this.relation = relation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
