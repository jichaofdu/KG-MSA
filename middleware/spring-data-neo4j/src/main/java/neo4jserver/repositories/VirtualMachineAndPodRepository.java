package neo4jserver.repositories;

import neo4jserver.domain.relationships.VirtualMachineAndPod;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import java.util.ArrayList;
import java.util.Optional;

public interface VirtualMachineAndPodRepository extends Neo4jRepository<VirtualMachineAndPod, Long> {

    Optional<VirtualMachineAndPod> findById(String id);

    @Query("MATCH p=()-[r:VirtualMachineAndPod]->() RETURN p")
    ArrayList<VirtualMachineAndPod> findAllVirtualMachineAndPod();

}
