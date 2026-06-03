package manager;

import java.util.ArrayList;
import java.util.List;

import config.Constants;
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
 * Quản lý việc spawn và xóa phương tiện trên bản đồ.
 *
 * <p>
 * Mỗi lần spawn:
 * <ol>
 * <li>Chọn ngẫu nhiên loại xe và hướng hợp lệ.</li>
 * <li>Kiểm tra khoảng cách tối thiểu — bỏ qua nếu vị trí đã có xe.</li>
 * <li>Thiết lập làn đường, loại rẽ, và chiến lược lái.</li>
 * </ol>
 * </p>
 */
public class VehicleSpawnManager {

    /** Xác suất để xe không phải xe máy có kiểu lái hung hăng. */
    private static final double AGGRESSIVE_PROBABILITY = 0.3;

    private final List<Vehicle> vehicles;

    /**
     * @param vehicles danh sách xe chung — được cập nhật trực tiếp
     */
    public VehicleSpawnManager(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    // ==========================================================
    // Spawn
    // ==========================================================

    /**
     * Spawn một xe ngẫu nhiên từ danh sách hướng hợp lệ.
     * Không làm gì nếu vị trí spawn quá gần xe hiện có.
     *
     * @param validDirections các hướng được phép spawn (theo loại ngã rẽ)
     */
    public void spawnRandomVehicle(List<Direction> validDirections) {
        Direction direction = randomFrom(validDirections);
        double spawnX = getSpawnX(direction);
        double spawnY = getSpawnY(direction);

        if (!canSpawn(spawnX, spawnY)) {
            return;
        }

        Vehicle vehicle = createRandomVehicle(direction);
        setupVehicle(vehicle);
        vehicles.add(vehicle);
    }

    // ----------------------------------------------------------
    // Tạo xe
    // ----------------------------------------------------------

    private Vehicle createRandomVehicle(Direction direction) {
        int type = (int) (Math.random() * 5);
        switch (type) {
            case 0:
                return new Car(getSpawnX(direction), getSpawnY(direction), direction);
            case 1:
                return new Motorbike(getSpawnX(direction), getSpawnY(direction), direction);
            case 2:
                return new Bicycle(getSpawnX(direction), getSpawnY(direction), direction);
            case 3:
                return new Ambulance(getSpawnX(direction), getSpawnY(direction), direction);
            default:
                return new FireTruck(getSpawnX(direction), getSpawnY(direction), direction);
        }
    }

    // ----------------------------------------------------------
    // Thiết lập xe sau khi tạo
    // ----------------------------------------------------------

    private void setupVehicle(Vehicle vehicle) {
        setupLane(vehicle);
        setupTurnType(vehicle);
        setupDriverBehavior(vehicle);
    }

    /**
     * Snap xe vào trung tâm làn ngẫu nhiên và lưu thông tin làn.
     */
    private void setupLane(Vehicle vehicle) {
        Lane lane = (Math.random() < 0.5) ? Lane.LEFT : Lane.RIGHT;
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
            default:
                break;
        }
    }

    /** Gán loại rẽ ngẫu nhiên theo tỉ lệ đều. */
    private void setupTurnType(Vehicle vehicle) {
        double r = Math.random();
        if (r < 0.33)
            vehicle.setTurnType(TurnType.LEFT);
        else if (r < 0.66)
            vehicle.setTurnType(TurnType.RIGHT);
        else
            vehicle.setTurnType(TurnType.STRAIGHT);
    }

    /**
     * Giữ nguyên behavior của xe ưu tiên và xe máy (đã gán trong constructor).
     * Đối với ô tô / xe đạp: random giữa NormalDriver và AggressiveDriver.
     */
    private void setupDriverBehavior(Vehicle vehicle) {
        if (vehicle instanceof Ambulance || vehicle instanceof FireTruck) {
            return; // EmergencyDriver đã gán, không ghi đè
        }
        if (vehicle instanceof Motorbike) {
            return; // AggressiveDriver đã gán, không ghi đè
        }

        DriverBehavior behavior = (Math.random() < AGGRESSIVE_PROBABILITY)
                ? new AggressiveDriver()
                : new NormalDriver();

        vehicle.setBehavior(behavior);
        vehicle.setSpeed(behavior.getSpeed());
        vehicle.setMaxSpeed(behavior.getSpeed());
    }

    // ----------------------------------------------------------
    // Tọa độ spawn theo hướng
    // ----------------------------------------------------------

    private double getSpawnX(Direction direction) {
        switch (direction) {
            case NORTH:
                return Constants.LANE_NORTH_RIGHT_X; // giữa đường dọc
            case SOUTH:
                return Constants.LANE_SOUTH_RIGHT_X;
            case EAST:
                return Constants.SPAWN_OFFSCREEN_NEGATIVE;
            case WEST:
                return Constants.SPAWN_OFFSCREEN_POSITIVE;
            case NORTHEAST:
                return Constants.SPAWN_OFFSCREEN_NEGATIVE;
            default:
                return 0;
        }
    }

    private double getSpawnY(Direction direction) {
        switch (direction) {
            case NORTH:
                return Constants.SPAWN_OFFSCREEN_POSITIVE;
            case SOUTH:
                return Constants.SPAWN_OFFSCREEN_NEGATIVE;
            case EAST:
                return Constants.LANE_EAST_RIGHT_Y;
            case WEST:
                return Constants.LANE_WEST_RIGHT_Y;
            case NORTHEAST:
                return Constants.SPAWN_OFFSCREEN_POSITIVE;
            default:
                return 0;
        }
    }

    // ==========================================================
    // Xóa xe ra ngoài bản đồ
    // ==========================================================

    /**
     * Xóa các xe đã ra ngoài biên bản đồ.
     * Gọi mỗi frame sau khi cập nhật vị trí.
     */
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
         // ← THÊM: tắt tiếng trước khi xóa
        for (Vehicle v : toRemove) {
            SoundManager.getInstance().onVehicleRemoved(v);
        }
        vehicles.removeAll(toRemove);
    }

    // ==========================================================
    // Tiện ích
    // ==========================================================

    /** Kiểm tra có đủ khoảng cách để spawn tại (x, y) không. */
    private boolean canSpawn(double x, double y) {
        for (Vehicle v : vehicles) {
            double dx = v.getX() - x;
            double dy = v.getY() - y;
            if (Math.sqrt(dx * dx + dy * dy) < Constants.SPAWN_MIN_DISTANCE) {
                return false;
            }
        }
        return true;
    }

    /** Chọn ngẫu nhiên một phần tử từ list. */
    private <T> T randomFrom(List<T> list) {
        return list.get((int) (Math.random() * list.size()));
    }
}