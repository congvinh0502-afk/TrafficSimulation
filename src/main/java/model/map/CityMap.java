package model.map;

import java.util.ArrayList;
import java.util.List;

public class CityMap {
    private List<IntersectionNode> nodes = new ArrayList<>();
    private List<RoadEdge> roads = new ArrayList<>();

    public CityMap() {
        buildDefaultCity();
    }

    private void buildDefaultCity() {
        // 1. TẠO CÁC NODE (Ngã tư) - Tọa độ trải rộng ra màn hình
        IntersectionNode center = new IntersectionNode("Ngã 5 Trung Tâm", 600, 400, IntersectionNode.NodeType.FIVE_WAY);
        IntersectionNode north  = new IntersectionNode("Ngã 3 Bắc", 600, 50, IntersectionNode.NodeType.THREE_WAY);
        IntersectionNode south  = new IntersectionNode("Ngã 4 Nam", 600, 750, IntersectionNode.NodeType.FOUR_WAY);
        IntersectionNode west   = new IntersectionNode("Ngã 3 Tây", 100, 400, IntersectionNode.NodeType.THREE_WAY);
        IntersectionNode east   = new IntersectionNode("Ngã 4 Đông", 1100, 400, IntersectionNode.NodeType.FOUR_WAY);

        nodes.add(center); nodes.add(north); nodes.add(south); nodes.add(west); nodes.add(east);

        // 2. TẠO CÁC EDGE (Rải nhựa đường nối các Ngã tư lại)
        roads.add(new RoadEdge(north, center));
        roads.add(new RoadEdge(center, south));
        roads.add(new RoadEdge(west, center));
        roads.add(new RoadEdge(center, east));
    }

    public List<IntersectionNode> getNodes() { return nodes; }
    public List<RoadEdge> getRoads() { return roads; }
}