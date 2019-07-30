package neo4jserver.repositories;

import neo4jserver.domain.entities.ServiceApiMetric;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import java.util.ArrayList;
import java.util.Optional;

public interface MetricOfServiceApiRepository extends Neo4jRepository<ServiceApiMetric, Long> {

    Optional<ServiceApiMetric> findById(String id);

    @Query("MATCH (n:Metric) where n.name={0} RETURN n")
    Optional<ServiceApiMetric> findByName(String name);

    @Query("MATCH (n:Metric) return n")
    ArrayList<ServiceApiMetric> findAllMetrics();

}
