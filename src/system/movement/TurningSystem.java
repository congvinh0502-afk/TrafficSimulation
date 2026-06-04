package system.movement;

import config.Constants;
import model.intersection.IntersectionLayout;
import model.intersection.IntersectionType;
import model.network.IntersectionNode;
import model.vehicle.Vehicle;
import util.Direction;
import util.DirectionHelper;

import java.util.List;

/**
 * Hệ thống rẽ — phát hiện giao lộ gần nhất và xử lý rẽ.
 * - Ngã 5 (FIVE_WAY): đi theo quỹ đạo tròn CCW trên vòng xuyến (giống v0.1.2).
 * - Ngã 3/4: rẽ góc thường (rotate + move).
 */
public class TurningSystem {

    private static final double TURN_ZONE = config.Constants.LANE_WIDTH + 15.0; // 65 px

    // Trạng thái vòng xuyến: lưu node đang dùng để smoothTurning biết cx/cy
    // (dùng per-vehicle thông qua vehicle field, không cần field ở đây)

    public void updateTurning(Vehicle v, List<IntersectionNode> intersections) {
        if (!v.isTurning()) {
            IntersectionNode node = findApproachingNode(v, intersections);
            if (node != null) initTurn(v, node);
        }
        if (v.isTurning()) {
            // Lấy node gần nhất để biết cx/cy cho smoothTurning vòng xuyến
            IntersectionNode nearest = findNearestNode(v, intersections);
            smoothTurning(v, nearest);
        }
    }

    // =========================================================
    // Phát hiện vào vùng rẽ
    // =========================================================

    private IntersectionNode findApproachingNode(Vehicle v, List<IntersectionNode> nodes) {
        if (v.hasTurned()) return null;

        for (IntersectionNode n : nodes) {
            if (n.type == IntersectionType.FIVE_WAY) {
                // Ngã 5 dùng turnZone từ layout
                IntersectionLayout layout = n.type.createLayout(n.cx, n.cy);
                double hw = v.getWidth() / 2, hh = v.getHeight() / 2;
                if (layout.getTurnZone().contains(v.getX(), v.getY(), hw, hh)) return n;
            } else {
                // Ngã 3/4: chỉ rẽ nếu không STRAIGHT
                if (v.getTurnType() == util.TurnType.STRAIGHT) continue;
                if (inTurnZone(v, n.cx, n.cy)) return n;
            }
        }
        return null;
    }

    private IntersectionNode findNearestNode(Vehicle v, List<IntersectionNode> nodes) {
        IntersectionNode best = null;
        double bestDist = Double.MAX_VALUE;
        for (IntersectionNode n : nodes) {
            double dx = v.getX() - n.cx;
            double dy = v.getY() - n.cy;
            double d = dx * dx + dy * dy;
            if (d < bestDist) { bestDist = d; best = n; }
        }
        return best;
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
        if (node.type == IntersectionType.FIVE_WAY) {
            initiateFiveWayTurn(v);
        } else {
            initTurnNormal(v, node);
        }
        v.setTurned(true);
    }

    // --- Ngã 5: vòng xuyến CCW ---

    private static final Direction[] FW_OUT_DIRS = {
        Direction.NORTH, Direction.FW_OUT_342, Direction.FW_OUT_54,
        Direction.FW_OUT_126, Direction.FW_OUT_198
    };
    private static final double[] FW_OUT_ANGLES = { -90, 342, 54, 126, 198 };
    private static final Direction[] FW_IN_DIRS = {
        Direction.SOUTH, Direction.FW_IN_342, Direction.FW_IN_54,
        Direction.FW_IN_126, Direction.FW_IN_198
    };

    private int getFiveWayIndex(Direction dir) {
        for (int i = 0; i < FW_IN_DIRS.length; i++) {
            if (FW_IN_DIRS[i] == dir) return i;
        }
        return -1;
    }

