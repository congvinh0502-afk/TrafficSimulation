package system.movement;

import config.Constants;
import model.intersection.IntersectionLayout;
import model.vehicle.Vehicle;

/**
 * Hệ thống căn giữa làn đường.
 * Dùng IntersectionLayout để lấy lane center chính xác.
 */
public class LaneAlignmentSystem {

    public void alignToLane(Vehicle vehicle, IntersectionLayout layout) {
        if (vehicle.isChangingLane() || vehicle.isTurning()) return;

        boolean postTurn = vehicle.isPostTurnAligning();
        double smoothFactor = postTurn ? Constants.POST_TURN_ALIGN_FACTOR : Constants.LANE_ALIGN_SMOOTH_FACTOR;
        double maxSmooth    = postTurn ? 4.0 : Constants.LANE_ALIGN_MAX_SMOOTH;
        double smooth       = Math.min(vehicle.getSpeed() * smoothFactor + (postTurn ? 0.5 : 0), maxSmooth);

        switch (vehicle.getDirection()) {
            case NORTH:
            case SOUTH: {
                int targetX = layout.getLaneCenterX(vehicle.getDirection(), vehicle.getLane());
                if (targetX == 0) break; // hướng không có trong layout
                double diff = targetX - vehicle.getX();
                if (postTurn && Math.abs(diff) < Constants.POST_TURN_SNAP_THRESHOLD) {
                    vehicle.setX(targetX);
                    vehicle.setPostTurnAligning(false);
                } else {
                    vehicle.setX(vehicle.getX() + Math.signum(diff) * Math.min(smooth, Math.abs(diff)));
                }
                break;
            }
            case EAST:
            case WEST: {
                int targetY = layout.getLaneCenterY(vehicle.getDirection(), vehicle.getLane());
                if (targetY == 0) break;
                double diff = targetY - vehicle.getY();
                if (postTurn && Math.abs(diff) < Constants.POST_TURN_SNAP_THRESHOLD) {
                    vehicle.setY(targetY);
                    vehicle.setPostTurnAligning(false);
                } else {
                    vehicle.setY(vehicle.getY() + Math.signum(diff) * Math.min(smooth, Math.abs(diff)));
                }
                break;
            }
            default:
                break;
        }
    }
}
