package graphapp.domain.entities;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label="Metric")
public class Metric extends BasicMetric{

    public Metric() {
        super();
    }

}
