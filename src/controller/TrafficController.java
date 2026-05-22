package controller;

import model.trafficlight.LightColor;
import model.trafficlight.TrafficLight;
import model.vehicle.Vehicle;
import model.vehicle.Ambulance;
import model.vehicle.FireTruck;
import util.Direction;
import java.util.List;
import manager.LaneManager;
import util.DirectionHelper;
import model.intersection.IntersectionType;

public class TrafficController {

    public void updateVehicles(
        List<Vehicle> vehicles,
        TrafficLight verticalLight,
        TrafficLight horizontalLight,
        IntersectionType type
){
        

        for (Vehicle vehicle : vehicles) {
            vehicle.setStopped(false);
            if (vehicle.getLaneChangeCooldown() > 0) {

    vehicle.setLaneChangeCooldown(
            vehicle.getLaneChangeCooldown() - 1
    );
}

    checkTrafficLight(
            vehicle,
            verticalLight,
            horizontalLight
    );

    maintainDistance(vehicle, vehicles);

    handleTurning(vehicle, type);

    if (vehicle.isTurning()) {

        smoothTurning(vehicle);
        updateVehicleAngle(vehicle);
        continue;
    }

    updateLaneChanging(vehicle);

    alignToLane(vehicle);

    alignVehicle(vehicle);

    vehicle.move();
}
        
    }

