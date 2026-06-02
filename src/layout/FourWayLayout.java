package layout;

import util.Direction;
import util.Lane;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;

/**
 * Layout ngã tư tiêu chuẩn.
 *
 * Toàn bộ các số magic từ LaneManager, TrafficRuleSystem,
 * TurningSystem, VehicleMovementSystem, CollisionSystem
 * được tập trung TẠI ĐÂY. Không còn hardcode ở những chỗ khác.
 *
 * Tọa độ gốc (giữ nguyên, không thay đổi hành vi):
 *   - Trục dọc (NORTH/SOUTH): x = 300–500, rộng 200px
 *   - Trục ngang (EAST/WEST) : y = 300–500, rộng 200px
 *   - Tâm ngã tư             : (400, 400)
 *
 * Mapping vạch dừng (stop line pixel, TRƯỚC khi vào ngã tư):
 *   SOUTH  → y = 320 (xe đi xuống, dừng trước vạch trên)
 *   NORTH  → y = 640 (xe đi lên,   dừng trước vạch dưới)
 *   EAST   → x = 320 (xe đi sang phải, dừng trước vạch trái)
 *   WEST   → x = 640 (xe đi sang trái, dừng trước vạch phải)
 */
public class FourWayLayout implements IntersectionLayout {

    // ─────────────────────────────────────────────────────────────
    // TÂM
    // ─────────────────────────────────────────────────────────────

    @Override
    public int getCenterX() { return 400; }

    @Override
    public int getCenterY() { return 400; }

    // ─────────────────────────────────────────────────────────────
    // BOUNDS
    // ─────────────────────────────────────────────────────────────

    /**
     * Bounds dùng cho đèn đỏ: xe đã vào trong này thì không cần dừng thêm.
     * Nguồn gốc: TrafficRuleSystem.isInsideIntersection() x > 360 && x < 640 ...
     */
    @Override
    public Rectangle getIntersectionBounds() {
        return new Rectangle(360, 360, 280, 280); // (360,360) → (640,640)
    }

    /**
     * Bounds kích hoạt turning: nhỏ hơn intersection bounds để xe vào giữa mới quẹo.
     * Nguồn gốc: TurningSystem.handleTurning() x+hw > 420 && x-hw < 530 ...
     */
    @Override
    public Rectangle getTriggerBounds() {
        return new Rectangle(420, 420, 110, 110); // (420,420) → (530,530)
    }

    /**
     * Bounds recover sau khi rẽ xong.
     * Nguồn gốc: VehicleMovementSystem.recoverAfterIntersection() x < 360 || x > 590 ...
     */
    @Override
    public Rectangle getRecoverBounds() {
        return new Rectangle(360, 360, 230, 230); // (360,360) → (590,590)
    }

    // ─────────────────────────────────────────────────────────────
    // VẠCH DỪNG
    // ─────────────────────────────────────────────────────────────

    /**
     * Trả về tọa độ vạch dừng cho từng hướng.
     * Xe gần vạch này (và chưa qua) thì mới bị kiểm tra đèn.
     *
     * nearStopLine logic (nguồn: TrafficRuleSystem.checkTrafficLight):
     *   SOUTH : y+h >= 320 && y < 450
     *   NORTH : y   <= 640 && y > 500
     *   EAST  : x+w >= 320 && x < 450
     *   WEST  : x   <= 640 && x > 500
     */
    @Override
    public int getStopLineForDirection(Direction direction) {
        switch (direction) {
            case SOUTH:     return 320;  // vạch trên của ngã tư (xe đi xuống)
            case NORTH:     return 640;  // vạch dưới (xe đi lên)
            case EAST:      return 320;  // vạch trái (xe đi sang phải)
            case WEST:      return 640;  // vạch phải (xe đi sang trái)
            default:        return 400;  // fallback tâm
        }
    }

    // ─────────────────────────────────────────────────────────────
    // LÀN ĐƯỜNG
    // ─────────────────────────────────────────────────────────────

    /**
     * Tọa độ X trung tâm làn — dùng cho hướng NORTH/SOUTH.
     * Nguồn gốc: LaneManager.getLaneCenterX()
     */
    @Override
    public int getLaneCenterX(Direction direction, Lane lane) {
        switch (direction) {
            case NORTH:
                return lane == Lane.RIGHT ? 430 : 470;
            case SOUTH:
                return lane == Lane.RIGHT ? 370 : 330;
            case NORTHEAST:
                return lane == Lane.LEFT  ? 360 : 420;
            default:
                return getCenterX();
        }
    }

    /**
     * Tọa độ Y trung tâm làn — dùng cho hướng EAST/WEST.
     * Nguồn gốc: LaneManager.getLaneCenterY()
     */
    @Override
    public int getLaneCenterY(Direction direction, Lane lane) {
        switch (direction) {
            case EAST:
                // đi từ trái sang phải — làn phải = phía dưới
                return lane == Lane.RIGHT ? 430 : 470;
            case WEST:
                // đi từ phải sang trái — làn phải = phía trên
                return lane == Lane.RIGHT ? 370 : 330;
            case NORTHEAST:
                return lane == Lane.LEFT  ? 640 : 580;
            default:
                return getCenterY();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // ĐÈN GIAO THÔNG
    // ─────────────────────────────────────────────────────────────

    /**
     * Vị trí đặt đèn.
     * Index 0 = đèn dọc (vertical)  → (520, 250)
     * Index 1 = đèn ngang (horizontal) → (250, 520)
     * Nguồn gốc: SimulationPanel.drawTrafficLights()
     */
    @Override
    public List<Point> getLightPositions() {
        return Arrays.asList(
            new Point(520, 250),   // đèn dọc (vertical)
            new Point(250, 520)    // đèn ngang (horizontal)
        );
    }

    // ─────────────────────────────────────────────────────────────
    // ENTER CHECK BOUNDS
    // ─────────────────────────────────────────────────────────────

    /**
     * Bounds kiểm tra xe sắp vào / đang trong ngã rẽ cho CollisionSystem.
     * Nguồn gốc: CollisionSystem.canEnterIntersection() các case SOUTH/NORTH/EAST/WEST.
     *
     * Mỗi hướng mở rộng thêm margin 30px so với intersection bounds để bắt
     * xe đang ở rìa ngã tư (phòng tránh bỏ sót).
     */
    @Override
    public Rectangle getEnterCheckBounds(Direction direction) {
        // (430,430) → (570,570) — buffer đủ rộng theo comment FIX M-04
        return new Rectangle(430, 430, 140, 140);
    }

    // ─────────────────────────────────────────────────────────────
    // SPAWN POINT
    // ─────────────────────────────────────────────────────────────

    /**
     * Spawn ngoài rìa màn hình (1200×800), tiến về tâm (400,400).
     * Tọa độ X/Y theo chiều đi của xe, chiều kia snap bởi setupLane().
     *   SOUTH : từ trên xuống  → y = -100 (trung tâm lane SOUTH ≈ 350)
     *   NORTH : từ dưới lên    → y = 1100
     *   EAST  : từ trái sang   → x = -100 (y snap bởi setupLane → 430/470)
     *   WEST  : từ phải sang   → x = 1100
     */
    @Override
    public java.awt.Point getSpawnPoint(Direction direction) {
        switch (direction) {
            case SOUTH:     return new java.awt.Point(350,  -100);
            case NORTH:     return new java.awt.Point(450,  1100);
            case EAST:      return new java.awt.Point(-100,  470);
            case WEST:      return new java.awt.Point(1100,  530);
            default:        return new java.awt.Point(getCenterX(), getCenterY());
        }
    }
}
