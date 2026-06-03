package system.collision;

import java.awt.Rectangle;
import java.util.List;

import layout.IntersectionLayout;
import manager.LaneManager;
import model.vehicle.Ambulance;
import model.vehicle.FireTruck;
import model.vehicle.Vehicle;
import system.movement.LaneChangeSystem;
import util.Lane;

/**
 * CollisionSystem – duy trì khoảng cách xe và xử lý vượt xe.
 *
 * THỨ TỰ ƯU TIÊN VƯỢT XE:
 *  1. Lách qua khoảng trống cạnh xe chặn trong CÙNG LANE
 *     (dùng overtakeOffsetX/Y riêng, không đụng targetX/Y của TurningSystem)
 *  2. Nếu cùng lane không còn chỗ → đổi sang LANE TRÁI
 *  3. Không làm được cả hai → DỪNG LẠI
 *
 * Sau khi vượt xong → tự về LANE PHẢI (tryReturnToRightLane).
 */
public class CollisionSystem {

    private final LaneChangeSystem laneChangeSystem;

    private static final double SAFE_DISTANCE          = 100;
    private static final double EMERGENCY_YIELD_RADIUS = 280;
    private static final double EMERGENCY_OVERTAKE_DIST = 90;
    private static final double LANE_HALF_WIDTH        = 45;

    public CollisionSystem() {
        laneChangeSystem = new LaneChangeSystem();
    }

    // ────────────────────────────────────────────────────────────────
    // ENTRY POINT
    // ────────────────────────────────────────────────────────────────

    public void maintainDistance(Vehicle current, List<Vehicle> vehicles) {

        if (current.isTurning() || current.isChangingLane()) return;

        // Xe khẩn cấp
        if (current instanceof Ambulance || current instanceof FireTruck) {
            handleEmergencyOvertake(current, vehicles);
            return;
        }

        // Nhường xe khẩn cấp
        if (shouldYieldToEmergency(current, vehicles)) {
            yieldToEmergency(current, vehicles);
            return;
        }

        // Về lane phải nếu đã vượt xong
        tryReturnToRightLane(current, vehicles);

        // Tìm xe chặn phía trước CÙNG LANE
        Vehicle blocker = findBlockerAhead(current, vehicles, SAFE_DISTANCE);

        if (blocker == null) {
            // Không có xe chặn → reset offset lách
            current.setOvertakeOffsetX(0);
            current.setOvertakeOffsetY(0);
            return;
        }

        // ƯU TIÊN 1: Lách trong cùng lane
        if (tryOvertakeInLane(current, blocker, vehicles)) return;

        // ƯU TIÊN 2: Đổi sang lane trái
        if (laneChangeSystem.tryChangeLane(current, vehicles)) {
            current.setOvertakeOffsetX(0);
            current.setOvertakeOffsetY(0);
            playHorn(current);
            return;
        }

        // ƯU TIÊN 3: Dừng lại
        current.setStopped(true);
    }

    // ────────────────────────────────────────────────────────────────
    // TÌM XE CHẶN PHÍA TRƯỚC
    // ────────────────────────────────────────────────────────────────

    /**
     * Tìm xe gần nhất CÙNG LANE, CÙNG HƯỚNG, ở PHÍA TRƯỚC trong safeDistance.
     * Dùng khoảng cách lateral rộng hơn (50px) để vẫn nhận ra blocker kể cả
     * khi current đang lách ngang.
     */
    private Vehicle findBlockerAhead(Vehicle current, List<Vehicle> vehicles, double safeDistance) {

        Vehicle closest = null;
        double  minDist = safeDistance;

        for (Vehicle other : vehicles) {
            if (other == current)                               continue;
            if (other.isTurning())                              continue;
            if (current.getDirection() != other.getDirection()) continue;
            if (current.getLane()      != other.getLane())      continue;

            double lateral;
            double axial;

            switch (current.getDirection()) {
                case SOUTH:
                    lateral = Math.abs(current.getX() - other.getX());
                    axial   = other.getY() - current.getY();
                    break;
                case NORTH:
                    lateral = Math.abs(current.getX() - other.getX());
                    axial   = current.getY() - other.getY();
                    break;
                case EAST:
                    lateral = Math.abs(current.getY() - other.getY());
                    axial   = other.getX() - current.getX();
                    break;
                case WEST:
                    lateral = Math.abs(current.getY() - other.getY());
                    axial   = current.getX() - other.getX();
                    break;
                default: continue;
            }

            // Phía trước + đủ gần + không lệch ngang quá lane
            if (axial > 0 && axial < safeDistance && lateral < LANE_HALF_WIDTH * 2) {
                if (axial < minDist) {
                    minDist = axial;
                    closest = other;
                }
            }
        }
        return closest;
    }

