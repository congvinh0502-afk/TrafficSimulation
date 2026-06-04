package system.movement;

import config.Constants;
import model.intersection.IntersectionLayout;
import model.vehicle.Vehicle;
import util.Direction;

/**
 * Hệ thống xử lý rẽ tại giao lộ.
 * Tách biệt hoàn toàn logic Ngã 4 và vòng xuyến CCW Ngã 5 để tránh lỗi xe biến mất, đi lấn cỏ.
 */
public class TurningSystem {

    public void updateTurning(Vehicle vehicle, IntersectionLayout layout) {
        handleTurning(vehicle, layout);
        if (vehicle.isTurning()) {
            smoothTurning(vehicle, layout);
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
                initiateTurnLeft(vehicle, isFiveWay);
                break;
            case RIGHT:
                initiateTurnRight(vehicle, isFiveWay);
                break;
            case STRAIGHT:
                initiateTurnStraight(vehicle, isFiveWay);
                break;
            default:
                return;
        }
        vehicle.setTurned(true);
    }

    private Direction[] fwOutDirs = {
            Direction.NORTH, Direction.FW_OUT_342, Direction.FW_OUT_54,
            Direction.FW_OUT_126, Direction.FW_OUT_198
    };
    private double[] fwOutAngles = { -90, 342, 54, 126, 198 };
    private Direction[] fwInDirs = {
            Direction.SOUTH, Direction.FW_IN_342, Direction.FW_IN_54,
            Direction.FW_IN_126, Direction.FW_IN_198
    };
    private double[] branchAngles = { 270, 342, 54, 126, 198 };

    private int getFiveWayIndex(Direction dir) {
        for (int i = 0; i < fwInDirs.length; i++) {
            if (fwInDirs[i] == dir)
                return i;
        }
        return -1;
    }

    private void initiateFiveWayTurn(Vehicle vehicle) {
        int inIdx = getFiveWayIndex(vehicle.getDirection());
        if (inIdx < 0) return;

        int outIdx;
        // Đi theo vòng xuyến ngược chiều kim đồng hồ (CCW)
        switch (vehicle.getTurnType()) {
            case RIGHT:    outIdx = (inIdx + 4) % 5; break; // Lối ra thứ 1
            case STRAIGHT: outIdx = (inIdx + 3) % 5; break; // Lối ra thứ 2
            case LEFT:     outIdx = (inIdx + 2) % 5; break; // Lối ra thứ 3
            default:       outIdx = (inIdx + 3) % 5; break;
        }

        vehicle.setTargetDirection(fwOutDirs[outIdx]);
        vehicle.setTargetAngle(fwOutAngles[outIdx]); 
        vehicle.setTurning(true);
    }

    private void initiateTurnLeft(Vehicle vehicle, boolean isFiveWay) {
        Direction dir = vehicle.getDirection();
        Direction target = dir;
        double angle = vehicle.getAngle();

        if (isFiveWay) {
            initiateFiveWayTurn(vehicle);
            return;
        } else {
            switch (dir) {
                case NORTH: target = Direction.WEST; angle = 180; break;
                case EAST:  target = Direction.NORTH; angle = -90; break;
                case WEST:  target = Direction.SOUTH; angle = 90; break;
                case SOUTH: target = Direction.EAST; angle = 0; break;
                default: break;
            }
        }
        vehicle.setTargetDirection(target);
        vehicle.setTargetAngle(angle);
        vehicle.setTurning(true);
    }

    private void initiateTurnRight(Vehicle vehicle, boolean isFiveWay) {
        Direction dir = vehicle.getDirection();
        Direction target = dir;
        double angle = vehicle.getAngle();

        if (isFiveWay) {
            initiateFiveWayTurn(vehicle);
            return;
        } else {
            switch (dir) {
                case NORTH: target = Direction.EAST; angle = 0; break;
                case EAST:  target = Direction.SOUTH; angle = 90; break;
                case WEST:  target = Direction.NORTH; angle = -90; break;
                case SOUTH: target = Direction.WEST; angle = 180; break;
                default: break;
            }
        }
        vehicle.setTargetDirection(target);
        vehicle.setTargetAngle(angle);
        vehicle.setTurning(true);
    }

