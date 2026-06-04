/* Dành cho Ngã ba, Ngã tư */

package strategy.turn;

import config.Constants;
import model.intersection.IntersectionLayout;
import model.vehicle.Vehicle;
import util.Direction;
import util.Lane;
import util.TurnType;

public class OrthogonalTurnStrategy implements TurnStrategy {

    @Override
    public void handleTurning(Vehicle vehicle, IntersectionLayout layout) {
        if (vehicle.hasTurned())
            return;

        double hw = vehicle.getWidth() / 2;
        double hh = vehicle.getHeight() / 2;

        if (!layout.getTurnZone().contains(vehicle.getX(), vehicle.getY(), hw, hh))
            return;

        Direction dir = vehicle.getDirection();
        Direction target = dir;
        double angle = vehicle.getAngle();

        switch (vehicle.getTurnType()) {
            case LEFT:
                switch (dir) {
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
                    case SOUTH:
                        target = Direction.EAST;
                        angle = 0;
                        break;
                    default:
                        break;
                }
                vehicle.setTargetDirection(target);
                vehicle.setTargetAngle(angle);
                vehicle.setTurning(true);
                break;
            case RIGHT:
                switch (dir) {
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
                    case SOUTH:
                        target = Direction.WEST;
                        angle = 180;
                        break;
                    default:
                        break;
                }
                vehicle.setTargetDirection(target);
                vehicle.setTargetAngle(angle);
                vehicle.setTurning(true);
                break;
            case STRAIGHT:
                switch (dir) {
                    case NORTH:
                        target = Direction.NORTH;
                        angle = -90;
                        break;
                    case SOUTH:
                        target = Direction.SOUTH;
                        angle = 90;
                        break;
                    case EAST:
                        target = Direction.EAST;
                        angle = 0;
                        break;
                    case WEST:
                        target = Direction.WEST;
                        angle = 180;
                        break;
                    default:
                        break;
                }
                vehicle.setTargetDirection(target);
                vehicle.setTargetAngle(angle);
                // NGÃ 3 VÀ 4: ĐI THẲNG LÀ ĐI THẲNG, TẮT BẺ LÁI
                vehicle.setTurning(false);
                break;
        }
        vehicle.setTurned(true);
    }

    @Override
    public void smoothTurning(Vehicle vehicle, IntersectionLayout layout) {
        if (!vehicle.isTurning())
            return;

        // Tính quỹ đạo rẽ tròn hoàn hảo:
        // Rẽ phải -> đi sát góc (bán kính 25px)
        // Rẽ trái -> đánh lái qua tâm giao lộ (bán kính 75px)
        double radius = (vehicle.getTurnType() == TurnType.LEFT) ? 75.0 : 25.0;

        double speed = vehicle.getSpeed();
        if (speed < 0.5)
            speed = 0.5; // Tránh kẹt khi tốc độ quá thấp

        // v = omega * r => Vận tốc góc omega = v / r (đổi ra độ)
        double angularSpeedDeg = Math.toDegrees(speed / radius);

        // Di chuyển tiếp theo vector vòng cung
        double rad = Math.toRadians(vehicle.getAngle());
        vehicle.setX(vehicle.getX() + Math.cos(rad) * speed);
        vehicle.setY(vehicle.getY() + Math.sin(rad) * speed);

        updateVehicleAngle(vehicle, angularSpeedDeg);
        finishTurning(vehicle);
    }

    private void updateVehicleAngle(Vehicle vehicle, double angularSpeed) {
        double current = vehicle.getAngle();
        double target = vehicle.getTargetAngle();
        double diff = target - current;

        // Chuẩn hóa góc chênh lệch về [-180, 180]
        while (diff > 180)
            diff -= 360;
        while (diff < -180)
            diff += 360;

        // Nếu xe đã xoay gần tới góc đích
        if (Math.abs(diff) <= angularSpeed) {
            vehicle.setAngle(target);
            return;
        }

        // Cập nhật góc tăng dần mỗi frame
        vehicle.setAngle(current + (diff > 0 ? angularSpeed : -angularSpeed));
    }

    private void finishTurning(Vehicle vehicle) {
        double diff = vehicle.getTargetAngle() - vehicle.getAngle();
        while (diff > 180)
            diff -= 360;
        while (diff < -180)
            diff += 360;

        // Cho sai số 2 độ để kết thúc vòng cua chuẩn xác
        if (Math.abs(diff) >= 2.0)
            return;

        vehicle.setDirection(vehicle.getTargetDirection());
        vehicle.setLane(Lane.RIGHT);
        vehicle.setTurning(false);
        vehicle.setAcceleration(Constants.DEFAULT_ACCELERATION);
        vehicle.setPostTurnAligning(true);
    }
}