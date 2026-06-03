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

        // --- FIX 1: ĐẢO LẠI DẤU VÙNG QUÉT (NHẬN DIỆN XE TRƯỚC KHI CHẠM VẠCH) ---
        switch (direction) {
            case SOUTH: {
                relevantLight = verticalLight;
                int stopY = layout.getStopLineForDirection(Direction.SOUTH);
                nearStopLine = (vehicle.getY() + vehicle.getHeight() <= stopY + 5) 
                            && (vehicle.getY() + vehicle.getHeight() >= stopY - 150);
                break;
            }
            case NORTH: {
                relevantLight = verticalLight;
                int stopY = layout.getStopLineForDirection(Direction.NORTH);
                nearStopLine = (vehicle.getY() >= stopY - 5) 
                            && (vehicle.getY() <= stopY + 150);
                break;
            }
            case EAST: {
                relevantLight = horizontalLight;
                int stopX = layout.getStopLineForDirection(Direction.EAST);
                nearStopLine = (vehicle.getX() + vehicle.getWidth() <= stopX + 5) 
                            && (vehicle.getX() + vehicle.getWidth() >= stopX - 150);
                break;
            }
            case WEST: {
                relevantLight = horizontalLight;
                int stopX = layout.getStopLineForDirection(Direction.WEST);
                nearStopLine = (vehicle.getX() >= stopX - 5) 
                            && (vehicle.getX() <= stopX + 150);
                break;
            }
            case NORTHEAST: {
                relevantLight = verticalLight;
                Rectangle bounds = layout.getIntersectionBounds();
                nearStopLine = !bounds.contains((int) vehicle.getX(), (int) vehicle.getY()) 
                            && isApproachingIntersection(vehicle, bounds);
                break;
            }
        }

        boolean mustStop = false;

        if (nearStopLine && relevantLight != null) {
            if (vehicle.getBehavior() != null) {
                mustStop = vehicle.getBehavior().shouldStop(vehicle, vehicles, relevantLight);
            } else {
                mustStop = relevantLight.getColor() == LightColor.RED;
            }
        }

        boolean blocked = nearStopLine
                && !isInsideIntersection(vehicle)
                && !collisionSystem.canEnterIntersection(vehicle, vehicles);

        vehicle.braking = false;

        if (blocked) {
            vehicle.setStopped(true);
            return;
        }

        // --- FIX 2: TĂNG LỰC PHANH VÀ KHÓA CHẶT TỌA ĐỘ VÀO MÉP VẠCH ---
        if (mustStop) {
            double dist = 0;
            if (direction == Direction.NORTH) dist = vehicle.getY() - layout.getStopLineForDirection(Direction.NORTH);
            else if (direction == Direction.SOUTH) dist = layout.getStopLineForDirection(Direction.SOUTH) - (vehicle.getY() + vehicle.getHeight());
            else if (direction == Direction.EAST)  dist = layout.getStopLineForDirection(Direction.EAST) - (vehicle.getX() + vehicle.getWidth());
            else if (direction == Direction.WEST)  dist = vehicle.getX() - layout.getStopLineForDirection(Direction.WEST);
            else if (direction == Direction.NORTHEAST) dist = vehicle.getY() - layout.getStopLineForDirection(Direction.NORTH);

            if (dist <= 1.5) { // Chỉ cần cách vạch 1.5 pixel là khóa bánh
                vehicle.setSpeed(0);
                vehicle.setStopped(true);
                
                // Nam châm hút chặt vào mép vạch, không thể nhích thêm 1 milimet nào
                if (direction == Direction.NORTH) vehicle.setY(layout.getStopLineForDirection(Direction.NORTH));
                else if (direction == Direction.SOUTH) vehicle.setY(layout.getStopLineForDirection(Direction.SOUTH) - vehicle.getHeight());
                else if (direction == Direction.EAST) vehicle.setX(layout.getStopLineForDirection(Direction.EAST) - vehicle.getWidth());
                else if (direction == Direction.WEST) vehicle.setX(layout.getStopLineForDirection(Direction.WEST));
                
            } else {
                // Tăng hệ số hãm từ 0.15 lên 0.2 để xe phanh dứt khoát hơn, không trôi
                double safeSpeed = Math.sqrt(2 * 0.2 * dist);
                if (vehicle.getSpeed() > safeSpeed) {
                    vehicle.setSpeed(Math.max(0, vehicle.getSpeed() - 0.45)); 
                }
                vehicle.braking = true; 
                vehicle.setStopped(false); 
            }
        } else {
            vehicle.setStopped(false);
        }
    } 
    // ─────────────────────────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────────

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
