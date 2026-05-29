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
        if (vehicle.getTurnType() == util.TurnType.RIGHT) {

            boolean blocked =
                !collisionSystem.canEnterIntersection(vehicle, vehicles);

                vehicle.setStopped(blocked);

        return;
        }

        Direction direction = vehicle.getDirection();

        TrafficLight relevantLight = null;
        boolean nearStopLine = false;

        switch (direction) {

            case SOUTH:
                relevantLight = verticalLight;
                nearStopLine = vehicle.getY() + vehicle.getHeight() >= 320
                && vehicle.getY() < 450;
            break;

            case NORTH:
                relevantLight = verticalLight;
                nearStopLine = vehicle.getY() <= 640
                && vehicle.getY() > 500;
            break;

            case EAST:
                relevantLight = horizontalLight;
                nearStopLine = vehicle.getX() + vehicle.getWidth() >= 320
                            && vehicle.getX() < 450;
                break;

            case WEST:
                relevantLight = horizontalLight;
                nearStopLine = vehicle.getX() <= 640
                            && vehicle.getX() > 500;
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

                    // CHỈ check blocked khi xe đang ở vùng nearStopLine
            boolean blocked = nearStopLine && !collisionSystem.canEnterIntersection(vehicle, vehicles);

            if (mustStop || blocked) {
                vehicle.setStopped(true);
            } else {
                vehicle.setStopped(false);
            }
    

    
    }
}