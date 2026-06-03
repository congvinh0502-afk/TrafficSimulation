package system.movement;

import config.Constants;
import manager.LaneManager;
import model.vehicle.Vehicle;

/**
 * Hệ thống căn giữa làn đường.
 *
 * <p>Hai chế độ:
 * <ul>
 *   <li><b>Bình thường</b> — dịch xe nhẹ về tâm làn mỗi frame
 *       (hệ số nhỏ, chuyển động gần như vô hình).</li>
 *   <li><b>Post-turn</b> — sau khi rẽ xong, alignment nhanh hơn
 *       ({@link Constants#POST_TURN_ALIGN_FACTOR}) để xe vào đúng làn
 *       mà không teleport. Khi đủ gần, snap và tắt cờ.</li>
 * </ul>
 * </p>
 */
public class LaneAlignmentSystem {

    public void alignToLane(Vehicle vehicle) {
        if (vehicle.isChangingLane() || vehicle.isTurning()) return;

        boolean postTurn = vehicle.isPostTurnAligning();

        double smoothFactor = postTurn
                ? Constants.POST_TURN_ALIGN_FACTOR
                : Constants.LANE_ALIGN_SMOOTH_FACTOR;

        double maxSmooth = postTurn
                ? 4.0
                : Constants.LANE_ALIGN_MAX_SMOOTH;

        double smooth = Math.min(vehicle.getSpeed() * smoothFactor + (postTurn ? 0.5 : 0), maxSmooth);

        switch (vehicle.getDirection()) {
            case NORTH:
            case SOUTH: {
                int targetX = LaneManager.getLaneCenterX(vehicle.getDirection(), vehicle.getLane());
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
                int targetY = LaneManager.getLaneCenterY(vehicle.getDirection(), vehicle.getLane());
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
