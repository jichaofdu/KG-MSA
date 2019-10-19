package neo4jserver.repositories;

import neo4jserver.domain.relationships.PodAndContainer;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import java.util.ArrayList;
import java.util.Optional;

public interface PodAndContainerRepository extends Neo4jRepository<PodAndContainer, Long> {

    Optional<PodAndContainer> findById(String id);

    @Query("MATCH p=()-[r:PodAndContainer]->() RETURN p")
    ArrayList<PodAndContainer> findAllPodAndContainer();

}
