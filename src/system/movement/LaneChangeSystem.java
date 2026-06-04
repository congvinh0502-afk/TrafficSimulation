package system.movement;

import config.Constants;
import manager.LaneManager;
import model.vehicle.Vehicle;
import util.Lane;

import java.util.List;

/** Đổi làn — tương thích với NetworkLayout. */
public class LaneChangeSystem {

    public void updateLaneChanging(Vehicle v) {
        if (!v.isChangingLane()) return;

        switch (v.getDirection()) {
            case NORTH: case SOUTH: {
                int tx = LaneManager.getLaneCenterX(v.getDirection(), v.getHomeIntersectionX());
                double diff = tx - v.getX();
                if (Math.abs(diff) < Constants.LANE_CHANGE_SNAP_THRESH) {
                    v.setX(tx); v.setChangingLane(false); v.setLaneChangeCooldown(Constants.LANE_CHANGE_COOLDOWN);
                } else {
                    v.setX(v.getX() + Math.signum(diff) * Math.min(Constants.LANE_CHANGE_SMOOTH, Math.abs(diff)));
                }
                break;
            }
            case EAST: case WEST: {
                int ty = LaneManager.getLaneCenterY(v.getDirection(), v.getHomeIntersectionY());
                double diff = ty - v.getY();
                if (Math.abs(diff) < Constants.LANE_CHANGE_SNAP_THRESH) {
                    v.setY(ty); v.setChangingLane(false); v.setLaneChangeCooldown(Constants.LANE_CHANGE_COOLDOWN);
                } else {
                    v.setY(v.getY() + Math.signum(diff) * Math.min(Constants.LANE_CHANGE_SMOOTH, Math.abs(diff)));
                }
                break;
            }
            default: v.setChangingLane(false); break;
        }
    }

    public boolean tryChangeLane(Vehicle cur, List<Vehicle> all) {
        if (cur.getLaneChangeCooldown() > 0 || cur.isChangingLane()) return false;
        // Trong right-hand traffic, chỉ có 1 làn mỗi chiều → không đổi làn
        return false;
    }

    public double distance(Vehicle a, Vehicle b) {
        double dx = a.getX()-b.getX(), dy = a.getY()-b.getY();
        return Math.sqrt(dx*dx + dy*dy);
    }
}
