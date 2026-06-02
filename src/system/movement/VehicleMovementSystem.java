package system.movement;

import layout.IntersectionLayout;
import manager.LaneManager;
import model.vehicle.Vehicle;

import java.awt.Rectangle;

/**
 * VehicleMovementSystem — di chuyển xe sau khi đã qua các check khác.
 *
 * Thay đổi so với phiên bản cũ:
 *   - recoverAfterIntersection() dùng layout.getRecoverBounds() thay vì hardcode (360/590).
 *   - Logic move() và alignVehicle() giữ nguyên.
 */
public class VehicleMovementSystem {

    public void move(Vehicle vehicle) {
        recoverAfterIntersection(vehicle);

        if (!vehicle.isStopped()) {
            vehicle.move();
        }
    }

    /**
     * Khi xe ra khỏi vùng ngã rẽ (ngoài recoverBounds), reset turning/changingLane.
     * Trước đây hardcode: x < 360 || x > 590 || y < 360 || y > 590.
     * Bây giờ lấy từ layout.getRecoverBounds().
     */
    private void recoverAfterIntersection(Vehicle vehicle) {
        IntersectionLayout layout = LaneManager.getLayout();
        Rectangle recover = layout.getRecoverBounds();

        boolean outside =
                vehicle.getX() < recover.getMinX()
             || vehicle.getX() > recover.getMaxX()
             || vehicle.getY() < recover.getMinY()
             || vehicle.getY() > recover.getMaxY();

        if (!outside) {
            return;
        }

        vehicle.setStopped(false);
        vehicle.setChangingLane(false);
        vehicle.setTurning(false);
    }

    // Giữ nguyên alignVehicle (không dùng trong pipeline hiện tại nhưng giữ để không mất)
    @SuppressWarnings("unused")
    private void alignVehicle(Vehicle vehicle) {
        if (vehicle.isChangingLane() || vehicle.isTurning()) {
            return;
        }

        double smooth = 2;

        switch (vehicle.getDirection()) {

            case NORTH:
            case SOUTH:
                if (vehicle.getTargetX() != 0) {
                    if (vehicle.getX() < vehicle.getTargetX()) {
                        vehicle.setX(vehicle.getX() + smooth);
                    } else if (vehicle.getX() > vehicle.getTargetX()) {
                        vehicle.setX(vehicle.getX() - smooth);
                    }
                }
                break;

            case EAST:
            case WEST:
                if (vehicle.getTargetY() != 0) {
                    if (vehicle.getY() < vehicle.getTargetY()) {
                        vehicle.setY(vehicle.getY() + smooth);
                    } else if (vehicle.getY() > vehicle.getTargetY()) {
                        vehicle.setY(vehicle.getY() - smooth);
                    }
                }
                break;
        }
    }
}
