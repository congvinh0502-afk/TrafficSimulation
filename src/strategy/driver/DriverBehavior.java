package strategy.driver;

import model.trafficlight.TrafficLight;
import model.vehicle.Vehicle;

import java.util.List;

/**
 * Chiến lược lái xe — xác định cách phương tiện phản ứng
 * với đèn giao thông và cho biết tốc độ di chuyển.
 *
 * <p>
 * Áp dụng Strategy Pattern: mỗi phương tiện giữ một
 * tham chiếu {@code DriverBehavior} có thể thay đổi lúc chạy.
 * Thêm kiểu lái mới chỉ cần implement interface này —
 * không cần sửa TrafficController hay Vehicle.
 * </p>
 *
 * <p>
 * Các implementation hiện có:
 * <ul>
 * <li>{@link NormalDriver} — tuân thủ đèn đỏ</li>
 * <li>{@link AggressiveDriver} — có xác suất vượt đèn đỏ</li>
 * <li>{@link EmergencyDriver} — luôn vượt đèn, ưu tiên tuyệt đối</li>
 * </ul>
 * </p>
 */
public interface DriverBehavior {

    /**
     * Quyết định xe có nên dừng tại đèn không.
     *
     * @param self          xe đang được kiểm tra
     * @param vehicles      toàn bộ xe trên bản đồ (để tham khảo tình huống)
     * @param relevantLight đèn áp dụng cho hướng đi của xe này
     * @return {@code true} nếu xe nên dừng
     */
    boolean shouldStop(Vehicle self, List<Vehicle> vehicles, TrafficLight relevantLight);

    /**
     * Tốc độ di chuyển của kiểu lái này (pixel/frame).
     *
     * @return tốc độ dương
     */
    int getSpeed();
}