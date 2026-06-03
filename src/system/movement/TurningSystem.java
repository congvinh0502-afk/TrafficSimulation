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

        switch (vehicle.getTurnType()) {
            case LEFT: {
                Direction target = DirectionHelper.getLeftDirection(vehicle.getDirection());
                if (!layout.hasDirection(target)) {
                    // Hướng rẽ trái không tồn tại — fallback sang phải
                    Direction fallback = DirectionHelper.getRightDirection(vehicle.getDirection());
                    if (!layout.hasDirection(fallback))
                        return;
                    initiateTurnRight(vehicle);
                    vehicle.setTurnType(util.TurnType.RIGHT);
                } else {
                    initiateTurnLeft(vehicle);
                }
                break;
            }
            case RIGHT: {
                Direction target = DirectionHelper.getRightDirection(vehicle.getDirection());
                if (!layout.hasDirection(target)) {
                    // Hướng rẽ phải không tồn tại — fallback sang trái
                    Direction fallback = DirectionHelper.getLeftDirection(vehicle.getDirection());
                    if (!layout.hasDirection(fallback))
                        return;
                    initiateTurnLeft(vehicle);
                    vehicle.setTurnType(util.TurnType.LEFT);
                } else {
                    initiateTurnRight(vehicle);
                }
                break;
            }
            case STRAIGHT: {
                // Kiểm tra hướng đối diện có tồn tại không
                Direction opposite = vehicle.getDirection().opposite();
                if (!layout.hasDirection(opposite)) {
                    // Không thể đi thẳng (3-way không có nhánh đối diện)
                    // Rẽ ngẫu nhiên sang trái hoặc phải
                    Direction left = DirectionHelper.getLeftDirection(vehicle.getDirection());
                    Direction right = DirectionHelper.getRightDirection(vehicle.getDirection());
                    boolean canLeft = layout.hasDirection(left);
                    boolean canRight = layout.hasDirection(right);
                    if (canLeft && canRight) {
                        if (Math.random() < 0.5)
                            initiateTurnLeft(vehicle);
                        else
                            initiateTurnRight(vehicle);
                    } else if (canLeft)
                        initiateTurnLeft(vehicle);
                    else if (canRight)
                        initiateTurnRight(vehicle);
                    else
                        return;
                } else {
                    return; // đi thẳng bình thường
                }
                break;
            }
            default:
                return;
        }
        vehicle.setTurned(true);
    }

    private void initiateTurnLeft(Vehicle vehicle) {
        switch (vehicle.getDirection()) {
            case NORTH: vehicle.setTargetDirection(Direction.WEST);  vehicle.setTargetAngle(180); break;
            case SOUTH: vehicle.setTargetDirection(Direction.EAST);  vehicle.setTargetAngle(0);   break;
            case EAST:  vehicle.setTargetDirection(Direction.NORTH); vehicle.setTargetAngle(-90); break;
            case WEST:  vehicle.setTargetDirection(Direction.SOUTH); vehicle.setTargetAngle(90);  break;
            default: return;
        }
        vehicle.setTurning(true);
    }

    private void initiateTurnRight(Vehicle vehicle) {
        switch (vehicle.getDirection()) {
            case NORTH: vehicle.setTargetDirection(Direction.EAST);  vehicle.setTargetAngle(0);   break;
            case SOUTH: vehicle.setTargetDirection(Direction.WEST);  vehicle.setTargetAngle(180); break;
            case EAST:  vehicle.setTargetDirection(Direction.SOUTH); vehicle.setTargetAngle(90);  break;
            case WEST:  vehicle.setTargetDirection(Direction.NORTH); vehicle.setTargetAngle(-90); break;
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
