package collector.domain.apiservice;

import java.util.ArrayList;

public class AppServiceList {

    private ArrayList<ApiAppService> items;

    public AppServiceList() {
    }

    public ArrayList<ApiAppService> getItems() {
        return items;
    }

    public void setItems(ArrayList<ApiAppService> items) {
        this.items = items;
    }
}
