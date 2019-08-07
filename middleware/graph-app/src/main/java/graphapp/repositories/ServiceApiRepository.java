package graphapp.repositories;

import graphapp.domain.entities.ServiceAPI;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import java.util.ArrayList;

public interface ServiceApiRepository extends Neo4jRepository<ServiceAPI, Long> {

    @Query("MATCH (n:ServiceAPI) return n")
    ArrayList<ServiceAPI> findAllAppService();


}
