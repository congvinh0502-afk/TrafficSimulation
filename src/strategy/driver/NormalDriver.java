package strategy.driver;

import java.util.List;

import model.trafficlight.LightColor;
import model.trafficlight.TrafficLight;
import model.vehicle.Vehicle;

/**
 * Lái xe bình thường — tuân thủ hoàn toàn luật giao thông.
 *
 * <p>
 * Luôn dừng khi đèn đỏ. Tốc độ vừa phải.
 * </p>
 */
public class NormalDriver implements DriverBehavior {

    private static final int SPEED = 1;

    @Override
    public boolean shouldStop(Vehicle self, List<Vehicle> vehicles, TrafficLight relevantLight) {
        return relevantLight.getColor() == LightColor.RED;
    }

    @Override
    public int getSpeed() {
        return SPEED;
    }
}