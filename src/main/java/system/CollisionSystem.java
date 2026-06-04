package system;

import java.util.List;
import model.vehicle.Vehicle;

public class CollisionSystem {
    private static final double SAFE_DISTANCE = 50; // Khoảng cách bắt đầu giảm tốc

    public void update(List<Vehicle> vehicles) {
        for (Vehicle current : vehicles) {
            Vehicle blocker = null;
            double minDist = 120; // Tầm nhìn Radar 120 pixel phía trước

            for (Vehicle other : vehicles) {
                if (current == other) continue;

                // Chỉ check các xe chạy cùng hướng (Góc lệch nhau < 10 độ)
                if (Math.abs(current.getAngle() - other.getAngle()) < 10) {
                    
                    double dx = other.getX() - current.getX();
                    double dy = other.getY() - current.getY();

                    // Lấy Vector hướng đi của xe hiện tại
                    double rad = Math.toRadians(current.getAngle());
                    double dirX = Math.cos(rad);
                    double dirY = Math.sin(rad);

                    // Toán học Vector: 
                    // axialDist = Khoảng cách dọc theo đường đi
                    // lateralDist = Khoảng cách lệch ngang sang hai bên
                    double axialDist = dx * dirX + dy * dirY;
                    double lateralDist = Math.abs(dx * (-dirY) + dy * dirX);

                    // Nếu xe kia ở PHÍA TRƯỚC (axial > 0) và CÙNG LÀN ĐƯỜNG (lateral < 20)
                    if (axialDist > 0 && axialDist < minDist && lateralDist < 20) {
                        minDist = axialDist;
                        blocker = other;
                    }
                }
            }

            // ĐIỀU CHỈNH CHÂN GA / CHÂN PHANH
            if (blocker != null) {
                // Khoảng cách thực tế giữa 2 cản xe
                double gap = minDist - (current.getWidth() / 2 + blocker.getWidth() / 2);
                
                if (gap < 8) {
                    current.setSpeed(0); // Khoảng cách < 8px -> Đạp phanh cứng
                } else if (gap < SAFE_DISTANCE) {
                    // Đang tiến lại gần -> Rà phanh để đi cùng vận tốc với xe trước
                    current.setSpeed(Math.min(current.getSpeed(), blocker.getSpeed()));
                }
            } else {
                // Đường thoáng -> Từ từ đạp ga lên tốc độ tối đa (3.0)
                if (current.getSpeed() < 3.0) {
                    current.setSpeed(Math.min(3.0, current.getSpeed() + 0.15));
                }
            }
        }
    }
}