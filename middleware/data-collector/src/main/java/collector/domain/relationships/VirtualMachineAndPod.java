package collector.domain.relationships;

import collector.domain.entities.Pod;
import collector.domain.entities.VirtualMachine;

public class VirtualMachineAndPod {

    private String id;

    private Pod pod;

    private String relation;

    private String className = this.getClass().toString();

    private VirtualMachine virtualMachine;

    public VirtualMachineAndPod() {
    }

    public VirtualMachineAndPod(Pod pod, String relation, VirtualMachine virtualMachine) {
        this.pod = pod;
        this.relation = relation;
        this.virtualMachine = virtualMachine;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Pod getPod() {
        return pod;
    }

    public void setPod(Pod pod) {
        this.pod = pod;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public VirtualMachine getVirtualMachine() {
        return virtualMachine;
    }

    public void setVirtualMachine(VirtualMachine virtualMachine) {
        this.virtualMachine = virtualMachine;
    }
}

