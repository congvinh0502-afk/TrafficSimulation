package system.collision;

import config.Constants;
import math.Vector2D;
import model.vehicle.Vehicle;
import system.movement.LaneChangeSystem;

import java.util.List;

/**
 * Hệ thống va chạm và khoảng cách an toàn.
 *
 * <p>Hai lớp kiểm tra:
 * <ol>
 *   <li><b>Hitbox overlap</b> — phát hiện xe thực sự chạm nhau
 *       (dùng {@link Hitbox} AABB, song song với hệ thống khoảng cách).</li>
 *   <li><b>Khoảng cách an toàn + gia tốc</b> — khi xe phía trước đủ gần,
 *       đặt gia tốc âm để giảm tốc dần (không dừng ngay lập tức).</li>
 * </ol>
 * </p>
 */
public class CollisionSystem {

    private final LaneChangeSystem laneChangeSystem;

    public CollisionSystem() {
        this.laneChangeSystem = new LaneChangeSystem();
    }

    // ==========================================================
    // Khoảng cách + gia tốc
    // ==========================================================

    /**
     * Kiểm tra xe phía trước, đặt gia tốc âm khi gần.
     * Nếu không thể tăng tốc: thử đổi làn; không được thì dừng.
     *
     * @param current  xe đang xử lý
     * @param vehicles toàn bộ xe
     */
    public void maintainDistance(Vehicle current, List<Vehicle> vehicles) {
        if (current.isTurning() || current.isChangingLane()) {
            return;
        }

        Vehicle leader = findLeader(current, vehicles);

        if (leader == null) {
            // Không có xe phía trước — gia tốc về maxSpeed
            current.setAcceleration(Constants.DEFAULT_ACCELERATION);
            current.setStopped(false);
            return;
        }

        double gap = gapToLeader(current, leader);
        double safeDistance = current.getWidth() * 2.2;
        double brakeStart   = Constants.BRAKE_START_DISTANCE;

        if (gap <= Constants.MIN_FOLLOW_DISTANCE) {
            // Quá gần — dừng hẳn
            current.setStopped(true);
            current.setAcceleration(0);
        } else if (gap < safeDistance) {
            // Trong vùng phanh khẩn — gia tốc âm mạnh
            double ratio = 1.0 - (gap / safeDistance);
            double decel = -Constants.MAX_BRAKE_DECEL * ratio;
            current.setAcceleration(decel);

            // Nếu quá chậm, thử đổi làn
            if (current.getSpeed() < current.getMaxSpeed() * 0.3) {
                if (!laneChangeSystem.tryChangeLane(current, vehicles)) {
                    if (current.getSpeed() < 0.5) {
                        current.setStopped(true);
                    }
                }
            }
        } else if (gap < brakeStart) {
            // Vùng tiếp cận — giảm tốc nhẹ
            double ratio = (brakeStart - gap) / (brakeStart - safeDistance);
            current.setAcceleration(-Constants.MAX_BRAKE_DECEL * 0.4 * ratio);
        } else {
            // Đường thoáng — tăng tốc
            current.setAcceleration(Constants.DEFAULT_ACCELERATION);
        }
    }

    // ==========================================================
    // Hitbox — phát hiện overlap thực sự
    // ==========================================================

    /**
     * Phát hiện xe nào đang thực sự chồng lên {@code current}.
     * Dùng làm cảnh báo / tách xe sau sự cố.
     *
     * @param current  xe đang kiểm tra
     * @param vehicles toàn bộ xe
     * @return {@code true} nếu có overlap
     */
    public boolean hasOverlap(Vehicle current, List<Vehicle> vehicles) {
        Hitbox hb = Hitbox.of(current);
        for (Vehicle other : vehicles) {
            if (other == current) continue;
            if (Hitbox.of(other).intersects(hb)) return true;
        }
        return false;
    }

    // ==========================================================
    // Kiểm tra được phép vào giao lộ
    // ==========================================================

    /**
     * Trả về {@code true} nếu vùng giao lộ phía trước trống.
     */
    public boolean canEnterIntersection(Vehicle vehicle, List<Vehicle> vehicles) {
        for (Vehicle other : vehicles) {
            if (other == vehicle) continue;
            if (isBlockingIntersection(vehicle, other)) return false;
        }
        return true;
    }

    // ==========================================================
    // Helpers nội bộ
    // ==========================================================

    /**
     * Tìm xe đi cùng hướng gần nhất phía trước.
     */
    private Vehicle findLeader(Vehicle current, List<Vehicle> vehicles) {
        Vector2D dir = current.getDirectionVector();
        double closestDot = Double.MAX_VALUE;
        Vehicle leader = null;

        for (Vehicle other : vehicles) {
            if (other == current) continue;
            if (other.isTurning()) continue;
            if (other.getDirection() != current.getDirection()) continue;

            // Khoảng cách dọc theo hướng di chuyển
            double dx = other.getX() - current.getX();
            double dy = other.getY() - current.getY();
            double forward = dx * dir.x + dy * dir.y; // projection

            if (forward <= 0) continue; // phía sau

            // Khoảng cách ngang (lateral offset)
            double lateral = Math.abs(dx * (-dir.y) + dy * dir.x);
            if (lateral > Constants.SAME_FILE_TOLERANCE * 1.5) continue;

            if (forward < closestDot) {
                closestDot = forward;
                leader = other;
            }
        }
        return leader;
    }

    /**
     * Khoảng cách trống (gap) từ mũi xe hiện tại đến đuôi xe leader.
     */
    private double gapToLeader(Vehicle current, Vehicle leader) {
        Vector2D dir = current.getDirectionVector();
        double dx = leader.getX() - current.getX();
        double dy = leader.getY() - current.getY();
        double centerDist = dx * dir.x + dy * dir.y;
        // Trừ nửa kích thước mỗi xe
        double halfLen = (current.getWidth() + leader.getWidth()) / 2;
        return Math.max(0, centerDist - halfLen);
    }

    private boolean isBlockingIntersection(Vehicle vehicle, Vehicle other) {
        int cl = Constants.INTERSECTION_CHECK_LEFT;
        int cr = Constants.INTERSECTION_CHECK_RIGHT;
        int ct = Constants.INTERSECTION_CHECK_TOP;
        int cb = Constants.INTERSECTION_CHECK_BOTTOM;
        int la = Constants.LOOKAHEAD_DISTANCE;
        int to = Constants.SAME_LANE_TOLERANCE;

        switch (vehicle.getDirection()) {
            case SOUTH:
                return Math.abs(other.getX() - vehicle.getX()) < to
                    && other.getY() > ct && other.getY() < cb
                    && other.getY() - vehicle.getY() < la;
            case NORTH:
                return Math.abs(other.getX() - vehicle.getX()) < to
                    && other.getY() > ct && other.getY() < cb
                    && vehicle.getY() - other.getY() < la;
            case EAST:
                return Math.abs(other.getY() - vehicle.getY()) < to
                    && other.getX() > cl && other.getX() < cr
                    && other.getX() - vehicle.getX() < la;
            case WEST:
                return Math.abs(other.getY() - vehicle.getY()) < to
                    && other.getX() > cl && other.getX() < cr
                    && vehicle.getX() - other.getX() < la;
            default:
                return false;
        }
    }
}
