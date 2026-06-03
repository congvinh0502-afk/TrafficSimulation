package system.collision;

import config.Constants;
import model.vehicle.Vehicle;
import system.movement.LaneChangeSystem;

import java.util.List;

/**
 * Hệ thống xử lý va chạm và khoảng cách giữa xe.
 *
 * <p>
 * Hai chức năng chính:
 * <ol>
 * <li>{@link #maintainDistance} — giữ khoảng cách an toàn với xe phía trước;
 * thử đổi làn nếu bị chặn, không thì dừng lại.</li>
 * <li>{@link #canEnterIntersection} — kiểm tra vùng giao lộ phía trước
 * có trống không trước khi cho xe tiến vào.</li>
 * </ol>
 * </p>
 */
public class CollisionSystem {

    private final LaneChangeSystem laneChangeSystem;

    public CollisionSystem() {
        this.laneChangeSystem = new LaneChangeSystem();
    }

    // ==========================================================
    // Giữ khoảng cách an toàn
    // ==========================================================

    /**
     * Kiểm tra xe phía trước và quyết định dừng hay đổi làn.
     *
     * <p>
     * Bỏ qua nếu xe đang rẽ hoặc đổi làn.
     * Khoảng cách an toàn = 2× chiều rộng xe hiện tại.
     * </p>
     *
     * @param current  xe cần kiểm tra
     * @param vehicles toàn bộ xe trên bản đồ
     */
    public void maintainDistance(Vehicle current, List<Vehicle> vehicles) {
        if (current.isTurning() || current.isChangingLane()) {
            return;
        }

        double safeDistance = current.getWidth() * 2.0;

        for (Vehicle other : vehicles) {
            if (other == current || other.isTurning())
                continue;
            if (current.getDirection() != other.getDirection())
                continue;

            if (isBlockedByVehicleAhead(current, other, safeDistance)) {
                boolean changed = laneChangeSystem.tryChangeLane(current, vehicles);
                if (!changed) {
                    current.setStopped(true);
                }
                return;
            }
        }
    }

    /**
     * Kiểm tra {@code other} có đang chặn phía trước {@code current} không.
     */
    private boolean isBlockedByVehicleAhead(Vehicle current, Vehicle other, double safeDistance) {
        switch (current.getDirection()) {
            case SOUTH:
                return Math.abs(current.getX() - other.getX()) < Constants.SAME_FILE_TOLERANCE
                        && other.getY() > current.getY()
                        && other.getY() - current.getY() < safeDistance;
            case NORTH:
                return Math.abs(current.getX() - other.getX()) < Constants.SAME_FILE_TOLERANCE
                        && other.getY() < current.getY()
                        && current.getY() - other.getY() < safeDistance;
            case EAST:
                return Math.abs(current.getY() - other.getY()) < Constants.SAME_FILE_TOLERANCE
                        && other.getX() > current.getX()
                        && other.getX() - current.getX() < safeDistance;
            case WEST:
                return Math.abs(current.getY() - other.getY()) < Constants.SAME_FILE_TOLERANCE
                        && other.getX() < current.getX()
                        && current.getX() - other.getX() < safeDistance;
            default:
                return false;
        }
    }

    // ==========================================================
    // Kiểm tra được phép vào giao lộ
    // ==========================================================

    /**
     * Trả về {@code true} nếu không có xe nào đang chiếm vùng
     * giao lộ phía trước xe này.
     *
     * @param vehicle  xe muốn vào giao lộ
     * @param vehicles toàn bộ xe trên bản đồ
     * @return {@code true} nếu giao lộ trống
     */
    public boolean canEnterIntersection(Vehicle vehicle, List<Vehicle> vehicles) {
        for (Vehicle other : vehicles) {
            if (other == vehicle)
                continue;
            if (isBlockingIntersection(vehicle, other))
                return false;
        }
        return true;
    }

    /**
     * Kiểm tra {@code other} có đang cản đường vào giao lộ của {@code vehicle}
     * không.
     */
    private boolean isBlockingIntersection(Vehicle vehicle, Vehicle other) {
        int checkLeft = Constants.INTERSECTION_CHECK_LEFT;
        int checkRight = Constants.INTERSECTION_CHECK_RIGHT;
        int checkTop = Constants.INTERSECTION_CHECK_TOP;
        int checkBottom = Constants.INTERSECTION_CHECK_BOTTOM;
        int lookahead = Constants.LOOKAHEAD_DISTANCE;
        int tolerance = Constants.SAME_LANE_TOLERANCE;

        switch (vehicle.getDirection()) {
            case SOUTH:
                return Math.abs(other.getX() - vehicle.getX()) < tolerance
                        && other.getY() > checkTop
                        && other.getY() < checkBottom
                        && other.getY() - vehicle.getY() < lookahead;
            case NORTH:
                return Math.abs(other.getX() - vehicle.getX()) < tolerance
                        && other.getY() > checkTop
                        && other.getY() < checkBottom
                        && vehicle.getY() - other.getY() < lookahead;
            case EAST:
                return Math.abs(other.getY() - vehicle.getY()) < tolerance
                        && other.getX() > checkLeft
                        && other.getX() < checkRight
                        && other.getX() - vehicle.getX() < lookahead;
            case WEST:
                return Math.abs(other.getY() - vehicle.getY()) < tolerance
                        && other.getX() > checkLeft
                        && other.getX() < checkRight
                        && vehicle.getX() - other.getX() < lookahead;
            default:
                return false;
        }
    }
}