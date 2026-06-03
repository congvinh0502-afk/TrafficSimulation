package system.collision;

import config.Constants;
import math.Vector2D;
import model.intersection.IntersectionLayout;
import model.vehicle.Vehicle;
import system.movement.LaneChangeSystem;

import java.util.List;

/**
 * Hệ thống va chạm và khoảng cách an toàn.
 * Dùng IntersectionLayout để lấy vùng check thay vì Constants hardcode.
 */
public class CollisionSystem {

    private final LaneChangeSystem laneChangeSystem;

    public CollisionSystem() {
        this.laneChangeSystem = new LaneChangeSystem();
    }

    // ==========================================================
    // Khoảng cách + gia tốc
    // ==========================================================

    public void maintainDistance(Vehicle current, List<Vehicle> vehicles) {
        if (current.isTurning() || current.isChangingLane()) return;

        Vehicle leader = findLeader(current, vehicles);

        if (leader == null) {
            current.setAcceleration(Constants.DEFAULT_ACCELERATION);
            current.setStopped(false);
            return;
        }

        double gap          = gapToLeader(current, leader);
        double safeDistance = current.getWidth() * 2.2;
        double brakeStart   = Constants.BRAKE_START_DISTANCE;

        if (gap <= Constants.MIN_FOLLOW_DISTANCE) {
            current.setStopped(true);
            current.setAcceleration(0);
        } else if (gap < safeDistance) {
            double ratio = 1.0 - (gap / safeDistance);
            current.setAcceleration(-Constants.MAX_BRAKE_DECEL * ratio);
            if (current.getSpeed() < current.getMaxSpeed() * 0.3) {
                if (!laneChangeSystem.tryChangeLane(current, vehicles)) {
                    if (current.getSpeed() < 0.5) current.setStopped(true);
                }
            }
        } else if (gap < brakeStart) {
            double ratio = (brakeStart - gap) / (brakeStart - safeDistance);
            current.setAcceleration(-Constants.MAX_BRAKE_DECEL * 0.4 * ratio);
        } else {
            current.setAcceleration(Constants.DEFAULT_ACCELERATION);
        }
    }

    // ==========================================================
    // Hitbox
    // ==========================================================

    public boolean hasOverlap(Vehicle current, List<Vehicle> vehicles) {
        Hitbox hb = Hitbox.of(current);
        for (Vehicle other : vehicles) {
            if (other == current) continue;
            if (Hitbox.of(other).intersects(hb)) return true;
        }
        return false;
    }

    // ==========================================================
    // Kiểm tra vào giao lộ — nhận layout
    // ==========================================================

    public boolean canEnterIntersection(Vehicle vehicle, List<Vehicle> vehicles,
                                        IntersectionLayout layout) {
        for (Vehicle other : vehicles) {
            if (other == vehicle) continue;
            if (isBlockingIntersection(vehicle, other, layout)) return false;
        }
        return true;
    }

    /** Overload không layout — dùng cho các caller cũ chưa migration. */
    public boolean canEnterIntersection(Vehicle vehicle, List<Vehicle> vehicles) {
        for (Vehicle other : vehicles) {
            if (other == vehicle) continue;
            if (isBlockingIntersectionFallback(vehicle, other)) return false;
        }
        return true;
    }

    // ==========================================================
    // Helpers
    // ==========================================================

    private Vehicle findLeader(Vehicle current, List<Vehicle> vehicles) {
        Vector2D dir = current.getDirectionVector();
        double closestDot = Double.MAX_VALUE;
        Vehicle leader = null;

        for (Vehicle other : vehicles) {
            if (other == current || other.isTurning()) continue;
            if (other.getDirection() != current.getDirection()) continue;

            double dx      = other.getX() - current.getX();
            double dy      = other.getY() - current.getY();
            double forward = dx * dir.x + dy * dir.y;
            if (forward <= 0) continue;

            double lateral = Math.abs(dx * (-dir.y) + dy * dir.x);
            if (lateral > Constants.SAME_FILE_TOLERANCE * 1.5) continue;

            if (forward < closestDot) { closestDot = forward; leader = other; }
        }
        return leader;
    }

    private double gapToLeader(Vehicle current, Vehicle leader) {
        Vector2D dir = current.getDirectionVector();
        double dx = leader.getX() - current.getX();
        double dy = leader.getY() - current.getY();
        double centerDist = dx * dir.x + dy * dir.y;
        return Math.max(0, centerDist - (current.getWidth() + leader.getWidth()) / 2.0);
    }

    private boolean isBlockingIntersection(Vehicle vehicle, Vehicle other,
                                            IntersectionLayout layout) {
        int cl = layout.getCheckLeft();
        int cr = layout.getCheckRight();
        int ct = layout.getCheckTop();
        int cb = layout.getCheckBottom();
        int la = Constants.LOOKAHEAD_DISTANCE;
        int to = Constants.SAME_LANE_TOLERANCE;

        switch (vehicle.getDirection()) {
            case SOUTH: return Math.abs(other.getX()-vehicle.getX())<to && other.getY()>ct && other.getY()<cb && other.getY()-vehicle.getY()<la;
            case NORTH: return Math.abs(other.getX()-vehicle.getX())<to && other.getY()>ct && other.getY()<cb && vehicle.getY()-other.getY()<la;
            case EAST:  return Math.abs(other.getY()-vehicle.getY())<to && other.getX()>cl && other.getX()<cr && other.getX()-vehicle.getX()<la;
            case WEST:  return Math.abs(other.getY()-vehicle.getY())<to && other.getX()>cl && other.getX()<cr && vehicle.getX()-other.getX()<la;
            default:    return false;
        }
    }

    /** Fallback dùng tâm giao lộ mặc định khi không có layout. */
    private boolean isBlockingIntersectionFallback(Vehicle vehicle, Vehicle other) {
        // Dùng tâm 400,400 với lw=50 buffer=70 → check 280..520
        int cl = 280, cr = 520, ct = 280, cb = 520;
        int la = Constants.LOOKAHEAD_DISTANCE;
        int to = Constants.SAME_LANE_TOLERANCE;
        switch (vehicle.getDirection()) {
            case SOUTH: return Math.abs(other.getX()-vehicle.getX())<to && other.getY()>ct && other.getY()<cb && other.getY()-vehicle.getY()<la;
            case NORTH: return Math.abs(other.getX()-vehicle.getX())<to && other.getY()>ct && other.getY()<cb && vehicle.getY()-other.getY()<la;
            case EAST:  return Math.abs(other.getY()-vehicle.getY())<to && other.getX()>cl && other.getX()<cr && other.getX()-vehicle.getX()<la;
            case WEST:  return Math.abs(other.getY()-vehicle.getY())<to && other.getX()>cl && other.getX()<cr && vehicle.getX()-other.getX()<la;
            default:    return false;
        }
    }
}
