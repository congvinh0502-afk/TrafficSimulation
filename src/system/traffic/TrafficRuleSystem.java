package system.traffic;

import java.util.List;

import config.Constants;
import model.network.IntersectionNode;
import model.network.NetworkLayout;
import model.trafficlight.LightColor;
import model.trafficlight.TrafficLight;
import model.vehicle.Vehicle;
import strategy.driver.DriverBehavior;
import system.collision.CollisionSystem;
import util.Direction;

/**
 * Kiểm tra đèn giao thông cho từng xe — hỗ trợ nhiều giao lộ.
 *
 * <p>Logic dừng đèn:
 * <ul>
 *   <li>Xe có {@link DriverBehavior} được hỏi qua {@code shouldStop()} trước.
 *       {@code EmergencyDriver} luôn trả về {@code false} → vượt đèn đỏ.</li>
 *   <li>Xe bình thường dừng khi đèn đỏ hoặc có xe khác chặn giao lộ.</li>
 *   <li>THREE_WAY không có đèn: xe E/W nhường N/S theo yield-rule.</li>
 * </ul>
 * </p>
 *
 * <p>Dừng trước stop-line (trước khi vào intersection):
 * xe bắt đầu giảm tốc từ {@code BRAKE_START_DISTANCE} và dừng hẳn
 * tại stop-line (dist &lt;= 0), không tiến vào giao lộ cho đến khi đèn xanh.</p>
 */
public class TrafficRuleSystem {

    private final CollisionSystem collision;
    public TrafficRuleSystem() { this.collision = new CollisionSystem(); }

    public void checkAllIntersections(Vehicle v, List<Vehicle> all, List<IntersectionNode> nodes) {
        if (v.isTurning()) return;

        IntersectionNode approaching = findApproaching(v, nodes);
        if (approaching == null) return;

        if (!approaching.hasTrafficLights()) {
            // THREE_WAY: chỉ yield theo collision
            checkYieldRule(v, all, approaching);
            return;
        }

        TrafficLight light = selectLight(v.getDirection(), approaching);
        if (light == null) return;

        // Hỏi DriverBehavior trước — EmergencyDriver trả về false → không dừng
        DriverBehavior beh = v.getBehavior();
        boolean behaviorSaysStop = (beh != null)
                ? beh.shouldStop(v, all, light)
                : (light.getColor() == LightColor.RED);

        // Ngoài ra, nếu có xe đang chặn giao lộ thì vẫn cần dừng
        // (EmergencyDriver vẫn tránh va chạm vật lý với xe đang trong giao lộ)
        boolean blockedByVehicle = !isEmergency(beh) && !collision.canEnterIntersection(v, all);

        boolean mustStop = behaviorSaysStop || blockedByVehicle;
        if (!mustStop) return;

        double dist = distToStopLine(v, approaching.cx, approaching.cy);

        // Dừng hẳn tại hoặc sau stop-line
        if (dist <= 0 || v.getSpeed() < 0.3) {
            v.setStopped(true);
            v.setAcceleration(0);
        } else {
            // Giảm tốc dần khi gần stop-line
            double ratio = Math.max(0, Math.min(1, 1.0 - dist / Constants.BRAKE_START_DISTANCE));
            v.setAcceleration(-Constants.MAX_BRAKE_DECEL * (0.3 + 0.7 * ratio));
        }
    }

    /** Trả về true nếu behavior là EmergencyDriver (hoặc tương đương). */
    private boolean isEmergency(DriverBehavior beh) {
        if (beh == null) return false;
        // EmergencyDriver luôn trả về false cho shouldStop với mọi đèn
        // Dùng class-check để tránh phụ thuộc vào logic nội bộ
        return beh.getClass().getSimpleName().equals("EmergencyDriver");
    }

    // ── Yield cho THREE_WAY: xe E/W-bound nhường N/S ──────────────
    private void checkYieldRule(Vehicle v, List<Vehicle> all, IntersectionNode node) {
        Direction dir = v.getDirection();
        if (dir != Direction.EAST && dir != Direction.WEST) return; // N/S có quyền ưu tiên

        // Emergency vehicles không cần yield
        if (isEmergency(v.getBehavior())) return;

        if (!collision.canEnterIntersection(v, all)) {
            double dist = distToStopLine(v, node.cx, node.cy);
            if (dist > 0) {
                double r = Math.max(0, Math.min(1, 1.0 - dist / Constants.BRAKE_START_DISTANCE));
                v.setAcceleration(-Constants.MAX_BRAKE_DECEL * 0.5 * r);
            } else {
                v.setStopped(true);
                v.setAcceleration(0);
            }
        }
    }

    // ── Tìm giao lộ đang tiếp cận ─────────────────────────────────
    private IntersectionNode findApproaching(Vehicle v, List<IntersectionNode> nodes) {
        double vx = v.getX(), vy = v.getY();
        int    B  = (int) Constants.BRAKE_START_DISTANCE + 20;
        int    RH = NetworkLayout.ROAD_HALF;

        for (IntersectionNode n : nodes) {
            int ix = n.cx, iy = n.cy;
            switch (v.getDirection()) {
                case NORTH:
                    if (Math.abs(vx - ix) < RH && vy > iy && vy < iy + B) return n;
                    break;
                case SOUTH:
                    if (Math.abs(vx - ix) < RH && vy < iy && vy > iy - B) return n;
                    break;
                case EAST:
                    if (Math.abs(vy - iy) < RH && vx < ix && vx > ix - B) return n;
                    break;
                case WEST:
                    if (Math.abs(vy - iy) < RH && vx > ix && vx < ix + B) return n;
                    break;
                default: break;
            }
        }
        return null;
    }

    private TrafficLight selectLight(Direction dir, IntersectionNode n) {
        switch (dir) {
            case NORTH: case SOUTH: return n.verticalLight;
            case EAST:  case WEST:  return n.horizontalLight;
            default: return null;
        }
    }

    private double distToStopLine(Vehicle v, int ix, int iy) {
        int sl = NetworkLayout.STOP_LINE;
        switch (v.getDirection()) {
            case NORTH: return (iy + sl) - v.getY();
            case SOUTH: return v.getY() - (iy - sl);
            case EAST:  return (ix - sl) - v.getX();
            case WEST:  return v.getX() - (ix + sl);
            default:    return Double.MAX_VALUE;
        }
    }
}
