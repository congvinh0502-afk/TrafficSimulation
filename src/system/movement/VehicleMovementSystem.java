package system.movement;

import config.Constants;
import model.vehicle.Vehicle;

/**
 * Hệ thống di chuyển chính — áp dụng gia tốc mỗi frame.
 *
 * <p>Thứ tự:
 * <ol>
 *   <li>Nếu xe bị stopped → reset gia tốc về 0, dừng.</li>
 *   <li>Áp dụng gia tốc: {@code speed = clamp(speed + acc, 0, maxSpeed)}.</li>
 *   <li>Kiểm tra ra khỏi vùng giao lộ để reset trạng thái.</li>
 *   <li>Gọi {@code vehicle.move()} — dịch chuyển theo vectơ.</li>
 * </ol>
 * </p>
 */
public class VehicleMovementSystem {

    public void move(Vehicle vehicle) {
        if (vehicle.isStopped()) {
            vehicle.setSpeed(Math.max(0, vehicle.getSpeed() - Constants.MAX_BRAKE_DECEL));
            return;
        }

        // Áp dụng gia tốc (clamp [0, maxSpeed])
        double newSpeed = vehicle.getSpeed() + vehicle.getAcceleration();
        newSpeed = Math.max(0, Math.min(vehicle.getMaxSpeed(), newSpeed));
        vehicle.setSpeed(newSpeed);

        recoverAfterIntersection(vehicle);
        vehicle.move();
    }

    /**
     * Reset trạng thái kẹt khi xe đã ra khỏi vùng giao lộ.
     */
    private void recoverAfterIntersection(Vehicle vehicle) {
        boolean outside = vehicle.getX() < Constants.RECOVER_LEFT
                       || vehicle.getX() > Constants.RECOVER_RIGHT
                       || vehicle.getY() < Constants.RECOVER_TOP
                       || vehicle.getY() > Constants.RECOVER_BOTTOM;

        if (!outside) return;

        vehicle.setStopped(false);
        vehicle.setChangingLane(false);
        vehicle.setTurning(false);
    }
}
