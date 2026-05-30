package system.traffic;

import config.Constants;
import model.trafficlight.LightColor;
import model.trafficlight.TrafficLight;
import model.vehicle.Vehicle;
import system.collision.CollisionSystem;
import util.Direction;

import java.util.List;

/**
 * Hệ thống kiểm tra đèn giao thông và quyết định dừng/đi.
 *
 * <p>
 * Mỗi frame, với từng xe:
 * <ol>
 * <li>Xác định đèn áp dụng (dọc hoặc ngang) theo hướng xe.</li>
 * <li>Kiểm tra xe có đang gần vạch dừng không.</li>
 * <li>Hỏi {@link strategy.driver.DriverBehavior#shouldStop} để lấy quyết
 * định.</li>
 * <li>Kiểm tra giao lộ phía trước có trống không
 * ({@link CollisionSystem#canEnterIntersection}).</li>
 * <li>Cập nhật trạng thái {@code stopped} của xe.</li>
 * </ol>
 * </p>
 */
public class TrafficRuleSystem {

    private final CollisionSystem collisionSystem;

    public TrafficRuleSystem() {
        this.collisionSystem = new CollisionSystem();
    }

    /**
     * Kiểm tra đèn và cập nhật trạng thái dừng/đi của xe.
     *
     * @param vehicle         xe cần kiểm tra
     * @param verticalLight   đèn cho hướng NORTH / SOUTH
     * @param horizontalLight đèn cho hướng EAST / WEST
     * @param vehicles        toàn bộ xe (để kiểm tra giao lộ)
     */
    public void checkTrafficLight(Vehicle vehicle,
            TrafficLight verticalLight,
            TrafficLight horizontalLight,
            List<Vehicle> vehicles) {
        if (vehicle.isTurning())
            return;

        Direction direction = vehicle.getDirection();
        TrafficLight relevantLight = selectLight(direction, verticalLight, horizontalLight);
        boolean nearStopLine = isNearStopLine(vehicle, direction);

        boolean mustStop = false;

        if (nearStopLine && relevantLight != null) {
            if (vehicle.getBehavior() != null) {
                mustStop = vehicle.getBehavior().shouldStop(vehicle, vehicles, relevantLight);
            } else {
                mustStop = (relevantLight.getColor() == LightColor.RED);
            }
        }

        boolean blocked = !collisionSystem.canEnterIntersection(vehicle, vehicles);

        vehicle.setStopped(mustStop || blocked);
    }

    // ----------------------------------------------------------
    // Tiện ích nội bộ
    // ----------------------------------------------------------

    /** Chọn đèn áp dụng theo hướng di chuyển. */
    private TrafficLight selectLight(Direction direction,
            TrafficLight verticalLight,
            TrafficLight horizontalLight) {
        switch (direction) {
            case NORTH:
            case SOUTH:
                return verticalLight;
            case EAST:
            case WEST:
                return horizontalLight;
            default:
                return null;
        }
    }

    /**
     * Kiểm tra xe có đang đủ gần vạch dừng để áp dụng đèn không.
     * Vùng kích hoạt rộng để xe dừng trước đèn, không phải tại đèn.
     */
    private boolean isNearStopLine(Vehicle vehicle, Direction direction) {
        int offset = Constants.STOP_SENSOR_OFFSET;
        int left = Constants.INTERSECTION_CHECK_LEFT;
        int right = Constants.INTERSECTION_CHECK_RIGHT;
        int top = Constants.INTERSECTION_CHECK_TOP;
        int bottom = Constants.INTERSECTION_CHECK_BOTTOM;

        switch (direction) {
            case SOUTH:
                return vehicle.getY() + offset >= top;
            case NORTH:
                return vehicle.getY() <= bottom;
            case EAST:
                return vehicle.getX() + offset >= left;
            case WEST:
                return vehicle.getX() <= right;
            default:
                return false;
        }
    }
}