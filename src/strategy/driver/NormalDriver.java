package strategy.driver;

import model.trafficlight.LightColor;
import model.trafficlight.TrafficLight;
import model.vehicle.Vehicle;

import java.util.List;

/**
 * Lái xe bình thường — tuân thủ hoàn toàn luật giao thông.
 *
 * <p>
 * Luôn dừng khi đèn đỏ. Tốc độ vừa phải.
 * </p>
 */
public class NormalDriver implements DriverBehavior {

    private static final int SPEED = 4;

    @Override
    public boolean shouldStop(Vehicle self, List<Vehicle> vehicles, TrafficLight relevantLight) {
        return relevantLight.getColor() == LightColor.RED;
    }

    @Override
    public int getSpeed() {
        return SPEED;
    }
}