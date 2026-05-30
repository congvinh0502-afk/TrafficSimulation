package manager;

import config.Constants;
import util.Direction;
import util.Lane;

/**
 * Quản lý tọa độ trung tâm làn đường.
 *
 * <p>
 * Cung cấp tọa độ X hoặc Y trung tâm của từng làn
 * dựa trên hướng di chuyển và vị trí làn (LEFT/RIGHT).
 * Tất cả giá trị được tính từ hằng số trong {@link Constants}
 * — không dùng số cứng trực tiếp.
 * </p>
 *
 * <p>
 * Dùng bởi:
 * <ul>
 * <li>{@link VehicleSpawnManager} — snap xe về làn khi spawn</li>
 * <li>{@code LaneAlignmentSystem} — căn xe vào giữa làn mỗi frame</li>
 * <li>{@code LaneChangeSystem} — tính đích khi đổi làn</li>
 * <li>{@code TurningSystem.recoverLane} — về làn sau khi rẽ xong</li>
 * </ul>
 * </p>
 */
public final class LaneManager {

    private LaneManager() {
        /* tiện ích tĩnh */ }

    /**
     * Tọa độ X trung tâm làn cho xe đi theo chiều dọc (NORTH / SOUTH).
     *
     * @param direction NORTH hoặc SOUTH (NORTHEAST dùng cùng giá trị với NORTH)
     * @param lane      LEFT hoặc RIGHT
     * @return tọa độ X trung tâm; 0 nếu hướng không áp dụng
     */
    public static int getLaneCenterX(Direction direction, Lane lane) {
        switch (direction) {
            case NORTH:
            case NORTHEAST:
                return (lane == Lane.LEFT)
                        ? Constants.LANE_NORTH_LEFT_X
                        : Constants.LANE_NORTH_RIGHT_X;
            case SOUTH:
                return (lane == Lane.LEFT)
                        ? Constants.LANE_SOUTH_LEFT_X
                        : Constants.LANE_SOUTH_RIGHT_X;
            default:
                return 0;
        }
    }

    /**
     * Tọa độ Y trung tâm làn cho xe đi theo chiều ngang (EAST / WEST).
     *
     * @param direction EAST hoặc WEST
     * @param lane      LEFT hoặc RIGHT
     * @return tọa độ Y trung tâm; 0 nếu hướng không áp dụng
     */
    public static int getLaneCenterY(Direction direction, Lane lane) {
        switch (direction) {
            case EAST:
                return (lane == Lane.LEFT)
                        ? Constants.LANE_EAST_LEFT_Y
                        : Constants.LANE_EAST_RIGHT_Y;
            case WEST:
                return (lane == Lane.LEFT)
                        ? Constants.LANE_WEST_LEFT_Y
                        : Constants.LANE_WEST_RIGHT_Y;
            default:
                return 0;
        }
    }
}