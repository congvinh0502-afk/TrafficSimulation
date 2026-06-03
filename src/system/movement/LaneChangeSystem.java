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

        switch (vehicle.getDirection()) {
            case NORTH:
            case SOUTH: {
                int targetX = layout.getLaneCenterX(vehicle.getDirection(), vehicle.getTargetLane());
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
                int targetY = layout.getLaneCenterY(vehicle.getDirection(), vehicle.getTargetLane());
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