package no.ntnu.message;

public class NodeRemoveMessage {
    private int nodeId;

    public NodeRemoveMessage(int nodeId) {
        this.nodeId = nodeId;
    }

    public int getNodeId() {
        return nodeId;
    }

}
