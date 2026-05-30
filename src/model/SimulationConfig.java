package model;

import model.intersection.IntersectionType;
import util.TrafficDensity;

/**
 * Cấu hình một phiên mô phỏng do người dùng chọn từ menu.
 *
 * <p>
 * Đối tượng này là bất biến sau khi tạo — các giá trị
 * không thay đổi trong suốt vòng đời của phiên mô phỏng.
 * </p>
 */
public class SimulationConfig {

    private final IntersectionType intersectionType;
    private final String trafficMode;
    private final String lightType;
    private final int vehicleCount;
    private final TrafficDensity trafficDensity;

    /**
     * @param intersectionType loại ngã rẽ (THREE_WAY / FOUR_WAY / FIVE_WAY)
     * @param trafficMode      chế độ đèn ("AUTO" hoặc "MANUAL")
     * @param lightType        kiểu hiển thị đếm ngược ("NO COUNTDOWN" / "ALWAYS
     *                         COUNTDOWN" / "COUNT <= 10")
     * @param vehicleCount     số lượng xe tối đa trên bản đồ
     * @param trafficDensity   mật độ spawn xe (LOW / MEDIUM / HIGH)
     */
    public SimulationConfig(IntersectionType intersectionType,
            String trafficMode,
            String lightType,
            int vehicleCount,
            TrafficDensity trafficDensity) {
        this.intersectionType = intersectionType;
        this.trafficMode = trafficMode;
        this.lightType = lightType;
        this.vehicleCount = vehicleCount;
        this.trafficDensity = trafficDensity;
    }

    /** @return loại ngã rẽ */
    public IntersectionType getIntersectionType() {
        return intersectionType;
    }

    /** @return "AUTO" hoặc "MANUAL" */
    public String getTrafficMode() {
        return trafficMode;
    }

    /** @return kiểu hiển thị đếm ngược đèn */
    public String getLightType() {
        return lightType;
    }

    /** @return số xe tối đa */
    public int getVehicleCount() {
        return vehicleCount;
    }

    /** @return mật độ giao thông */
    public TrafficDensity getTrafficDensity() {
        return trafficDensity;
    }
}