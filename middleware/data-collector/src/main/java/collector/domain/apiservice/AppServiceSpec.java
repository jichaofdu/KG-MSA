package collector.domain.apiservice;

import java.util.ArrayList;
import java.util.HashMap;

public class AppServiceSpec {

    private HashMap<String,String> selector;

    private ArrayList<AppServicePort> ports;

    private String clusterIP;

    private String type;

    public AppServiceSpec() {
    }

    public HashMap<String, String> getSelector() {
        return selector;
    }

    public void setSelector(HashMap<String, String> selector) {
        this.selector = selector;
    }

    public ArrayList<AppServicePort> getPorts() {
        return ports;
    }

    public void setPorts(ArrayList<AppServicePort> ports) {
        this.ports = ports;
    }

    public String getClusterIP() {
        return clusterIP;
    }

    public void setClusterIP(String clusterIP) {
        this.clusterIP = clusterIP;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