    // ────────────────────────────────────────────────────────────────
    // ƯU TIÊN 1: LÁCH TRONG CÙNG LANE
    // ────────────────────────────────────────────────────────────────

    private boolean tryOvertakeInLane(Vehicle current, Vehicle blocker, List<Vehicle> vehicles) {

        double blockerHalfW = blocker.getWidth()  / 2.0;
        double blockerHalfH = blocker.getHeight() / 2.0;
        double currentHalfW = current.getWidth()  / 2.0;
        double currentHalfH = current.getHeight() / 2.0;

        switch (current.getDirection()) {

            case NORTH:
            case SOUTH: {
                int    laneCenter = LaneManager.getLaneCenterX(current.getDirection(), current.getLane());
                double laneLeft   = laneCenter - LANE_HALF_WIDTH;
                double laneRight  = laneCenter + LANE_HALF_WIDTH;

                double gapLeft  = blocker.getX() - blockerHalfW - laneLeft;
                double gapRight = laneRight - (blocker.getX() + blockerHalfW);
                double needed   = current.getWidth() + 4;

                double offsetX = Double.NaN;
                if (gapLeft >= needed && gapLeft >= gapRight) {
                    offsetX = blocker.getX() - blockerHalfW - currentHalfW - 2;
                    offsetX = Math.max(offsetX, laneLeft + currentHalfW);
                } else if (gapRight >= needed) {
                    offsetX = blocker.getX() + blockerHalfW + currentHalfW + 2;
                    offsetX = Math.min(offsetX, laneRight - currentHalfW);
                }

                if (Double.isNaN(offsetX)) return false;
                if (isOffsetBlockedAhead(current, offsetX, current.getY(), vehicles, blocker)) return false;

                current.setOvertakeOffsetX(offsetX);
                current.setOvertakeOffsetY(0);
                return true;
            }

            case EAST:
            case WEST: {
                int    laneCenter = LaneManager.getLaneCenterY(current.getDirection(), current.getLane());
                double laneTop    = laneCenter - LANE_HALF_WIDTH;
                double laneBottom = laneCenter + LANE_HALF_WIDTH;

                double gapTop    = blocker.getY() - blockerHalfH - laneTop;
                double gapBottom = laneBottom - (blocker.getY() + blockerHalfH);
                double needed    = current.getHeight() + 4;

                double offsetY = Double.NaN;
                if (gapTop >= needed && gapTop >= gapBottom) {
                    offsetY = blocker.getY() - blockerHalfH - currentHalfH - 2;
                    offsetY = Math.max(offsetY, laneTop + currentHalfH);
                } else if (gapBottom >= needed) {
                    offsetY = blocker.getY() + blockerHalfH + currentHalfH + 2;
                    offsetY = Math.min(offsetY, laneBottom - currentHalfH);
                }

                if (Double.isNaN(offsetY)) return false;
                if (isOffsetBlockedAhead(current, current.getX(), offsetY, vehicles, blocker)) return false;

                current.setOvertakeOffsetY(offsetY);
                current.setOvertakeOffsetX(0);
                return true;
            }

            default: return false;
        }
    }

