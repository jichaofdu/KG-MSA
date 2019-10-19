package neo4jserver.repositories;

import neo4jserver.domain.relationships.ServiceApiAndMetric;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import java.util.ArrayList;
import java.util.Optional;

public interface MetricAndServiceApiRepository extends Neo4jRepository<ServiceApiAndMetric,Long> {

    Optional<ServiceApiAndMetric> findById(String id);

    @Query("MATCH p=()-[r:ServiceApiAndMetric]->() RETURN p")
    ArrayList<ServiceApiAndMetric> findAllPodAndContainer();
}
