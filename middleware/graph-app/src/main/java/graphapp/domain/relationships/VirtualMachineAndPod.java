package graphapp.domain.relationships;

import graphapp.domain.entities.Pod;
import graphapp.domain.entities.VirtualMachine;
import org.neo4j.ogm.annotation.*;

import java.util.Objects;

@RelationshipEntity(type = "VirtualMachineAndPod")
public class VirtualMachineAndPod {

    @Id
    private String id;

    @StartNode
    private Pod pod;

    @Property(name="relation")
    private String relation;

    @Property(name="className")
    private String className = this.getClass().toString();

    @EndNode
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VirtualMachineAndPod)) return false;
        VirtualMachineAndPod that = (VirtualMachineAndPod) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(pod, that.pod) &&
                Objects.equals(relation, that.relation) &&
                Objects.equals(className, that.className) &&
                Objects.equals(virtualMachine, that.virtualMachine);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pod, relation, className, virtualMachine);
    }
}

