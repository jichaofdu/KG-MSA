package graphapp.repositories;

import graphapp.domain.entities.PodMetric;
import graphapp.domain.entities.ServiceApiMetric;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface MetricOfPodRepository extends Neo4jRepository<PodMetric,Long>{

    @Query("MATCH (n:PodMetric) where n.id={0} RETURN n")
    Optional<PodMetric> findById(String id);

    @Query("MATCH (n:PodMetric) return n")
    ArrayList<PodMetric> findAllMetrics();

}
