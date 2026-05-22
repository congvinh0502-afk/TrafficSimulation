package strategy.driver;

import model.trafficlight.LightColor;
import model.trafficlight.TrafficLight;
import model.vehicle.Vehicle;

import java.util.List;

public class NormalDriver implements DriverBehavior {

    @Override
    public boolean shouldStop(
            Vehicle self,
            List<Vehicle> vehicles,
            TrafficLight relevantLight
    ) {

        return relevantLight.getColor()
                == LightColor.RED;
    }

    @Override
    public int getSpeed() {

        return 4;
    }
}