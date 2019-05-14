package collector.domain.apinode;

public class ApiNode {

    private NodeMetadata metadata;

    private NodeStatus status;

    public ApiNode() {
    }

    public NodeMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(NodeMetadata metadata) {
        this.metadata = metadata;
    }

    public NodeStatus getStatus() {
        return status;
    }

    public void setStatus(NodeStatus status) {
        this.status = status;
    }
}
