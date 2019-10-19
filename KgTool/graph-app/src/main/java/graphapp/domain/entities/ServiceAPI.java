package graphapp.domain.entities;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import java.util.Objects;

@NodeEntity(label="ServiceAPI")
public class ServiceAPI extends GraphNode {

    //Example "/ticketinfo/queryForStationId" -> name
    //Example "ts-ticketinfo-service" -> hostname
    @Property(name="hostName")
    private String hostName;

    public ServiceAPI() {
        super();
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceAPI)) return false;
        if (!super.equals(o)) return false;
        ServiceAPI api = (ServiceAPI) o;
        return Objects.equals(hostName, api.hostName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), hostName);
    }
}
