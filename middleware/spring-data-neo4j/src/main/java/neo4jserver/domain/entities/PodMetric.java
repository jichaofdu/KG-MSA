package neo4jserver.domain.entities;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label="PodMetric")
public class PodMetric extends BasicMetric {

    public PodMetric() {
        super();
    }



}
