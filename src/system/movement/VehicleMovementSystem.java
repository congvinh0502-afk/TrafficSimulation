package system.movement;

import model.vehicle.Vehicle;
import static util.Direction.EAST;
import static util.Direction.NORTH;
import static util.Direction.SOUTH;
import static util.Direction.WEST;

public class VehicleMovementSystem {

    public void move(Vehicle vehicle) {

        alignVehicle(vehicle);

        recoverAfterIntersection(vehicle);

        if (!vehicle.isStopped()) {
            vehicle.move();
        }
    }
    private void alignVehicle(Vehicle vehicle) {

        if (vehicle.isChangingLane()
                || vehicle.isTurning()) {
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
     private void recoverAfterIntersection(Vehicle vehicle) {

        // [FIX M-05] Cũ: buffer < 380 || > 570 — quá hẹp.
        // Xe sau khi rẽ có thể ở x=375 (sát biên 380), turning=false set sớm
        // trước khi xe align đúng làn → snap sai vị trí.
        // Fix: mở rộng buffer ra 360 / 590 để xe hoàn toàn thoát khỏi
        // vùng giao lộ trước khi reset turning.
        boolean outside =
                vehicle.getX() < 360
                        || vehicle.getX() > 590
                        || vehicle.getY() < 360
                        || vehicle.getY() > 590;

        if (!outside) {
            return;
        }

        vehicle.setStopped(false);
        vehicle.setChangingLane(false);
        vehicle.setTurning(false);
    }
}