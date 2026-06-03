package system.vehicle;

import java.util.List;

import config.Constants;
import manager.SoundManager;
import model.intersection.IntersectionLayout;
import model.trafficlight.TrafficLight;
import model.vehicle.Vehicle;
import system.collision.CollisionSystem;
import system.movement.LaneAlignmentSystem;
import system.movement.LaneChangeSystem;
import system.movement.TurningSystem;
import system.movement.VehicleMovementSystem;
import system.traffic.TrafficRuleSystem;

/**
 * Pipeline cập nhật trạng thái xe mỗi frame.
 * Nhận IntersectionLayout thay vì IntersectionType.
 */
public class VehicleUpdatePipeline {

    private final TurningSystem         turningSystem;
    private final TrafficRuleSystem     trafficRuleSystem;
    private final CollisionSystem       collisionSystem;
    private final LaneChangeSystem      laneChangeSystem;
    private final LaneAlignmentSystem   laneAlignmentSystem;
    private final VehicleMovementSystem movementSystem;
    private final SoundManager          soundManager;

    public VehicleUpdatePipeline() {
        turningSystem       = new TurningSystem();
        trafficRuleSystem   = new TrafficRuleSystem();
        collisionSystem     = new CollisionSystem();
        laneChangeSystem    = new LaneChangeSystem();
        laneAlignmentSystem = new LaneAlignmentSystem();
        movementSystem      = new VehicleMovementSystem();
        soundManager        = SoundManager.getInstance();
    }

    public void update(Vehicle vehicle,
                       List<Vehicle> vehicles,
                       TrafficLight verticalLight,
                       TrafficLight horizontalLight,
                       IntersectionLayout layout) {

        // 1. Reset
        vehicle.setStopped(false);
        vehicle.setAcceleration(Constants.DEFAULT_ACCELERATION);

        // 2. Cooldown đổi làn
        if (vehicle.getLaneChangeCooldown() > 0)
            vehicle.setLaneChangeCooldown(vehicle.getLaneChangeCooldown() - 1);

        // 3. Rẽ
        turningSystem.updateTurning(vehicle, layout);
        if (vehicle.isTurning()) {
            collisionSystem.maintainDistance(vehicle, vehicles);
            soundManager.updateVehicleSound(vehicle);
            return;
        }

        // 4. Đèn
        trafficRuleSystem.checkTrafficLight(vehicle, verticalLight, horizontalLight, vehicles, layout);

        // 5. Giữ khoảng cách
        collisionSystem.maintainDistance(vehicle, vehicles);

        if (vehicle.isStopped()) {
            soundManager.updateVehicleSound(vehicle);
            return;
        }

        // 6. Đổi làn
        laneChangeSystem.updateLaneChanging(vehicle, layout);

        // 7. Căn làn
        laneAlignmentSystem.alignToLane(vehicle, layout);

        // 8. Di chuyển
        movementSystem.move(vehicle, layout);

        // 9. Âm thanh
        soundManager.updateVehicleSound(vehicle);
    }
}