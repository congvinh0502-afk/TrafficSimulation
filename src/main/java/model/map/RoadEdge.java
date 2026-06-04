package model.map;

public class RoadEdge {
    private IntersectionNode startNode;
    private IntersectionNode endNode;

    public RoadEdge(IntersectionNode startNode, IntersectionNode endNode) {
        this.startNode = startNode;
        this.endNode = endNode;
    }

    public IntersectionNode getStartNode() { return startNode; }
    public IntersectionNode getEndNode() { return endNode; }
}