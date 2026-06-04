package controller;

import model.network.IntersectionNode;
import model.vehicle.Vehicle;
import system.vehicle.VehicleUpdatePipeline;
import util.Direction;

import java.util.List;

/**
 * Điều phối cập nhật toàn bộ phương tiện mỗi frame.
 * Dùng List<IntersectionNode> thay vì IntersectionLayout đơn lẻ.
 */
public class TrafficController {

    private final VehicleUpdatePipeline pipeline;

    public TrafficController() {
        this.pipeline = new VehicleUpdatePipeline();
    }

    public void updateVehicles(List<Vehicle> vehicles, List<IntersectionNode> intersections) {
        for (Vehicle vehicle : vehicles) {
            pipeline.update(vehicle, vehicles, intersections);
        }
    }

    public int countVehiclesByDirection(List<Vehicle> vehicles, Direction d1, Direction d2) {
        int count = 0;
        for (Vehicle v : vehicles) {
            if (v.getDirection() == d1 || v.getDirection() == d2) count++;
        }
        return count;
    }

    public int countStoppedVehicles(List<Vehicle> vehicles) {
        int count = 0;
        for (Vehicle v : vehicles) { if (v.isStopped()) count++; }
        return count;
    }
}
