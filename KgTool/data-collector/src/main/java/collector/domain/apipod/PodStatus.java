package collector.domain.apipod;

public class PodStatus {

    private String phase;

    private String podIP;

    private String qosClass;

    public PodStatus() {
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getPodIP() {
        return podIP;
    }

    public void setPodIP(String podIP) {
        this.podIP = podIP;
    }

    public String getQosClass() {
        return qosClass;
    }

    public void setQosClass(String qosClass) {
        this.qosClass = qosClass;
    }
}
