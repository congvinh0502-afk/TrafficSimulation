package controller;

import model.network.IntersectionNode;
import model.vehicle.Vehicle;
import system.vehicle.VehicleUpdatePipeline;
import util.Direction;

import java.util.List;

/** Điều phối cập nhật phương tiện — hỗ trợ mạng lưới nhiều giao lộ. */
public class TrafficController {

    private final VehicleUpdatePipeline pipeline = new VehicleUpdatePipeline();

    public void updateVehicles(List<Vehicle> vehicles, List<IntersectionNode> intersections) {
        for (Vehicle v : vehicles) pipeline.update(v, vehicles, intersections);
    }

    public int countStopped(List<Vehicle> vehicles) {
        int n = 0; for (Vehicle v : vehicles) if (v.isStopped()) n++; return n;
    }

    public int countByDirection(List<Vehicle> vehicles, Direction d1, Direction d2) {
        int n = 0;
        for (Vehicle v : vehicles) if (v.getDirection()==d1 || v.getDirection()==d2) n++;
        return n;
    }
}
