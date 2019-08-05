package neo4jserver.repositories;

import neo4jserver.domain.relationships.TraceInvokePodToApi;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import java.util.ArrayList;
import java.util.Optional;

public interface TraceInvokePodToApiRepository extends Neo4jRepository<TraceInvokePodToApi, Long> {

    @Query("MATCH p=()-[r:TraceInvokePodToApi]->() WHERE r.id={0} RETURN p")
    Optional<TraceInvokePodToApi> findById(String id);

}
