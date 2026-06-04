/* Dành cho Ngã năm, Đường vòng */

package strategy.turn;

import config.Constants;
import model.intersection.IntersectionLayout;
import model.vehicle.Vehicle;
import util.Direction;
import util.Lane;

public class RoundaboutTurnStrategy implements TurnStrategy {

    private final Direction[] fwOutDirs = {
            Direction.NORTH, Direction.FW_OUT_342, Direction.FW_OUT_54,
            Direction.FW_OUT_126, Direction.FW_OUT_198
    };
    private final double[] fwOutAngles = { -90, 342, 54, 126, 198 };
    private final Direction[] fwInDirs = {
            Direction.SOUTH, Direction.FW_IN_342, Direction.FW_IN_54,
            Direction.FW_IN_126, Direction.FW_IN_198
    };

    @Override
    public void handleTurning(Vehicle vehicle, IntersectionLayout layout) {
        if (vehicle.hasTurned())
            return;

        double hw = vehicle.getWidth() / 2;
        double hh = vehicle.getHeight() / 2;

        if (!layout.getTurnZone().contains(vehicle.getX(), vehicle.getY(), hw, hh))
            return;

        int inIdx = getFiveWayIndex(vehicle.getDirection());
        if (inIdx < 0)
            return;

        int outIdx;
        switch (vehicle.getTurnType()) {
            case RIGHT:
                outIdx = (inIdx + 4) % 5;
                break;
            case STRAIGHT:
                outIdx = (inIdx + 3) % 5;
                break;
            case LEFT:
                outIdx = (inIdx + 2) % 5;
                break;
            default:
                outIdx = (inIdx + 3) % 5;
                break;
        }

        vehicle.setTargetDirection(fwOutDirs[outIdx]);
        vehicle.setTargetAngle(fwOutAngles[outIdx]);
        vehicle.setTurning(true);
        vehicle.setTurned(true);
    }

    @Override
    public void smoothTurning(Vehicle vehicle, IntersectionLayout layout) {
        if (!vehicle.isTurning())
            return;

        double cx = layout.getCx();
        double cy = layout.getCy();

        double dx = vehicle.getX() - cx;
        double dy = vehicle.getY() - cy;
        double radius = Math.sqrt(dx * dx + dy * dy);

        if (radius > 120)
            radius -= 1.8;
        else if (radius < 115)
            radius += 1.8;

        double currentAngleRad = Math.atan2(dy, dx);
        double speed = vehicle.getSpeed() * Constants.TURNING_SPEED_FACTOR * 1.6;
        double angularSpeed = speed / radius;

        double nextAngleRad = currentAngleRad - angularSpeed;

        vehicle.setX(cx + Math.cos(nextAngleRad) * radius);
        vehicle.setY(cy + Math.sin(nextAngleRad) * radius);

        double facingAngle = Math.toDegrees(nextAngleRad) - 90;
        vehicle.setAngle(facingAngle);

        double exitAngleRad = Math.toRadians(vehicle.getTargetAngle());
        double diff = Math.toDegrees(nextAngleRad - exitAngleRad);
        while (diff > 180)
            diff -= 360;
        while (diff < -180)
            diff += 360;

        if (Math.abs(diff) < Math.toDegrees(angularSpeed) * 1.5 && radius <= 130) {
            vehicle.setDirection(vehicle.getTargetDirection());
            vehicle.setAngle(vehicle.getTargetDirection().toAngleDeg());
            vehicle.setLane(Lane.RIGHT);
            vehicle.setTurning(false);
            vehicle.setAcceleration(Constants.DEFAULT_ACCELERATION);
            vehicle.setPostTurnAligning(true);
        }
    }

    private int getFiveWayIndex(Direction dir) {
        for (int i = 0; i < fwInDirs.length; i++) {
            if (fwInDirs[i] == dir)
                return i;
        }
        return -1;
    }
}