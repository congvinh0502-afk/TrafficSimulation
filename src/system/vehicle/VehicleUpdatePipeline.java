package system.vehicle;

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
 * <p>
 * Thứ tự xử lý được thiết kế cẩn thận để đảm bảo
 * tính nhất quán — thay đổi thứ tự có thể gây lỗi:
 * <ol>
 * <li>Reset {@code stopped = false} (điểm khởi đầu mỗi frame).</li>
 * <li>Giảm cooldown đổi làn.</li>
 * <li>Xử lý rẽ — nếu đang rẽ thì kiểm tra khoảng cách rồi thoát sớm.</li>
 * <li>Kiểm tra đèn giao thông → có thể set {@code stopped = true}.</li>
 * <li>Kiểm tra khoảng cách xe phía trước → có thể set
 * {@code stopped = true}.</li>
 * <li>Nếu bị dừng → bỏ qua bước còn lại.</li>
 * <li>Cập nhật đổi làn và căn giữa làn.</li>
 * <li>Di chuyển xe.</li>
 * </ol>
 * </p>
 */
public class VehicleUpdatePipeline {

    private final TurningSystem turningSystem;
    private final TrafficRuleSystem trafficRuleSystem;
    private final CollisionSystem collisionSystem;
    private final LaneChangeSystem laneChangeSystem;
    private final LaneAlignmentSystem laneAlignmentSystem;
    private final VehicleMovementSystem movementSystem;

    public VehicleUpdatePipeline() {
        turningSystem = new TurningSystem();
        trafficRuleSystem = new TrafficRuleSystem();
        collisionSystem = new CollisionSystem();
        laneChangeSystem = new LaneChangeSystem();
        laneAlignmentSystem = new LaneAlignmentSystem();
        movementSystem = new VehicleMovementSystem();
    }

    /**
     * Chạy toàn bộ pipeline cho một xe trong một frame.
     *
     * @param vehicle         xe cần cập nhật
     * @param vehicles        toàn bộ xe (dùng cho kiểm tra va chạm)
     * @param verticalLight   đèn hướng dọc
     * @param horizontalLight đèn hướng ngang
     * @param type            loại ngã rẽ hiện tại
     */
    public void update(Vehicle vehicle,
            List<Vehicle> vehicles,
            TrafficLight verticalLight,
            TrafficLight horizontalLight,
            IntersectionType type) {

        // 1. Reset trạng thái dừng — mỗi frame quyết định lại
        vehicle.setStopped(false);

        // 2. Giảm cooldown đổi làn
        if (vehicle.getLaneChangeCooldown() > 0) {
            vehicle.setLaneChangeCooldown(vehicle.getLaneChangeCooldown() - 1);
        }

        // 3. Xử lý rẽ
        turningSystem.updateTurning(vehicle, type);
        if (vehicle.isTurning()) {
            // Vẫn giữ khoảng cách cơ bản khi đang rẽ để tránh đâm xe khác
            collisionSystem.maintainDistance(vehicle, vehicles);
            return;
        }

        // 4. Kiểm tra đèn giao thông
        trafficRuleSystem.checkTrafficLight(vehicle, verticalLight, horizontalLight, vehicles);

        // 5. Kiểm tra khoảng cách xe phía trước
        collisionSystem.maintainDistance(vehicle, vehicles);

        // 6. Nếu bị dừng thì không cần xử lý thêm
        if (vehicle.isStopped())
            return;

        // 7. Cập nhật đổi làn
        laneChangeSystem.updateLaneChanging(vehicle);

        // 8. Căn giữa làn
        laneAlignmentSystem.alignToLane(vehicle);

        // 9. Di chuyển chính
        movementSystem.move(vehicle);
    }
}