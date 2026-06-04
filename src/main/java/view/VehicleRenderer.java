package view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.vehicle.Vehicle;

public class VehicleRenderer {
    
    public static void draw(GraphicsContext gc, Vehicle v) {
        gc.save(); // Lưu trạng thái bút vẽ hiện tại
        
        // 1. Dời tâm bản đồ về chính giữa chiếc xe
        gc.translate(v.getX(), v.getY());
        
        // 2. Xoay góc vẽ theo đúng hướng đầu xe đang chĩa tới
        gc.rotate(v.getAngle());
        
        // 3. Vẽ thân xe (Vì đã dời tâm nên vẽ từ -width/2, -height/2)
        gc.setFill(v.getColor());
        gc.fillRoundRect(-v.getWidth() / 2, -v.getHeight() / 2, v.getWidth(), v.getHeight(), 8, 8);
        
        // 4. Vẽ kính lái (Màu xanh lam nhạt) ở phần đầu xe để dễ nhận biết hướng
        gc.setFill(Color.web("#87ceeb"));
        gc.fillRect(v.getWidth() / 4 - 2, -v.getHeight() / 2 + 2, 8, v.getHeight() - 4);
        
        gc.restore(); // Phục hồi trạng thái cũ để không ảnh hưởng đến việc vẽ các xe khác
    }
}