    private void initiateFiveWayTurn(Vehicle v) {
        int inIdx = getFiveWayIndex(v.getDirection());
        if (inIdx < 0) return;

        int outIdx;
        // Đi theo vòng xuyến ngược chiều kim đồng hồ (CCW)
        switch (v.getTurnType()) {
            case RIGHT:    outIdx = (inIdx + 4) % 5; break; // Lối ra thứ 1
            case STRAIGHT: outIdx = (inIdx + 3) % 5; break; // Lối ra thứ 2
            case LEFT:     outIdx = (inIdx + 2) % 5; break; // Lối ra thứ 3
            default:       outIdx = (inIdx + 3) % 5; break;
        }

        v.setTargetDirection(FW_OUT_DIRS[outIdx]);
        v.setTargetAngle(FW_OUT_ANGLES[outIdx]);
        v.setTurning(true);
    }

    // --- Ngã 3/4: rẽ góc thường ---

    private void initTurnNormal(Vehicle v, IntersectionNode node) {
        Direction current = v.getDirection();
        Direction target;
        double targetAngle;

        switch (v.getTurnType()) {
            case LEFT:
                target = DirectionHelper.getLeftDirection(current);
                break;
            case RIGHT:
                target = DirectionHelper.getRightDirection(current);
                break;
            case STRAIGHT:
                // Đi thẳng: set hướng, không bẻ lái
                setDirectionStraight(v);
                return;
            default:
                return;
        }

        if (target == null) return;
        if (!node.type.getDirections().contains(target)) return;

        targetAngle = target.toAngleDeg();
        v.setTargetDirection(target);
        v.setTargetAngle(targetAngle);
        v.setTurning(true);
    }

    private void setDirectionStraight(Vehicle v) {
        Direction dir = v.getDirection();
        double angle = v.getAngle();
        switch (dir) {
            case NORTH: angle = -90; break;
            case SOUTH: angle = 90;  break;
            case EAST:  angle = 0;   break;
            case WEST:  angle = 180; break;
            default: break;
        }
        v.setTargetDirection(dir);
        v.setTargetAngle(angle);
        v.setTurning(false);
    }

    // =========================================================
    // Di chuyển mượt khi đang rẽ
    // =========================================================

    public void smoothTurning(Vehicle v, IntersectionNode node) {
        if (!v.isTurning()) return;

        // --- XỬ LÝ VÒNG XUYẾN (QUỸ ĐẠO TRÒN CCW) ---
        if (node != null && node.type == IntersectionType.FIVE_WAY) {
            double cx = node.cx;
            double cy = node.cy;

            double dx = v.getX() - cx;
            double dy = v.getY() - cy;
            double radius = Math.sqrt(dx * dx + dy * dy);

            // Ép xe dần bám vào bán kính an toàn của bùng binh
            if (radius > 120) radius -= 1.8;
            else if (radius < 115) radius += 1.8;

            double currentAngleRad = Math.atan2(dy, dx);
            double speed = v.getSpeed() * Constants.TURNING_SPEED_FACTOR * 1.6;
            double angularSpeed = speed / radius;

            // Chạy CCW (ngược chiều kim đồng hồ) -> góc giảm dần
            double nextAngleRad = currentAngleRad - angularSpeed;

            v.setX(cx + Math.cos(nextAngleRad) * radius);
            v.setY(cy + Math.sin(nextAngleRad) * radius);

            // Chỉnh hướng đầu xe dọc theo tiếp tuyến của đường tròn
            double facingAngle = Math.toDegrees(nextAngleRad) - 90;
            v.setAngle(facingAngle);

            // Kiểm tra lối ra
            double exitAngleRad = Math.toRadians(v.getTargetAngle());
            double diff = Math.toDegrees(nextAngleRad - exitAngleRad);
            while (diff >  180) diff -= 360;
            while (diff < -180) diff += 360;

            // Nếu đã tới vị trí góc rẽ hướng ra -> Kết thúc cua
            if (Math.abs(diff) < Math.toDegrees(angularSpeed) * 1.5 && radius <= 130) {
                v.setDirection(v.getTargetDirection());
                v.setAngle(v.getTargetDirection().toAngleDeg());
                v.setLane(util.Lane.RIGHT);
                v.setTurning(false);
                v.setAcceleration(Constants.DEFAULT_ACCELERATION);
                v.setPostTurnAligning(true);
            }
            return;
        }

        // --- XỬ LÝ RẼ GÓC THƯỜNG (NGÃ 3, NGÃ 4) ---
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
        v.setPostTurnAligning(true);
    }
}
