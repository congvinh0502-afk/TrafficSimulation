package system.traffic;

import config.Constants;
import math.Vector2D;
import model.trafficlight.LightColor;
import model.trafficlight.TrafficLight;
import model.vehicle.Vehicle;
import system.collision.CollisionSystem;
import util.Direction;

import java.util.List;

/**
 * Hệ thống quy tắc giao thông — đèn đỏ gây phanh dần, không dừng ngay.
 *
 * <p>Thay đổi so với phiên bản cũ:
 * <ul>
 *   <li>Khi tiến vào vùng đèn đỏ, đặt {@code acceleration} âm tỉ lệ
 *       với khoảng cách còn lại đến vạch dừng.</li>
 *   <li>Chỉ khi đã đủ gần (speed ≈ 0 và gần vạch) mới set stopped=true.</li>
 *   <li>THREE_WAY không có đèn ngang — không áp dụng đèn ngang.</li>
 * </ul>
 * </p>
 */
public class TrafficRuleSystem {

    private final CollisionSystem collisionSystem;

    public TrafficRuleSystem() {
        this.collisionSystem = new CollisionSystem();
    }

    public void checkTrafficLight(Vehicle vehicle,
                                  TrafficLight verticalLight,
                                  TrafficLight horizontalLight,
                                  List<Vehicle> vehicles) {
        if (vehicle.isTurning()) return;

        Direction dir     = vehicle.getDirection();
        TrafficLight light = selectLight(dir, verticalLight, horizontalLight);
        if (light == null) return;

        double distToStop = distanceToStopLine(vehicle, dir);

        if (distToStop > Constants.BRAKE_START_DISTANCE) {
            // Xa đèn — không ảnh hưởng từ đèn
            return;
        }

        boolean mustStop   = shouldStopForLight(vehicle, vehicles, light);
        boolean blockedInt = !collisionSystem.canEnterIntersection(vehicle, vehicles);

        if (mustStop || blockedInt) {
            if (distToStop <= 0 || vehicle.getSpeed() < 0.3) {
                // Đã đến vạch dừng hoặc gần như dừng
                vehicle.setStopped(true);
                vehicle.setAcceleration(0);
            } else {
                // Giảm tốc dần tỉ lệ khoảng cách
                double ratio  = Math.max(0, Math.min(1, 1.0 - distToStop / Constants.BRAKE_START_DISTANCE));
                double decel  = -Constants.MAX_BRAKE_DECEL * (0.3 + 0.7 * ratio);
                vehicle.setAcceleration(decel);
            }
        }
        // else: acceleration không bị thay đổi ở đây
        // (CollisionSystem hoặc VehicleMovementSystem quản lý acceleration bình thường)
    }

    // --------------------------------------------------------
    // Tiện ích nội bộ
    // --------------------------------------------------------

    private TrafficLight selectLight(Direction dir,
                                     TrafficLight vertical,
                                     TrafficLight horizontal) {
        switch (dir) {
            case NORTH: case SOUTH:   return vertical;
            case EAST:  case WEST:    return horizontal;
            default:                  return null; // NORTHEAST không có đèn
        }
    }

    private boolean shouldStopForLight(Vehicle vehicle,
                                        List<Vehicle> vehicles,
                                        TrafficLight light) {
        if (vehicle.getBehavior() != null) {
            return vehicle.getBehavior().shouldStop(vehicle, vehicles, light);
        }
        return light.getColor() == LightColor.RED;
    }

    /**
     * Khoảng cách px từ mũi xe đến vạch dừng.
     * Giá trị âm nghĩa là đã qua vạch.
     */
    private double distanceToStopLine(Vehicle vehicle, Direction dir) {
        int cl = Constants.INTERSECTION_CHECK_LEFT;
        int cr = Constants.INTERSECTION_CHECK_RIGHT;
        int ct = Constants.INTERSECTION_CHECK_TOP;
        int cb = Constants.INTERSECTION_CHECK_BOTTOM;
        int off = Constants.STOP_SENSOR_OFFSET;

        switch (dir) {
            case SOUTH: return ct - (vehicle.getY() + off);
            case NORTH: return (vehicle.getY() - off) - cb;
            case EAST:  return cl - (vehicle.getX() + off);
            case WEST:  return (vehicle.getX() - off) - cr;
            default:    return Double.MAX_VALUE;
        }
    }
}