    /** Kiểm tra vị trí offset phía trước có xe khác block không. */
    private boolean isOffsetBlockedAhead(
            Vehicle current, double ox, double oy,
            List<Vehicle> vehicles, Vehicle blocker) {

        for (Vehicle other : vehicles) {
            if (other == current || other == blocker) continue;
            if (other.getDirection() != current.getDirection()) continue;

            double lateral, axial;
            switch (current.getDirection()) {
                case NORTH:
                    lateral = Math.abs(ox - other.getX());
                    axial   = current.getY() - other.getY();
                    break;
                case SOUTH:
                    lateral = Math.abs(ox - other.getX());
                    axial   = other.getY() - current.getY();
                    break;
                case EAST:
                    lateral = Math.abs(oy - other.getY());
                    axial   = other.getX() - current.getX();
                    break;
                case WEST:
                    lateral = Math.abs(oy - other.getY());
                    axial   = current.getX() - other.getX();
                    break;
                default: continue;
            }

            if (axial > 0 && axial < SAFE_DISTANCE) {
                double minLateral = (current.getWidth() + other.getWidth()) / 2.0 + 2;
                if (lateral < minLateral) return true;
            }
        }
        return false;
    }

    // ────────────────────────────────────────────────────────────────
    // VỀ LANE PHẢI SAU KHI VƯỢT
    // ────────────────────────────────────────────────────────────────

    private void tryReturnToRightLane(Vehicle current, List<Vehicle> vehicles) {

        if (current.getLane() != Lane.LEFT)      return;
        if (current.isChangingLane())            return;
        if (current.isTurning())                 return;
        if (current.getLaneChangeCooldown() > 0) return;

        // Còn xe LEFT phía trước gần → chưa về
        if (findBlockerAhead(current, vehicles, SAFE_DISTANCE + 30) != null) return;

        // Lane RIGHT phía trước/sau có đủ trống không
        for (Vehicle other : vehicles) {
            if (other == current)                               continue;
            if (other.getDirection() != current.getDirection()) continue;
            if (other.getLane() != Lane.RIGHT)                  continue;

            double axialAhead  = axialAhead(current, other);
            double axialBehind = axialAhead(other, current); // other phía sau current

            if (axialAhead  >= 0 && axialAhead  < SAFE_DISTANCE + 20) return;
            if (axialBehind >= 0 && axialBehind < 80)                  return;
        }

        current.setOvertakeOffsetX(0);
        current.setOvertakeOffsetY(0);
        current.setTargetLane(Lane.RIGHT);
        current.setChangingLane(true);
        current.setLaneChangeCooldown(60);
    }

    // ────────────────────────────────────────────────────────────────
    // XE KHẨN CẤP
    // ────────────────────────────────────────────────────────────────

    private void handleEmergencyOvertake(Vehicle emergency, List<Vehicle> vehicles) {

        Vehicle blocker = findBlockerAhead(emergency, vehicles, EMERGENCY_OVERTAKE_DIST);
        if (blocker == null) return;

        if (tryOvertakeInLane(emergency, blocker, vehicles)) return;
        laneChangeSystem.tryChangeLane(emergency, vehicles);
    }

    private boolean shouldYieldToEmergency(Vehicle current, List<Vehicle> vehicles) {
        for (Vehicle v : vehicles) {
            if (!(v instanceof Ambulance) && !(v instanceof FireTruck)) continue;
            double dx = v.getX() - current.getX(), dy = v.getY() - current.getY();
            if (Math.sqrt(dx*dx + dy*dy) < EMERGENCY_YIELD_RADIUS) return true;
        }
        return false;
    }

    private void yieldToEmergency(Vehicle current, List<Vehicle> vehicles) {
        if (current.getLane() != Lane.RIGHT && !current.isChangingLane()) {
            current.setTargetLane(Lane.RIGHT);
            current.setChangingLane(true);
            current.setLaneChangeCooldown(40);
        }
        current.setStopped(true);
    }

    // ────────────────────────────────────────────────────────────────
    // HELPERS
    // ────────────────────────────────────────────────────────────────

