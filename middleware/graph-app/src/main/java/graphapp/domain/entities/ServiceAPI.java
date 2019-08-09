package graphapp.domain.entities;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

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

}
