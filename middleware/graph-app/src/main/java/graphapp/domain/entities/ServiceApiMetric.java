package graphapp.domain.entities;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label="ServiceApiMetric")
public class ServiceApiMetric extends BasicMetric {

    public ServiceApiMetric() {
        super();
    }

}
