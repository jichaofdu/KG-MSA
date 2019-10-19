package neo4jserver.repositories;

import neo4jserver.domain.entities.PodMetric;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import java.util.Optional;

public interface MetricOfPodRepository extends Neo4jRepository<PodMetric,Long>{

    @Query("MATCH (n:PodMetric) where n.id={0} RETURN n")
    Optional<PodMetric> findById(String id);

}
