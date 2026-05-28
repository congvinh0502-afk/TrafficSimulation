package system.traffic;

import java.util.List;
import model.trafficlight.LightColor;
import model.trafficlight.TrafficLight;
import model.vehicle.Vehicle;
import util.Direction;
import system.collision.CollisionSystem;


public class TrafficRuleSystem {
    private CollisionSystem collisionSystem;
    public TrafficRuleSystem() {
    collisionSystem = new CollisionSystem();
}
    public void checkTrafficLight(
            Vehicle vehicle,
            TrafficLight verticalLight,
            TrafficLight horizontalLight,
            List<Vehicle> vehicles
    ) {
        if (vehicle.isTurning()) {
            return;
        }

        Direction direction = vehicle.getDirection();

        TrafficLight relevantLight = null;
        boolean nearStopLine = false;

        switch (direction) {

            case SOUTH:
                relevantLight = verticalLight;
                nearStopLine = vehicle.getY() + 60 >= 360;
                break;

            case NORTH:
                relevantLight = verticalLight;
                nearStopLine = vehicle.getY() <= 640;
                break;

            case EAST:
                relevantLight = horizontalLight;
                nearStopLine = vehicle.getX() + 60 >= 360;
                break;

            case WEST:
                relevantLight = horizontalLight;
                nearStopLine = vehicle.getX() <= 640;
                break;
        }

        boolean mustStop = false;

        if (nearStopLine && relevantLight != null) {

            if (vehicle.getBehavior() != null) {
                mustStop = vehicle.getBehavior().shouldStop(
                        vehicle,
                        vehicles,
                        relevantLight
                );
            } else {
                mustStop = relevantLight.getColor() == LightColor.RED;
            }
        }

        boolean blocked =
        !collisionSystem.canEnterIntersection(vehicle, vehicles);

        if (mustStop || blocked) {
            vehicle.setStopped(true);
        }
    

    
    }
}