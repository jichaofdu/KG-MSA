package neo4jserver.repositories;

import neo4jserver.domain.entities.PodMetric;
import neo4jserver.domain.entities.ServiceApiMetric;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import java.util.ArrayList;
import java.util.Optional;

public interface MetricOfPodRepository extends Neo4jRepository<PodMetric,Long>{

    Optional<PodMetric> findById(String id);

    @Query("MATCH (n:PodMetric) where n.name={0} RETURN n")
    Optional<ServiceApiMetric> findByName(String name);

    @Query("MATCH (n:PodMetric) return n")
    ArrayList<ServiceApiMetric> findAllMetrics();


}
