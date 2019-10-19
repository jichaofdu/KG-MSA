package neo4jserver.repositories;

import neo4jserver.domain.AppServiceResult;
import neo4jserver.domain.entities.AppService;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import java.util.ArrayList;
import java.util.Optional;

public interface AppServiceRepository extends Neo4jRepository<AppService, Long> {

    Optional<AppService> findById(String id);

    @Query("MATCH (n:AppService) where id(n)={0} return labels(n) as labels, n as node")
    AppServiceResult getServiceWithLabels(Long id);

    @Query("MATCH (n:AppService) return n")
    ArrayList<AppService> findAllAppService();

}
