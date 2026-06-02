package layout;

import util.Direction;
import util.Lane;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;

/**
 * Layout ngã năm hình ngôi sao — 5 nhánh cách đều 72°.
 *
 * Mapping hướng → góc (theo RoadRenderer.renderFiveWay):
 *   NORTH     = 270° (thẳng lên)
 *   NORTHEAST = 342° (lên-phải)
 *   EAST      =  54° (xuống-phải)  ← tên "EAST" nhưng thực ra góc 54°
 *   SOUTH     = 126° (xuống-trái)  ← tên "SOUTH" nhưng thực ra góc 126°
 *   WEST      = 198° (xuống-trái)
 *
 * Tâm ngã năm: (400, 400)
 * Đảo trung tâm bán kính: 72px
 * Vùng nhựa quanh đảo (outerR): 100px
 *
 * Làn đường ngã năm:
 *   Mỗi nhánh rộng 200px (roadW = 100 mỗi bên).
 *   Hai làn trên mỗi nhánh (RIGHT = làn gần phải chiều đi, LEFT = làn xa hơn).
 *
 * LƯU Ý VỀ LÀN ĐƯỜNG CHÉO (NORTHEAST, EAST, SOUTH, WEST của ngã 5):
 *   Vì xe đi theo hướng chéo, getLaneCenterX/Y không thể dùng đơn giản như
 *   ngã tư. Phần này trả về tọa độ spawn gốc — các system movement sẽ dùng
 *   vector-based align theo góc thay vì snap X/Y riêng lẻ.
 *   (Được xử lý trong LaneAlignmentSystem khi direction == NORTHEAST, v.v.)
 */
public class FiveWayLayout implements IntersectionLayout {

    // Các hằng số layout khớp với RoadRenderer.renderFiveWay()
    private static final int CX       = 400;
    private static final int CY       = 400;
    private static final int ISLAND_R = 72;   // bán kính đảo
    private static final int OUTER_R  = 100;  // bán kính vùng nhựa quanh đảo (islandR + 28)
    private static final int ROAD_W   = 100;  // nửa chiều rộng 1 nhánh

    // ─────────────────────────────────────────────────────────────
    // TÂM
    // ─────────────────────────────────────────────────────────────

    @Override
    public int getCenterX() { return CX; }

    @Override
    public int getCenterY() { return CY; }

    // ─────────────────────────────────────────────────────────────
    // BOUNDS
    // ─────────────────────────────────────────────────────────────

    /**
     * Vùng đảo + nhựa bao quanh = vòng tròn bán kính OUTER_R.
     * Dùng bounding box vuông cho đơn giản.
     * Xe ở trong bounds này = đang ở khu vực trung tâm ngã năm.
     */
    @Override
    public Rectangle getIntersectionBounds() {
        int r = OUTER_R + ISLAND_R; // = 172
        return new Rectangle(CX - r, CY - r, r * 2, r * 2);
        // = (228, 228) → (572, 572)
    }

    /**
     * Trigger turning: nhỏ hơn, chỉ vùng đảo + buffer nhỏ.
     */
    @Override
    public Rectangle getTriggerBounds() {
        int r = ISLAND_R + 20; // = 92
        return new Rectangle(CX - r, CY - r, r * 2, r * 2);
        // = (308, 308) → (492, 492)
    }

    /**
     * Recover bounds: rộng hơn intersection để xe thoát hẳn mới reset.
     */
    @Override
    public Rectangle getRecoverBounds() {
        int r = OUTER_R + ISLAND_R + 20; // = 192
        return new Rectangle(CX - r, CY - r, r * 2, r * 2);
        // = (208, 208) → (592, 592)
    }

    // ─────────────────────────────────────────────────────────────
    // VẠCH DỪNG
    // ─────────────────────────────────────────────────────────────

