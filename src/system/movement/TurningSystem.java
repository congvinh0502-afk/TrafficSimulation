package system.movement;

import config.Constants;
import model.intersection.IntersectionType;
import model.vehicle.Vehicle;
import util.Direction;
import util.DirectionHelper;

/**
 * Hệ thống xử lý rẽ tại giao lộ.
 *
 * <p><b>Fix teleport:</b> Trước đây {@code recoverLane()} snap xe về
 * trung tâm làn ngay lập tức → gây teleport nhìn thấy được.
 * Giờ thay bằng cờ {@code postTurnAligning = true} để
 * {@link LaneAlignmentSystem} xử lý alignment dần dần.</p>
 *
 * <p>Luồng mỗi frame:
 * <ol>
 *   <li>{@link #handleTurning} — phát hiện vào vùng rẽ, kích hoạt.</li>
 *   <li>{@link #smoothTurning} — di chuyển theo góc hiện tại khi đang rẽ.</li>
 *   <li>{@link #updateVehicleAngle} — xoay góc dần về targetAngle.</li>
 *   <li>{@link #finishTurning} — xác nhận hoàn thành, set postTurnAligning.</li>
 * </ol>
 * </p>
 */
public class TurningSystem {

    public void updateTurning(Vehicle vehicle, IntersectionType type) {
        handleTurning(vehicle, type);
        if (vehicle.isTurning()) {
            smoothTurning(vehicle);
        }
    }

    // ==========================================================
    // Phát hiện và kích hoạt rẽ
    // ==========================================================

    public void handleTurning(Vehicle vehicle, IntersectionType type) {
        if (vehicle.hasTurned()) return;

        double hw = vehicle.getWidth()  / 2;
        double hh = vehicle.getHeight() / 2;

        boolean inZone = vehicle.getX() + hw > Constants.TURN_TRIGGER_LEFT
                      && vehicle.getX() - hw < Constants.TURN_TRIGGER_RIGHT
                      && vehicle.getY() + hh > Constants.TURN_TRIGGER_TOP
                      && vehicle.getY() - hh < Constants.TURN_TRIGGER_BOTTOM;

        if (!inZone) return;

        switch (vehicle.getTurnType()) {
            case LEFT: {
                Direction target = DirectionHelper.getLeftDirection(vehicle.getDirection());
                if (!type.getDirections().contains(target)) return;
                initiateTurnLeft(vehicle);
                break;
            }
            case RIGHT: {
                Direction target = DirectionHelper.getRightDirection(vehicle.getDirection());
                if (!type.getDirections().contains(target)) return;
                initiateTurnRight(vehicle);
                break;
            }
            default:
                return;
        }
        vehicle.setTurned(true);
    }

    private void initiateTurnLeft(Vehicle vehicle) {
        switch (vehicle.getDirection()) {
            case NORTH: vehicle.setTargetDirection(Direction.WEST);  vehicle.setTargetAngle(180);  break;
            case SOUTH: vehicle.setTargetDirection(Direction.EAST);  vehicle.setTargetAngle(0);    break;
            case EAST:  vehicle.setTargetDirection(Direction.NORTH); vehicle.setTargetAngle(-90);  break;
            case WEST:  vehicle.setTargetDirection(Direction.SOUTH); vehicle.setTargetAngle(90);   break;
            default: return;
        }
        vehicle.setTurning(true);
    }

    private void initiateTurnRight(Vehicle vehicle) {
        switch (vehicle.getDirection()) {
            case NORTH: vehicle.setTargetDirection(Direction.EAST);  vehicle.setTargetAngle(0);    break;
            case SOUTH: vehicle.setTargetDirection(Direction.WEST);  vehicle.setTargetAngle(180);  break;
            case EAST:  vehicle.setTargetDirection(Direction.SOUTH); vehicle.setTargetAngle(90);   break;
            case WEST:  vehicle.setTargetDirection(Direction.NORTH); vehicle.setTargetAngle(-90);  break;
            default: return;
        }
        vehicle.setTurning(true);
    }

    // ==========================================================
    // Di chuyển mượt khi đang rẽ
    // ==========================================================

    public void smoothTurning(Vehicle vehicle) {
        if (!vehicle.isTurning()) return;

        double speed = vehicle.getSpeed() * Constants.TURNING_SPEED_FACTOR;
        // Di chuyển theo góc hiện tại (không theo direction vector)
        double rad = Math.toRadians(vehicle.getAngle());
        vehicle.setX(vehicle.getX() + Math.cos(rad) * speed);
        vehicle.setY(vehicle.getY() + Math.sin(rad) * speed);

        updateVehicleAngle(vehicle);
        finishTurning(vehicle);
    }

    /**
     * Xoay góc hiển thị dần về targetAngle.
     */
    public void updateVehicleAngle(Vehicle vehicle) {
        double current = vehicle.getAngle();
        double target  = vehicle.getTargetAngle();
        double diff    = target - current;

        while (diff >  180) diff -= 360;
        while (diff < -180) diff += 360;

        if (Math.abs(diff) < Constants.ROTATE_SPEED) {
            vehicle.setAngle(target);
            return;
        }
        vehicle.setAngle(current + (diff > 0 ? Constants.ROTATE_SPEED : -Constants.ROTATE_SPEED));
    }

    /**
     * Xác nhận rẽ xong khi góc đủ gần targetAngle.
     *
     * <p><b>Fix teleport:</b> Không còn gọi {@code recoverLane()} ngay lập tức.
     * Thay vào đó bật {@code postTurnAligning} để {@link LaneAlignmentSystem}
     * alignment từ từ → không có jump đột ngột.</p>
     */
    private void finishTurning(Vehicle vehicle) {
        if (Math.abs(vehicle.getAngle() - vehicle.getTargetAngle()) >= Constants.TURN_FINISH_TOLERANCE) {
            return;
        }

        // Hoàn thành rẽ
        vehicle.setDirection(vehicle.getTargetDirection());
        vehicle.setLane(util.Lane.RIGHT);
        vehicle.setTurning(false);

        // Khôi phục gia tốc bình thường
        vehicle.setAcceleration(Constants.DEFAULT_ACCELERATION);

        // FIX TELEPORT: không snap ngay — alignment dần bởi LaneAlignmentSystem
        vehicle.setPostTurnAligning(true);
    }
}
