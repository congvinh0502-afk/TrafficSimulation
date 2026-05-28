package system.movement;

import manager.LaneManager;
import model.vehicle.Vehicle;

public class LaneAlignmentSystem {

    public void alignToLane(Vehicle vehicle) {

        if (vehicle.isChangingLane()
                || vehicle.isTurning()
                || vehicle.isStopped()) {
            return;
        }

        double smooth = 0.5;

        switch (vehicle.getDirection()) {

            case NORTH:
            case SOUTH:

                int targetX = LaneManager.getLaneCenterX(
                        vehicle.getDirection(),
                        vehicle.getLane()
                );

                if (vehicle.getX() < targetX) {
                    vehicle.setX(vehicle.getX() + smooth);
                } else if (vehicle.getX() > targetX) {
                    vehicle.setX(vehicle.getX() - smooth);
                }

                break;

            case EAST:
            case WEST:

                int targetY = LaneManager.getLaneCenterY(
                        vehicle.getDirection(),
                        vehicle.getLane()
                );

                if (vehicle.getY() < targetY) {
                    vehicle.setY(vehicle.getY() + smooth);
                } else if (vehicle.getY() > targetY) {
                    vehicle.setY(vehicle.getY() - smooth);
                }

                break;
        }
    }
}