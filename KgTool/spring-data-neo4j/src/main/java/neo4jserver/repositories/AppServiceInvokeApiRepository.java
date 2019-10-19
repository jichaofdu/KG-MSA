package neo4jserver.repositories;

import neo4jserver.domain.relationships.AppServiceInvokeServiceAPI;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface AppServiceInvokeApiRepository extends Neo4jRepository<AppServiceInvokeServiceAPI, Long> {

    @Query("MATCH p=()-[r:AppServiceInvokeServiceAPI]->() WHERE r.id={0} RETURN p")
    Optional<AppServiceInvokeServiceAPI> findById(String id);

    @Query("MATCH p=()-[r:AppServiceInvokeServiceAPI]->() RETURN p")
    ArrayList<AppServiceInvokeServiceAPI> findAllAppServiceAndPod();

}

