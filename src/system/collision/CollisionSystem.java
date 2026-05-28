package system.collision;

import java.util.List;
import model.vehicle.Vehicle;
import system.movement.LaneChangeSystem;


public class CollisionSystem {
    
    private LaneChangeSystem laneChangeSystem;

    public CollisionSystem() {
        laneChangeSystem = new LaneChangeSystem();
    }
    

    public void maintainDistance(
            Vehicle current,
            List<Vehicle> vehicles
    ) {

        if (current.isTurning()
                || current.isChangingLane()) {
            return;
        }

        for (Vehicle other : vehicles) {

            if (current == other) {
                continue;
            }

            if (other.isTurning()) {
                continue;
            }

            if (current.getDirection() != other.getDirection()) {
                continue;
            }

            double safeDistance = 120;

            switch (current.getDirection()) {

                case SOUTH:

                    if (Math.abs(current.getX() - other.getX()) < 25
                            && other.getY() > current.getY()
                            && other.getY() - current.getY() < safeDistance) {

                        boolean changedLane = laneChangeSystem.tryChangeLane(current, vehicles);

                        if (!changedLane) {
                            current.setStopped(true);
                        }

                        return;
                    }

                    break;

                case NORTH:

                    if (Math.abs(current.getX() - other.getX()) < 25
                            && other.getY() < current.getY()
                            && current.getY() - other.getY() < safeDistance) {

                        boolean changedLane = laneChangeSystem.tryChangeLane(current, vehicles);

                        if (!changedLane) {
                            current.setStopped(true);
                        }

                        return;
                    }

                    break;

                case EAST:

                    if (Math.abs(current.getY() - other.getY()) < 25
                            && other.getX() > current.getX()
                            && other.getX() - current.getX() < safeDistance) {

                        boolean changedLane = laneChangeSystem.tryChangeLane(current, vehicles);

                        if (!changedLane) {
                            current.setStopped(true);
                        }

                        return;
                    }

                    break;

                case WEST:

                    if (Math.abs(current.getY() - other.getY()) < 25
                            && other.getX() < current.getX()
                            && current.getX() - other.getX() < safeDistance) {

                        boolean changedLane = laneChangeSystem.tryChangeLane(current, vehicles);

                        if (!changedLane) {
                            current.setStopped(true);
                        }

                        return;
                    }

                    break;
            }
        }
    }

   public boolean canEnterIntersection(
            Vehicle vehicle,
            List<Vehicle> vehicles
    ) {

        switch (vehicle.getDirection()) {

            case SOUTH:

                for (Vehicle other : vehicles) {

                    if (other == vehicle) continue;

                    // [FIX M-04] Cũ: other.getY() > 520 — chỉ check phía SAU giao lộ,
                    // bỏ sót xe đang ở GIỮA giao lộ (y=430–520).
                    // Fix: check toàn bộ vùng giao lộ + buffer (430–570).
                    if (Math.abs(other.getX() - vehicle.getX()) < 50
                            && other.getY() > 430
                            && other.getY() < 570
                            && other.getY() - vehicle.getY() < 120) {
                        return false;
                    }
                }

                break;

            case NORTH:

                for (Vehicle other : vehicles) {

                    if (other == vehicle) continue;

                    // [FIX M-04] Cũ: other.getY() < 430 — bỏ sót xe trong giao lộ.
                    // Fix: check vùng 360–520 (giao lộ + buffer phía trên).
                    if (Math.abs(other.getX() - vehicle.getX()) < 50
                            && other.getY() > 360
                            && other.getY() < 520
                            && vehicle.getY() - other.getY() < 120) {
                        return false;
                    }
                }

                break;

            case EAST:

                for (Vehicle other : vehicles) {

                    if (other == vehicle) continue;

                    // [FIX M-04] Cũ: other.getX() > 520 — bỏ sót xe trong giao lộ.
                    // Fix: check vùng 430–570.
                    if (Math.abs(other.getY() - vehicle.getY()) < 50
                            && other.getX() > 430
                            && other.getX() < 570
                            && other.getX() - vehicle.getX() < 120) {
                        return false;
                    }
                }

                break;

            case WEST:

                for (Vehicle other : vehicles) {

                    if (other == vehicle) continue;

                    // [FIX M-04] Cũ: other.getX() < 430 — bỏ sót xe trong giao lộ.
                    // Fix: check vùng 360–520.
                    if (Math.abs(other.getY() - vehicle.getY()) < 50
                            && other.getX() > 360
                            && other.getX() < 520
                            && vehicle.getX() - other.getX() < 120) {
                        return false;
                    }
                }

                break;
        }

        return true;
    }
}
