package neo4jserver.domain;

import neo4jserver.domain.entities.AppService;
import org.springframework.data.neo4j.annotation.QueryResult;
import java.util.List;

@QueryResult
public class AppServiceResult {

    public AppService appService;

    public List<String> labels;

}
