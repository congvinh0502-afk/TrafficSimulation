package system.vehicle;

import config.Constants;
import model.intersection.IntersectionType;
import model.trafficlight.TrafficLight;
import model.vehicle.Vehicle;
import system.collision.CollisionSystem;
import system.movement.LaneAlignmentSystem;
import system.movement.LaneChangeSystem;
import system.movement.TurningSystem;
import system.movement.VehicleMovementSystem;
import system.traffic.TrafficRuleSystem;

import java.util.List;

/**
 * Pipeline cập nhật trạng thái xe mỗi frame.
 *
 * <p>Thứ tự xử lý (quan trọng — thay đổi sẽ gây lỗi):
 * <ol>
 *   <li>Reset stopped = false, đặt gia tốc về mặc định.</li>
 *   <li>Giảm cooldown đổi làn.</li>
 *   <li>Xử lý rẽ (nếu đang rẽ → kiểm tra khoảng cách rồi thoát sớm).</li>
 *   <li>Kiểm tra đèn → có thể đặt gia tốc âm.</li>
 *   <li>Kiểm tra xe phía trước → có thể đặt gia tốc âm / dừng.</li>
 *   <li>Nếu stopped → bỏ qua bước còn lại.</li>
 *   <li>Cập nhật đổi làn.</li>
 *   <li>Căn giữa làn (bao gồm post-turn alignment).</li>
 *   <li>Di chuyển (áp dụng gia tốc + vectơ).</li>
 * </ol>
 * </p>
 */
public class VehicleUpdatePipeline {

    private final TurningSystem       turningSystem;
    private final TrafficRuleSystem   trafficRuleSystem;
    private final CollisionSystem     collisionSystem;
    private final LaneChangeSystem    laneChangeSystem;
    private final LaneAlignmentSystem laneAlignmentSystem;
    private final VehicleMovementSystem movementSystem;

    public VehicleUpdatePipeline() {
        turningSystem       = new TurningSystem();
        trafficRuleSystem   = new TrafficRuleSystem();
        collisionSystem     = new CollisionSystem();
        laneChangeSystem    = new LaneChangeSystem();
        laneAlignmentSystem = new LaneAlignmentSystem();
        movementSystem      = new VehicleMovementSystem();
    }

    public void update(Vehicle vehicle,
                       List<Vehicle> vehicles,
                       TrafficLight verticalLight,
                       TrafficLight horizontalLight,
                       IntersectionType type) {

        // 1. Reset trạng thái mỗi frame
        vehicle.setStopped(false);
        vehicle.setAcceleration(Constants.DEFAULT_ACCELERATION);

        // 2. Giảm cooldown đổi làn
        if (vehicle.getLaneChangeCooldown() > 0) {
            vehicle.setLaneChangeCooldown(vehicle.getLaneChangeCooldown() - 1);
        }

        // 3. Xử lý rẽ
        turningSystem.updateTurning(vehicle, type);
        if (vehicle.isTurning()) {
            collisionSystem.maintainDistance(vehicle, vehicles);
            return;
        }

        // 4. Kiểm tra đèn giao thông → gia tốc âm khi gần đèn đỏ
        trafficRuleSystem.checkTrafficLight(vehicle, verticalLight, horizontalLight, vehicles);

        // 5. Kiểm tra xe phía trước → gia tốc âm / dừng
        collisionSystem.maintainDistance(vehicle, vehicles);

        // 6. Nếu bị dừng thì không tiếp tục
        if (vehicle.isStopped()) return;

        // 7. Đổi làn
        laneChangeSystem.updateLaneChanging(vehicle);

        // 8. Căn giữa làn (kể cả post-turn alignment)
        laneAlignmentSystem.alignToLane(vehicle);

        // 9. Di chuyển (gia tốc + vectơ)
        movementSystem.move(vehicle);
    }
}
