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
import util.Direction;

/**
 * CollisionSystem – duy trì khoảng cách xe và xử lý vượt xe.
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
        
        // 1. NẾU ĐANG CHUYỂN LÀN -> BỎ QUA KIỂM TRA VA CHẠM DỌC
        if (current.isChangingLane()) return;

        // 2. XE KHẨN CẤP & TẤP LỀ
        if (current instanceof Ambulance || current instanceof FireTruck) {
            handleEmergencyOvertake(current, vehicles);
            return;
        }

        if (shouldYieldToEmergency(current, vehicles)) {
            yieldToEmergency(current, vehicles);
            return; 
        } else {
            recoverFromCurb(current);
        }

        tryReturnToRightLane(current, vehicles);
        if (current.maxSpeed == -1) current.maxSpeed = current.getSpeed();
        
        Vehicle blocker = findBlockerAhead(current, vehicles, SAFE_DISTANCE);
        
        if (blocker == null) {
            current.setOvertakeOffsetX(0);
            current.setOvertakeOffsetY(0);
            if (!current.isStopped() && !current.braking && current.getSpeed() < current.maxSpeed) {
                current.setSpeed(Math.min(current.maxSpeed, current.getSpeed() + 0.15));
            }
            return;
        }

        // TÍNH KHOẢNG CÁCH CẢN TRƯỚC - CẢN SAU 
        double gap = calculateBumperGap(current, blocker);

        // NẾU ĐANG RẼ -> CHỈ PHANH KHẨN CẤP ĐỂ TRÁNH KẸT XE, KHÔNG VƯỢT
        if (current.isTurning() || current.isFollowingPath()) {
            if (gap < 15) {
                current.setSpeed(0);
                current.setStopped(true);
            } else {
                 double targetSpd = (gap / SAFE_DISTANCE) * current.maxSpeed;
                 current.setSpeed(Math.min(targetSpd, current.getSpeed()));
                 current.setStopped(false);
            }
            return;
        }

        // --- ƯU TIÊN VƯỢT XE KHI GẶP VẬT CẢN (Đặc biệt xe rùa bò như Bicycle) ---
        // Cho phép lách/chuyển làn khi cách xe trước từ 15 đến 70 pixel
        if (gap > 15 && gap < 70) {
            if (tryOvertakeInLane(current, blocker, vehicles)) return;
            if (laneChangeSystem.tryChangeLane(current, vehicles)) {
                current.setOvertakeOffsetX(0);
                current.setOvertakeOffsetY(0);
                playHorn(current);
                return;
            }
        }

        // KHÓA PHANH CỨNG NẾU QUÁ SÁT THUỘC LÀN THẲNG
        if (gap < 8) { 
            current.setSpeed(0);
            current.setStopped(true);
            current.setOvertakeOffsetX(0); 
            current.setOvertakeOffsetY(0); 
            return; 
        }

        // ÁP DỤNG PHANH DẦN ĐỀU NẾU KHÔNG THỂ LÁCH
        double targetSpd = (gap / SAFE_DISTANCE) * current.maxSpeed;
        targetSpd = Math.min(targetSpd, blocker.getSpeed());

        if (current.getSpeed() < targetSpd) {
            current.setSpeed(Math.min(targetSpd, current.getSpeed() + 0.15));
        } else {
            current.setSpeed(Math.max(targetSpd, current.getSpeed() - 0.3));
        }
        current.braking = true; 
        current.setStopped(false);
    }
    
    // Hàm phụ trợ tính Bumper Gap để code gọn gàng hơn
    private double calculateBumperGap(Vehicle current, Vehicle blocker) {
        if (current.getDirection() == Direction.NORTH) return current.getY() - (blocker.getY() + blocker.getHeight());
        if (current.getDirection() == Direction.SOUTH) return blocker.getY() - (current.getY() + current.getHeight());
        if (current.getDirection() == Direction.EAST)  return blocker.getX() - (current.getX() + current.getWidth());
        if (current.getDirection() == Direction.WEST)  return current.getX() - (blocker.getX() + blocker.getWidth());
        return 999;
    }


    // ────────────────────────────────────────────────────────────────
    // TÌM XE CHẶN PHÍA TRƯỚC
    // ────────────────────────────────────────────────────────────────

    private Vehicle findBlockerAhead(Vehicle current, List<Vehicle> vehicles, double safeDistance) {

        Vehicle closest = null;
        double  minDist = safeDistance;

        for (Vehicle other : vehicles) {
            if (other == current)                               continue;
            // XÓA ĐIỀU KIỆN "other.isTurning() continue" 
            // Để xe thẳng nhận diện được xe đang rẽ và phanh lại
            
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

        if (findBlockerAhead(current, vehicles, SAFE_DISTANCE + 30) != null) return;

        for (Vehicle other : vehicles) {
            if (other == current)                               continue;
            if (other.getDirection() != current.getDirection()) continue;
            if (other.getLane() != Lane.RIGHT)                  continue;

            double axialAhead  = axialAhead(current, other);
            double axialBehind = axialAhead(other, current); 

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
            
            Direction activeEmergencyDir = v.isTurning() ? v.getTargetDirection() : v.getDirection();
            if (current.getDirection() != activeEmergencyDir) continue;

            double dx = v.getX() - current.getX();
            double dy = v.getY() - current.getY();
            double dist = Math.sqrt(dx * dx + dy * dy);
            
            if (dist < 280) {
                if (current.getDirection() == Direction.NORTH && current.getY() < v.getY()) return true;
                if (current.getDirection() == Direction.SOUTH && current.getY() > v.getY()) return true;
                if (current.getDirection() == Direction.EAST  && current.getX() > v.getX()) return true;
                if (current.getDirection() == Direction.WEST  && current.getX() < v.getX()) return true;
            }
        }
        return false;
    }

    // ────────────────────────────────────────────────────────────────
    // HELPERS
    // ────────────────────────────────────────────────────────────────

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
    
    // --- HÀM 1: ĐÁNH LÁI VÀO SÁT LỀ CỎ ---
    private void yieldToEmergency(Vehicle current, List<Vehicle> vehicles) {
        if (current.isTurning() || current.isFollowingPath()) {
            if (current.getSpeed() < current.maxSpeed) {
                current.setSpeed(Math.min(current.maxSpeed, current.getSpeed() + 0.2));
            }
            current.setStopped(false);
            return;
        }

        boolean reachedCurb = false;
        double pullOverSpeed = 1.2; 

        switch (current.getDirection()) {
            case NORTH: {
                double curbX = LaneManager.getLaneCenterX(Direction.NORTH, Lane.RIGHT) + 28;
                if (current.getX() < curbX) {
                    current.setX(current.getX() + pullOverSpeed);
                    current.setAngle(-75); 
                } else {
                    reachedCurb = true;
                    current.setAngle(-90); 
                }
                break;
            }
            case SOUTH: {
                double curbX = LaneManager.getLaneCenterX(Direction.SOUTH, Lane.RIGHT) - 28;
                if (current.getX() > curbX) {
                    current.setX(current.getX() - pullOverSpeed);
                    current.setAngle(105); 
                } else {
                    reachedCurb = true;
                    current.setAngle(90);
                }
                break;
            }
            case EAST: {
                double curbY = LaneManager.getLaneCenterY(Direction.EAST, Lane.RIGHT) + 28;
                if (current.getY() < curbY) {
                    current.setY(current.getY() + pullOverSpeed);
                    current.setAngle(15); 
                } else {
                    reachedCurb = true;
                    current.setAngle(0);
                }
                break;
            }
            case WEST: {
                double curbY = LaneManager.getLaneCenterY(Direction.WEST, Lane.RIGHT) - 28;
                if (current.getY() > curbY) {
                    current.setY(current.getY() - pullOverSpeed);
                    current.setAngle(195); 
                } else {
                    reachedCurb = true;
                    current.setAngle(180);
                }
                break;
            }
            default:
                reachedCurb = true;
        }

        if (reachedCurb) {
            current.setSpeed(0);
            current.setStopped(true);
        } else {
            if (current.getSpeed() > 0.5) {
                current.setSpeed(Math.max(0.5, current.getSpeed() - 0.25));
            }
            current.setStopped(false);
        }
    }

    // --- HÀM 2: TỰ ĐỘNG BÒ TỪ CỎ RA GIỮA LÀN ĐƯỜNG ---
    private void recoverFromCurb(Vehicle current) {
        if (current.isTurning() || current.isFollowingPath() || current.isChangingLane()) return;
        
        double shiftAmount = 0.8; 
        boolean isAligned = true;

        switch (current.getDirection()) {
            case NORTH: {
                double center = LaneManager.getLaneCenterX(Direction.NORTH, current.getLane());
                if (current.getX() > center + 1.5) { 
                    current.setX(current.getX() - shiftAmount);
                    current.setAngle(-105); 
                    isAligned = false;
                }
                break;
            }
            case SOUTH: {
                double center = LaneManager.getLaneCenterX(Direction.SOUTH, current.getLane());
                if (current.getX() < center - 1.5) {
                    current.setX(current.getX() + shiftAmount);
                    current.setAngle(75);
                    isAligned = false;
                }
                break;
            }
            case EAST: {
                double center = LaneManager.getLaneCenterY(Direction.EAST, current.getLane());
                if (current.getY() > center + 1.5) {
                    current.setY(current.getY() - shiftAmount);
                    current.setAngle(-15);
                    isAligned = false;
                }
                break;
            }
            case WEST: {
                double center = LaneManager.getLaneCenterY(Direction.WEST, current.getLane());
                if (current.getY() < center - 1.5) {
                    current.setY(current.getY() + shiftAmount);
                    current.setAngle(165);
                    isAligned = false;
                }
                break;
            }
        }
        
        if (isAligned) {
            if (current.getDirection() == Direction.NORTH) current.setAngle(-90);
            if (current.getDirection() == Direction.SOUTH) current.setAngle(90);
            if (current.getDirection() == Direction.EAST) current.setAngle(0);
            if (current.getDirection() == Direction.WEST) current.setAngle(180);
        }
    }
}