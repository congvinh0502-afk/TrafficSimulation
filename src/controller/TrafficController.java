package controller;

import model.intersection.IntersectionType;
import model.trafficlight.TrafficLight;
import model.vehicle.Vehicle;
import system.vehicle.VehicleUpdatePipeline;
import util.Direction;

import java.util.List;

/**
 * Điều phối cập nhật toàn bộ phương tiện mỗi frame.
 *
 * <p>
 * Đây là cầu nối giữa tầng view ({@code SimulationPanel})
 * và tầng logic ({@link VehicleUpdatePipeline}).
 * View không gọi trực tiếp vào các system — chỉ gọi qua lớp này.
 * </p>
 *
 * <p>
 * Cũng cung cấp các phương thức thống kê phục vụ hiển thị
 * bảng điều khiển (HUD) trên màn hình mô phỏng.
 * </p>
 *
 * <p>
 * <b>Mở rộng:</b> Để thêm loại ngã rẽ mới, chỉ cần mở rộng
 * {@link IntersectionType} — không cần sửa lớp này.
 * </p>
 */
public class TrafficController {

    private final VehicleUpdatePipeline pipeline;

    public TrafficController() {
        this.pipeline = new VehicleUpdatePipeline();
    }

    // ==========================================================
    // Cập nhật xe
    // ==========================================================

    /**
     * Cập nhật tất cả xe một frame.
     *
     * @param vehicles        danh sách xe đang lưu thông
     * @param verticalLight   đèn hướng dọc (NORTH / SOUTH)
     * @param horizontalLight đèn hướng ngang (EAST / WEST)
     * @param type            loại ngã rẽ hiện tại
     */
    public void updateVehicles(List<Vehicle> vehicles,
            TrafficLight verticalLight,
            TrafficLight horizontalLight,
            IntersectionType type) {
        for (Vehicle vehicle : vehicles) {
            pipeline.update(vehicle, vehicles, verticalLight, horizontalLight, type);
        }
    }

    // ==========================================================
    // Thống kê — dùng cho HUD
    // ==========================================================

    /**
     * Đếm số xe đang đi theo một trong hai hướng cho trước.
     *
     * @param vehicles danh sách xe
     * @param d1       hướng thứ nhất
     * @param d2       hướng thứ hai
     * @return tổng số xe khớp d1 hoặc d2
     */
    public int countVehiclesByDirection(List<Vehicle> vehicles, Direction d1, Direction d2) {
        int count = 0;
        for (Vehicle v : vehicles) {
            if (v.getDirection() == d1 || v.getDirection() == d2)
                count++;
        }
        return count;
    }

    /**
     * Đếm số xe đang trong trạng thái dừng.
     *
     * @param vehicles danh sách xe
     * @return số xe bị dừng
     */
    public int countStoppedVehicles(List<Vehicle> vehicles) {
        int count = 0;
        for (Vehicle v : vehicles) {
            if (v.isStopped())
                count++;
        }
        return count;
    }
}