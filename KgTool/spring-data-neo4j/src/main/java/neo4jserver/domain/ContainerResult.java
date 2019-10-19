package neo4jserver.domain;

import neo4jserver.domain.entities.Container;
import org.springframework.data.neo4j.annotation.QueryResult;
import java.util.List;

@QueryResult
public class ContainerResult {

    public Container container;

    public List<String> labels;

}
