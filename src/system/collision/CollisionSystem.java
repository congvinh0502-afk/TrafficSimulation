package system.collision;

import java.awt.Rectangle;
import java.util.List;

import layout.IntersectionLayout;
import manager.LaneManager;
import model.vehicle.Vehicle;
import system.movement.LaneChangeSystem;

public class CollisionSystem {

    private final LaneChangeSystem laneChangeSystem;

    public CollisionSystem() {
        laneChangeSystem = new LaneChangeSystem();
    }

    public void maintainDistance(Vehicle current, List<Vehicle> vehicles) {
        if (current.isTurning() || current.isChangingLane()) {
            return;
        }

        for (Vehicle other : vehicles) {
            if (current == other)         continue;
            if (other.isTurning())        continue;
            if (current.getDirection() != other.getDirection()) continue;
            if (current.getLane()      != other.getLane())      continue;

            double safeDistance = 100;
            boolean tooClose = false;

            switch (current.getDirection()) {
                case SOUTH:
                    if (Math.abs(current.getX() - other.getX()) < 25 && other.getY() > current.getY() && other.getY() - current.getY() < safeDistance) {
                        tooClose = true;
                    }
                    break;
                case NORTH:
                    if (Math.abs(current.getX() - other.getX()) < 25 && other.getY() < current.getY() && current.getY() - other.getY() < safeDistance) {
                        tooClose = true;
                    }
                    break;
                case EAST:
                    if (Math.abs(current.getY() - other.getY()) < 25 && other.getX() > current.getX() && other.getX() - current.getX() < safeDistance) {
                        tooClose = true;
                    }
                    break;
                case WEST:
                    if (Math.abs(current.getY() - other.getY()) < 25 && other.getX() < current.getX() && current.getX() - other.getX() < safeDistance) {
                        tooClose = true;
                    }
                    break;
                case NORTHEAST:
                    double dx = other.getX() - current.getX();
                    double dy = current.getY() - other.getY();
                    double dist = Math.sqrt(dx * dx + dy * dy);
                    if (dx > 0 && dy > 0 && Math.abs(dx - dy) < 40 && dist < safeDistance) {
                        tooClose = true;
                    }
                    break;
            }

            if (tooClose) {
                // OVERTAKING LOGIC (VƯỢT XE): Thay vì dừng lại, thử chuyển làn trước
                boolean canChange = laneChangeSystem.tryChangeLane(current, vehicles);
                if (!canChange) {
                    current.setStopped(true); 
                }
                return;
            }
        }
    }

    public boolean canEnterIntersection(Vehicle vehicle, List<Vehicle> vehicles) {
        IntersectionLayout layout = LaneManager.getLayout();
        Rectangle checkBounds = layout.getEnterCheckBounds(vehicle.getDirection());

        switch (vehicle.getDirection()) {
            case SOUTH:
                for (Vehicle other : vehicles) {
                    if (other == vehicle || other.isTurning()) continue;
                    if (Math.abs(other.getX() - vehicle.getX()) < 50 && checkBounds.contains((int) other.getX(), (int) other.getY()) && other.getY() > vehicle.getY() && other.getY() - vehicle.getY() < 80) return false;
                }
                break;
            case NORTH:
                for (Vehicle other : vehicles) {
                    if (other == vehicle || other.isTurning()) continue;
                    if (Math.abs(other.getX() - vehicle.getX()) < 50 && checkBounds.contains((int) other.getX(), (int) other.getY()) && other.getY() < vehicle.getY() && vehicle.getY() - other.getY() < 80) return false;
                }
                break;
            case EAST:
                for (Vehicle other : vehicles) {
                    if (other == vehicle || other.isTurning()) continue;
                    if (Math.abs(other.getY() - vehicle.getY()) < 50 && checkBounds.contains((int) other.getX(), (int) other.getY()) && other.getX() > vehicle.getX() && other.getX() - vehicle.getX() < 80) return false;
                }
                break;
            case WEST:
                for (Vehicle other : vehicles) {
                    if (other == vehicle || other.isTurning()) continue;
                    if (Math.abs(other.getY() - vehicle.getY()) < 50 && checkBounds.contains((int) other.getX(), (int) other.getY()) && other.getX() < vehicle.getX() && vehicle.getX() - other.getX() < 80) return false;
                }
                break;
            case NORTHEAST:
                for (Vehicle other : vehicles) {
                    if (other == vehicle || other.isTurning()) continue;
                    if (checkBounds.contains((int) other.getX(), (int) other.getY())) {
                        double dx = other.getX() - vehicle.getX();
                        double dy = vehicle.getY() - other.getY();
                        if (Math.sqrt(dx * dx + dy * dy) < 80) return false;
                    }
                }
                break;
        }
        return true;
    }
}