package neo4jserver.repositories;

import neo4jserver.domain.relationships.AppServiceHostServiceAPI;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import java.util.ArrayList;
import java.util.Optional;

public interface AppServiceHostApiRepository extends Neo4jRepository<AppServiceHostServiceAPI, Long> {

    Optional<AppServiceHostServiceAPI> findById(String id);

    @Query("MATCH p=()-[r:AppServiceHostServiceAPI]->() RETURN p")
    ArrayList<AppServiceHostServiceAPI> findAllAppServiceAndPod();
}
