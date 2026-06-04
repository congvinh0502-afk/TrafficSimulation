package strategy.driver;

import model.trafficlight.LightColor;
import model.trafficlight.TrafficLight;
import model.vehicle.Vehicle;

import java.util.List;

/**
 * Lái xe hung hăng — tốc độ cao, thường xuyên vượt đèn đỏ.
 *
 * <p>
 * Khi đèn đỏ: chỉ 40% khả năng dừng, 60% khả năng vượt.
 * Tốc độ nhanh hơn {@link NormalDriver}.
 * </p>
 */
public class AggressiveDriver implements DriverBehavior {

    private static final int SPEED = 7;
    private static final double STOP_PROBABILITY = 0.4; // xác suất dừng khi đèn đỏ

    @Override
    public boolean shouldStop(Vehicle self, List<Vehicle> vehicles, TrafficLight relevantLight) {
        if (relevantLight.getColor() == LightColor.RED) {
            return Math.random() < STOP_PROBABILITY;
        }
        return false;
    }

    @Override
    public int getSpeed() {
        return SPEED;
    }
}