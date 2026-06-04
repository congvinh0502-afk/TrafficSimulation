package system.collision;

import config.Constants;
import model.network.NetworkLayout;
import model.vehicle.Vehicle;
import system.movement.LaneChangeSystem;

import java.util.List;

/** Khoảng cách an toàn + hitbox overlap. */
public class CollisionSystem {

    private final LaneChangeSystem laneChange = new LaneChangeSystem();

    public void maintainDistance(Vehicle cur, List<Vehicle> all) {
        if (cur.isTurning() || cur.isChangingLane()) return;

        Vehicle leader = findLeader(cur, all);
        if (leader == null) {
            cur.setAcceleration(Constants.DEFAULT_ACCELERATION);
            cur.setStopped(false);
            return;
        }

        double gap  = gap(cur, leader);
        double safe = cur.getWidth() * 2.2;
        double brkS = Constants.BRAKE_START_DISTANCE;

        if (gap <= Constants.MIN_FOLLOW_DISTANCE) {
            cur.setStopped(true); cur.setAcceleration(0);
        } else if (gap < safe) {
            double r = 1.0 - gap/safe;
            cur.setAcceleration(-Constants.MAX_BRAKE_DECEL * r);
            if (cur.getSpeed() < cur.getMaxSpeed()*0.3) {
                if (!laneChange.tryChangeLane(cur, all) && cur.getSpeed()<0.5)
                    cur.setStopped(true);
            }
        } else if (gap < brkS) {
            double r = (brkS-gap)/(brkS-safe);
            cur.setAcceleration(-Constants.MAX_BRAKE_DECEL*0.4*r);
        } else {
            cur.setAcceleration(Constants.DEFAULT_ACCELERATION);
        }
    }

    public boolean hasOverlap(Vehicle cur, List<Vehicle> all) {
        Hitbox hb = Hitbox.of(cur);
        for (Vehicle o : all) { if (o==cur) continue; if (Hitbox.of(o).intersects(hb)) return true; }
        return false;
    }

    public boolean canEnterIntersection(Vehicle v, List<Vehicle> all) {
        for (Vehicle o : all) {
            if (o==v) continue;
            if (blocking(v, o)) return false;
        }
        return true;
    }

    // ── Helpers ────────────────────────────────────────────────
    private Vehicle findLeader(Vehicle cur, List<Vehicle> all) {
        double best = Double.MAX_VALUE; Vehicle leader = null;
        var dir = cur.getDirectionVector();
        for (Vehicle o : all) {
            if (o==cur || o.isTurning()) continue;
            if (o.getDirection() != cur.getDirection()) continue;
            double dx=o.getX()-cur.getX(), dy=o.getY()-cur.getY();
            double fwd = dx*dir.x + dy*dir.y;
            if (fwd <= 0) continue;
            double lat = Math.abs(dx*(-dir.y) + dy*dir.x);
            if (lat > Constants.SAME_FILE_TOLERANCE*1.6) continue;
            if (fwd < best) { best=fwd; leader=o; }
        }
        return leader;
    }

    private double gap(Vehicle cur, Vehicle leader) {
        var dir = cur.getDirectionVector();
        double dx=leader.getX()-cur.getX(), dy=leader.getY()-cur.getY();
        double cd = dx*dir.x + dy*dir.y;
        return Math.max(0, cd - (cur.getWidth()+leader.getWidth())/2.0);
    }

    private boolean blocking(Vehicle v, Vehicle o) {
        int RH = NetworkLayout.ROAD_HALF + 20;
        int LA = Constants.LOOKAHEAD_DISTANCE;
        int TO = Constants.SAME_LANE_TOLERANCE;
        switch (v.getDirection()) {
            case SOUTH: return Math.abs(o.getX()-v.getX())<TO && inBox(o.getY(), -RH, RH) && o.getY()-v.getY()<LA;
            case NORTH: return Math.abs(o.getX()-v.getX())<TO && inBox(o.getY(), -RH, RH) && v.getY()-o.getY()<LA;
            case EAST:  return Math.abs(o.getY()-v.getY())<TO && inBox(o.getX(), -RH, RH) && o.getX()-v.getX()<LA;
            case WEST:  return Math.abs(o.getY()-v.getY())<TO && inBox(o.getX(), -RH, RH) && v.getX()-o.getX()<LA;
            default:    return false;
        }
    }

    private boolean inBox(double v, int lo, int hi) { return v>lo && v<hi; }
}
