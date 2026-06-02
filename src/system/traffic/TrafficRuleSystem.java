package system.traffic;

import java.awt.Rectangle;
import java.util.List;

import layout.IntersectionLayout;
import manager.LaneManager;
import model.trafficlight.LightColor;
import model.trafficlight.TrafficLight;
import model.vehicle.Vehicle;
import system.collision.CollisionSystem;
import util.Direction;

/**
 * TrafficRuleSystem — kiểm tra đèn giao thông và vạch dừng.
 *
 * Thay đổi so với phiên bản cũ:
 *   - isInsideIntersection() dùng layout.getIntersectionBounds() thay vì hardcode.
 *   - nearStopLine check dùng layout.getStopLineForDirection() thay vì hardcode.
 *   - Thêm case NORTHEAST cho ngã năm.
 *   - Layout lấy từ LaneManager.getLayout() — không cần truyền qua constructor.
 */
public class TrafficRuleSystem {

    private final CollisionSystem collisionSystem;

    public TrafficRuleSystem() {
        collisionSystem = new CollisionSystem();
    }

    public void checkTrafficLight(
            Vehicle vehicle,
            TrafficLight verticalLight,
            TrafficLight horizontalLight,
            List<Vehicle> vehicles
    ) {
        // Xe đang rẽ thì không kiểm tra đèn
        if (vehicle.isTurning()) {
            return;
        }

        // Xe đã vào ngã rẽ thì không bị chặn
        if (isInsideIntersection(vehicle)) {
            vehicle.setStopped(false);
            return;
        }

        // Xe rẽ phải: chỉ kiểm tra xe trước có block không
        if (vehicle.getTurnType() == util.TurnType.RIGHT) {
            boolean blocked = !collisionSystem.canEnterIntersection(vehicle, vehicles);
            vehicle.setStopped(blocked);
            return;
        }

        IntersectionLayout layout = LaneManager.getLayout();
        Direction direction       = vehicle.getDirection();

        TrafficLight relevantLight = null;
        boolean nearStopLine       = false;

        switch (direction) {

            case SOUTH: {
                relevantLight = verticalLight;
                int stopY = layout.getStopLineForDirection(Direction.SOUTH);
                // xe đang tiếp cận từ trên xuống → tiến về stopY
                nearStopLine = vehicle.getY() + vehicle.getHeight() >= stopY
                            && vehicle.getY() < stopY + 130;
                break;
            }

            case NORTH: {
                relevantLight = verticalLight;
                int stopY = layout.getStopLineForDirection(Direction.NORTH);
                // xe đang tiếp cận từ dưới lên → tiến về stopY (giảm y)
                nearStopLine = vehicle.getY() <= stopY
                            && vehicle.getY() > stopY - 140;
                break;
            }

            case EAST: {
                relevantLight = horizontalLight;
                int stopX = layout.getStopLineForDirection(Direction.EAST);
                nearStopLine = vehicle.getX() + vehicle.getWidth() >= stopX
                            && vehicle.getX() < stopX + 130;
                break;
            }

            case WEST: {
                relevantLight = horizontalLight;
                int stopX = layout.getStopLineForDirection(Direction.WEST);
                nearStopLine = vehicle.getX() <= stopX
                            && vehicle.getX() > stopX - 140;
                break;
            }

            case NORTHEAST: {
                // Hướng chéo — dùng đèn dọc (vertical) làm proxy
                // nearStopLine: khi xe còn chưa chạm vào bounds ngã rẽ
                relevantLight = verticalLight;
                Rectangle bounds = layout.getIntersectionBounds();
                nearStopLine = !bounds.contains(
                        (int) vehicle.getX(),
                        (int) vehicle.getY()
                ) && isApproachingIntersection(vehicle, bounds);
                break;
            }

            default:
                break;
        }

        boolean mustStop = false;

        if (nearStopLine && relevantLight != null) {
            if (vehicle.getBehavior() != null) {
                mustStop = vehicle.getBehavior().shouldStop(
                        vehicle, vehicles, relevantLight
                );
            } else {
                mustStop = relevantLight.getColor() == LightColor.RED;
            }
        }

        // Chỉ check blocked khi xe đang ở vùng nearStopLine
        boolean blocked = nearStopLine
                && !isInsideIntersection(vehicle)
                && !collisionSystem.canEnterIntersection(vehicle, vehicles);

        if (mustStop || blocked) {
            vehicle.setStopped(true);
        } else {
            vehicle.setStopped(false);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────────

    /**
     * Xe có đang ở trong ngã rẽ không?
     * Dùng layout.getIntersectionBounds() — không còn hardcode.
     */
    private boolean isInsideIntersection(Vehicle vehicle) {
        Rectangle bounds = LaneManager.getLayout().getIntersectionBounds();
        return bounds.contains((int) vehicle.getX(), (int) vehicle.getY());
    }

    /**
     * Xe hướng chéo (NORTHEAST) đang tiến về phía ngã rẽ không?
     * Check khoảng cách từ xe đến bounds < 150px.
     */
    private boolean isApproachingIntersection(Vehicle vehicle, Rectangle bounds) {
        double cx = bounds.getCenterX();
        double cy = bounds.getCenterY();
        double dx = vehicle.getX() - cx;
        double dy = vehicle.getY() - cy;
        double dist = Math.sqrt(dx * dx + dy * dy);
        // "sắp vào" nếu trong vòng 250px từ tâm nhưng chưa trong bounds
        return dist < 250;
    }
}
