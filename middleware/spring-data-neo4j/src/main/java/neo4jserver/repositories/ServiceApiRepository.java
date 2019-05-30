package neo4jserver.repositories;

import neo4jserver.domain.AppServiceResult;
import neo4jserver.domain.entities.ServiceAPI;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import java.util.ArrayList;
import java.util.Optional;

public interface ServiceApiRepository extends Neo4jRepository<ServiceAPI, Long> {

    Optional<ServiceAPI> findById(String id);

    @Query("MATCH (n:ServiceAPI) where id(n)={0} return labels(n) as labels, n as node")
    AppServiceResult getServiceWithLabels(Long id);

    @Query("MATCH (n:ServiceAPI) return n")
    ArrayList<ServiceAPI> findAllAppService();

}
