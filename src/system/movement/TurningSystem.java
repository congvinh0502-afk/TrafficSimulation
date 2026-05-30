package system.movement;

import config.Constants;
import manager.LaneManager;
import model.intersection.IntersectionType;
import model.vehicle.Vehicle;
import util.Direction;
import util.DirectionHelper;

/**
 * Hệ thống xử lý rẽ tại giao lộ.
 *
 * <p>
 * Luồng hoạt động mỗi frame:
 * <ol>
 * <li>{@link #handleTurning} — phát hiện xe vào vùng rẽ, kích hoạt rẽ.</li>
 * <li>{@link #smoothTurning} — dịch chuyển xe theo góc hiện tại khi đang
 * rẽ.</li>
 * <li>{@link #updateVehicleAngle} — xoay góc dần về {@code targetAngle}.</li>
 * <li>{@link #finishTurning} — xác nhận rẽ xong, gán hướng mới, về làn
 * đúng.</li>
 * </ol>
 * </p>
 */
public class TurningSystem {

    /**
     * Cập nhật trạng thái rẽ của xe mỗi frame.
     *
     * @param vehicle xe cần xử lý
     * @param type    loại ngã rẽ để kiểm tra hướng rẽ có hợp lệ không
     */
    public void updateTurning(Vehicle vehicle, IntersectionType type) {
        handleTurning(vehicle, type);
        if (vehicle.isTurning()) {
            smoothTurning(vehicle);
        }
    }

    // ==========================================================
    // Phát hiện và kích hoạt rẽ
    // ==========================================================

    /**
     * Kiểm tra xe có trong vùng rẽ không và kích hoạt rẽ nếu cần.
     * Bỏ qua nếu đã rẽ trong giao lộ này rồi.
     */
    public void handleTurning(Vehicle vehicle, IntersectionType type) {
        if (vehicle.hasTurned())
            return;

        double hw = vehicle.getWidth() / 2;
        double hh = vehicle.getHeight() / 2;

        boolean inTurnZone = vehicle.getX() + hw > Constants.TURN_TRIGGER_LEFT
                && vehicle.getX() - hw < Constants.TURN_TRIGGER_RIGHT
                && vehicle.getY() + hh > Constants.TURN_TRIGGER_TOP
                && vehicle.getY() - hh < Constants.TURN_TRIGGER_BOTTOM;

        if (!inTurnZone)
            return;

        switch (vehicle.getTurnType()) {
            case LEFT: {
                Direction target = DirectionHelper.getLeftDirection(vehicle.getDirection());
                if (!type.getDirections().contains(target))
                    return;
                initiateTurnLeft(vehicle);
                break;
            }
            case RIGHT: {
                Direction target = DirectionHelper.getRightDirection(vehicle.getDirection());
                if (!type.getDirections().contains(target))
                    return;
                initiateTurnRight(vehicle);
                break;
            }
            default:
                return;
        }

        vehicle.setTurned(true);
    }

    /** Thiết lập thông số rẽ trái. */
    private void initiateTurnLeft(Vehicle vehicle) {
        switch (vehicle.getDirection()) {
            case NORTH:
                vehicle.setTargetDirection(Direction.WEST);
                vehicle.setTargetAngle(180);
                break;
            case SOUTH:
                vehicle.setTargetDirection(Direction.EAST);
                vehicle.setTargetAngle(0);
                break;
            case EAST:
                vehicle.setTargetDirection(Direction.NORTH);
                vehicle.setTargetAngle(-90);
                break;
            case WEST:
                vehicle.setTargetDirection(Direction.SOUTH);
                vehicle.setTargetAngle(90);
                break;
            default:
                return;
        }
        vehicle.setTurning(true);
    }

    /** Thiết lập thông số rẽ phải. */
    private void initiateTurnRight(Vehicle vehicle) {
        switch (vehicle.getDirection()) {
            case NORTH:
                vehicle.setTargetDirection(Direction.EAST);
                vehicle.setTargetAngle(0);
                break;
            case SOUTH:
                vehicle.setTargetDirection(Direction.WEST);
                vehicle.setTargetAngle(180);
                break;
            case EAST:
                vehicle.setTargetDirection(Direction.SOUTH);
                vehicle.setTargetAngle(90);
                break;
            case WEST:
                vehicle.setTargetDirection(Direction.NORTH);
                vehicle.setTargetAngle(-90);
                break;
            default:
                return;
        }
        vehicle.setTurning(true);
    }

    // ==========================================================
    // Di chuyển mượt khi đang rẽ
    // ==========================================================

    /**
     * Dịch chuyển xe theo hướng của góc hiện tại.
     * Đồng thời cập nhật góc và kiểm tra hoàn thành.
     */
    public void smoothTurning(Vehicle vehicle) {
        if (!vehicle.isTurning())
            return;

        double speed = vehicle.getSpeed() * Constants.TURNING_SPEED_FACTOR;
        double rad = Math.toRadians(vehicle.getAngle());
        vehicle.setX(vehicle.getX() + Math.cos(rad) * speed);
        vehicle.setY(vehicle.getY() + Math.sin(rad) * speed);

        finishTurning(vehicle);
        updateVehicleAngle(vehicle);
    }

    /** Xác nhận rẽ xong khi góc đủ gần targetAngle. */
    private void finishTurning(Vehicle vehicle) {
        if (Math.abs(vehicle.getAngle() - vehicle.getTargetAngle()) >= Constants.TURN_FINISH_TOLERANCE) {
            return;
        }

        vehicle.setDirection(vehicle.getTargetDirection());
        vehicle.setLane(util.Lane.RIGHT); // về làn phải mặc định sau rẽ
        recoverLane(vehicle);
        vehicle.setTurning(false);
    }

    /**
     * Snap xe về đúng trung tâm làn sau khi rẽ xong.
     */
    public void recoverLane(Vehicle vehicle) {
        switch (vehicle.getDirection()) {
            case NORTH:
            case SOUTH:
                vehicle.setX(LaneManager.getLaneCenterX(vehicle.getDirection(), vehicle.getLane()));
                break;
            case EAST:
            case WEST:
                vehicle.setY(LaneManager.getLaneCenterY(vehicle.getDirection(), vehicle.getLane()));
                break;
            default:
                break;
        }
    }

    // ==========================================================
    // Xoay góc dần về đích
    // ==========================================================

    /**
     * Xoay góc hiển thị xe tiến gần đến {@code targetAngle} mỗi frame.
     */
    public void updateVehicleAngle(Vehicle vehicle) {
        double current = vehicle.getAngle();
        double target = vehicle.getTargetAngle();
        double diff = target - current;

        while (diff > 180)
            diff -= 360;
        while (diff < -180)
            diff += 360;

        if (Math.abs(diff) < Constants.ROTATE_SPEED) {
            vehicle.setAngle(target);
            return;
        }
        vehicle.setAngle(current + (diff > 0 ? Constants.ROTATE_SPEED : -Constants.ROTATE_SPEED));
    }
}