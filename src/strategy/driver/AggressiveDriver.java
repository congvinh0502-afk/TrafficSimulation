package strategy.driver;

import model.trafficlight.LightColor;
import model.trafficlight.TrafficLight;
import model.vehicle.Vehicle;

import java.util.List;

public class AggressiveDriver
        implements DriverBehavior {

    @Override
    public boolean shouldStop(
            Vehicle self,
            List<Vehicle> vehicles,
            TrafficLight relevantLight
    ) {

        // chỉ dừng nếu rất gần giao lộ

        if (relevantLight.getColor()
                == LightColor.RED) {

            return Math.random() > 0.4;
        }

        return false;
    }

    @Override
    public int getSpeed() {

        return 7;
    }
}