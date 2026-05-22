package strategy.driver;

import model.trafficlight.TrafficLight;
import model.vehicle.Vehicle;

import java.util.List;

public class EmergencyDriver
        implements DriverBehavior {

    @Override
    public boolean shouldStop(
            Vehicle self,
            List<Vehicle> vehicles,
            TrafficLight relevantLight
    ) {

        return false;
    }

    @Override
    public int getSpeed() {

        return 9;
    }
}