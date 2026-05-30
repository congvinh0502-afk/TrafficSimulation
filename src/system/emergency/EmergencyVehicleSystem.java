package system.emergency;

import config.Constants;
import model.vehicle.Ambulance;
import model.vehicle.FireTruck;
import model.vehicle.Vehicle;

import java.util.List;

/**
 * Hệ thống ưu tiên xe cứu thương và xe cứu hỏa.
 *
 * <p>
 * Mỗi frame, với mỗi xe ưu tiên đang lưu thông:
 * buộc các xe thường cùng hướng trong bán kính
 * {@link Constants#EMERGENCY_YIELD_RADIUS} phải dừng lại nhường đường.
 * </p>
 *
 * <p>
 * Xe ưu tiên không bao giờ bị hệ thống này dừng.
 * </p>
 */
public class EmergencyVehicleSystem {

    /**
     * Cập nhật trạng thái nhường đường cho xe ưu tiên.
     *
     * @param vehicles toàn bộ xe trên bản đồ
     */
    public void updateEmergencyVehicles(List<Vehicle> vehicles) {
        for (Vehicle emergency : vehicles) {
            if (!isEmergency(emergency))
                continue;

            for (Vehicle other : vehicles) {
                if (other == emergency)
                    continue;
                if (isEmergency(other))
                    continue;

                double dx = emergency.getX() - other.getX();
                double dy = emergency.getY() - other.getY();
                double dist = Math.sqrt(dx * dx + dy * dy);

                boolean sameDirection = (emergency.getDirection() == other.getDirection());
                boolean tooClose = (dist < Constants.EMERGENCY_YIELD_RADIUS);

                if (tooClose && sameDirection) {
                    other.setStopped(true);
                }
            }
        }
    }

    /** Kiểm tra xe có phải xe ưu tiên không. */
    private boolean isEmergency(Vehicle vehicle) {
        return vehicle instanceof Ambulance || vehicle instanceof FireTruck;
    }
}