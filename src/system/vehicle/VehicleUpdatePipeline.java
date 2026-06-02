package system.vehicle;

import java.util.List;

import model.intersection.IntersectionType;
import model.trafficlight.TrafficLight;
import model.vehicle.Vehicle;

import system.collision.CollisionSystem;
import system.movement.LaneAlignmentSystem;
import system.movement.LaneChangeSystem;
import system.movement.TurningSystem;
import system.movement.VehicleMovementSystem;
import system.traffic.TrafficRuleSystem;

public class VehicleUpdatePipeline {

    private TurningSystem turningSystem;
    private TrafficRuleSystem trafficRuleSystem;
    private CollisionSystem collisionSystem;
    private LaneChangeSystem laneChangeSystem;
    private LaneAlignmentSystem laneAlignmentSystem;
    private VehicleMovementSystem movementSystem;

    public VehicleUpdatePipeline() {

        turningSystem = new TurningSystem();

        trafficRuleSystem = new TrafficRuleSystem();

        collisionSystem = new CollisionSystem();

        laneChangeSystem = new LaneChangeSystem();

        laneAlignmentSystem = new LaneAlignmentSystem();

        movementSystem = new VehicleMovementSystem();
    }

    public void update(
            Vehicle vehicle,
            List<Vehicle> vehicles,
            TrafficLight verticalLight,
            TrafficLight horizontalLight,
            IntersectionType type
    ) {

                    // cooldown Ä‘á»•i lane
            if (vehicle.getLaneChangeCooldown() > 0) {
                vehicle.setLaneChangeCooldown(
                        vehicle.getLaneChangeCooldown() - 1
                );
            }

            // xá»­ lÃ½ ráº½ TRÆ¯á»šC â€“ khÃ´ng reset stopped náº¿u Ä‘ang turning
            turningSystem.updateTurning(vehicle, type);

            if (vehicle.isTurning()) {
                collisionSystem.maintainDistance(vehicle, vehicles);
                return;
            }

            // reset stopped CHá»ˆ khi khÃ´ng Ä‘ang ráº½
            vehicle.setStopped(false);

        // kiá»ƒm tra Ä‘Ã¨n giao thÃ´ng
        trafficRuleSystem.checkTrafficLight(
                vehicle,
                verticalLight,
                horizontalLight,
                vehicles
        );

        // xá»­ lÃ½ khoáº£ng cÃ¡ch xe
        collisionSystem.maintainDistance(
                vehicle,
                vehicles
        );

        // náº¿u Ä‘ang stop thÃ¬ dá»«ng update movement
        if (vehicle.isStopped()) {
            return;
        }

        // Ä‘á»•i lane
        laneChangeSystem.updateLaneChanging(vehicle);

        // cÄƒn giá»¯a lane
        laneAlignmentSystem.alignToLane(vehicle);

        // movement chÃ­nh
        movementSystem.move(vehicle);
    }
}