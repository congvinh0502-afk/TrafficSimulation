package system.movement;

import java.awt.Rectangle;
import layout.IntersectionLayout;
import manager.LaneManager;
import model.intersection.IntersectionType;
import model.vehicle.Vehicle;
import util.Direction;
import util.DirectionHelper;

/**
 * TurningSystem — xử lý rẽ xe tại ngã rẽ.
 *
 * Thay đổi so với phiên bản cũ:
 *   - handleTurning() dùng layout.getTriggerBounds() thay vì hardcode (420–530).
 *   - recoverLane() dùng LaneManager.getLaneCenterX/Y() (đã delegate sang layout).
 *   - Logic turning (smoothTurning, updateVehicleAngle) giữ nguyên hoàn toàn.
 */
public class TurningSystem {

    public void updateTurning(Vehicle vehicle, IntersectionType type) {
        handleTurning(vehicle, type);

        if (vehicle.isTurning()) {
            smoothTurning(vehicle);
        }
    }

    public void handleTurning(Vehicle vehicle, IntersectionType type) {

        if (vehicle.hasTurned()) {
            return;
        }

        // Lấy trigger bounds từ layout thay vì hardcode
        IntersectionLayout layout = LaneManager.getLayout();
        Rectangle trigger = layout.getTriggerBounds();

        double hw = vehicle.getWidth()  / 2;
        double hh = vehicle.getHeight() / 2;

        boolean insideIntersection =
                vehicle.getX() + hw > trigger.getMinX()
             && vehicle.getX() - hw < trigger.getMaxX()
             && vehicle.getY() + hh > trigger.getMinY()
             && vehicle.getY() - hh < trigger.getMaxY();

        if (!insideIntersection) {
            return;
        }

        Direction targetDirection;

        switch (vehicle.getTurnType()) {

            case LEFT:
                targetDirection = DirectionHelper.getLeftDirection(
                        vehicle.getDirection()
                );
                if (!type.getDirections().contains(targetDirection)) {
                    return;
                }
                turnLeft(vehicle);
                break;

            case RIGHT:
                targetDirection = DirectionHelper.getRightDirection(
                        vehicle.getDirection()
                );
                if (!type.getDirections().contains(targetDirection)) {
                    return;
                }
                turnRight(vehicle);
                break;

            default:
                return;
        }

        vehicle.setTurned(true);
    }

    // ─────────────────────────────────────────────────────────────
    // TURN LEFT / RIGHT — giữ nguyên logic
    // ─────────────────────────────────────────────────────────────

    public void turnLeft(Vehicle vehicle) {
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
            case NORTHEAST:
                // rẽ trái từ NORTHEAST → sang hướng NORTH (gần nhất bên trái)
                vehicle.setTargetDirection(Direction.NORTH);
                vehicle.setTargetAngle(-90);
                break;
        }
        if (!(vehicle instanceof model.vehicle.Bicycle)) {
            manager.SoundManager.getInstance().onVehicleSpawned("turnsignal");
        }
        vehicle.setTurning(true);
    }

    public void turnRight(Vehicle vehicle) {
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
            case NORTHEAST:
                // rẽ phải từ NORTHEAST → sang hướng EAST
                vehicle.setTargetDirection(Direction.EAST);
                vehicle.setTargetAngle(0);
                break;
        }
        
        if (!(vehicle instanceof model.vehicle.Bicycle)) {
        manager.SoundManager.getInstance().onVehicleSpawned("turnsignal");
        }
        vehicle.setTurning(true);
    }

    // ─────────────────────────────────────────────────────────────
    // SMOOTH TURNING — giữ nguyên logic
    // ─────────────────────────────────────────────────────────────

    public void smoothTurning(Vehicle vehicle) {
        if (!vehicle.isTurning()) {
            return;
        }

        double speed = vehicle.getSpeed() * 0.35;
        double angle = Math.toRadians(vehicle.getAngle());

        double dx = Math.cos(angle) * speed;
        double dy = Math.sin(angle) * speed;

        vehicle.setX(vehicle.getX() + dx);
        vehicle.setY(vehicle.getY() + dy);

        finishTurning(vehicle);
        updateVehicleAngle(vehicle);
    }

    private void finishTurning(Vehicle vehicle) {
        boolean finished = Math.abs(vehicle.getAngle() - vehicle.getTargetAngle()) < 5;

        if (!finished) {
            return;
        }

        vehicle.setDirection(vehicle.getTargetDirection());
        assignLaneAfterTurn(vehicle);
        recoverLane(vehicle);

        if (!(vehicle instanceof model.vehicle.Bicycle) && vehicle.isTurning()) {
            manager.SoundManager.getInstance().onVehicleRemoved("turnsignal");
        }
        vehicle.setTurning(false);
    }

    /**
     * Snap xe về trung tâm làn sau khi rẽ xong.
     * Dùng LaneManager.getLaneCenterX/Y() — đã delegate về layout.
     */
    public void recoverLane(Vehicle vehicle) {
        switch (vehicle.getDirection()) {
            case NORTH:
            case SOUTH:
                vehicle.setX(
                    LaneManager.getLaneCenterX(
                        vehicle.getDirection(),
                        vehicle.getLane()
                    )
                );
                break;

            case EAST:
            case WEST:
                vehicle.setY(
                    LaneManager.getLaneCenterY(
                        vehicle.getDirection(),
                        vehicle.getLane()
                    )
                );
                break;

            // NORTHEAST: không snap vì đường chéo không có lane center cố định
            default:
                break;
        }
    }

    public void assignLaneAfterTurn(Vehicle vehicle) {
        vehicle.setLane(util.Lane.RIGHT);
    }

    // ─────────────────────────────────────────────────────────────
    // UPDATE ANGLE — giữ nguyên logic
    // ─────────────────────────────────────────────────────────────

    public void updateVehicleAngle(Vehicle vehicle) {
        double rotateSpeed = 2;
        double current     = vehicle.getAngle();
        double target      = vehicle.getTargetAngle();

        double diff = target - current;
        while (diff >  180) diff -= 360;
        while (diff < -180) diff += 360;

        if (Math.abs(diff) < rotateSpeed) {
            vehicle.setAngle(target);
            return;
        }

        if (diff > 0) {
            vehicle.setAngle(current + rotateSpeed);
        } else {
            vehicle.setAngle(current - rotateSpeed);
        }
    }
}
