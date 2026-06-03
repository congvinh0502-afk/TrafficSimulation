package controller;

import java.util.List;

import model.intersection.IntersectionType;
import model.trafficlight.TrafficLight;
import model.vehicle.Vehicle;

import system.vehicle.VehicleUpdatePipeline;

import util.Direction;

/**
 * TrafficController – điều phối cập nhật xe.
 *
 * FIX: Gọi pipeline.updateAll() thay vì loop pipeline.update() thủ công,
 *      để EmergencyVehicleSystem được chạy một lần trước toàn bộ xe.
 */
public class TrafficController {

    private VehicleUpdatePipeline pipeline;

    public TrafficController() {
        pipeline = new VehicleUpdatePipeline();
    }

    public void updateVehicles(
            List<Vehicle> vehicles,
            TrafficLight verticalLight,
            TrafficLight horizontalLight,
            IntersectionType type
    ) {
        // FIX: dùng updateAll() để EmergencyVehicleSystem chạy trước
        pipeline.updateAll(vehicles, verticalLight, horizontalLight, type);
    }

    public int countVehiclesByDirection(
            List<Vehicle> vehicles,
            Direction d1,
            Direction d2
    ) {

        int count = 0;

        for (Vehicle vehicle : vehicles) {

            if (vehicle.getDirection() == d1
                    || vehicle.getDirection() == d2) {

                count++;
            }
        }

        return count;
    }

    public int countStoppedVehicles(
            List<Vehicle> vehicles
    ) {

        int count = 0;

        for (Vehicle vehicle : vehicles) {

            if (vehicle.isStopped()) {
                count++;
            }
        }

        return count;
    }
}
