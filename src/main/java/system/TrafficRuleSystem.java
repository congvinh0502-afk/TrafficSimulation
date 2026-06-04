package system;

import model.map.IntersectionNode;
import model.traffic.TrafficLight;
import model.vehicle.Vehicle;
import util.Direction;

public class TrafficRuleSystem {
    
    public void update(Vehicle v, IntersectionNode node, TrafficLight light) {
        // Nếu đèn Xanh thì không cần check gì cả
        if (light.getColor() == TrafficLight.Color.GREEN) return;

        // Nếu đèn ĐỎ, kiểm tra xem xe có đang ở gần vạch dừng không
        // Khoảng cách dừng an toàn là 50px trước Node
        double dist = Math.hypot(v.getX() - node.getX(), v.getY() - node.getY());
        
        // Nếu xe nằm trong vùng 50px đến 150px so với tâm ngã tư và đèn ĐỎ
        if (dist > 50 && dist < 150) {
            // Xe phải dừng lại
            v.setSpeed(0);
        }
    }
}