    /** Khoảng cách axial của other ở phía trước current (>0 = phía trước). */
    private double axialAhead(Vehicle current, Vehicle other) {
        switch (current.getDirection()) {
            case SOUTH: return other.getY() - current.getY();
            case NORTH: return current.getY() - other.getY();
            case EAST:  return other.getX() - current.getX();
            case WEST:  return current.getX() - other.getX();
            default:    return Double.MAX_VALUE;
        }
    }

    private boolean isTooClose(Vehicle current, Vehicle other, double safeDistance) {
        double lateral, axial;
        switch (current.getDirection()) {
            case SOUTH: lateral = Math.abs(current.getX()-other.getX()); axial = other.getY()-current.getY(); break;
            case NORTH: lateral = Math.abs(current.getX()-other.getX()); axial = current.getY()-other.getY(); break;
            case EAST:  lateral = Math.abs(current.getY()-other.getY()); axial = other.getX()-current.getX(); break;
            case WEST:  lateral = Math.abs(current.getY()-other.getY()); axial = current.getX()-other.getX(); break;
            case NORTHEAST: {
                double dx = other.getX()-current.getX(), dy = current.getY()-other.getY();
                return dx>0 && dy>0 && Math.abs(dx-dy)<50 && Math.sqrt(dx*dx+dy*dy)<safeDistance;
            }
            default: return false;
        }
        return lateral < 30 && axial > 0 && axial < safeDistance;
    }

    private void playHorn(Vehicle current) {
        if      (current instanceof model.vehicle.Car)       manager.SoundManager.playCarHorn();
        else if (current instanceof model.vehicle.Motorbike) manager.SoundManager.playMotorbikeHorn();
        else                                                  manager.SoundManager.playGeneralHorn();
    }

    // ────────────────────────────────────────────────────────────────
    // CAN ENTER INTERSECTION
    // ────────────────────────────────────────────────────────────────

    public boolean canEnterIntersection(Vehicle vehicle, List<Vehicle> vehicles) {

        IntersectionLayout layout = LaneManager.getLayout();
        Rectangle checkBounds = layout.getEnterCheckBounds(vehicle.getDirection());

        switch (vehicle.getDirection()) {
            case SOUTH:
                for (Vehicle o : vehicles) {
                    if (o==vehicle||o.isTurning()) continue;
                    if (Math.abs(o.getX()-vehicle.getX())<50 && checkBounds.contains((int)o.getX(),(int)o.getY())
                            && o.getY()>vehicle.getY() && o.getY()-vehicle.getY()<80) return false;
                } break;
            case NORTH:
                for (Vehicle o : vehicles) {
                    if (o==vehicle||o.isTurning()) continue;
                    if (Math.abs(o.getX()-vehicle.getX())<50 && checkBounds.contains((int)o.getX(),(int)o.getY())
                            && o.getY()<vehicle.getY() && vehicle.getY()-o.getY()<80) return false;
                } break;
            case EAST:
                for (Vehicle o : vehicles) {
                    if (o==vehicle||o.isTurning()) continue;
                    if (Math.abs(o.getY()-vehicle.getY())<50 && checkBounds.contains((int)o.getX(),(int)o.getY())
                            && o.getX()>vehicle.getX() && o.getX()-vehicle.getX()<80) return false;
                } break;
            case WEST:
                for (Vehicle o : vehicles) {
                    if (o==vehicle||o.isTurning()) continue;
                    if (Math.abs(o.getY()-vehicle.getY())<50 && checkBounds.contains((int)o.getX(),(int)o.getY())
                            && o.getX()<vehicle.getX() && vehicle.getX()-o.getX()<80) return false;
                } break;
            case NORTHEAST:
                for (Vehicle o : vehicles) {
                    if (o==vehicle||o.isTurning()) continue;
                    if (checkBounds.contains((int)o.getX(),(int)o.getY())) {
                        double dx=o.getX()-vehicle.getX(), dy=vehicle.getY()-o.getY();
                        if (Math.sqrt(dx*dx+dy*dy)<80) return false;
                    }
                } break;
        }
        return true;
    }
}
