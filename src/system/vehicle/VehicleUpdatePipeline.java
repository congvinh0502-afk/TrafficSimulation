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

        // cooldown đổi lane
        if (vehicle.getLaneChangeCooldown() > 0) {
            vehicle.setLaneChangeCooldown(
                    vehicle.getLaneChangeCooldown() - 1
            );
        }

        // xử lý rẽ TRƯỚC – không reset stopped nếu đang turning
        turningSystem.updateTurning(vehicle, type);

        if (vehicle.isTurning()) {
            collisionSystem.maintainDistance(vehicle, vehicles);
            return;
        }

        // reset stopped CHỈ khi không đang rẽ
        vehicle.setStopped(false);

        // kiểm tra đèn giao thông
        trafficRuleSystem.checkTrafficLight(
                vehicle,
                verticalLight,
                horizontalLight,
                vehicles
        );

        // xử lý khoảng cách xe
        collisionSystem.maintainDistance(
                vehicle,
                vehicles
        );

        // nếu đang stop thì dừng update movement
        if (vehicle.isStopped()) {
            return;
        }

        // đổi lane
        laneChangeSystem.updateLaneChanging(vehicle);

        // căn giữa lane
        laneAlignmentSystem.alignToLane(vehicle);

        // movement chính
        movementSystem.move(vehicle);
    }
}