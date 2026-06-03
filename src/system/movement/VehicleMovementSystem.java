package system.movement;

import config.Constants;
import model.intersection.IntersectionLayout;
import model.vehicle.Vehicle;

/**
 * Hệ thống di chuyển chính — áp dụng gia tốc mỗi frame.
 */
public class VehicleMovementSystem {

    public void move(Vehicle vehicle, IntersectionLayout layout) {
        if (vehicle.isStopped()) {
            vehicle.setSpeed(Math.max(0, vehicle.getSpeed() - Constants.MAX_BRAKE_DECEL));
            return;
        }

        double newSpeed = Math.max(0, Math.min(vehicle.getMaxSpeed(),
                vehicle.getSpeed() + vehicle.getAcceleration()));
        vehicle.setSpeed(newSpeed);

        recoverAfterIntersection(vehicle, layout);
        vehicle.move();
    }

    private void recoverAfterIntersection(Vehicle vehicle, IntersectionLayout layout) {
        if (!layout.isOutsideRecover(vehicle.getX(), vehicle.getY())) return;
        vehicle.setStopped(false);
        vehicle.setChangingLane(false);
        vehicle.setTurning(false);
    }
}
