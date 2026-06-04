package manager;

import java.util.ArrayList;
import java.util.List;

import config.Constants;
import model.intersection.IntersectionLayout;
import model.vehicle.Ambulance;
import model.vehicle.Bicycle;
import model.vehicle.Car;
import model.vehicle.FireTruck;
import model.vehicle.Motorbike;
import model.vehicle.Vehicle;
import strategy.driver.AggressiveDriver;
import strategy.driver.DriverBehavior;
import strategy.driver.NormalDriver;
import util.Direction;
import util.Lane;
import util.TurnType;

/**
 * Quản lý việc spawn và xóa phương tiện.
 * Dùng IntersectionLayout để lấy tọa độ spawn và lane center chính xác
 * cho từng loại giao lộ.
 */
public class VehicleSpawnManager {

    private static final double AGGRESSIVE_PROBABILITY = 0.3;

    private final List<Vehicle> vehicles;
    private IntersectionLayout layout;

    public VehicleSpawnManager(List<Vehicle> vehicles, IntersectionLayout layout) {
        this.vehicles = vehicles;
        this.layout = layout;
    }

    public void setLayout(IntersectionLayout layout) {
        this.layout = layout;
    }

    // ==========================================================
    // Spawn
    // ==========================================================

    public void spawnRandomVehicle(List<Direction> validDirections) {
        Direction direction = randomFrom(validDirections);
        double spawnX = layout.getSpawnX(direction);
        double spawnY = layout.getSpawnY(direction);

        if (!canSpawn(spawnX, spawnY))
            return;
        Vehicle vehicle = createRandomVehicle(direction, spawnX, spawnY);
        setupVehicle(vehicle);
        vehicles.add(vehicle);
    }

    // ----------------------------------------------------------

    private Vehicle createRandomVehicle(Direction dir, double x, double y) {
        int type = (int) (Math.random() * 5);
        switch (type) {
            case 0:
                return new Car(x, y, dir);
            case 1:
                return new Motorbike(x, y, dir);
            case 2:
                return new Bicycle(x, y, dir);
            case 3:
                return new Ambulance(x, y, dir);
            default:
                return new FireTruck(x, y, dir);
        }
    }

    private void setupVehicle(Vehicle vehicle) {
        setupLane(vehicle);
        setupTurnType(vehicle);
        setupDriverBehavior(vehicle);
    }

    private void setupLane(Vehicle vehicle) {
        Lane lane = (Math.random() < 0.5) ? Lane.LEFT : Lane.RIGHT;
        vehicle.setLane(lane);
        Direction dir = vehicle.getDirection();

        boolean isFiveWay = layout.getDirections().size() == 5;

        if (!isFiveWay && (dir == Direction.NORTH || dir == Direction.SOUTH || dir == Direction.EAST
                || dir == Direction.WEST)) {
            if (dir == Direction.NORTH || dir == Direction.SOUTH) {
                vehicle.setX(layout.getLaneCenterX(dir, lane));
            } else {
                vehicle.setY(layout.getLaneCenterY(dir, lane));
            }
        } else {
            double rad = Math.toRadians(dir.toAngleDeg());
            double fx = Math.cos(rad);
            double fy = Math.sin(rad);
            // Sửa vector pháp tuyến (chỉ về đúng lề phải thay vì lề trái gây đi lên cỏ)
            double nx = -fy;
            double ny = fx;
            double laneOffset = (lane == Lane.RIGHT) ? 25.0 : -25.0;
            vehicle.setX(vehicle.getX() + nx * laneOffset);
            vehicle.setY(vehicle.getY() + ny * laneOffset);
        }
    }

    private void setupTurnType(Vehicle vehicle) {
        double r = Math.random();
        if (r < 0.33)
            vehicle.setTurnType(TurnType.LEFT);
        else if (r < 0.66)
            vehicle.setTurnType(TurnType.RIGHT);
        else
            vehicle.setTurnType(TurnType.STRAIGHT);
    }

    private void setupDriverBehavior(Vehicle vehicle) {
        if (vehicle instanceof Ambulance || vehicle instanceof FireTruck)
            return;
        if (vehicle instanceof Motorbike)
            return;

        DriverBehavior behavior = (Math.random() < AGGRESSIVE_PROBABILITY)
                ? new AggressiveDriver()
                : new NormalDriver();
        vehicle.setBehavior(behavior);
        vehicle.setSpeed(behavior.getSpeed());
        vehicle.setMaxSpeed(behavior.getSpeed());
    }

    // ==========================================================
    // Xóa xe
    // ==========================================================

    public void removeOutsideVehicles() {
        List<Vehicle> toRemove = new ArrayList<>();
        for (Vehicle v : vehicles) {
            if (v.getX() < Constants.REMOVE_THRESHOLD_MIN
                    || v.getX() > Constants.REMOVE_THRESHOLD_MAX
                    || v.getY() < Constants.REMOVE_THRESHOLD_MIN
                    || v.getY() > Constants.REMOVE_THRESHOLD_MAX) {
                toRemove.add(v);
            }
        }
        for (Vehicle v : toRemove) {
            SoundManager.getInstance().onVehicleRemoved(v);
        }
        vehicles.removeAll(toRemove);
    }

    // ==========================================================
    // Tiện ích
    // ==========================================================

    private boolean canSpawn(double x, double y) {
        for (Vehicle v : vehicles) {
            double dx = v.getX() - x;
            double dy = v.getY() - y;
            if (Math.sqrt(dx * dx + dy * dy) < Constants.SPAWN_MIN_DISTANCE)
                return false;
        }
        return true;
    }

    private <T> T randomFrom(List<T> list) {
        return list.get((int) (Math.random() * list.size()));
    }
}