package collector.domain.relationships;

import collector.domain.entities.AppService;
import collector.domain.entities.ServiceAPI;
import java.util.Objects;

public class AppServiceInvokeServiceAPI extends BasicRelationship {

    private AppService appService;

    private ServiceAPI serviceAPI;

    private int count;

    public AppServiceInvokeServiceAPI() {
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppServiceInvokeServiceAPI)) return false;
        if (!super.equals(o)) return false;
        AppServiceInvokeServiceAPI that = (AppServiceInvokeServiceAPI) o;
        return count == that.count &&
                Objects.equals(appService, that.appService) &&
                Objects.equals(serviceAPI, that.serviceAPI);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), appService, serviceAPI, count);
    }
}
