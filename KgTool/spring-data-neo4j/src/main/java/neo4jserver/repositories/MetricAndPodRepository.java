package neo4jserver.repositories;

import neo4jserver.domain.relationships.PodAndMetric;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import java.util.Optional;

public interface MetricAndPodRepository extends Neo4jRepository<PodAndMetric, Long> {

    @Query("MATCH p=()-[r:PodAndMetric]->() WHERE r.id={0} RETURN p")
    Optional<PodAndMetric> findById(String id);

}
