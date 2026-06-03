package controller;

import model.intersection.IntersectionLayout;
import model.trafficlight.TrafficLight;
import model.vehicle.Vehicle;
import system.vehicle.VehicleUpdatePipeline;
import util.Direction;

import java.util.List;

/**
 * Điều phối cập nhật toàn bộ phương tiện mỗi frame.
 * Nhận IntersectionLayout thay vì IntersectionType.
 */
public class TrafficController {

    private final VehicleUpdatePipeline pipeline;

    public TrafficController() {
        this.pipeline = new VehicleUpdatePipeline();
    }

    public void updateVehicles(List<Vehicle> vehicles,
                               TrafficLight verticalLight,
                               TrafficLight horizontalLight,
                               IntersectionLayout layout) {
        for (Vehicle vehicle : vehicles) {
            pipeline.update(vehicle, vehicles, verticalLight, horizontalLight, layout);
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