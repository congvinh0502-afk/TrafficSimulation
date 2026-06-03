package system.vehicle;

import java.util.List;
import model.intersection.IntersectionType;
import model.trafficlight.TrafficLight;
import model.vehicle.Vehicle;
import system.collision.CollisionSystem;
import system.emergency.EmergencyVehicleSystem;
import system.movement.LaneAlignmentSystem;
import system.movement.LaneChangeSystem;
import system.movement.TurningSystem;
import system.movement.VehicleMovementSystem;
import system.traffic.TrafficRuleSystem;

/**
 * VehicleUpdatePipeline – pipeline cập nhật trạng thái xe mỗi frame.
 *
 * FIX: Thêm EmergencyVehicleSystem vào đầu pipeline để xe thường
 *      nhường đường xe cứu thương / cứu hỏa trước khi xử lý movement.
 */
public class VehicleUpdatePipeline {

    private TurningSystem          turningSystem;
    private TrafficRuleSystem      trafficRuleSystem;
    private CollisionSystem        collisionSystem;
    private LaneChangeSystem       laneChangeSystem;
    private LaneAlignmentSystem    laneAlignmentSystem;
    private VehicleMovementSystem  movementSystem;
    private EmergencyVehicleSystem emergencySystem;   // FIX: thêm mới

    public VehicleUpdatePipeline() {

        turningSystem       = new TurningSystem();
        trafficRuleSystem   = new TrafficRuleSystem();
        collisionSystem     = new CollisionSystem();
        laneChangeSystem    = new LaneChangeSystem();
        laneAlignmentSystem = new LaneAlignmentSystem();
        movementSystem      = new VehicleMovementSystem();
        emergencySystem     = new EmergencyVehicleSystem();  // FIX
    }

    /**
     * Gọi một lần mỗi frame cho TOÀN BỘ danh sách xe (xử lý emergency trước).
     * Sau đó gọi update() cho từng xe.
     *
     * QUAN TRỌNG: TrafficController phải gọi updateAll() thay vì lặp update() trực tiếp,
     * hoặc gọi emergencySystem.updateEmergencyVehicles(vehicles) ở TrafficController
     * trước vòng lặp for.
     */
    public void updateAll(
            List<Vehicle> vehicles,
            TrafficLight verticalLight,
            TrafficLight horizontalLight,
            IntersectionType type
    ) {
        // FIX: xử lý xe khẩn cấp ưu tiên đầu tiên cho toàn bộ danh sách
        emergencySystem.updateEmergencyVehicles(vehicles);

        for (Vehicle vehicle : vehicles) {
            update(vehicle, vehicles, verticalLight, horizontalLight, type);
        }
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
            // --- THÊM BLOCK NÀY ĐỂ XE TIẾN LÊN THEO ĐƯỜNG CONG ---
            if (!vehicle.isStopped()) {
                vehicle.move(); 
            }
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

        // xử lý khoảng cách xe (bao gồm logic vượt xe và nhường xe khẩn cấp)
        collisionSystem.maintainDistance(vehicle, vehicles);

        // nếu đang stop thì dừng update movement
        if (vehicle.isStopped()) {
            return;
        }

        // đổi lane (smooth animation)
        laneChangeSystem.updateLaneChanging(vehicle);

        // căn giữa lane
        laneAlignmentSystem.alignToLane(vehicle);

        // movement chính
        movementSystem.move(vehicle);
    }
}
