package collector.domain.apinode;

import java.util.ArrayList;

public class NodeStatus {

    private ArrayList<NodeAddress> addresses;

    private NodeCapacity capacity;

    private NodeInfo nodeInfo;

    public NodeStatus() {
    }

    public ArrayList<NodeAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(ArrayList<NodeAddress> addresses) {
        this.addresses = addresses;
    }

    public NodeCapacity getCapacity() {
        return capacity;
    }

    public void setCapacity(NodeCapacity capacity) {
        this.capacity = capacity;
    }

    public NodeInfo getNodeInfo() {
        return nodeInfo;
    }

    public void setNodeInfo(NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
    }
}
