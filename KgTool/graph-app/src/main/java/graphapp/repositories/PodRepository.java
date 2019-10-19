package graphapp.repositories;

import graphapp.domain.entities.Pod;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import java.util.ArrayList;

public interface PodRepository extends Neo4jRepository<Pod, Long> {

    @Query("MATCH (n:Pod) return n")
    ArrayList<Pod> findAllPods();
}
