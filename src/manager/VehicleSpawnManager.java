package manager;

import java.util.ArrayList;
import java.util.List;

import model.vehicle.Ambulance;
import model.vehicle.Bicycle;
import model.vehicle.Car;
import model.vehicle.FireTruck;
import model.vehicle.Motorbike;
import model.vehicle.Vehicle;

import strategy.driver.AggressiveDriver;
import strategy.driver.EmergencyDriver;
import strategy.driver.NormalDriver;

import util.Direction;
import util.Lane;
import util.TurnType;

public class VehicleSpawnManager {

    private final List<Vehicle> vehicles;

    public VehicleSpawnManager(
            List<Vehicle> vehicles
    ) {

        this.vehicles = vehicles;
    }

    // =========================
    // SPAWN RANDOM VEHICLE
    // =========================

    public void spawnRandomVehicle(List<Direction> directions) {

    int randomType =
            (int) (Math.random() * 5);

    Direction direction =
        directions.get(
                (int)(
                        Math.random()
                        * directions.size()
                )
        );

    double x = getSpawnX(direction);
    double y = getSpawnY(direction);

    if (!canSpawn(x, y)) {
        return;
    }

    Vehicle vehicle;

    switch (randomType) {

        case 0:

            vehicle =
                    createCar(direction);

            break;

        case 1:

            vehicle =
                    createMotorbike(direction);

            break;

        case 2:

            vehicle =
                    createBicycle(direction);

            break;

        case 3:

            vehicle =
                    createAmbulance(direction);

            break;

        default:

            vehicle =
                    createFireTruck(direction);

            break;
    }

    setupVehicle(vehicle);

    vehicles.add(vehicle);
}

    // =========================
    // CREATE VEHICLES
    // =========================

    private Vehicle createCar(
            Direction direction
    ) {

        return new Car(
                getSpawnX(direction),
                getSpawnY(direction),
                direction
        );
    }

    private Vehicle createMotorbike(
            Direction direction
    ) {

        return new Motorbike(
                getSpawnX(direction),
                getSpawnY(direction),
                direction
        );
    }

    private Vehicle createBicycle(
            Direction direction
    ) {

        return new Bicycle(
                getSpawnX(direction),
                getSpawnY(direction),
                direction
        );
    }

    private Vehicle createAmbulance(
            Direction direction
    ) {

        return new Ambulance(
                getSpawnX(direction),
                getSpawnY(direction),
                direction
        );
    }

    private Vehicle createFireTruck(
            Direction direction
    ) {

        return new FireTruck(
                getSpawnX(direction),
                getSpawnY(direction),
                direction
        );
    }

    // =========================
    // SETUP VEHICLE
    // =========================

    private void setupVehicle(
            Vehicle vehicle
    ) {

        setupLane(vehicle);

        setupTurnType(vehicle);

        setupDriverBehavior(vehicle);
    }

    // =========================
    // SETUP LANE
    // =========================

    private void setupLane(
        Vehicle vehicle
) {

    Lane lane;

    if (Math.random() < 0.5) {

        lane = Lane.LEFT;

    } else {

        lane = Lane.RIGHT;
    }

    vehicle.setLane(lane);

    // FIX POSITION THEO LANE

    switch (vehicle.getDirection()) {

        case NORTH:
        case SOUTH:

            vehicle.setX(
                    LaneManager.getLaneCenterX(
                            vehicle.getDirection(),
                            lane
                    )
            );

            break;

        case EAST:
        case WEST:

            vehicle.setY(
                    LaneManager.getLaneCenterY(
                            vehicle.getDirection(),
                            lane
                    )
            );

            break;
    }
}

    // =========================
    // SETUP TURN TYPE
    // =========================

    private void setupTurnType(
            Vehicle vehicle
    ) {

        double random =
                Math.random();

        if (random < 0.33) {

            vehicle.setTurnType(
                    TurnType.LEFT
            );

        } else if (random < 0.66) {

            vehicle.setTurnType(
                    TurnType.RIGHT
            );

        } else {

            vehicle.setTurnType(
                    TurnType.STRAIGHT
            );
        }
    }

    // =========================
    // SETUP DRIVER
    // =========================

    private void setupDriverBehavior(
            Vehicle vehicle
    ) {

        if (vehicle instanceof Ambulance
                || vehicle instanceof FireTruck) {

            vehicle.setBehavior(
                    new EmergencyDriver()
            );

            return;
        }

        if (Math.random() < 0.3) {

            vehicle.setBehavior(
                    new AggressiveDriver()
            );

        } else {

            vehicle.setBehavior(
                    new NormalDriver()
            );
        }
    }

    // =========================
    // DIRECTION
    // =========================

    private Direction getDirection(
            int value
    ) {

        switch (value) {

            case 0:
                return Direction.NORTH;

            case 1:
                return Direction.SOUTH;

            case 2:
                return Direction.EAST;

            default:
                return Direction.WEST;
        }
    }

    // =========================
    // SPAWN POSITION X
    // =========================

    private double getSpawnX(
            Direction direction
    ) {

        switch (direction) {

            case NORTH:

                return 470;

            case SOUTH:

                return 530;

            case EAST:

                return -100;

            case WEST:

                return 1100;

            default:

                return 0;
        }
    }

    // =========================
    // SPAWN POSITION Y
    // =========================

    private double getSpawnY(
            Direction direction
    ) {

        switch (direction) {

            case NORTH:

                return 1100;

            case SOUTH:

                return -100;

            case EAST:

                return 470;

            case WEST:

                return 530;

            default:

                return 0;
        }
    }

    // =========================
    // REMOVE OUTSIDE VEHICLE
    // =========================

    public void removeOutsideVehicles() {

        List<Vehicle> removeList =
                new ArrayList<>();

        for (Vehicle vehicle : vehicles) {

            if (vehicle.getX() < -200
                    || vehicle.getX() > 1400
                    || vehicle.getY() < -200
                    || vehicle.getY() > 1400) {

                removeList.add(vehicle);
            }
        }

        vehicles.removeAll(removeList);
    }
    private boolean canSpawn(
        double x,
        double y
) {

    for (Vehicle vehicle : vehicles) {

        double dx = vehicle.getX() - x;
        double dy = vehicle.getY() - y;

        double distance =
                Math.sqrt(dx * dx + dy * dy);

        if (distance < 120) {

            return false;
        }
    }

    return true;
}
}