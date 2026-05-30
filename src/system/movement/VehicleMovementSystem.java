package system.movement;

import config.Constants;
import model.vehicle.Vehicle;

/**
 * Hệ thống di chuyển chính của xe mỗi frame.
 *
 * <p>
 * Trước khi gọi {@link Vehicle#move()}, reset trạng thái bị kẹt
 * cho xe đã ra khỏi vùng giao lộ ({@link #recoverAfterIntersection}).
 * Điều này đảm bảo xe không bị đóng băng sau khi rẽ xong.
 * </p>
 */
public class VehicleMovementSystem {

    /**
     * Di chuyển xe một bước nếu không bị dừng.
     * Trước tiên kiểm tra xe đã thoát khỏi vùng giao lộ chưa.
     *
     * @param vehicle xe cần cập nhật
     */
    public void move(Vehicle vehicle) {
        recoverAfterIntersection(vehicle);
        if (!vehicle.isStopped()) {
            vehicle.move();
        }
    }

    /**
     * Reset trạng thái kẹt khi xe đã hoàn toàn ra khỏi vùng giao lộ.
     *
     * <p>
     * Vùng giao lộ mở rộng ({@link Constants#RECOVER_LEFT} ..
     * {@link Constants#RECOVER_RIGHT}) cho phép xe kết thúc animation
     * rẽ trước khi bị reset, tránh snap sai vị trí.
     * </p>
     */
    private void recoverAfterIntersection(Vehicle vehicle) {
        boolean outside = vehicle.getX() < Constants.RECOVER_LEFT
                || vehicle.getX() > Constants.RECOVER_RIGHT
                || vehicle.getY() < Constants.RECOVER_TOP
                || vehicle.getY() > Constants.RECOVER_BOTTOM;

        if (!outside)
            return;

        vehicle.setStopped(false);
        vehicle.setChangingLane(false);
        vehicle.setTurning(false);
    }
}