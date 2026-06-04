package model.road;

import model.network.NetworkLayout;
import util.Direction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Đường giao thông — chứa 2 làn ngược chiều. */
public class Road {
    public static final double LANE_WIDTH = NetworkLayout.ROAD_HALF;
    public static final double ROAD_WIDTH = LANE_WIDTH * 2;

    private final List<Lane> lanes = new ArrayList<>();

    /** Đường dọc tại giao lộ có tâm X = ix. */
    public static Road createVertical(int ix) {
        Road r = new Road();
        r.lanes.add(new Lane(ix - LANE_WIDTH, -600, LANE_WIDTH, 1200, Direction.SOUTH));
        r.lanes.add(new Lane(ix,              -600, LANE_WIDTH, 1200, Direction.NORTH));
        return r;
    }

    /** Đường ngang ở y = 0. */
    public static Road createHorizontal() {
        Road r = new Road();
        int left  = NetworkLayout.TW_X - NetworkLayout.ROAD_HALF;
        int right = NetworkLayout.VW_X + NetworkLayout.ROAD_HALF;
        r.lanes.add(new Lane(left, 0,           right-left, LANE_WIDTH, Direction.EAST));
        r.lanes.add(new Lane(left, LANE_WIDTH,  right-left, LANE_WIDTH, Direction.WEST));
        return r;
    }

    public void addLane(Lane l) { lanes.add(l); }
    public List<Lane> getLanes() { return Collections.unmodifiableList(lanes); }
    public List<Lane> getLanesByDirection(Direction d) {
        List<Lane> res = new ArrayList<>();
        for (Lane l : lanes) if (l.getDirection()==d) res.add(l);
        return res;
    }
    public int getLaneCount() { return lanes.size(); }
}
