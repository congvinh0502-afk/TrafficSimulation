package system.vehicle;

import config.Constants;
import model.network.IntersectionNode;
import model.vehicle.Vehicle;
import system.collision.CollisionSystem;
import system.movement.LaneAlignmentSystem;
import system.movement.LaneChangeSystem;
import system.movement.TurningSystem;
import system.movement.VehicleMovementSystem;
import system.traffic.TrafficRuleSystem;

import java.util.List;

/** Pipeline cập nhật mỗi frame, dùng danh sách IntersectionNode. */
public class VehicleUpdatePipeline {

    private final TurningSystem       turning   = new TurningSystem();
    private final TrafficRuleSystem   traffic   = new TrafficRuleSystem();
    private final CollisionSystem     collision = new CollisionSystem();
    private final LaneChangeSystem    laneChg   = new LaneChangeSystem();
    private final LaneAlignmentSystem align     = new LaneAlignmentSystem();
    private final VehicleMovementSystem move    = new VehicleMovementSystem();

    public void update(Vehicle v, List<Vehicle> all, List<IntersectionNode> intersections) {
        v.setStopped(false);
        v.setAcceleration(Constants.DEFAULT_ACCELERATION);
        if (v.getLaneChangeCooldown() > 0) v.setLaneChangeCooldown(v.getLaneChangeCooldown() - 1);

        turning.updateTurning(v, intersections);
        if (v.isTurning()) {
            collision.maintainDistance(v, all);
            return;
        }

        traffic.checkAllIntersections(v, all, intersections);
        collision.maintainDistance(v, all);

        if (v.isStopped()) return;

        laneChg.updateLaneChanging(v);
        align.alignToLane(v);
        move.move(v);
    }
}
