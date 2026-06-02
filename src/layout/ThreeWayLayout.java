package layout;

import util.Direction;
import util.Lane;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Layout ngã ba chữ T.
 *
 * Hình dạng:
 *   - Trục dọc (NORTH): x = 300–500, từ trên xuống chỉ đến giữa (không có SOUTH)
 *   - Trục ngang (EAST + WEST): y = 300–500, full chiều ngang màn hình
 *   - Tâm ngã ba: (400, 400)
 *
 * Cấu trúc đường (theo RoadRenderer.renderThreeWay):
 *   g.fillRect(300, 0, 200, 800)   → trục dọc (NORTH đi xuống gặp ngang)
 *   g.fillRect(0, 300, 500, 200)   → trục ngang chỉ bên TRÁI (0–500)
 *
 * Xe có thể đến từ: NORTH (từ trên), EAST (từ trái sang), WEST (từ phải sang)
 * Không có SOUTH vì không có đường đi lên.
 *
 * Khoảng cách ngã ba nhỏ hơn ngã tư, trigger bounds cũng nhỏ hơn.
 */
public class ThreeWayLayout implements IntersectionLayout {

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
     * Ngã ba chỉ có 3 nhánh — vùng ngã rẽ thực tế hình chữ L.
     * Dùng bounds vuông bao quanh toàn bộ góc giao nhau.
     */
    @Override
    public Rectangle getIntersectionBounds() {
        return new Rectangle(300, 300, 200, 200); // (300,300) → (500,500)
    }

    @Override
    public Rectangle getTriggerBounds() {
        return new Rectangle(360, 360, 80, 80); // (360,360) → (440,440)
    }

    @Override
    public Rectangle getRecoverBounds() {
        return new Rectangle(290, 290, 220, 220); // hơi rộng hơn intersection
    }

    // ─────────────────────────────────────────────────────────────
    // VẠCH DỪNG
    // ─────────────────────────────────────────────────────────────

    /**
     * Ngã ba:
     *   NORTH: đi từ trên xuống, dừng ở y = 310 (trước vạch trên của ngang)
     *   EAST : đi từ trái sang phải, dừng ở x = 310 (trước vạch trái)
     *   WEST : đi từ phải sang trái, dừng ở x = 490 (trước vạch phải)
     */
    @Override
    public int getStopLineForDirection(Direction direction) {
        switch (direction) {
            case NORTH: return 490;  // xe đi lên, dừng trước vạch dưới
            case EAST:  return 310;  // xe đi sang phải, dừng trước vạch trái
            case WEST:  return 490;  // xe đi sang trái, dừng trước vạch phải
            default:    return getCenterY();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // LÀN ĐƯỜNG
    // ─────────────────────────────────────────────────────────────

    /**
     * Tọa độ X trung tâm làn cho NORTH (không có SOUTH ở ngã ba này).
     * Giữ cùng layout với FourWay để xe NORTH không bị lệch.
     */
    @Override
    public int getLaneCenterX(Direction direction, Lane lane) {
        switch (direction) {
            case NORTH:
                return lane == Lane.RIGHT ? 430 : 470;
            default:
                return getCenterX();
        }
    }

    /**
     * Tọa độ Y trung tâm làn cho EAST / WEST.
     */
    @Override
    public int getLaneCenterY(Direction direction, Lane lane) {
        switch (direction) {
            case EAST:
                return lane == Lane.RIGHT ? 430 : 470;
            case WEST:
                return lane == Lane.RIGHT ? 370 : 330;
            default:
                return getCenterY();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // ĐÈN GIAO THÔNG
    // ─────────────────────────────────────────────────────────────

    /**
     * Ngã ba chỉ cần 1 đèn (đèn dọc cho NORTH).
     * Đèn ngang điều tiết EAST/WEST chung với nhau.
     * Đặt đèn gần cột phân làn.
     */
    @Override
    public List<Point> getLightPositions() {
        return Arrays.asList(
            new Point(490, 200),   // đèn dọc — phía trên nhánh NORTH
            new Point(200, 480)    // đèn ngang — phía trái nhánh EAST/WEST
        );
    }

    // ─────────────────────────────────────────────────────────────
    // ENTER CHECK BOUNDS
    // ─────────────────────────────────────────────────────────────

    @Override
    public Rectangle getEnterCheckBounds(Direction direction) {
        return new Rectangle(320, 320, 160, 160); // (320,320) → (480,480)
    }

    // ─────────────────────────────────────────────────────────────
    // SPAWN POINT
    // ─────────────────────────────────────────────────────────────

    /**
     * Ngã ba: chỉ có NORTH (từ dưới lên), EAST (từ trái), WEST (từ phải).
     * Không có SOUTH vì không có nhánh đi xuống.
     */
    @Override
    public java.awt.Point getSpawnPoint(Direction direction) {
        switch (direction) {
            case NORTH: return new java.awt.Point(430,  1100); // từ dưới lên
            case EAST:  return new java.awt.Point(-100,  430); // từ trái sang
            case WEST:  return new java.awt.Point(1100,  370); // từ phải sang
            default:    return new java.awt.Point(getCenterX(), getCenterY());
        }
    }
}
