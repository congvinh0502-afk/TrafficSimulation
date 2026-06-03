package model.road;

import config.Constants;
import util.Direction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Đại diện một con đường gồm ít nhất 2 làn theo hướng ngược nhau.
 *
 * <p>Cấu trúc:
 * <ul>
 *   <li>Đường dọc: có các làn hướng NORTH và SOUTH.</li>
 *   <li>Đường ngang: có các làn hướng EAST và WEST.</li>
 *   <li>Đường chéo (FIVE_WAY): có làn NORTHEAST và SOUTHWEST.</li>
 * </ul>
 * </p>
 *
 * <p>Dùng các factory method tĩnh để tạo đường chuẩn.</p>
 */
public class Road {

    /** Độ rộng mỗi làn đơn lẻ (px). */
    public static final double LANE_WIDTH = 50;

    /** Kích thước đường (2 làn × LANE_WIDTH). */
    public static final double ROAD_WIDTH = LANE_WIDTH * 2;

    // --------------------------------------------------------

    private final List<Lane> lanes;

    private Road() {
        this.lanes = new ArrayList<>();
    }

    // --------------------------------------------------------
    // Factory methods
    // --------------------------------------------------------

    /**
     * Đường dọc chuẩn chạy qua tâm x=400.
     * Làn NORTH (phải đường) + làn SOUTH (trái đường).
     */
    public static Road createVertical() {
        Road road = new Road();
        int left  = Constants.INTERSECTION_LEFT;
        int right = Constants.INTERSECTION_RIGHT;

        // Làn đi về phía SOUTH (trái, x=300..400)
        road.lanes.add(new Lane(left, -100, LANE_WIDTH, 1000, Direction.SOUTH));
        // Làn đi về phía NORTH (phải, x=400..500)
        road.lanes.add(new Lane(left + LANE_WIDTH, -100, LANE_WIDTH, 1000, Direction.NORTH));
        return road;
    }

    /**
     * Đường ngang chuẩn chạy qua tâm y=400.
     * Làn EAST (phía dưới trục) + làn WEST (phía trên trục).
     */
    public static Road createHorizontal() {
        Road road = new Road();
        int top    = Constants.INTERSECTION_TOP;

        // Làn đi về phía EAST (trên, y=300..400)
        road.lanes.add(new Lane(-100, top, 1400, LANE_WIDTH, Direction.EAST));
        // Làn đi về phía WEST (dưới, y=400..500)
        road.lanes.add(new Lane(-100, top + LANE_WIDTH, 1400, LANE_WIDTH, Direction.WEST));
        return road;
    }

    /**
     * Đường chéo 45° cho FIVE_WAY.
     * Chỉ có một làn NORTHEAST (xe từ SW đến NE).
     * Trong thực tế FIVE_WAY thêm đường này vào ngã tư.
     */
    public static Road createDiagonal() {
        Road road = new Road();
        // Làn chéo đại diện — hitbox xấp xỉ (đường thực vẽ bằng transform)
        road.lanes.add(new Lane(400, -100, LANE_WIDTH, 700, Direction.NORTHEAST));
        return road;
    }

    // --------------------------------------------------------
    // Thao tác
    // --------------------------------------------------------

    /**
     * Thêm làn vào đường.
     */
    public void addLane(Lane lane) {
        lanes.add(lane);
    }

    /**
     * Danh sách làn (chỉ đọc).
     */
    public List<Lane> getLanes() {
        return Collections.unmodifiableList(lanes);
    }

    /**
     * Lấy tất cả làn đi theo một hướng cụ thể.
     *
     * @param direction hướng cần tìm
     * @return danh sách làn (có thể rỗng)
     */
    public List<Lane> getLanesByDirection(Direction direction) {
        List<Lane> result = new ArrayList<>();
        for (Lane lane : lanes) {
            if (lane.getDirection() == direction) {
                result.add(lane);
            }
        }
        return result;
    }

    /**
     * Số lượng làn.
     */
    public int getLaneCount() {
        return lanes.size();
    }
}
