package neo4jserver.domain.entities;

import org.neo4j.ogm.annotation.*;

@NodeEntity(label="VirtualMachine")
public class VirtualMachine extends GraphNode{

    @Property(name="memory")
    private String memory;

    @Property(name="cpu")
    private String cpu;

    @Property(name="selflink")
    private String selflink;

    @Property(name="kernelVersion")
    private String kernelVersion;

    @Property(name="osImage")
    private String osImage;

    @Property(name="containerRuntimeVersion")
    private String containerRuntimeVersion;

    @Property(name="operatingSystem")
    private String operatingSystem;

    @Property(name="architecture")
    private String architecture;

    @Property(name="type")
    private String type;

    @Property(name="address")
    private String address;

    public VirtualMachine() {
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getSelflink() {
        return selflink;
    }

    public void setSelflink(String selflink) {
        this.selflink = selflink;
    }

    public String getKernelVersion() {
        return kernelVersion;
    }

    public void setKernelVersion(String kernelVersion) {
        this.kernelVersion = kernelVersion;
    }

    public String getOsImage() {
        return osImage;
    }

    public void setOsImage(String osImage) {
        this.osImage = osImage;
    }

    public String getContainerRuntimeVersion() {
        return containerRuntimeVersion;
    }

    public void setContainerRuntimeVersion(String containerRuntimeVersion) {
        this.containerRuntimeVersion = containerRuntimeVersion;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
