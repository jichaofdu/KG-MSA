package neo4jserver.repositories;

import neo4jserver.domain.entities.VirtualMachine;
import neo4jserver.domain.VirtualMachineResult;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import java.util.ArrayList;
import java.util.Optional;

public interface VirtualMachineRepository extends Neo4jRepository<VirtualMachine, Long> {

    Optional<VirtualMachine> findById(String id);

    @Query("MATCH (n:VirtualMachine) where id(n)={0} return labels(n) as labels, n as node")
    VirtualMachineResult getVitualMachineWithLabels(Long id);

    @Query("MATCH (n:VirtualMachine) return n")
    ArrayList<VirtualMachine> findAllVms();

}
