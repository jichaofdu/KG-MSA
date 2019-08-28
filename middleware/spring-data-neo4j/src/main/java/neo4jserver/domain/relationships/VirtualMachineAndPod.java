package neo4jserver.domain.relationships;

import neo4jserver.domain.entities.Pod;
import neo4jserver.domain.entities.VirtualMachine;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.util.Objects;

@RelationshipEntity(type = "VirtualMachineAndPod")
public class VirtualMachineAndPod extends BasicRelationship  {

    @StartNode
    private Pod pod;

    @EndNode
    private VirtualMachine virtualMachine;

    public VirtualMachineAndPod() {
        super();
    }

    public Pod getPod() {
        return pod;
    }

    public void setPod(Pod pod) {
        this.pod = pod;
    }

    public VirtualMachine getVirtualMachine() {
        return virtualMachine;
    }

    public void setVirtualMachine(VirtualMachine virtualMachine) {
        this.virtualMachine = virtualMachine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VirtualMachineAndPod)) return false;
        if (!super.equals(o)) return false;
        VirtualMachineAndPod that = (VirtualMachineAndPod) o;
        return Objects.equals(pod, that.pod) &&
                Objects.equals(virtualMachine, that.virtualMachine);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), pod, virtualMachine);
    }
}

