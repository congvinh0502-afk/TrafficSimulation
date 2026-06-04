package system.movement;

import config.Constants;
import model.network.IntersectionNode;
import model.network.NetworkLayout;
import model.vehicle.Vehicle;
import util.Direction;
import util.DirectionHelper;

import java.util.List;

/**
 * Hệ thống rẽ — phát hiện giao lộ gần nhất và xử lý rẽ.
 * FIX TELEPORT: dùng postTurnAligning thay vì snap ngay.
 */
public class TurningSystem {

    private static final double TURN_ZONE = NetworkLayout.ROAD_HALF + 20.0; // 56 px

    public void updateTurning(Vehicle v, List<IntersectionNode> intersections) {
        if (!v.isTurning()) {
            IntersectionNode node = findApproachingNode(v, intersections);
            if (node != null) initTurn(v, node);
        }
        if (v.isTurning()) {
            smoothTurning(v);
        }
    }

    // =========================================================
    // Phát hiện vào vùng rẽ
    // =========================================================

    private IntersectionNode findApproachingNode(Vehicle v, List<IntersectionNode> nodes) {
        if (v.hasTurned()) return null;
        if (v.getTurnType() == util.TurnType.STRAIGHT) return null;

        for (IntersectionNode n : nodes) {
            if (inTurnZone(v, n.cx, n.cy)) return n;
        }
        return null;
    }

    private boolean inTurnZone(Vehicle v, int ix, int iy) {
        double hx = v.getWidth()  / 2, hy = v.getHeight() / 2;
        return v.getX() + hx > ix - TURN_ZONE && v.getX() - hx < ix + TURN_ZONE
            && v.getY() + hy > iy - TURN_ZONE && v.getY() - hy < iy + TURN_ZONE;
    }

    // =========================================================
    // Khởi tạo rẽ
    // =========================================================

    private void initTurn(Vehicle v, IntersectionNode node) {
        Direction current = v.getDirection();
        Direction target;
        double    targetAngle;

        switch (v.getTurnType()) {
            case LEFT:
                target = DirectionHelper.getLeftDirection(current);
                break;
            case RIGHT:
                target = DirectionHelper.getRightDirection(current);
                break;
            default:
                return;
        }

        if (target == null) return;
        if (!node.type.getDirections().contains(target)) return;

        targetAngle = target.toAngleDeg();
        v.setTargetDirection(target);
        v.setTargetAngle(targetAngle);
        v.setTurning(true);
        v.setTurned(true);
    }

    // =========================================================
    // Di chuyển mượt khi đang rẽ
    // =========================================================

    public void smoothTurning(Vehicle v) {
        double speed = v.getSpeed() * Constants.TURNING_SPEED_FACTOR;
        double rad   = Math.toRadians(v.getAngle());
        v.setX(v.getX() + Math.cos(rad) * speed);
        v.setY(v.getY() + Math.sin(rad) * speed);
        updateAngle(v);
        tryFinish(v);
    }

    private void updateAngle(Vehicle v) {
        double diff = v.getTargetAngle() - v.getAngle();
        while (diff >  180) diff -= 360;
        while (diff < -180) diff += 360;
        if (Math.abs(diff) < Constants.ROTATE_SPEED) {
            v.setAngle(v.getTargetAngle());
            return;
        }
        v.setAngle(v.getAngle() + (diff > 0 ? Constants.ROTATE_SPEED : -Constants.ROTATE_SPEED));
    }

    private void tryFinish(Vehicle v) {
        if (Math.abs(v.getAngle() - v.getTargetAngle()) >= Constants.TURN_FINISH_TOLERANCE) return;

        v.setDirection(v.getTargetDirection());
        v.setLane(util.Lane.RIGHT);
        v.setTurning(false);
        v.setAcceleration(Constants.DEFAULT_ACCELERATION);

        // Cập nhật homeIntersectionX sau rẽ
        if (v.getDirection() == Direction.NORTH || v.getDirection() == Direction.SOUTH) {
            v.setHomeIntersectionX(NetworkLayout.nearestIntersectionX(v.getX()));
        }

        // FIX TELEPORT: alignment dần thay vì snap
        v.setPostTurnAligning(true);
    }
}
