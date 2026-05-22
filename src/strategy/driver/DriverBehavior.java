package strategy.driver;

import model.vehicle.Vehicle;
import model.trafficlight.TrafficLight;

import java.util.List;

public interface DriverBehavior {

    boolean shouldStop(
            Vehicle self,
            List<Vehicle> vehicles,
            TrafficLight relevantLight
    );

    int getSpeed();
}