package no.ntnu.message;

public class NodeAddedMessage {
    private int nodeId;

    public NodeAddedMessage (int nodeId) {
        this.nodeId = nodeId;
    }

    public int getNodeId() {
        return nodeId;
    }
}
