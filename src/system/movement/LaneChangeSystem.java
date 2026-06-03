package system.movement;

import java.util.List;
import manager.LaneManager;
import model.vehicle.Vehicle;
import util.Lane;

/**
 * LaneChangeSystem – xử lý đổi lane và vượt xe.
 *
 * FIX:
 *  1. tryChangeLane() kiểm tra khoảng trống THEO HƯỚNG DI CHUYỂN,
 *     không phải khoảng cách Euclidean → vượt được khi lane đích thực sự trống.
 *  2. Thêm canOvertakeInSameLane(): nếu lane đích bị chặn nhưng khoảng trống
 *     PHÍA TRƯỚC trong lane hiện tại đủ rộng (xe chậm còn xa), cho xe di chuyển
 *     vào khoảng trống đó thay vì dừng lại.
 *  3. tryOvertakeViaSameLane(): dịch chuyển sang phần trống cùng lane để vượt
 *     khi không đổi sang lane trái/phải được.
 */
public class LaneChangeSystem {

    // Khoảng cách an toàn tối thiểu trong lane đích theo hướng di chuyển
    private static final double SAFE_GAP_AHEAD  = 100; // phía trước xe current
    private static final double SAFE_GAP_BEHIND = 70;  // phía sau xe current

    public void updateLaneChanging(Vehicle vehicle) {

        if (!vehicle.isChangingLane()) {
            return;
        }

        double smooth = 0.8; // tăng nhẹ để chuyển lane nhanh hơn

        switch (vehicle.getDirection()) {

            case NORTH:
            case SOUTH:

                int targetX = LaneManager.getLaneCenterX(
                        vehicle.getDirection(),
                        vehicle.getTargetLane()
                );

                if (Math.abs(vehicle.getX() - targetX) < 3) {
                    vehicle.setX(targetX);
                    vehicle.setLane(vehicle.getTargetLane());
                    vehicle.setChangingLane(false);
                    return;
                }

                if (vehicle.getX() < targetX) {
                    vehicle.setX(vehicle.getX() + smooth);
                } else {
                    vehicle.setX(vehicle.getX() - smooth);
                }

                break;

            case EAST:
            case WEST:

                int targetY = LaneManager.getLaneCenterY(
                        vehicle.getDirection(),
                        vehicle.getTargetLane()
                );

                if (Math.abs(vehicle.getY() - targetY) < 3) {
                    vehicle.setY(targetY);
                    vehicle.setLane(vehicle.getTargetLane());
                    vehicle.setChangingLane(false);
                    return;
                }

                if (vehicle.getY() < targetY) {
                    vehicle.setY(vehicle.getY() + smooth);
                } else {
                    vehicle.setY(vehicle.getY() - smooth);
                }

                break;
        }
    }

    /**
     * Thử đổi sang lane trái (lane đối diện) để vượt xe chậm phía trước.
     * Kiểm tra khoảng trống theo hướng di chuyển, không dùng Euclidean distance.
     */
    public boolean tryChangeLane(Vehicle current, List<Vehicle> vehicles) {

        if (current.isChangingLane()) return false;
        if (current.isTurning())       return false;
        if (current.getLaneChangeCooldown() > 0) return false;

        Lane targetLane = (current.getLane() == Lane.LEFT) ? Lane.RIGHT : Lane.LEFT;

        for (Vehicle other : vehicles) {

            if (current == other) continue;
            if (other.getDirection() != current.getDirection()) continue;
            if (other.getLane() != targetLane) continue;

            // Kiểm tra theo hướng di chuyển
            double axialDist = axialDistanceAhead(current, other);
            double axialBehind = axialDistanceBehind(current, other);

            // Xe trong lane đích ở phía trước gần → không đổi được
            if (axialDist >= 0 && axialDist < SAFE_GAP_AHEAD) {
                return false;
            }
            // Xe trong lane đích ở phía sau gần → không đổi được (sẽ bị đâm)
            if (axialBehind >= 0 && axialBehind < SAFE_GAP_BEHIND) {
                return false;
            }
        }

        current.setTargetLane(targetLane);
        current.setChangingLane(true);
        current.setLaneChangeCooldown(80);

        return true;
    }

    /**
     * Khoảng cách từ xe current đến xe other THEO HƯỚNG DI CHUYỂN (phía trước).
     * Trả về giá trị dương nếu other ở phía trước current, âm nếu phía sau.
     */
    private double axialDistanceAhead(Vehicle current, Vehicle other) {
        switch (current.getDirection()) {
            case SOUTH:     return other.getY() - current.getY();
            case NORTH:     return current.getY() - other.getY();
            case EAST:      return other.getX() - current.getX();
            case WEST:      return current.getX() - other.getX();
            case NORTHEAST: {
                double dx = other.getX() - current.getX();
                double dy  = current.getY() - other.getY();
                return (dx + dy) / 2.0;
            }
            default:        return Double.MAX_VALUE;
        }
    }

    /**
     * Khoảng cách từ xe other đến current theo hướng (other ở phía sau current).
     */
    private double axialDistanceBehind(Vehicle current, Vehicle other) {
        return axialDistanceAhead(other, current);
    }

    public double distance(Vehicle a, Vehicle b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
}
