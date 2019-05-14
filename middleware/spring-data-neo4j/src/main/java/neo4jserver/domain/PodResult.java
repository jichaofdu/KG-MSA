package neo4jserver.domain;

import neo4jserver.domain.entities.Pod;
import org.springframework.data.neo4j.annotation.QueryResult;
import java.util.List;

@QueryResult
public class PodResult {

    public Pod node;

    public List<String> labels;

}
