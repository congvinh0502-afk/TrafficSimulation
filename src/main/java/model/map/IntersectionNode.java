package model.map;

public class IntersectionNode {
    public enum NodeType { THREE_WAY, FOUR_WAY, FIVE_WAY }

    private String id;
    private double x, y;
    private NodeType type;

    public IntersectionNode(String id, double x, double y, NodeType type) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public String getId() { return id; }
    public double getX() { return x; }
    public double getY() { return y; }
    public NodeType getType() { return type; }
}