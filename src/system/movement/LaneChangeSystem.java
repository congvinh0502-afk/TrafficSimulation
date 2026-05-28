package system.movement;

import java.util.List;
import manager.LaneManager;
import model.vehicle.Vehicle;

public class LaneChangeSystem {

    public void updateLaneChanging(Vehicle vehicle) {

        if (!vehicle.isChangingLane()) {
            return;
        }

        double smooth = 0.6;

        switch (vehicle.getDirection()) {

            case NORTH:
            case SOUTH:

                int targetX = LaneManager.getLaneCenterX(
                        vehicle.getDirection(),
                        vehicle.getTargetLane()
                );

                if (Math.abs(vehicle.getX() - targetX) < 3) {
                    vehicle.setX(targetX);
                    vehicle.setLane(vehicle.getTargetLane());
                    vehicle.setChangingLane(false);
                    return;
                }

                if (vehicle.getX() < targetX) {
                    vehicle.setX(vehicle.getX() + smooth);
                } else {
                    vehicle.setX(vehicle.getX() - smooth);
                }

                break;

            case EAST:
            case WEST:

                int targetY = LaneManager.getLaneCenterY(
                        vehicle.getDirection(),
                        vehicle.getTargetLane()
                );

                if (Math.abs(vehicle.getY() - targetY) < 3) {
                    vehicle.setY(targetY);
                    vehicle.setLane(vehicle.getTargetLane());
                    vehicle.setChangingLane(false);
                    return;
                }

                if (vehicle.getY() < targetY) {
                    vehicle.setY(vehicle.getY() + smooth);
                } else {
                    vehicle.setY(vehicle.getY() - smooth);
                }

                break;
        }
    }
    public boolean tryChangeLane(
            Vehicle current,
            List<Vehicle> vehicles
    ) {
        if (current.isChangingLane()) {
            return false;
        }
        if (current.isTurning()) {
            return false;
        }
        if (current.getLaneChangeCooldown() > 0) {
            return false;
        }

        util.Lane targetLane;

        if (current.getLane() == util.Lane.LEFT) {
            targetLane = util.Lane.RIGHT;
        } else {
            targetLane = util.Lane.LEFT;
        }

        for (Vehicle other : vehicles) {

            if (current == other) {
                continue;
            }

            if (other.getDirection() != current.getDirection()) {
                continue;
            }

            if (other.getLane() == targetLane) {

                if (distance(current, other) < 120) {
                    return false;
                }
            }
        }

        current.setTargetLane(targetLane);
        current.setChangingLane(true);
        current.setLaneChangeCooldown(60);

        return true;
    }
    public double distance(Vehicle a, Vehicle b) {

        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();

        return Math.sqrt(dx * dx + dy * dy);
    }
}