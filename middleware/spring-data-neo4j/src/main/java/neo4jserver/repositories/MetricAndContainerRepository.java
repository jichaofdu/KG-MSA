package neo4jserver.repositories;

import neo4jserver.domain.relationships.MetricAndContainer;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import java.util.ArrayList;
import java.util.Optional;

public interface MetricAndContainerRepository extends Neo4jRepository<MetricAndContainer,Long> {

    Optional<MetricAndContainer> findById(String id);

    @Query("MATCH p=()-[r:MetricAndContainer]->() RETURN p")
    ArrayList<MetricAndContainer> findAllPodAndContainer();
}
