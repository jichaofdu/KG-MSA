package neo4jserver.domain;

import neo4jserver.domain.entities.VirtualMachine;
import org.springframework.data.neo4j.annotation.QueryResult;
import java.util.List;

@QueryResult
public class VirtualMachineResult {

    public VirtualMachine node;

    public List<String> labels;

}
