package neo4jserver.repositories;

import neo4jserver.domain.ContainerResult;
import neo4jserver.domain.entities.Container;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import java.util.ArrayList;
import java.util.Optional;

public interface ContainerRepository extends Neo4jRepository<Container, Long> {

    Optional<Container> findById(String id);

    @Query("MATCH (n:Container) where id(n)={0} return labels(n) as labels, n as node")
    ContainerResult getContainerWithLabels(Long id);

    @Query("MATCH (n:Container) return n")
    ArrayList<Container> findAllContainers();
}
