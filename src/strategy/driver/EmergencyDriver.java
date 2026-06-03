package strategy.driver;

import java.util.List;

import model.trafficlight.TrafficLight;
import model.vehicle.Vehicle;

/**
 * Lái xe ưu tiên (cứu thương / cứu hỏa) — không bao giờ dừng đèn đỏ.
 *
 * <p>
 * Luôn trả về {@code false} cho {@link #shouldStop} —
 * xe ưu tiên có quyền vượt tất cả đèn hiệu.
 * Tốc độ cao nhất trong các loại lái.
 * </p>
 */
public class EmergencyDriver implements DriverBehavior {

    private static final int SPEED = 3;

    @Override
    public boolean shouldStop(Vehicle self, List<Vehicle> vehicles, TrafficLight relevantLight) {
        return false; // xe ưu tiên không dừng đèn đỏ
    }

    @Override
    public int getSpeed() {
        return SPEED;
    }
}