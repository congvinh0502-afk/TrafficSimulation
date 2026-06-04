package system.movement;

import config.Constants;
import model.intersection.IntersectionLayout;
import model.vehicle.Vehicle;
import util.Direction;
import util.DirectionHelper;

/**
 * Hệ thống xử lý rẽ tại giao lộ.
 * Dùng IntersectionLayout.getTurnZone() thay vì Constants hardcode.
 */
public class TurningSystem {

    public void updateTurning(Vehicle vehicle, IntersectionLayout layout) {
        handleTurning(vehicle, layout);
        if (vehicle.isTurning()) {
            smoothTurning(vehicle);
        }
    }

    // ==========================================================
    // Phát hiện và kích hoạt rẽ
    // ==========================================================

    public void handleTurning(Vehicle vehicle, IntersectionLayout layout) {
        if (vehicle.hasTurned())
            return;

        double hw = vehicle.getWidth() / 2;
        double hh = vehicle.getHeight() / 2;

        if (!layout.getTurnZone().contains(vehicle.getX(), vehicle.getY(), hw, hh))
            return;

        boolean isFiveWay = layout.getDirections().size() == 5;

        switch (vehicle.getTurnType()) {
            case LEFT:
                initiateTurnLeft(vehicle);
                break;
            case RIGHT:
                initiateTurnRight(vehicle);
                break;
            case STRAIGHT:
                if (isFiveWay) {
                    // Ngã 5 không có đường thẳng đối diện tuyệt đối -> rẽ ngẫu nhiên
                    if (Math.random() < 0.5)
                        initiateTurnLeft(vehicle);
                    else
                        initiateTurnRight(vehicle);
                } else {
                    Direction opposite = vehicle.getDirection().opposite();
                    if (!layout.hasDirection(opposite)) {
                        if (Math.random() < 0.5)
                            initiateTurnLeft(vehicle);
                        else
                            initiateTurnRight(vehicle);
                    }
                }
                break;
            default:
                return;
        }
        vehicle.setTurned(true);
    }

    private void initiateTurnLeft(Vehicle vehicle) {
        Direction dir = vehicle.getDirection();
        Direction target = dir;
        double angle = vehicle.getAngle();
        switch (dir) {
            // Ngã 4
            case NORTH:
                target = Direction.WEST;
                angle = 180;
                break;
            case EAST:
                target = Direction.NORTH;
                angle = -90;
                break;
            case WEST:
                target = Direction.SOUTH;
                angle = 90;
                break;

            // Ngã 5 (Inbound -> Outbound nhảy sang nhánh ngược chiều kim đồng hồ)
            case SOUTH:
                target = Direction.FW_OUT_198;
                angle = 198;
                break; // Từ nhánh 270 (Top) rẽ sang 198
            case FW_IN_198:
                target = Direction.FW_OUT_126;
                angle = 126;
                break;
            case FW_IN_126:
                target = Direction.FW_OUT_54;
                angle = 54;
                break;
            case FW_IN_54:
                target = Direction.FW_OUT_342;
                angle = 342;
                break;
            case FW_IN_342:
                target = Direction.NORTH;
                angle = -90;
                break;
        }
        vehicle.setTargetDirection(target);
        vehicle.setTargetAngle(angle);
        vehicle.setTurning(true);
    }

    private void initiateTurnRight(Vehicle vehicle) {
        Direction dir = vehicle.getDirection();
        Direction target = dir;
        double angle = vehicle.getAngle();
        switch (dir) {
            // Ngã 4
            case NORTH:
                target = Direction.EAST;
                angle = 0;
                break;
            case EAST:
                target = Direction.SOUTH;
                angle = 90;
                break;
            case WEST:
                target = Direction.NORTH;
                angle = -90;
                break;

            // Ngã 5 (Inbound -> Outbound nhảy sang nhánh thuận chiều kim đồng hồ)
            case SOUTH:
                target = Direction.FW_OUT_342;
                angle = 342;
                break; // Từ nhánh 270 (Top) rẽ sang 342
            case FW_IN_342:
                target = Direction.FW_OUT_54;
                angle = 54;
                break;
            case FW_IN_54:
                target = Direction.FW_OUT_126;
                angle = 126;
                break;
            case FW_IN_126:
                target = Direction.FW_OUT_198;
                angle = 198;
                break;
            case FW_IN_198:
                target = Direction.NORTH;
                angle = -90;
                break;
        }
        vehicle.setTargetDirection(target);
        vehicle.setTargetAngle(angle);
        vehicle.setTurning(true);
    }

    // ==========================================================
    // Di chuyển mượt khi đang rẽ
    // ==========================================================

    public void smoothTurning(Vehicle vehicle) {
        if (!vehicle.isTurning()) return;

        double speed = vehicle.getSpeed() * Constants.TURNING_SPEED_FACTOR;
        double rad   = Math.toRadians(vehicle.getAngle());
        vehicle.setX(vehicle.getX() + Math.cos(rad) * speed);
        vehicle.setY(vehicle.getY() + Math.sin(rad) * speed);

        updateVehicleAngle(vehicle);
        finishTurning(vehicle);
    }

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

    private void finishTurning(Vehicle vehicle) {
        if (Math.abs(vehicle.getAngle() - vehicle.getTargetAngle()) >= Constants.TURN_FINISH_TOLERANCE) return;

        vehicle.setDirection(vehicle.getTargetDirection());
        vehicle.setLane(util.Lane.RIGHT);
        vehicle.setTurning(false);
        vehicle.setAcceleration(Constants.DEFAULT_ACCELERATION);
        vehicle.setPostTurnAligning(true);
    }
}
