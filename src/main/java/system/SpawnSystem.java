package system;

import config.Constants;
import java.util.List;
import java.util.Random;
import model.map.CityMap;
import model.map.RoadEdge;
import model.vehicle.Car;
import model.vehicle.Vehicle;

public class SpawnSystem {
    private Random random = new Random();
    
    public void spawnRandom(List<Vehicle> vehicles, CityMap cityMap) {
        // Tỷ lệ sinh xe ngẫu nhiên (Khoảng 20 frame sẽ cố gắng sinh 1 xe)
        if (random.nextInt(20) != 0) return;
        if (cityMap.getRoads().isEmpty()) return;
        
        // Chọn ngẫu nhiên 1 con đường để thả xe vào
        RoadEdge road = cityMap.getRoads().get(random.nextInt(cityMap.getRoads().size()));
        
        double sx = road.getStartNode().getX();
        double sy = road.getStartNode().getY();
        double ex = road.getEndNode().getX();
        double ey = road.getEndNode().getY();
        
        // Tính góc của con đường
        double angleRad = Math.atan2(ey - sy, ex - sx);
        
        // Tịnh tiến điểm sinh xe sang phải một chút (Để xe chạy đúng Làn Phải sát vỉa hè)
        // Đường rộng 120 (Mỗi nửa là 60). Làn sát lề cỏ là 1.5 lần chiều rộng làn (1.5 * 30 = 45px)
        double offset = Constants.LANE_WIDTH * 1.5; 
        double spawnX = sx + Math.cos(angleRad + Math.PI/2) * offset;
        double spawnY = sy + Math.sin(angleRad + Math.PI/2) * offset;
        
        // Sinh một chiếc xe đỏ tại vị trí đó
        vehicles.add(new Car(spawnX, spawnY, Math.toDegrees(angleRad)));
    }
}