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
     // xử lý rẽ TRƯỚC
    handleTurning(vehicle, type);
    // đang rẽ thì bỏ toàn bộ logic đèn + khoảng cách
    if (vehicle.isTurning()) {

        smoothTurning(vehicle);

        

        continue;
    }

    checkTrafficLight(
        vehicle,
        verticalLight,
        horizontalLight,
        vehicles
);

    maintainDistance(vehicle, vehicles);
    if (vehicle.isStopped()) {
    continue;
}
    updateLaneChanging(vehicle);
    alignToLane(vehicle);
    alignVehicle(vehicle);
    recoverAfterIntersection(vehicle);
    if (!vehicle.isStopped()) {
    vehicle.move();
}
    
    }
        
}

    private void checkTrafficLight(
        Vehicle vehicle,
        TrafficLight verticalLight,
        TrafficLight horizontalLight,
        List<Vehicle> vehicles
) {
    if (vehicle.isTurning()) {
    return;
}

    boolean mustStop = false;

    Direction direction = vehicle.getDirection();

   
switch (direction) {

    case SOUTH:

        mustStop =
                vehicle.getY() + 60 >= 360
                && verticalLight.getColor()
                == LightColor.RED;

        break;

    case NORTH:

        mustStop =
                vehicle.getY() <= 640
                && verticalLight.getColor()
                == LightColor.RED;

        break;

    case EAST:

        mustStop =
                vehicle.getX() + 60 >= 360
                && horizontalLight.getColor()
                == LightColor.RED;

        break;

    case WEST:

        mustStop =
                vehicle.getX() <= 640
                && horizontalLight.getColor()
                == LightColor.RED;

        break;
}

    boolean blocked =
        !canEnterIntersection(
                vehicle,
                vehicles
        );

        if (mustStop || blocked) {

            vehicle.setStopped(true);
        }
}
   
private void maintainDistance(
        Vehicle current,
        List<Vehicle> vehicles
) {

    if (current.isTurning()
            || current.isChangingLane()) {

        return;
    }

    for (Vehicle other : vehicles) {

        if (current == other) {
            continue;
        }

        if (other.isTurning()) {
            continue;
        }

        // chỉ check cùng hướng

        if (current.getDirection()
                != other.getDirection()) {

            continue;
        }

        double safeDistance = 120;

        switch (current.getDirection()) {

            case SOUTH:

                if (Math.abs(
                        current.getX()
                        - other.getX()
                ) < 25

                        && other.getY() > current.getY()

                        && other.getY()
                        - current.getY() < safeDistance) {

                    boolean changedLane =
                            tryChangeLane(
                                    current,
                                    vehicles
                            );

                    if (!changedLane) {

                        current.setStopped(true);
                    }

                    return;
                }

                break;

            case NORTH:

                if (Math.abs(
                        current.getX()
                        - other.getX()
                ) < 25

                        && other.getY() < current.getY()

                        && current.getY()
                        - other.getY() < safeDistance) {

                    boolean changedLane =
                            tryChangeLane(
                                    current,
                                    vehicles
                            );

                    if (!changedLane) {

                        current.setStopped(true);
                    }

                    return;
                }

                break;

            case EAST:

                if (Math.abs(
                        current.getY()
                        - other.getY()
                ) < 25

                        && other.getX() > current.getX()

                        && other.getX()
                        - current.getX() < safeDistance) {

                    boolean changedLane =
                            tryChangeLane(
                                    current,
                                    vehicles
                            );

                    if (!changedLane) {

                        current.setStopped(true);
                    }

                    return;
                }

                break;

            case WEST:

                if (Math.abs(
                        current.getY()
                        - other.getY()
                ) < 25

                        && other.getX() < current.getX()

                        && current.getX()
                        - other.getX() < safeDistance) {

                    boolean changedLane =
                            tryChangeLane(
                                    current,
                                    vehicles
                            );

                    if (!changedLane) {

                        current.setStopped(true);
                    }

                    return;
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
    if (vehicle.isChangingLane()
            || vehicle.isTurning()
            || vehicle.isStopped()) {

        return;
    }
    double smooth = 0.5;

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

    double speed = 1.8;

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

    finishTurning(vehicle);

    updateVehicleAngle(vehicle);
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

    double smooth = 0.6;

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
    private boolean canEnterIntersection(
        Vehicle vehicle,
        List<Vehicle> vehicles
) {

    switch (vehicle.getDirection()) {

        case SOUTH:

            for (Vehicle other : vehicles) {

                if (other == vehicle) {
                    continue;
                }

                // phía sau giao lộ đang kẹt
                if (Math.abs(
                        other.getX() - vehicle.getX()
                ) < 50
                        &&
                        other.getY() > 520
                        &&
                        other.getY() - vehicle.getY() < 120) {

                    return false;
                }
            }

            break;

        case NORTH:

            for (Vehicle other : vehicles) {

                if (other == vehicle) {
                    continue;
                }

                if (Math.abs(
                        other.getX() - vehicle.getX()
                ) < 50
                        &&
                        other.getY() < 430
                        &&
                        vehicle.getY() - other.getY() < 120) {

                    return false;
                }
            }

            break;

        case EAST:

            for (Vehicle other : vehicles) {

                if (other == vehicle) {
                    continue;
                }

                if (Math.abs(
                        other.getY() - vehicle.getY()
                ) < 50
                        &&
                        other.getX() > 520
                        &&
                        other.getX() - vehicle.getX() < 120) {

                    return false;
                }
            }

            break;

        case WEST:

            for (Vehicle other : vehicles) {

                if (other == vehicle) {
                    continue;
                }

                if (Math.abs(
                        other.getY() - vehicle.getY()
                ) < 50
                        &&
                        other.getX() < 430
                        &&
                        vehicle.getX() - other.getX() < 120) {

                    return false;
                }
            }

            break;
    }

    return true;
}
    public int countVehiclesByDirection(
        List<Vehicle> vehicles,
        Direction d1,
        Direction d2
) {

    int count = 0;

    for (Vehicle vehicle : vehicles) {

        if (vehicle.getDirection() == d1
                ||
                vehicle.getDirection() == d2) {

            count++;
        }
    }

    return count;
}
    private void recoverAfterIntersection(
        Vehicle vehicle
) {

    boolean outside =

            vehicle.getX() < 380
            || vehicle.getX() > 570
            || vehicle.getY() < 380
            || vehicle.getY() > 570;

    if (!outside) {
        return;
    }

    vehicle.setStopped(false);

    vehicle.setChangingLane(false);

    vehicle.setTurning(false);
}
    public int countStoppedVehicles(
        List<Vehicle> vehicles
) {

    int count = 0;

    for (Vehicle vehicle : vehicles) {

        if (vehicle.isStopped()) {

            count++;
        }
    }

    return count;
}
    
}