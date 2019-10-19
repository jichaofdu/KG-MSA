package neo4jserver.repositories;

import neo4jserver.domain.MetricResult;
import neo4jserver.domain.entities.Metric;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import java.util.ArrayList;
import java.util.Optional;

public interface MetricRepository extends Neo4jRepository<Metric, Long> {

    Optional<Metric> findById(String id);

    @Query("MATCH (n:Metric) where n.name={0} RETURN n")
    Optional<Metric> findByName(String name);


    @Query("MATCH (n:Metric) where id(n)={0} return labels(n) as labels, n as node")
    MetricResult getContainerWithLabels(Long id);

    @Query("MATCH (n:Metric) return n")
    ArrayList<Metric> findAllMetrics();

}
