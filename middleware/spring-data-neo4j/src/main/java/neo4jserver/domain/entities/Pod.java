package neo4jserver.domain.entities;

import org.neo4j.ogm.annotation.*;

@NodeEntity(label="Pod")
public class Pod extends GraphNode{

    @Property(name="namespace")
    private String namespace;

    @Property(name="selflink")
    private String selflink;

    @Property(name="restartPolicy")
    private String restartPolicy;

    @Property(name="dnsPolicy")
    private String dnsPolicy;

    @Property(name="terminationGracePeriodSeconds")
    private String terminationGracePeriodSeconds;

    @Property(name="phase")
    private String phase;

    @Property(name="podIP")
    private String podIP;

    @Property(name="qosClass")
    private String qosClass;

    public Pod() {
        super();
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getSelflink() {
        return selflink;
    }

    public void setSelflink(String selflink) {
        this.selflink = selflink;
    }

    public String getRestartPolicy() {
        return restartPolicy;
    }

    public void setRestartPolicy(String restartPolicy) {
        this.restartPolicy = restartPolicy;
    }

    public String getDnsPolicy() {
        return dnsPolicy;
    }

    public void setDnsPolicy(String dnsPolicy) {
        this.dnsPolicy = dnsPolicy;
    }

    public String getTerminationGracePeriodSeconds() {
        return terminationGracePeriodSeconds;
    }

    public void setTerminationGracePeriodSeconds(String terminationGracePeriodSeconds) {
        this.terminationGracePeriodSeconds = terminationGracePeriodSeconds;
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
