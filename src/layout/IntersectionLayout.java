package layout;

import util.Direction;
import util.Lane;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

/**
 * Cung cấp toàn bộ thông tin tọa độ phụ thuộc loại ngã rẽ.
 *
 * Nguyên tắc thiết kế:
 *  - LaneManager, TrafficRuleSystem, TurningSystem, VehicleMovementSystem
 *    đều INJECT interface này thay vì hardcode số.
 *  - Khi thêm loại ngã rẽ mới, chỉ cần implements IntersectionLayout,
 *    không sửa bất kỳ system nào ở trên.
 */
public interface IntersectionLayout {

    // ─────────────────────────────────────────────────────────────
    // TỌA ĐỘ TÂM
    // ─────────────────────────────────────────────────────────────

    int getCenterX();
    int getCenterY();

    // ─────────────────────────────────────────────────────────────
    // VÙNG BOUNDS
    // ─────────────────────────────────────────────────────────────

    /**
     * Vùng "bên trong" ngã rẽ — dùng cho TrafficRuleSystem:
     * xe đã vào trong này thì không bị chặn bởi đèn đỏ nữa.
     */
    Rectangle getIntersectionBounds();

    /**
     * Vùng kích hoạt turning — dùng cho TurningSystem.
     * Thường nhỏ hơn getIntersectionBounds() để xe vào giữa ngã rẽ
     * mới bắt đầu quẹo, tránh quẹo quá sớm ở vạch dừng.
     */
    Rectangle getTriggerBounds();

    /**
     * Vùng "thoát" sau khi rẽ — dùng cho VehicleMovementSystem.recoverAfterIntersection().
     * Xe ra ngoài bounds này thì reset turning/changingLane.
     * Thường bằng hoặc rộng hơn một chút so với getIntersectionBounds().
     */
    Rectangle getRecoverBounds();

    // ─────────────────────────────────────────────────────────────
    // VẠCH DỪNG (STOP LINE)
    // ─────────────────────────────────────────────────────────────

    /**
     * Tọa độ vạch dừng của hướng đi vào.
     * - NORTH / SOUTH: trả về tọa độ Y
     * - EAST  / WEST : trả về tọa độ X
     * - Hướng chéo  : tùy implementation
     */
    int getStopLineForDirection(Direction direction);

    // ─────────────────────────────────────────────────────────────
    // LÀN ĐƯỜNG
    // ─────────────────────────────────────────────────────────────

    /**
     * Tọa độ X trung tâm của làn đường.
     * Dùng cho hướng NORTH / SOUTH (xe đi dọc, cần biết X để căn làn).
     */
    int getLaneCenterX(Direction direction, Lane lane);

    /**
     * Tọa độ Y trung tâm của làn đường.
     * Dùng cho hướng EAST / WEST (xe đi ngang, cần biết Y để căn làn).
     */
    int getLaneCenterY(Direction direction, Lane lane);

    // ─────────────────────────────────────────────────────────────
    // ĐÈN GIAO THÔNG
    // ─────────────────────────────────────────────────────────────

    /**
     * Danh sách vị trí đặt đèn giao thông trên màn hình.
     * Dùng cho TrafficLightRenderer và click detection trong SimulationPanel.
     * Index 0 = đèn dọc (vertical), index 1 = đèn ngang (horizontal), v.v.
     */
    List<Point> getLightPositions();

    // ─────────────────────────────────────────────────────────────
    // CANENTER CHECK BOUNDS — dùng cho CollisionSystem
    // ─────────────────────────────────────────────────────────────

    /**
     * Vùng buffer kiểm tra "xe có đang ở trong / sắp vào ngã rẽ không"
     * cho canEnterIntersection(). Mỗi hướng có thể trả về bounds khác nhau.
     * Mặc định có thể delegate về getIntersectionBounds() mở rộng thêm margin.
     */
    Rectangle getEnterCheckBounds(Direction direction);

    // ─────────────────────────────────────────────────────────────
    // SPAWN POSITION — dùng cho VehicleSpawnManager
    // ─────────────────────────────────────────────────────────────

    /**
     * Tọa độ spawn của xe đến từ hướng direction.
     * Xe spawn ngoài rìa màn hình, tiến về phía ngã rẽ.
     * VehicleSpawnManager gọi method này thay vì hardcode (450, 1100) v.v.
     */
    java.awt.Point getSpawnPoint(Direction direction);

    /**
     * Khoảng cách giãn cách giữa các xe trong queue spawn (px).
     * Ngã tư/ba dùng 90, ngã năm có thể nhỏ hơn nếu nhánh chéo ngắn.
     */
    default double getQueueSpacing() { return 90.0; }
}
