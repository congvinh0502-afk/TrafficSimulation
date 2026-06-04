package system;

import java.util.List;
import model.vehicle.Vehicle;

public class MovementSystem {
    public void updatePositions(List<Vehicle> vehicles) {
        // Thuật toán lượng giác: Tịnh tiến tọa độ (x, y) của xe theo hướng (Angle)
        for (Vehicle v : vehicles) {
            double angleRad = Math.toRadians(v.getAngle());
            v.setX(v.getX() + v.getSpeed() * Math.cos(angleRad));
            v.setY(v.getY() + v.getSpeed() * Math.sin(angleRad));
        }
    }
}