    private void initiateTurnStraight(Vehicle vehicle, boolean isFiveWay) {
        Direction dir = vehicle.getDirection();
        Direction target = dir;
        double angle = vehicle.getAngle();

        if (isFiveWay) {
            initiateFiveWayTurn(vehicle);
            return;
        } else {
            switch (dir) {
                case NORTH: target = Direction.NORTH; angle = -90; break;
                case SOUTH: target = Direction.SOUTH; angle = 90; break;
                case EAST:  target = Direction.EAST; angle = 0; break;
                case WEST:  target = Direction.WEST; angle = 180; break;
                default: break;
            }
        }
        vehicle.setTargetDirection(target);
        vehicle.setTargetAngle(angle);
        // Đi thẳng: không bẻ lái, chỉ set hướng
        vehicle.setTurning(false);
        vehicle.setTurned(true);
    }

    // ==========================================================
    // Di chuyển mượt khi đang rẽ
    // ==========================================================

    public void smoothTurning(Vehicle vehicle, IntersectionLayout layout) {
        if (!vehicle.isTurning())
            return;

        boolean isFiveWay = layout.getDirections().size() == 5;
        
        // --- XỬ LÝ VÒNG XUYẾN (ĐI THEO QUỸ ĐẠO TRÒN) ---
        if (isFiveWay) {
            double cx = layout.getCx();
            double cy = layout.getCy();
            
            double dx = vehicle.getX() - cx;
            double dy = vehicle.getY() - cy;
            double radius = Math.sqrt(dx * dx + dy * dy);
            
            // Ép xe dần bám vào bán kính an toàn của bùng binh
            if (radius > 120) radius -= 1.8;
            else if (radius < 115) radius += 1.8;
            
            double currentAngleRad = Math.atan2(dy, dx);
            double speed = vehicle.getSpeed() * Constants.TURNING_SPEED_FACTOR * 1.6;
            double angularSpeed = speed / radius;
            
            // Chạy CCW (ngược chiều kim đồng hồ) -> góc giảm dần
            double nextAngleRad = currentAngleRad - angularSpeed;
            
            vehicle.setX(cx + Math.cos(nextAngleRad) * radius);
            vehicle.setY(cy + Math.sin(nextAngleRad) * radius);
            
            // Chỉnh hướng đầu xe dọc theo tiếp tuyến của đường tròn
            double facingAngle = Math.toDegrees(nextAngleRad) - 90;
            vehicle.setAngle(facingAngle);
            
            // Kiểm tra lối ra
            double exitAngleRad = Math.toRadians(vehicle.getTargetAngle());
            double diff = Math.toDegrees(nextAngleRad - exitAngleRad);
            while (diff > 180) diff -= 360;
            while (diff < -180) diff += 360;
            
            // Nếu đã tới vị trí góc rẽ hướng ra -> Kết thúc cua
            if (Math.abs(diff) < Math.toDegrees(angularSpeed) * 1.5 && radius <= 130) {
                vehicle.setDirection(vehicle.getTargetDirection());
                vehicle.setAngle(vehicle.getTargetDirection().toAngleDeg());
                vehicle.setLane(util.Lane.RIGHT);
                vehicle.setTurning(false);
                vehicle.setAcceleration(Constants.DEFAULT_ACCELERATION);
                vehicle.setPostTurnAligning(true);
            }
            return;
        }

        // --- XỬ LÝ RẼ GÓC THƯỜNG (NGÃ 3, NGÃ 4) ---
        double speed = vehicle.getSpeed() * Constants.TURNING_SPEED_FACTOR;
        double rad = Math.toRadians(vehicle.getAngle());
        vehicle.setX(vehicle.getX() + Math.cos(rad) * speed);
        vehicle.setY(vehicle.getY() + Math.sin(rad) * speed);

        updateVehicleAngle(vehicle);
        finishTurning(vehicle);
    }

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

    private void finishTurning(Vehicle vehicle) {
        if (Math.abs(vehicle.getAngle() - vehicle.getTargetAngle()) >= Constants.TURN_FINISH_TOLERANCE)
            return;

        vehicle.setDirection(vehicle.getTargetDirection());
        vehicle.setLane(util.Lane.RIGHT);
        vehicle.setTurning(false);
        vehicle.setAcceleration(Constants.DEFAULT_ACCELERATION);
        vehicle.setPostTurnAligning(true);
    }
}