package system.movement;

import config.Constants;
import manager.LaneManager;
import model.vehicle.Vehicle;

/**
 * Hệ thống căn giữa làn đường.
 *
 * <p>
 * Mỗi frame dịch xe nhẹ về trung tâm làn của nó.
 * Giúp xe không bị lệch sau va chạm nhẹ hoặc sau khi rẽ.
 * </p>
 *
 * <p>
 * Không hoạt động khi xe đang đổi làn, rẽ, hoặc dừng.
 * </p>
 */
public class LaneAlignmentSystem {

    /**
     * Căn nhẹ xe về trung tâm làn mỗi frame.
     *
     * @param vehicle xe cần căn chỉnh
     */
    public void alignToLane(Vehicle vehicle) {
        if (vehicle.isChangingLane() || vehicle.isTurning() || vehicle.isStopped()) {
            return;
        }

        double smooth = Math.min(
                vehicle.getSpeed() * Constants.LANE_ALIGN_SMOOTH_FACTOR,
                Constants.LANE_ALIGN_MAX_SMOOTH);

        switch (vehicle.getDirection()) {
            case NORTH:
            case SOUTH: {
                int targetX = LaneManager.getLaneCenterX(vehicle.getDirection(), vehicle.getLane());
                if (vehicle.getX() < targetX)
                    vehicle.setX(vehicle.getX() + smooth);
                else if (vehicle.getX() > targetX)
                    vehicle.setX(vehicle.getX() - smooth);
                break;
            }
            case EAST:
            case WEST: {
                int targetY = LaneManager.getLaneCenterY(vehicle.getDirection(), vehicle.getLane());
                if (vehicle.getY() < targetY)
                    vehicle.setY(vehicle.getY() + smooth);
                else if (vehicle.getY() > targetY)
                    vehicle.setY(vehicle.getY() - smooth);
                break;
            }
            default:
                break;
        }
    }
}