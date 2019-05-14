package neo4jserver.repositories;

import neo4jserver.domain.relationships.AppServiceAndPod;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import java.util.ArrayList;
import java.util.Optional;

public interface AppServiceAndPodRepository extends Neo4jRepository<AppServiceAndPod, Long> {

    Optional<AppServiceAndPod> findById(String id);

    @Query("MATCH p=()-[r:AppServiceAndPod]->() RETURN p")
    ArrayList<AppServiceAndPod> findAllAppServiceAndPod();

}
