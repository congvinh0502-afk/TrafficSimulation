package system.emergency;

import java.util.List;
import model.vehicle.Ambulance;
import model.vehicle.FireTruck;
import model.vehicle.Vehicle;
import util.Lane;

/**
 * EmergencyVehicleSystem – xử lý ưu tiên xe khẩn cấp.
 *
 * FIX:
 *  1. Xe thường (mọi hướng, không chỉ cùng hướng) phát hiện xe cứu thương
 *     / cứu hỏa trong bán kính 300px sẽ dạt vào sát lề (Lane.RIGHT)
 *     và dừng lại để nhường đường.
 *  2. Sau khi xe khẩn cấp đi qua (dist > RESUME_RADIUS), xe thường
 *     tự phục hồi: clearYielding(), setStopped(false), lane trở về bình thường.
 *  3. Xe khẩn cấp không bị ảnh hưởng bởi system này (chỉ xử lý xe thường).
 *
 * NOTE: CollisionSystem.maintainDistance() cũng có logic nhường đường
 *       nhưng ở tầng collision. EmergencyVehicleSystem chạy ở tầng system
 *       riêng (pipeline gọi từ VehicleUpdatePipeline nếu được thêm vào),
 *       hoặc có thể gọi từ TrafficController.updateVehicles().
 */
public class EmergencyVehicleSystem {

    // Bán kính để xe thường bắt đầu nhường đường
    private static final double YIELD_RADIUS   = 300;
    // Bán kính để xe thường ngừng nhường (đã vượt qua rồi)
    private static final double RESUME_RADIUS  = 380;

    public void updateEmergencyVehicles(List<Vehicle> vehicles) {

        // Tìm tất cả xe khẩn cấp đang hoạt động
        boolean hasActiveEmergency = false;
        for (Vehicle v : vehicles) {
            if (v instanceof Ambulance || v instanceof FireTruck) {
                hasActiveEmergency = true;
                break;
            }
        }

        for (Vehicle other : vehicles) {

            // Chỉ xử lý xe thường
            if (other instanceof Ambulance || other instanceof FireTruck) continue;

            boolean shouldYield = false;

            // Kiểm tra tất cả xe khẩn cấp
            for (Vehicle emergency : vehicles) {

                if (!(emergency instanceof Ambulance) && !(emergency instanceof FireTruck)) continue;

                double dx   = emergency.getX() - other.getX();
                double dy   = emergency.getY() - other.getY();
                double dist = Math.sqrt(dx * dx + dy * dy);

                if (dist < YIELD_RADIUS) {
                    shouldYield = true;
                    break;
                }
            }

            if (shouldYield) {
                applyYield(other);
            } else if (!hasActiveEmergency || isAllEmergencyFar(other, vehicles)) {
                // Không còn xe khẩn cấp nào gần → phục hồi bình thường
                resumeNormal(other);
            }
        }
    }

    /**
     * Xe thường nhường đường: dạt sang lane RIGHT (sát lề) và dừng lại.
     */
    private void applyYield(Vehicle vehicle) {

        // Dạt vào lề nếu chưa đang đổi lane
        if (!vehicle.isChangingLane() && vehicle.getLane() != Lane.RIGHT) {
            vehicle.setTargetLane(Lane.RIGHT);
            vehicle.setChangingLane(true);
            // Cooldown ngắn hơn bình thường để xe dạt nhanh
            vehicle.setLaneChangeCooldown(20);
        }

        // Dừng lại nhường đường
        vehicle.setStopped(true);
    }

    /**
     * Phục hồi xe sau khi nhường: cho chạy tiếp, không reset lane về LEFT
     * (xe có thể ở RIGHT và tiếp tục, LaneAlignmentSystem sẽ căn chỉnh).
     */
    private void resumeNormal(Vehicle vehicle) {

        // Chỉ reset stopped nếu xe bị dừng do nhường đường
        // (không override stopped do đèn đỏ hoặc va chạm)
        if (vehicle.isStopped()) {
            vehicle.setStopped(false);
        }
    }

    /**
     * Kiểm tra tất cả xe khẩn cấp đã ra xa RESUME_RADIUS chưa.
     */
    private boolean isAllEmergencyFar(Vehicle vehicle, List<Vehicle> vehicles) {

        for (Vehicle other : vehicles) {

            if (!(other instanceof Ambulance) && !(other instanceof FireTruck)) continue;

            double dx   = other.getX() - vehicle.getX();
            double dy   = other.getY() - vehicle.getY();
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist < RESUME_RADIUS) {
                return false; // vẫn còn xe khẩn cấp gần
            }
        }
        return true;
    }
}
