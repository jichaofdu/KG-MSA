package collector.domain.relationships;

import collector.domain.entities.AppService;
import collector.domain.entities.ServiceAPI;
import java.util.Objects;

public class AppServiceHostServiceAPI extends BasicRelationship {

    private AppService appService;

    private ServiceAPI serviceAPI;

    public AppServiceHostServiceAPI() {
        super();
    }

    public AppService getAppService() {
        return appService;
    }

    public void setAppService(AppService appService) {
        this.appService = appService;
    }

    public ServiceAPI getServiceAPI() {
        return serviceAPI;
    }

    public void setServiceAPI(ServiceAPI serviceAPI) {
        this.serviceAPI = serviceAPI;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppServiceHostServiceAPI)) return false;
        if (!super.equals(o)) return false;
        AppServiceHostServiceAPI that = (AppServiceHostServiceAPI) o;
        return Objects.equals(appService, that.appService) &&
                Objects.equals(serviceAPI, that.serviceAPI);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), appService, serviceAPI);
    }
}