    /**
     * Vạch dừng = rìa đảo + stop line offset (46px theo ArmMarkings).
     * Khoảng cách từ tâm đến stop line = ISLAND_R + 46 = 118.
     *
     * Cho các hướng dọc/ngang:
     *   NORTH: dừng trước đảo phía dưới → y = CY + 118 = 518
     *   SOUTH: dừng trước đảo phía trên → y = CY - 118 = 282
     *   EAST:  dừng trước đảo bên trái  → x = CX - 118 = 282
     *   WEST:  dừng trước đảo bên phải  → x = CX + 118 = 518
     *
     * NORTHEAST: stop line ≈ theo hướng 342°, lấy tọa độ bounding của trục Y
     */
    @Override
    public int getStopLineForDirection(Direction direction) {
        int stopOffset = ISLAND_R + 46; // = 118
        switch (direction) {
            case NORTH:     return CY + stopOffset;   // 518 — xe đi lên dừng phía dưới
            case SOUTH:     return CY - stopOffset;   // 282 — xe đi xuống dừng phía trên
            case EAST:      return CX - stopOffset;   // 282 — xe đi sang phải dừng bên trái
            case WEST:      return CX + stopOffset;   // 518 — xe đi sang trái dừng bên phải
            case NORTHEAST: return CY + stopOffset;   // dùng Y-axis làm proxy
            default:        return CY;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // LÀN ĐƯỜNG
    // ─────────────────────────────────────────────────────────────

    /**
     * Tọa độ X trung tâm làn cho các nhánh dọc.
     * Nhánh NORTH ở ngã năm nằm đúng tại x = CX (400), rộng ±ROAD_W.
     *   RIGHT lane (làn vào): x = CX - ROAD_W/2 = 350 (lề phải theo chiều đi)
     *   LEFT  lane (làn ra) : x = CX + ROAD_W/2 = 450
     *
     * Giải thích:
     *   Xe NORTH đi từ dưới lên → lề phải của xe = phía ĐÔNG của nhánh NORTH
     *   Trong hệ tọa độ màn hình, phía ĐÔNG = x lớn hơn
     *   → RIGHT = x lớn hơn = 450; nhưng ta dùng convention "làn vào = RIGHT"
     *   Giữ nhất quán với FourWayLayout để TurningSystem không bị ảnh hưởng:
     *   NORTH RIGHT = 430, NORTH LEFT = 470
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
     * Tọa độ Y trung tâm làn cho các nhánh ngang.
     * Giữ nhất quán với FourWayLayout.
     */
    @Override
    public int getLaneCenterY(Direction direction, Lane lane) {
        switch (direction) {
            case EAST:
                return lane == Lane.RIGHT ? 430 : 470;
            case WEST:
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
     * Ngã năm — 2 đèn chính:
     *   Đèn 0 (vertical): điều tiết NORTH/SOUTH
     *   Đèn 1 (horizontal): điều tiết EAST/WEST/NORTHEAST
     *
     * Vị trí đặt đèn: cách tâm theo hướng nhánh, ngoài đảo ~130px.
     * Đặt cạnh nhánh NORTH (bên phải nhánh) và cạnh nhánh WEST (phía trên nhánh).
     */
    @Override
    public List<Point> getLightPositions() {
        return Arrays.asList(
            new Point(CX + ROAD_W + 20, CY - ISLAND_R - 100), // đèn dọc: (520, 228) ≈ bên nhánh NORTH
            new Point(CX - ISLAND_R - 100, CY + ROAD_W + 20)  // đèn ngang: (228, 520) ≈ bên nhánh WEST
        );
    }

    // ─────────────────────────────────────────────────────────────
    // ENTER CHECK BOUNDS
    // ─────────────────────────────────────────────────────────────

    /**
     * Ngã năm dùng bounds tròn (proxy bằng rectangle) quanh đảo.
     * Rộng hơn trigger để bắt xe sắp vào từ mọi hướng.
     */
    @Override
    public Rectangle getEnterCheckBounds(Direction direction) {
        int r = ISLAND_R + 60; // = 132
        return new Rectangle(CX - r, CY - r, r * 2, r * 2);
        // = (268, 268) → (532, 532)
    }

    // ─────────────────────────────────────────────────────────────
    // SPAWN POINT
    // ─────────────────────────────────────────────────────────────

    /**
     * Ngã năm — 5 hướng, mỗi hướng spawn ngoài rìa màn hình.
     *
     * NORTHEAST: hướng chéo 45° (từ góc dưới-trái tiến lên-phải).
     * Spawn tại (−100, 900) để xe đi theo đường chéo vào tâm (400,400).
     */
    @Override
    public java.awt.Point getSpawnPoint(Direction direction) {
        switch (direction) {
            case SOUTH:     return new java.awt.Point(370,  -100);
            case NORTH:     return new java.awt.Point(430,  1100);
            case EAST:      return new java.awt.Point(-100,  430);
            case WEST:      return new java.awt.Point(1100,  370);
            case NORTHEAST: return new java.awt.Point(-100,  900); // chéo dưới-trái → trên-phải
            default:        return new java.awt.Point(CX, CY);
        }
    }

    @Override
    public double getQueueSpacing() {
        return 80.0; // nhánh chéo hơi ngắn hơn, giãn nhỏ hơn
    }
}
