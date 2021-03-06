package neo4jserver.repositories;

import neo4jserver.domain.relationships.TraceInvokeApiToPod;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import java.util.ArrayList;
import java.util.Optional;

public interface TraceInvokeApiToPodRepository extends Neo4jRepository<TraceInvokeApiToPod, Long> {

    @Query("MATCH p=()-[r:TraceInvokeApiToPod]->() WHERE r.id={0} RETURN p")
    Optional<TraceInvokeApiToPod> findById(String id);

}
