package manager;

import config.Constants;
import model.intersection.IntersectionType;
import model.network.NetworkLayout;
import model.vehicle.*;
import strategy.driver.NormalDriver;
import util.Direction;
import util.Lane;
import util.TurnType;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class VehicleSpawnManager {

    private static final int MIN_DIST = 80;
    private final List<Vehicle> vehicles;
    private final Random rng = new Random();
    private static final int[] NS_IX = {NetworkLayout.TW_X, NetworkLayout.FW_X, NetworkLayout.VW_X};

    public VehicleSpawnManager(List<Vehicle> vehicles) { this.vehicles = vehicles; }

    public void spawnRandomVehicle(List<Direction> dirs) {
        Direction dir = dirs.get(rng.nextInt(dirs.size()));
        double[] pos = spawnPos(dir);
        if (pos == null) return;
        int homeIX = (int) pos[2];
        if (!canSpawn(pos[0], pos[1])) return;

        Vehicle v = mkVehicle(pos[0], pos[1], dir);
        snapLane(v, dir, homeIX);
        v.setLane(Lane.RIGHT);
        v.setHomeIntersectionX(homeIX);
        v.setTurnType(randomTurn(dir, typeFor(homeIX)));
        v.setTurned(false);
        v.setSpeed(v.getBehavior().getSpeed());
        v.setMaxSpeed(v.getBehavior().getSpeed());
        v.setAcceleration(Constants.DEFAULT_ACCELERATION);
        vehicles.add(v);
    }

    public void removeOutsideVehicles() {
        int lim = NetworkLayout.ARM_EXT + 120;
        vehicles.removeIf(v ->
            v.getX() < NetworkLayout.TW_X - lim || v.getX() > NetworkLayout.VW_X + lim ||
            v.getY() < -lim || v.getY() > lim);
    }

    private double[] spawnPos(Direction dir) {
        int ext = NetworkLayout.ARM_EXT;
        switch (dir) {
            case NORTH: { int ix = NS_IX[rng.nextInt(3)]; return new double[]{NetworkLayout.northLaneX(ix), +ext, ix}; }
            case SOUTH: { int ix = NS_IX[rng.nextInt(3)]; return new double[]{NetworkLayout.southLaneX(ix), -ext, ix}; }
            case EAST:  return new double[]{NetworkLayout.TW_X - ext, NetworkLayout.EAST_LANE_Y, NetworkLayout.TW_X};
            case WEST:  return new double[]{NetworkLayout.VW_X + ext, NetworkLayout.WEST_LANE_Y, NetworkLayout.VW_X};
            case NORTHEAST: {
                double s = ext * 0.55;
                return new double[]{NetworkLayout.VW_X + s, NetworkLayout.VW_Y + s, NetworkLayout.VW_X};
            }
            default: return null;
        }
    }

    private void snapLane(Vehicle v, Direction dir, int homeIX) {
        switch (dir) {
            case NORTH: v.setX(NetworkLayout.northLaneX(homeIX)); break;
            case SOUTH: v.setX(NetworkLayout.southLaneX(homeIX)); break;
            case EAST:  v.setY(NetworkLayout.EAST_LANE_Y); break;
            case WEST:  v.setY(NetworkLayout.WEST_LANE_Y); break;
            default: break;
        }
    }

    private Vehicle mkVehicle(double x, double y, Direction dir) {
        int r = rng.nextInt(10);
        if (r < 4) return new Car(x, y, dir);
        if (r < 7) return new Motorbike(x, y, dir);
        if (r < 9) return new Bicycle(x, y, dir);
        return rng.nextBoolean() ? new Ambulance(x, y, dir) : new FireTruck(x, y, dir);
    }

    private TurnType randomTurn(Direction dir, IntersectionType type) {
        if (type == IntersectionType.THREE_WAY && dir == Direction.EAST)
            return rng.nextBoolean() ? TurnType.LEFT : TurnType.RIGHT;
        TurnType[] t = TurnType.values();
        return t[rng.nextInt(t.length)];
    }

    private IntersectionType typeFor(int ix) {
        if (ix == NetworkLayout.TW_X) return IntersectionType.THREE_WAY;
        if (ix == NetworkLayout.VW_X) return IntersectionType.FIVE_WAY;
        return IntersectionType.FOUR_WAY;
    }

    private boolean canSpawn(double sx, double sy) {
        for (Vehicle v : vehicles) {
            double dx = v.getX()-sx, dy = v.getY()-sy;
            if (dx*dx + dy*dy < MIN_DIST*MIN_DIST) return false;
        }
        return true;
    }
}