    private void checkTrafficLight(
        Vehicle vehicle,
        TrafficLight verticalLight,
        TrafficLight horizontalLight
) {
    if (vehicle.isTurning()) {
    return;
}

    boolean mustStop = false;

    Direction direction = vehicle.getDirection();

    switch (direction) {

        case SOUTH:

            mustStop =
                    vehicle.getY() + 60 >= 250
                    && verticalLight.getColor() == LightColor.RED;

            break;

        case NORTH:

            mustStop =
                    vehicle.getY() <= 500
                    && verticalLight.getColor() == LightColor.RED;

            break;

        case EAST:

            mustStop =
                    vehicle.getX() + 60 >= 250
                    && horizontalLight.getColor() == LightColor.RED;

            break;

        case WEST:

            mustStop =
                    vehicle.getX() <= 500
                    && horizontalLight.getColor() == LightColor.RED;

            break;
    }

    if (mustStop) {

        vehicle.setStopped(true);
    }
}
    private void maintainDistance(
        Vehicle current,
        List<Vehicle> vehicles
) {
        
        if (current.isTurning()) {
    return;
}
    for (Vehicle other : vehicles) {

        if (current == other) {
            continue;
        }

        // 🚑 nhường đường xe ưu tiên

        if (other instanceof Ambulance
                || other instanceof FireTruck) {

            if (distance(current, other) < 120) {

                current.setStopped(true);

                return;
            }
        }

        switch (current.getDirection()) {

            case SOUTH:

                if (Math.abs(current.getX() - other.getX()) < 20
                        && other.getY() > current.getY()
                        && other.getY() - current.getY() < 80) {

                    boolean changedLane =
        tryChangeLane(current, vehicles);

if (!changedLane) {

    current.setStopped(true);
}

                }

                break;

            case NORTH:

                if (Math.abs(current.getX() - other.getX()) < 20
                        && current.getY() > other.getY()
                        && current.getY() - other.getY() < 80) {

                    boolean changedLane =
        tryChangeLane(current, vehicles);

if (!changedLane) {

    current.setStopped(true);
}

                }

                break;

            case EAST:

                if (Math.abs(current.getY() - other.getY()) < 20
                        && other.getX() > current.getX()
                        && other.getX() - current.getX() < 80) {

                    boolean changedLane =
        tryChangeLane(current, vehicles);

if (!changedLane) {

    current.setStopped(true);
}

                }

                break;

            case WEST:

                if (Math.abs(current.getY() - other.getY()) < 20
                        && current.getX() > other.getX()
                        && current.getX() - other.getX() < 80) {

                    boolean changedLane =
        tryChangeLane(current, vehicles);

if (!changedLane) {

    current.setStopped(true);
}

                }

                break;
        }
    }
}
    private boolean tryChangeLane(
        Vehicle current,
        List<Vehicle> vehicles
){
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

    // kiểm tra lane mới có trống không

    for (Vehicle other : vehicles) {

        if (current == other) {
            continue;
        }

        if (other.getDirection()
                != current.getDirection()) {

            continue;
        }

        if (other.getLane() == targetLane) {

            // khoảng cách quá gần

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
    private void shiftVehiclePosition(
        Vehicle vehicle
) {

    int offset = 40;

    switch (vehicle.getDirection()) {

        case NORTH:
        case SOUTH:

            if (vehicle.getLane()
                    == util.Lane.LEFT) {

                vehicle.setX(vehicle.getX() - offset);

            } else {

                vehicle.setX(vehicle.getX() + offset);
            }

            break;

        case EAST:
        case WEST:

            if (vehicle.getLane()
                    == util.Lane.LEFT) {

                vehicle.setY(vehicle.getY() - offset);

            } else {

                vehicle.setY(vehicle.getY() + offset);
            }

            break;
    }
}
    
    private double distance(
        Vehicle a,
        Vehicle b
) {

    double dx = a.getX() - b.getX();
    double dy = a.getY() - b.getY();

    return Math.sqrt(dx * dx + dy * dy);
}
    private void turnLeft(
        Vehicle vehicle
) {

    switch (vehicle.getDirection()) {

        case NORTH:

            vehicle.setTargetDirection(
                    Direction.WEST
            );

            vehicle.setTargetAngle(180);

            break;

        case SOUTH:

            vehicle.setTargetDirection(
                    Direction.EAST
            );

            vehicle.setTargetAngle(0);

            break;

        case EAST:

            vehicle.setTargetDirection(
                    Direction.NORTH
            );

            vehicle.setTargetAngle(-90);

            break;

        case WEST:

            vehicle.setTargetDirection(
                    Direction.SOUTH
            );

            vehicle.setTargetAngle(90);

            break;
    }

    vehicle.setTurning(true);
}
    private void turnRight(
        Vehicle vehicle
) {

    switch (vehicle.getDirection()) {

        case NORTH:

            vehicle.setTargetDirection(
                    Direction.EAST
            );

            vehicle.setTargetAngle(0);

            break;

        case SOUTH:

            vehicle.setTargetDirection(
                    Direction.WEST
            );

            vehicle.setTargetAngle(180);

            break;

        case EAST:

            vehicle.setTargetDirection(
                    Direction.SOUTH
            );

            vehicle.setTargetAngle(90);

            break;

        case WEST:

            vehicle.setTargetDirection(
                    Direction.NORTH
            );

            vehicle.setTargetAngle(-90);

            break;
    }

    vehicle.setTurning(true);
}
    private void handleTurning(
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

            targetDirection =
                    DirectionHelper.getLeftDirection(
                            vehicle.getDirection()
                    );

            if (!type.getDirections()
                    .contains(targetDirection)) {

                return;
            }

            turnLeft(vehicle);

            break;

        case RIGHT:

            targetDirection =
                    DirectionHelper.getRightDirection(
                            vehicle.getDirection()
                    );

            if (!type.getDirections()
                    .contains(targetDirection)) {

                return;
            }

            turnRight(vehicle);

            break;

        default:
            return;
    }

    vehicle.setTurned(true);
}
    
    private void alignToLane(
        Vehicle vehicle
) {
    if (vehicle.isChangingLane()) {
        return;
    }
    double smooth = 2;

    switch (vehicle.getDirection()) {

        case NORTH:
        case SOUTH:

            int targetX =
                    LaneManager.getLaneCenterX(
                            vehicle.getDirection(),
                            vehicle.getLane()
                    );

            if (vehicle.getX() < targetX) {

                vehicle.setX(
                        vehicle.getX() + smooth
                );

            } else if (vehicle.getX() > targetX) {

                vehicle.setX(
                        vehicle.getX() - smooth
                );
            }

            break;

        case EAST:
        case WEST:

            int targetY =
                    LaneManager.getLaneCenterY(
                            vehicle.getDirection(),
                            vehicle.getLane()
                    );

            if (vehicle.getY() < targetY) {

                vehicle.setY(
                        vehicle.getY() + smooth
                );

            } else if (vehicle.getY() > targetY) {

                vehicle.setY(
                        vehicle.getY() - smooth
                );
            }

            break;
    }
}
    private void smoothTurning(
        Vehicle vehicle
) {

    if (!vehicle.isTurning()) {
        return;
    }

    double speed = 2;

    double angle =
            Math.toRadians(
                    vehicle.getAngle()
            );

    double dx =
            Math.cos(angle) * speed;

    double dy =
            Math.sin(angle) * speed;

    vehicle.setX(
            vehicle.getX() + dx
    );

    vehicle.setY(
            vehicle.getY() + dy
    );

    updateVehicleAngle(vehicle);

    finishTurning(vehicle);
}
    private void finishTurning(
        Vehicle vehicle
) {

    boolean finished =
            Math.abs(
                    vehicle.getAngle()
                    - vehicle.getTargetAngle()
            ) < 5;

    if (!finished) {
        return;
    }

    vehicle.setDirection(
            vehicle.getTargetDirection()
    );

    assignLaneAfterTurn(vehicle);

    recoverLane(vehicle);

    vehicle.setTurning(false);
}
    private void recoverLane(
        Vehicle vehicle
) {

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
    private void assignLaneAfterTurn(
        Vehicle vehicle
) {

    // mặc định luôn chạy lane phải

    vehicle.setLane(
            util.Lane.RIGHT
    );
}
    private void alignVehicle(Vehicle vehicle) {
       
    if (vehicle.isChangingLane()) {
        return;
    }
    double smooth = 2;

    switch (vehicle.getDirection()) {

        case NORTH:
        case SOUTH:

            if (vehicle.getTargetX() != 0) {

                if (vehicle.getX() < vehicle.getTargetX()) {

                    vehicle.setX(
                            vehicle.getX() + smooth
                    );

                } else if (vehicle.getX() > vehicle.getTargetX()) {

                    vehicle.setX(
                            vehicle.getX() - smooth
                    );
                }
            }

            break;

        case EAST:
        case WEST:

            if (vehicle.getTargetY() != 0) {

                if (vehicle.getY() < vehicle.getTargetY()) {

                    vehicle.setY(
                            vehicle.getY() + smooth
                    );

                } else if (vehicle.getY() > vehicle.getTargetY()) {

                    vehicle.setY(
                            vehicle.getY() - smooth
                    );
                }
            }

            break;
    }
}
    private void updateVehicleAngle(
        Vehicle vehicle
) {

    double rotateSpeed = 2;

    double current =
            vehicle.getAngle();

    double target =
            vehicle.getTargetAngle();

    double diff =
            target - current;

    // normalize

    while (diff > 180) {

        diff -= 360;
    }

    while (diff < -180) {

        diff += 360;
    }

    if (Math.abs(diff) < rotateSpeed) {

        vehicle.setAngle(target);

        return;
    }

    if (diff > 0) {

        vehicle.setAngle(
                current + rotateSpeed
        );

    } else {

        vehicle.setAngle(
                current - rotateSpeed
        );
    }
}
    private void updateLaneChanging(
        Vehicle vehicle
) {

    if (!vehicle.isChangingLane()) {
        return;
    }

    double smooth = 1;

    switch (vehicle.getDirection()) {

        case NORTH:
        case SOUTH:

            int targetX =
                    LaneManager.getLaneCenterX(
                            vehicle.getDirection(),
                            vehicle.getTargetLane()
                    );

            if (Math.abs(
                    vehicle.getX() - targetX
            ) < 3) {

                vehicle.setX(targetX);

                vehicle.setLane(
                        vehicle.getTargetLane()
                );

                vehicle.setChangingLane(false);

                return;
            }

            if (vehicle.getX() < targetX) {

                vehicle.setX(
                        vehicle.getX() + smooth
                );

            } else {

                vehicle.setX(
                        vehicle.getX() - smooth
                );
            }

            break;

        case EAST:
        case WEST:

            int targetY =
                    LaneManager.getLaneCenterY(
                            vehicle.getDirection(),
                            vehicle.getTargetLane()
                    );

            if (Math.abs(
                    vehicle.getY() - targetY
            ) < 3) {

                vehicle.setY(targetY);

                vehicle.setLane(
                        vehicle.getTargetLane()
                );

                vehicle.setChangingLane(false);

                return;
            }

            if (vehicle.getY() < targetY) {

                vehicle.setY(
                        vehicle.getY() + smooth
                );

            } else {

                vehicle.setY(
                        vehicle.getY() - smooth
                );
            }

            break;
    }
}
}