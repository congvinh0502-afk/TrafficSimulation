package system.movement;

import manager.LaneManager;
import model.intersection.IntersectionType;
import model.vehicle.Vehicle;
import util.Direction;

import util.DirectionHelper;


public class TurningSystem {

    public void updateTurning(
        Vehicle vehicle,
        IntersectionType type
) {

    handleTurning(vehicle, type);

    if (vehicle.isTurning()) {
        smoothTurning(vehicle);
    }
}
    public void handleTurning(
            Vehicle vehicle,
            IntersectionType type
    ) {

        if (vehicle.hasTurned()) {
            return;
        }

        boolean insideIntersection =
                vehicle.getX() > 430
                        && vehicle.getX() < 520
                        && vehicle.getY() > 430
                        && vehicle.getY() < 520;

        if (!insideIntersection) {
            return;
        }

        Direction targetDirection;

        switch (vehicle.getTurnType()) {

            case LEFT:

                targetDirection = DirectionHelper.getLeftDirection(
                        vehicle.getDirection()
                );

                if (!type.getDirections().contains(targetDirection)) {
                    return;
                }

                turnLeft(vehicle);
                break;

            case RIGHT:

                targetDirection = DirectionHelper.getRightDirection(
                        vehicle.getDirection()
                );

                if (!type.getDirections().contains(targetDirection)) {
                    return;
                }

                turnRight(vehicle);
                break;

            default:
                return;
        }

        vehicle.setTurned(true);
    }
    public void turnLeft(Vehicle vehicle) {

        switch (vehicle.getDirection()) {

            case NORTH:
                vehicle.setTargetDirection(Direction.WEST);
                vehicle.setTargetAngle(180);
                break;

            case SOUTH:
                vehicle.setTargetDirection(Direction.EAST);
                vehicle.setTargetAngle(0);
                break;

            case EAST:
                vehicle.setTargetDirection(Direction.NORTH);
                vehicle.setTargetAngle(-90);
                break;

            case WEST:
                vehicle.setTargetDirection(Direction.SOUTH);
                vehicle.setTargetAngle(90);
                break;
        }

        vehicle.setTurning(true);
    }

    public void turnRight(Vehicle vehicle) {

        switch (vehicle.getDirection()) {

            case NORTH:
                vehicle.setTargetDirection(Direction.EAST);
                vehicle.setTargetAngle(0);
                break;

            case SOUTH:
                vehicle.setTargetDirection(Direction.WEST);
                vehicle.setTargetAngle(180);
                break;

            case EAST:
                vehicle.setTargetDirection(Direction.SOUTH);
                vehicle.setTargetAngle(90);
                break;

            case WEST:
                vehicle.setTargetDirection(Direction.NORTH);
                vehicle.setTargetAngle(-90);
                break;
        }

        vehicle.setTurning(true);
    }
    public void smoothTurning(Vehicle vehicle) {

        if (!vehicle.isTurning()) {
            return;
        }

        double speed = 1.8;

        double angle = Math.toRadians(vehicle.getAngle());

        double dx = Math.cos(angle) * speed;
        double dy = Math.sin(angle) * speed;

        vehicle.setX(vehicle.getX() + dx);
        vehicle.setY(vehicle.getY() + dy);

        finishTurning(vehicle);
        updateVehicleAngle(vehicle);
    }
    private void finishTurning(Vehicle vehicle) {

        boolean finished =
                Math.abs(vehicle.getAngle() - vehicle.getTargetAngle()) < 5;

        if (!finished) {
            return;
        }

        vehicle.setDirection(vehicle.getTargetDirection());

        assignLaneAfterTurn(vehicle);
        recoverLane(vehicle);

        vehicle.setTurning(false);
    }
    public void recoverLane(Vehicle vehicle) {

        switch (vehicle.getDirection()) {

            case NORTH:
            case SOUTH:

                vehicle.setX(
                        LaneManager.getLaneCenterX(
                                vehicle.getDirection(),
                                vehicle.getLane()
                        )
                );

                break;

            case EAST:
            case WEST:

                vehicle.setY(
                        LaneManager.getLaneCenterY(
                                vehicle.getDirection(),
                                vehicle.getLane()
                        )
                );

                break;
        }
    }

    public void assignLaneAfterTurn(Vehicle vehicle) {
        vehicle.setLane(util.Lane.RIGHT);
    }

    

    public void updateVehicleAngle(Vehicle vehicle) {

        double rotateSpeed = 2;

        double current = vehicle.getAngle();
        double target = vehicle.getTargetAngle();

        double diff = target - current;

        while (diff > 180) diff -= 360;
        while (diff < -180) diff += 360;

        if (Math.abs(diff) < rotateSpeed) {
            vehicle.setAngle(target);
            return;
        }

        if (diff > 0) {
            vehicle.setAngle(current + rotateSpeed);
        } else {
            vehicle.setAngle(current - rotateSpeed);
        }
    }
}