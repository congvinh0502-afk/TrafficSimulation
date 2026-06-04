package system.traffic;

import config.Constants;
import model.intersection.IntersectionLayout;
import model.trafficlight.LightColor;
import model.trafficlight.TrafficLight;
import model.vehicle.Vehicle;
import system.collision.CollisionSystem;
import util.Direction;

import java.util.List;

/**
 * Hệ thống quy tắc giao thông.
 * Dùng IntersectionLayout để lấy vùng check và stop line chính xác.
 */
public class TrafficRuleSystem {

    private final CollisionSystem collisionSystem;

    public TrafficRuleSystem() {
        this.collisionSystem = new CollisionSystem();
    }

    public void checkTrafficLight(Vehicle vehicle,
                                  TrafficLight verticalLight,
                                  TrafficLight horizontalLight,
                                  List<Vehicle> vehicles,
                                  IntersectionLayout layout) {
        if (vehicle.isTurning()) return;

        Direction dir = vehicle.getDirection();
        // Chỉ áp dụng đèn ngang nếu layout có hướng EAST/WEST
        TrafficLight light = selectLight(dir, verticalLight, horizontalLight, layout);
        if (light == null) return;

        double distToStop = distanceToStopLine(vehicle, dir, layout);
        if (distToStop > Constants.BRAKE_START_DISTANCE) return;

        boolean mustStop   = shouldStopForLight(vehicle, vehicles, light);
        boolean blockedInt = !collisionSystem.canEnterIntersection(vehicle, vehicles, layout);

        if (mustStop || blockedInt) {
            if (distToStop <= 0 || vehicle.getSpeed() < 0.3) {
                vehicle.setStopped(true);
                vehicle.setAcceleration(0);
            } else {
                double ratio = Math.max(0, Math.min(1, 1.0 - distToStop / Constants.BRAKE_START_DISTANCE));
                vehicle.setAcceleration(-Constants.MAX_BRAKE_DECEL * (0.3 + 0.7 * ratio));
            }
        }
    }

    // --------------------------------------------------------

    private TrafficLight selectLight(Direction dir, TrafficLight vertical, TrafficLight horizontal,
            IntersectionLayout layout) {
        switch (dir) {
            case NORTH:
            case SOUTH:
            case FW_IN_342:
            case FW_IN_126:
                return vertical;
            case EAST:
            case WEST:
            case FW_IN_54:
            case FW_IN_198:
                return (layout.hasDirection(Direction.EAST) && layout.hasDirection(Direction.WEST))
                        || layout.hasDirection(Direction.FW_IN_54) ? horizontal : null;
            default:
                return null;
        }
    }

    private boolean shouldStopForLight(Vehicle vehicle, List<Vehicle> vehicles, TrafficLight light) {
        if (vehicle.getBehavior() != null) {
            return vehicle.getBehavior().shouldStop(vehicle, vehicles, light);
        }
        return light.getColor() == LightColor.RED;
    }

    private double distanceToStopLine(Vehicle vehicle, Direction dir, IntersectionLayout layout) {
        int off = Constants.STOP_SENSOR_OFFSET;
        boolean isFiveWay = layout.getDirections().size() == 5;

        if (!isFiveWay) {
            int cl = layout.getCheckLeft(), cr = layout.getCheckRight();
            int ct = layout.getCheckTop(), cb = layout.getCheckBottom();
            switch (dir) {
                case SOUTH:
                    return ct - (vehicle.getY() + off);
                case NORTH:
                    return (vehicle.getY() - off) - cb;
                case EAST:
                    return cl - (vehicle.getX() + off);
                case WEST:
                    return (vehicle.getX() - off) - cr;
                default:
                    return Double.MAX_VALUE;
            }
        } else {
            // Toán học tính khoảng cách dừng xe bằng dot product cho góc chéo
            double dx = layout.getCx() - vehicle.getX();
            double dy = layout.getCy() - vehicle.getY();
            double rad = Math.toRadians(dir.toAngleDeg());
            double distToCenter = dx * Math.cos(rad) + dy * Math.sin(rad);
            return distToCenter - (170 + 10);
        }
    }
}
