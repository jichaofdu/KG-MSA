package collector.domain.relationships;

import collector.domain.entities.Pod;
import collector.domain.entities.VirtualMachine;
import java.util.Objects;

public class VirtualMachineAndPod extends BasicRelationship  {

    private Pod pod;

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

