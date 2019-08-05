package neo4jserver.repositories;

import neo4jserver.domain.relationships.PodAndMetric;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import java.util.ArrayList;
import java.util.Optional;

public interface MetricAndPodRepository extends Neo4jRepository<PodAndMetric, Long> {

    Optional<PodAndMetric> findById(String id);

    @Query("MATCH p=()-[r:PodAndMetric]->() RETURN p")
    ArrayList<PodAndMetric> findAllPodAndContainer();

}
