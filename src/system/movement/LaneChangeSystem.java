package system.movement;

import config.Constants;
import model.intersection.IntersectionLayout;
import model.vehicle.Vehicle;
import util.Lane;

import java.util.List;

/**
 * Hệ thống đổi làn.
 * Dùng IntersectionLayout để lấy lane center đích.
 */
public class LaneChangeSystem {

    public void updateLaneChanging(Vehicle vehicle, IntersectionLayout layout) {
        if (!vehicle.isChangingLane()) return;

        double smooth = Constants.LANE_CHANGE_SMOOTH;
        util.Direction dir = vehicle.getDirection();
        boolean isFiveWay = layout.getDirections().size() == 5;

        if (!isFiveWay && (dir == util.Direction.NORTH || dir == util.Direction.SOUTH || dir == util.Direction.EAST || dir == util.Direction.WEST)) {
            switch (dir) {
                case NORTH:
                case SOUTH: {
                    int targetX = layout.getLaneCenterX(dir, vehicle.getTargetLane());
                    if (targetX == 0) return;
                    if (Math.abs(vehicle.getX() - targetX) < Constants.LANE_CHANGE_SNAP_THRESHOLD) {
                        vehicle.setX(targetX);
                        vehicle.setLane(vehicle.getTargetLane());
                        vehicle.setChangingLane(false);
                        return;
                    }
                    vehicle.setX(vehicle.getX() + (vehicle.getX() < targetX ? smooth : -smooth));
                    break;
                }
                case EAST:
                case WEST: {
                    int targetY = layout.getLaneCenterY(dir, vehicle.getTargetLane());
                    if (targetY == 0) return;
                    if (Math.abs(vehicle.getY() - targetY) < Constants.LANE_CHANGE_SNAP_THRESHOLD) {
                        vehicle.setY(targetY);
                        vehicle.setLane(vehicle.getTargetLane());
                        vehicle.setChangingLane(false);
                        return;
                    }
                    vehicle.setY(vehicle.getY() + (vehicle.getY() < targetY ? smooth : -smooth));
                    break;
                }
                default:
                    break;
            }
        } else {
            double rad = Math.toRadians(dir.toAngleDeg());
            double fx = Math.cos(rad);
            double fy = Math.sin(rad);
            // Sửa vector pháp tuyến (chỉ về đúng lề phải thay vì lề trái gây đi lên cỏ)
            double nx = -fy; 
            double ny = fx;
            double targetOffset = (vehicle.getTargetLane() == util.Lane.RIGHT) ? 25.0 : -25.0;
            double linePX = layout.getCx() + nx * targetOffset;
            double linePY = layout.getCy() + ny * targetOffset;
            double dx = vehicle.getX() - linePX;
            double dy = vehicle.getY() - linePY;
            double dist = dx * nx + dy * ny;
            if (Math.abs(dist) < Constants.LANE_CHANGE_SNAP_THRESHOLD) {
                vehicle.setX(vehicle.getX() - dist * nx);
                vehicle.setY(vehicle.getY() - dist * ny);
                vehicle.setLane(vehicle.getTargetLane());
                vehicle.setChangingLane(false);
            } else {
                vehicle.setX(vehicle.getX() - Math.signum(dist) * smooth * nx);
                vehicle.setY(vehicle.getY() - Math.signum(dist) * smooth * ny);
            }
        }
    }

    public boolean tryChangeLane(Vehicle current, List<Vehicle> vehicles) {
        if (current.isChangingLane() || current.isTurning()) return false;
        if (current.getLaneChangeCooldown() > 0) return false;

        Lane targetLane = (current.getLane() == Lane.LEFT) ? Lane.RIGHT : Lane.LEFT;

        for (Vehicle other : vehicles) {
            if (other == current) continue;
            if (other.getDirection() != current.getDirection()) continue;
            if (other.getLane() == targetLane && distance(current, other) < Constants.LANE_CHANGE_SAFE_DIST)
                return false;
        }

        current.setTargetLane(targetLane);
        current.setChangingLane(true);
        current.setLaneChangeCooldown(Constants.LANE_CHANGE_COOLDOWN);
        return true;
    }

    public double distance(Vehicle a, Vehicle b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
}