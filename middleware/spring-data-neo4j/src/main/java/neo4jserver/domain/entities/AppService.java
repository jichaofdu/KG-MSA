package neo4jserver.domain.entities;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import java.util.ArrayList;
import java.util.Objects;

@NodeEntity(label="AppService")
public class AppService extends GraphNode {

    @Property(name="selflink")
    private String selflink;

    @Property(name="namespace")
    private String namespace;

    @Property(name="clusterIP")
    private String clusterIP;

    @Property(name="type")
    private String type;

    @Property(name="port")
    private String port;


    public AppService() {
        super();
    }

    public String getSelflink() {
        return selflink;
    }

    public void setSelflink(String selflink) {
        this.selflink = selflink;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
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

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppService)) return false;
        if (!super.equals(o)) return false;
        AppService that = (AppService) o;
        return Objects.equals(selflink, that.selflink) &&
                Objects.equals(namespace, that.namespace) &&
                Objects.equals(clusterIP, that.clusterIP) &&
                Objects.equals(type, that.type) &&
                Objects.equals(port, that.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), selflink, namespace, clusterIP, type, port);
    }
}
