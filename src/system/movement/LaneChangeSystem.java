package system.movement;

import config.Constants;
import manager.LaneManager;
import model.vehicle.Vehicle;
import util.Lane;

import java.util.List;

/**
 * Hệ thống đổi làn.
 *
 * <p>
 * Hai chức năng:
 * <ul>
 * <li>{@link #updateLaneChanging} — di chuyển xe từng pixel về làn đích mỗi
 * frame.</li>
 * <li>{@link #tryChangeLane} — kiểm tra điều kiện và kích hoạt đổi làn nếu an
 * toàn.</li>
 * </ul>
 * </p>
 */
public class LaneChangeSystem {

    // ==========================================================
    // Cập nhật vị trí khi đang đổi làn
    // ==========================================================

    /**
     * Dịch chuyển xe dần về trung tâm làn đích.
     * Khi đến nơi, snap chính xác và xác nhận hoàn thành.
     *
     * @param vehicle xe đang đổi làn
     */
    public void updateLaneChanging(Vehicle vehicle) {
        if (!vehicle.isChangingLane())
            return;

        double smooth = Constants.LANE_CHANGE_SMOOTH;

        switch (vehicle.getDirection()) {
            case NORTH:
            case SOUTH: {
                int targetX = LaneManager.getLaneCenterX(vehicle.getDirection(), vehicle.getTargetLane());
                if (Math.abs(vehicle.getX() - targetX) < Constants.LANE_CHANGE_SNAP_THRESHOLD) {
                    vehicle.setX(targetX);
                    vehicle.setLane(vehicle.getTargetLane());
                    vehicle.setChangingLane(false);
                    return;
                }
                vehicle.setX(vehicle.getX() + (vehicle.getX() < targetX ? smooth : -smooth));
                break;
            }
            case EAST:
            case WEST: {
                int targetY = LaneManager.getLaneCenterY(vehicle.getDirection(), vehicle.getTargetLane());
                if (Math.abs(vehicle.getY() - targetY) < Constants.LANE_CHANGE_SNAP_THRESHOLD) {
                    vehicle.setY(targetY);
                    vehicle.setLane(vehicle.getTargetLane());
                    vehicle.setChangingLane(false);
                    return;
                }
                vehicle.setY(vehicle.getY() + (vehicle.getY() < targetY ? smooth : -smooth));
                break;
            }
            default:
                break;
        }
    }

    // ==========================================================
    // Thử đổi làn
    // ==========================================================

    /**
     * Kiểm tra điều kiện an toàn và kích hoạt đổi làn nếu hợp lệ.
     *
     * <p>
     * Không đổi làn nếu:
     * <ul>
     * <li>Đang trong quá trình đổi làn khác.</li>
     * <li>Đang rẽ.</li>
     * <li>Đang trong thời gian cooldown.</li>
     * <li>Có xe trong làn đích cách dưới
     * {@link Constants#LANE_CHANGE_SAFE_DISTANCE}.</li>
     * </ul>
     * </p>
     *
     * @param current  xe muốn đổi làn
     * @param vehicles toàn bộ xe trên bản đồ
     * @return {@code true} nếu đã kích hoạt đổi làn thành công
     */
    public boolean tryChangeLane(Vehicle current, List<Vehicle> vehicles) {
        if (current.isChangingLane())
            return false;
        if (current.isTurning())
            return false;
        if (current.getLaneChangeCooldown() > 0)
            return false;

        Lane targetLane = (current.getLane() == Lane.LEFT) ? Lane.RIGHT : Lane.LEFT;

        for (Vehicle other : vehicles) {
            if (other == current)
                continue;
            if (other.getDirection() != current.getDirection())
                continue;
            if (other.getLane() == targetLane
                    && distance(current, other) < Constants.LANE_CHANGE_SAFE_DISTANCE) {
                return false;
            }
        }

        current.setTargetLane(targetLane);
        current.setChangingLane(true);
        current.setLaneChangeCooldown(Constants.LANE_CHANGE_COOLDOWN);
        return true;
    }

    // ----------------------------------------------------------
    // Tiện ích
    // ----------------------------------------------------------

    /** Khoảng cách Euclidean giữa hai xe. */
    public double distance(Vehicle a, Vehicle b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
}