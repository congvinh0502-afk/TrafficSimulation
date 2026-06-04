package system.movement;

import config.Constants;
import manager.LaneManager;
import model.network.NetworkLayout;
import model.vehicle.Vehicle;

/** Căn giữa làn — dùng NetworkLayout cho right-hand traffic. */
public class LaneAlignmentSystem {

    public void alignToLane(Vehicle v) {
        if (v.isChangingLane() || v.isTurning()) return;

        boolean post   = v.isPostTurnAligning();
        double  factor = post ? Constants.POST_TURN_ALIGN_FACTOR : Constants.LANE_ALIGN_SMOOTH_FACTOR;
        double  smooth = Math.min(v.getSpeed() * factor + (post ? 0.5 : 0), post ? 4.0 : Constants.LANE_ALIGN_MAX_SMOOTH);

        switch (v.getDirection()) {
            case NORTH:
            case SOUTH: {
                int target = LaneManager.getLaneCenterX(v.getDirection(), v.getHomeIntersectionX());
                double diff = target - v.getX();
                if (post && Math.abs(diff) < Constants.POST_TURN_SNAP_THRESHOLD) {
                    v.setX(target);
                    v.setPostTurnAligning(false);
                } else {
                    v.setX(v.getX() + Math.signum(diff) * Math.min(smooth, Math.abs(diff)));
                }
                break;
            }
            case EAST:
            case WEST: {
                int target = LaneManager.getLaneCenterY(v.getDirection(), v.getHomeIntersectionY());
                double diff = target - v.getY();
                if (post && Math.abs(diff) < Constants.POST_TURN_SNAP_THRESHOLD) {
                    v.setY(target);
                    v.setPostTurnAligning(false);
                } else {
                    v.setY(v.getY() + Math.signum(diff) * Math.min(smooth, Math.abs(diff)));
                }
                break;
            }
            default: break;
        }
    }
}
