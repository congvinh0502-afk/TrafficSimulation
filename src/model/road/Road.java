package model.road;

import config.Constants;
import util.Direction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Đại diện một con đường gồm ít nhất 2 làn theo hướng ngược nhau.
 *
 * <p>Cấu trúc làn mặc định (single-intersection, tâm 400,400):
 * <ul>
 *   <li>Đường dọc: SOUTH (x = 300..350) + NORTH (x = 350..400... thực ra 350..400).</li>
 *   <li>Đường ngang: EAST (y = 300..350) + WEST (y = 350..400).</li>
 *   <li>Đường chéo (FIVE_WAY): NORTHEAST — hitbox xấp xỉ.</li>
 * </ul>
 * </p>
 *
 * <p>Các factory method dùng {@link Constants#INTERSECTION_LEFT/TOP} để nhất quán
 * với renderer và IntersectionLayout.</p>
 */
public class Road {

    /** Độ rộng mỗi làn đơn (px) — khớp với Constants.LANE_WIDTH. */
    public static final double LANE_WIDTH = Constants.LANE_WIDTH;

    /** Tổng chiều rộng đường (2 làn). */
    public static final double ROAD_WIDTH = LANE_WIDTH * 2;

    private final List<Lane> lanes;

    private Road() {
        this.lanes = new ArrayList<>();
    }

    // =========================================================
    // Factory methods
    // =========================================================

    /**
     * Đường dọc chuẩn chạy qua vùng giao lộ mặc định (x = 300..500).
     * <ul>
     *   <li>Làn SOUTH (đi xuống): x = INTERSECTION_LEFT .. +LANE_WIDTH</li>
     *   <li>Làn NORTH (đi lên):   x = INTERSECTION_LEFT + LANE_WIDTH .. +LANE_WIDTH</li>
     * </ul>
     */
    public static Road createVertical() {
        Road road = new Road();
        int left = Constants.INTERSECTION_LEFT;

        // Làn SOUTH: bên trái trục dọc (x nhỏ hơn)
        road.lanes.add(new Lane(left,              -100, LANE_WIDTH, 1000, Direction.SOUTH));
        // Làn NORTH: bên phải trục dọc (x lớn hơn)
        road.lanes.add(new Lane(left + LANE_WIDTH, -100, LANE_WIDTH, 1000, Direction.NORTH));
        return road;
    }

    /**
     * Đường ngang chuẩn chạy qua vùng giao lộ mặc định (y = 300..500).
     * <ul>
     *   <li>Làn EAST (đi phải): y = INTERSECTION_TOP .. +LANE_WIDTH</li>
     *   <li>Làn WEST (đi trái): y = INTERSECTION_TOP + LANE_WIDTH .. +LANE_WIDTH</li>
     * </ul>
     */
    public static Road createHorizontal() {
        Road road = new Road();
        int top = Constants.INTERSECTION_TOP;

        // Làn EAST: phía trên trục ngang
        road.lanes.add(new Lane(-100, top,              1400, LANE_WIDTH, Direction.EAST));
        // Làn WEST: phía dưới trục ngang
        road.lanes.add(new Lane(-100, top + LANE_WIDTH, 1400, LANE_WIDTH, Direction.WEST));
        return road;
    }

    /**
     * Đường chéo 45° cho FIVE_WAY (SW → NE).
     * Hitbox xấp xỉ — đường thực được vẽ bằng canvas transform trong RoadRenderer.
     */
    public static Road createDiagonal() {
        Road road = new Road();
        // Tâm mặc định x=400 = INTERSECTION_LEFT(300) + ROAD_WIDTH(100)
        int diagX = Constants.INTERSECTION_LEFT + (int) ROAD_WIDTH;
        road.lanes.add(new Lane(diagX, -100, LANE_WIDTH, 700, Direction.NORTHEAST));
        return road;
    }

    // =========================================================
    // Thao tác
    // =========================================================

    /** Thêm làn vào đường. */
    public void addLane(Lane lane) {
        lanes.add(lane);
    }

    /** Danh sách làn (chỉ đọc). */
    public List<Lane> getLanes() {
        return Collections.unmodifiableList(lanes);
    }

    /**
     * Lấy tất cả làn theo một hướng cụ thể.
     *
     * @param direction hướng cần lọc
     * @return danh sách làn khớp (có thể rỗng)
     */
    public List<Lane> getLanesByDirection(Direction direction) {
        List<Lane> result = new ArrayList<>();
        for (Lane lane : lanes) {
            if (lane.getDirection() == direction) result.add(lane);
        }
        return result;
    }

    /** Số lượng làn. */
    public int getLaneCount() {
        return lanes.size();
    }
}
