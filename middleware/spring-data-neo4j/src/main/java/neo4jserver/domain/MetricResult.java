package neo4jserver.domain;

import neo4jserver.domain.entities.Metric;
import org.springframework.data.neo4j.annotation.QueryResult;
import java.util.List;

@QueryResult
public class MetricResult {

    public Metric metric;

    public List<String> labels;

}
