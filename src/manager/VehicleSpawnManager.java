package manager;

import java.util.ArrayList;
import java.util.List;
import layout.IntersectionLayout;
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

public class VehicleSpawnManager {

    private final List<Vehicle> vehicles;
    private final SoundManager soundManager = SoundManager.getInstance();

    public VehicleSpawnManager(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    // ─────────────────────────────────────────────────────────────
    // SPAWN RANDOM (dùng cho auto-spawn)
    // ─────────────────────────────────────────────────────────────

    public void spawnRandomVehicle(List<Direction> directions) {
        Direction direction = directions.get(
                (int)(Math.random() * directions.size())
        );
        java.awt.Point spawn = LaneManager.getLayout().getSpawnPoint(direction);
        if (!canSpawn(spawn.x, spawn.y)) return;

        Vehicle vehicle = createRandom(direction);
        setupVehicle(vehicle, directions);
        addVehicleWithSound(vehicle);
    }

    // ─────────────────────────────────────────────────────────────
    // SPAWN QUEUE
    // ─────────────────────────────────────────────────────────────

    public void spawnTrafficQueue(
            Direction direction,
            int amount,
            List<Direction> availableDirections
    ) {
        IntersectionLayout layout  = LaneManager.getLayout();
        java.awt.Point     spawn   = layout.getSpawnPoint(direction);
        double             spacing = layout.getQueueSpacing();

        for (int i = 0; i < amount; i++) {
            Vehicle vehicle = createRandom(direction);
            vehicle.setX(spawn.x);
            vehicle.setY(spawn.y);
            setupVehicle(vehicle, availableDirections);

            switch (direction) {
                case NORTH:     vehicle.setY(vehicle.getY() + i * spacing); break;
                case SOUTH:     vehicle.setY(vehicle.getY() - i * spacing); break;
                case EAST:      vehicle.setX(vehicle.getX() - i * spacing); break;
                case WEST:      vehicle.setX(vehicle.getX() + i * spacing); break;
                case NORTHEAST:
                    vehicle.setX(vehicle.getX() - i * spacing * 0.707);
                    vehicle.setY(vehicle.getY() + i * spacing * 0.707);
                    break;
                default: break;
            }

            addVehicleWithSound(vehicle);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // SPAWN EMERGENCY
    // ─────────────────────────────────────────────────────────────

    public void spawnEmergencyVehicle(List<Direction> availableDirections) {
        Direction direction = availableDirections.get(
                (int)(Math.random() * availableDirections.size())
        );
        java.awt.Point spawn = LaneManager.getLayout().getSpawnPoint(direction);
        if (!canSpawn(spawn.x, spawn.y)) return;

        Vehicle vehicle = createEmergency(direction);
        setupVehicle(vehicle, availableDirections);
        addVehicleWithSound(vehicle);
    }

    // ─────────────────────────────────────────────────────────────
    // REMOVE
    // ─────────────────────────────────────────────────────────────

    public void removeOutsideVehicles() {
        // Dùng iterator thay vì removeIf để có thể gọi sound trước khi xóa
        List<Vehicle> toRemove = new ArrayList<>();
        for (Vehicle v : vehicles) {
            if (v.getX() < -200 || v.getX() > 1400
             || v.getY() < -200 || v.getY() > 1400) {
                toRemove.add(v);
            }
        }
        for (Vehicle v : toRemove) {
            removeVehicleWithSound(v);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // HELPER — thêm/xóa kèm sound
    // ─────────────────────────────────────────────────────────────

    /** Thêm xe vào danh sách và báo SoundManager. */
    private void addVehicleWithSound(Vehicle vehicle) {
        vehicles.add(vehicle);
        soundManager.onVehicleSpawned(vehicle.getSoundKey());
    }

    /** Xóa xe khỏi danh sách và báo SoundManager. */
    private void removeVehicleWithSound(Vehicle vehicle) {
        vehicles.remove(vehicle);
        soundManager.onVehicleRemoved(vehicle.getSoundKey());
    }

    // ─────────────────────────────────────────────────────────────
    // CREATE VEHICLE
    // ─────────────────────────────────────────────────────────────

    private Vehicle createRandom(Direction direction) {
        java.awt.Point spawn = LaneManager.getLayout().getSpawnPoint(direction);
        int type = (int)(Math.random() * 3);
        switch (type) {
            case 0: return new Car      (spawn.x, spawn.y, direction);
            case 1: return new Motorbike(spawn.x, spawn.y, direction);
            default:return new Bicycle  (spawn.x, spawn.y, direction);
        }
    }

    private Vehicle createEmergency(Direction direction) {
        java.awt.Point spawn = LaneManager.getLayout().getSpawnPoint(direction);
        if (Math.random() < 0.5) {
            return new Ambulance(spawn.x, spawn.y, direction);
        } else {
            return new FireTruck(spawn.x, spawn.y, direction);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // SETUP
    // ─────────────────────────────────────────────────────────────

    private void setupVehicle(Vehicle vehicle, List<Direction> availableDirections) {
        setupTurnType(vehicle, availableDirections);
        setupLane(vehicle);
        setupDriverBehavior(vehicle);
    }

    private void setupLane(Vehicle vehicle) {
        Lane lane = vehicle.getTurnType() == TurnType.LEFT ? Lane.LEFT : Lane.RIGHT;
        vehicle.setLane(lane);

        switch (vehicle.getDirection()) {
            case NORTH:
            case SOUTH:
                vehicle.setX(LaneManager.getLaneCenterX(vehicle.getDirection(), lane));
                break;
            case EAST:
            case WEST:
                vehicle.setY(LaneManager.getLaneCenterY(vehicle.getDirection(), lane));
                break;
            case NORTHEAST:
                double angleRad = Math.toRadians(-18);
                double normalAngle = angleRad + Math.PI / 2;
                double laneOffset = (lane == Lane.LEFT) ? -20 : 20;
                vehicle.setX(vehicle.getX() + laneOffset * Math.cos(normalAngle));
                vehicle.setY(vehicle.getY() + laneOffset * Math.sin(normalAngle));
                break;
        }
    }

    private void setupTurnType(Vehicle vehicle, List<Direction> availableDirections) {
        double random = Math.random();
        TurnType turnType;

        if      (random < 0.33) turnType = TurnType.LEFT;
        else if (random < 0.66) turnType = TurnType.RIGHT;
        else                    turnType = TurnType.STRAIGHT;

        Direction target = vehicle.getDirection();
        switch (turnType) {
            case LEFT:  target = util.DirectionHelper.getLeftDirection (vehicle.getDirection()); break;
            case RIGHT: target = util.DirectionHelper.getRightDirection(vehicle.getDirection()); break;
            default: break;
        }

        if (!availableDirections.contains(target)) {
            turnType = TurnType.STRAIGHT;
        }

        vehicle.setTurnType(turnType);
    }

    private void setupDriverBehavior(Vehicle vehicle) {
        if (vehicle instanceof Ambulance || vehicle instanceof FireTruck) return;
        if (vehicle instanceof Motorbike) return;

        DriverBehavior b = Math.random() < 0.3 ? new AggressiveDriver() : new NormalDriver();
        vehicle.setBehavior(b);
        vehicle.setSpeed(b.getSpeed());
    }

    // ─────────────────────────────────────────────────────────────
    // CAN SPAWN
    // ─────────────────────────────────────────────────────────────

    private boolean canSpawn(double x, double y) {
        for (Vehicle v : vehicles) {
            double dx = v.getX() - x;
            double dy = v.getY() - y;
            if (Math.sqrt(dx*dx + dy*dy) < 150) return false;
        }
        return true;
    }
}