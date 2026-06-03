package system.movement;

import manager.LaneManager;
import model.vehicle.Vehicle;

/**
 * LaneAlignmentSystem – căn xe vào đúng tâm lane.
 *
 * FIX: Đọc overtakeOffsetX/Y (field riêng) thay vì targetX/Y
 *      để không xung đột với TurningSystem.
 *      - overtakeOffset != 0 → dịch mượt về vị trí lách
 *      - overtakeOffset == 0 → căn về laneCenter bình thường
 */
public class LaneAlignmentSystem {

    public void alignToLane(Vehicle vehicle) {

        if (vehicle.isChangingLane() || vehicle.isTurning() || vehicle.isStopped()) {
            return;
        }

        double smooth = Math.min(vehicle.getSpeed() * 0.15, 2.0);

        switch (vehicle.getDirection()) {

            case NORTH:
            case SOUTH: {
                double offsetX = vehicle.getOvertakeOffsetX();
                double targetX = (offsetX != 0)
                        ? offsetX
                        : LaneManager.getLaneCenterX(vehicle.getDirection(), vehicle.getLane());

                if (Math.abs(vehicle.getX() - targetX) < smooth + 0.5) {
                    vehicle.setX(targetX);
                } else if (vehicle.getX() < targetX) {
                    vehicle.setX(vehicle.getX() + smooth);
                } else {
                    vehicle.setX(vehicle.getX() - smooth);
                }
                break;
            }

            case EAST:
            case WEST: {
                double offsetY = vehicle.getOvertakeOffsetY();
                double targetY = (offsetY != 0)
                        ? offsetY
                        : LaneManager.getLaneCenterY(vehicle.getDirection(), vehicle.getLane());

                if (Math.abs(vehicle.getY() - targetY) < smooth + 0.5) {
                    vehicle.setY(targetY);
                } else if (vehicle.getY() < targetY) {
                    vehicle.setY(vehicle.getY() + smooth);
                } else {
                    vehicle.setY(vehicle.getY() - smooth);
                }
                break;
            }
        }
    }
}
