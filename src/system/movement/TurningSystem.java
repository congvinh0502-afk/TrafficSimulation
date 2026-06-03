package system.movement;

import layout.IntersectionLayout;
import manager.LaneManager;
import model.intersection.IntersectionType;
import model.vehicle.Vehicle;
import util.Direction;
import util.DirectionHelper;

import java.awt.Rectangle;

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

        // ĐỔI SANG INTERSECTION BOUNDS ĐỂ XE BO CUA NGAY TỪ MÉP VỈA HÈ
        IntersectionLayout layout = LaneManager.getLayout();
        Rectangle trigger = layout.getIntersectionBounds();

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
        vehicle.setTurning(true);
    }

    // ─────────────────────────────────────────────────────────────
    // SMOOTH TURNING — giữ nguyên logic
    // ─────────────────────────────────────────────────────────────

    /*public void smoothTurning(Vehicle vehicle) {
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
    }*/
    public void smoothTurning(Vehicle vehicle) {
        if (!vehicle.isTurning()) return;

        // 1. TẠO QUỸ ĐẠO RẼ (Chỉ chạy 1 lần lúc bắt đầu rẽ)
        if (!vehicle.isFollowingPath() && vehicle.getPath() == null) {
            layout.IntersectionLayout layout = LaneManager.getLayout();
            java.awt.Rectangle bounds = layout.getRecoverBounds();
            
            // Tìm điểm kết thúc (làn thoát) dựa vào Recover Bounds
            double endX = vehicle.getX();
            double endY = vehicle.getY();
            
            if (vehicle.getTargetDirection() == Direction.EAST) { endX = bounds.getMaxX(); endY = LaneManager.getLaneCenterY(Direction.EAST, util.Lane.RIGHT); }
            if (vehicle.getTargetDirection() == Direction.WEST) { endX = bounds.getMinX(); endY = LaneManager.getLaneCenterY(Direction.WEST, util.Lane.RIGHT); }
            if (vehicle.getTargetDirection() == Direction.SOUTH) { endX = LaneManager.getLaneCenterX(Direction.SOUTH, util.Lane.RIGHT); endY = bounds.getMaxY(); }
            if (vehicle.getTargetDirection() == Direction.NORTH) { endX = LaneManager.getLaneCenterX(Direction.NORTH, util.Lane.RIGHT); endY = bounds.getMinY(); }

            java.util.List<double[]> curvePath;

            // Xử lý riêng biệt cho vòng xuyến Ngã 5
            if (layout instanceof layout.FiveWayLayout) {
                curvePath = util.PathBuilder.buildRoundaboutPath(
                        vehicle.getX(), vehicle.getY(), endX, endY,
                        layout.getCenterX(), layout.getCenterY(), 80.0
                );
            } else {
                double dist = Math.hypot(endX - vehicle.getX(), endY - vehicle.getY());
                
                // DÙNG HẰNG SỐ 0.39: Quỹ đạo sẽ tạo thành một cung tròn hoàn hảo
                double curveOffset = dist * 0.39; 

                double c1X = vehicle.getX() + getDx(vehicle.getDirection()) * curveOffset;
                double c1Y = vehicle.getY() + getDy(vehicle.getDirection()) * curveOffset;
                double c2X = endX - getDx(vehicle.getTargetDirection()) * curveOffset;
                double c2Y = endY - getDy(vehicle.getTargetDirection()) * curveOffset;

                curvePath = util.PathBuilder.buildCubicPath(
                        vehicle.getX(), vehicle.getY(), c1X, c1Y, c2X, c2Y, endX, endY, 30
                );
            }
            vehicle.startFollowingPath(curvePath);
        }

        // 2. CẬP NHẬT GÓC ĐẦU XE ĐỂ RENDER CHUẨN XÁC
        updateVehicleAngle(vehicle);

        // 3. NẾU ĐI HẾT ĐƯỜNG CONG THÌ KẾT THÚC RẼ
        if (!vehicle.isFollowingPath() && vehicle.getPath() != null) {
            // Bắt buộc đổi Direction và Angle về hướng làn đường mới
            vehicle.setDirection(vehicle.getTargetDirection());
            vehicle.setAngle(vehicle.getTargetAngle());
            
            // Snap xe vào đúng mép đường (tránh bị lệch pixel)
            vehicle.setLane(util.Lane.RIGHT);
            recoverLane(vehicle);
            
            // Dọn dẹp và kết thúc trạng thái rẽ
            vehicle.setTurning(false);
            vehicle.setPath(null); 
        }
    }

    // Copy thêm 2 hàm tiện ích nhỏ này xuống dưới cùng class TurningSystem
    private double getDx(Direction dir) {
        if (dir == Direction.EAST) return 1;
        if (dir == Direction.WEST) return -1;
        if (dir == Direction.NORTHEAST) return 0.31; 
        return 0;
    }

    private double getDy(Direction dir) {
        if (dir == Direction.SOUTH) return 1;
        if (dir == Direction.NORTH) return -1;
        if (dir == Direction.NORTHEAST) return -0.95;
        return 0;
    }

    private void finishTurning(Vehicle vehicle) {
        boolean finished = Math.abs(vehicle.getAngle() - vehicle.getTargetAngle()) < 5;

        if (!finished) {
            return;
        }

        vehicle.setDirection(vehicle.getTargetDirection());
        assignLaneAfterTurn(vehicle);
        recoverLane(vehicle);
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
        // NẾU ĐANG CHẠY ĐƯỜNG CONG THÌ BỎ QUA BỘ QUAY CŨ
        if (vehicle.isFollowingPath()) {
            return; 
        }
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
