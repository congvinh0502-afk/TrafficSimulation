package layout;

import util.Direction;
import util.Lane;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;

/**
 * Layout nga nam hinh ngoi sao - 5 nhanh cach deu 72 do.
 *
 * Mapping huong → goc (theo RoadRenderer.renderFiveWay):
 *   NORTH     = 270° (thang len)
 *   NORTHEAST = 342° (len-phai)
 *   EAST      =  54° (xuong-phai)
 *   SOUTH     = 126° (xuong-trai)
 *   WEST      = 198° (xuong-trai)
 *
 * Tam nga nam: (500, 400)  -- doi tu 400,400 de can doi tren canvas 1200x800
 * Dao trung tam ban kinh: 80px  (tang tu 72)
 * Vung nhua quanh dao (outerR): 115px  (tang tu 100)
 * Duong moi nhanh rong: 260px  (roadW=130 moi ben, tang tu 100)
 */
public class FiveWayLayout implements IntersectionLayout {

    // Hang so layout khop voi RoadRenderer.renderFiveWay()
    private static final int CX       = 500;   // THAY DOI: 400 → 500
    private static final int CY       = 400;
    private static final int ISLAND_R = 80;    // THAY DOI: 72 → 80
    private static final int OUTER_R  = 115;   // THAY DOI: 100 → 115 (islandR + 35)
    private static final int ROAD_W   = 130;   // THAY DOI: 100 → 130

    // ─────────────────────────────────────────────────────────────
    // TAM
    // ─────────────────────────────────────────────────────────────

    @Override
    public int getCenterX() { return CX; }

    @Override
    public int getCenterY() { return CY; }

    // ─────────────────────────────────────────────────────────────
    // BOUNDS
    // ─────────────────────────────────────────────────────────────

    @Override
    public Rectangle getIntersectionBounds() {
        int r = OUTER_R + ISLAND_R; // = 195
        return new Rectangle(CX - r, CY - r, r * 2, r * 2);
    }

    @Override
    public Rectangle getTriggerBounds() {
        int r = ISLAND_R + 20; // = 100
        return new Rectangle(CX - r, CY - r, r * 2, r * 2);
    }

    @Override
    public Rectangle getRecoverBounds() {
        int r = OUTER_R + ISLAND_R + 20; // = 215
        return new Rectangle(CX - r, CY - r, r * 2, r * 2);
    }

    // ─────────────────────────────────────────────────────────────
    // VACH DUNG
    // ─────────────────────────────────────────────────────────────

    @Override
    public int getStopLineForDirection(Direction direction) {
        int stopOffset = ISLAND_R + 46; // = 126
        switch (direction) {
            case NORTH:     return CY + stopOffset;   // 526
            case SOUTH:     return CY - stopOffset;   // 274
            case EAST:      return CX - stopOffset;   // 374
            case WEST:      return CX + stopOffset;   // 626
            case NORTHEAST: return CY + stopOffset;
            default:        return CY;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // LAN DUONG
    // ─────────────────────────────────────────────────────────────

    @Override
    public int getLaneCenterX(Direction direction, Lane lane) {
        // ROAD_W=130, offset 30% va 70% tu tam
        switch (direction) {
            case NORTH:
                return lane == Lane.RIGHT ? CX + 39 : CX + 91;  // 539, 591
            case SOUTH:
                return lane == Lane.RIGHT ? CX - 39 : CX - 91;  // 461, 409
            case NORTHEAST:
                return lane == Lane.LEFT  ? CX - 80 : CX - 20;  // 420, 480
            default:
                return getCenterX();
        }
    }

    @Override
    public int getLaneCenterY(Direction direction, Lane lane) {
        switch (direction) {
            case EAST:
                return lane == Lane.RIGHT ? CY + 39 : CY + 91;  // 439, 491
            case WEST:
                return lane == Lane.RIGHT ? CY - 39 : CY - 91;  // 361, 309
            case NORTHEAST:
                return lane == Lane.LEFT  ? CY + 200 : CY + 150; // 600, 550
            default:
                return getCenterY();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // DEN GIAO THONG
    // ─────────────────────────────────────────────────────────────

    @Override
    public List<Point> getLightPositions() {
        return Arrays.asList(
            new Point(CX + ROAD_W + 20, CY - ISLAND_R - 100), // den doc: ben nhanh NORTH
            new Point(CX - ISLAND_R - 100, CY + ROAD_W + 20)  // den ngang: ben nhanh WEST
        );
    }

    // ─────────────────────────────────────────────────────────────
    // ENTER CHECK BOUNDS
    // ─────────────────────────────────────────────────────────────

    @Override
    public Rectangle getEnterCheckBounds(Direction direction) {
        int r = ISLAND_R + 60; // = 140
        return new Rectangle(CX - r, CY - r, r * 2, r * 2);
    }

    // ─────────────────────────────────────────────────────────────
    // SPAWN POINT
    // ─────────────────────────────────────────────────────────────

    /**
     * Nga nam - 5 huong, moi huong spawn ngoai ria man hinh.
     * Diem spawn duoc tinh lai tuong ung voi tam moi (500, 400).
     *
     * Goc nhanh (drawFiveWayArm: rotate = angle + PI/2):
     *   NORTH=270°: dx=sin(270)=-1, dy=-cos(270)=0  → sang trai → spawn x<0
     *   NORTHEAST=342°: dx=sin(342)≈-0.31, dy=-cos(342)≈-0.95 → len-trai
     *   EAST=54°: dx=sin(54)≈0.81, dy=-cos(54)≈-0.59 → phai-len
     *   SOUTH=126°: dx=sin(126)≈0.81, dy=-cos(126)≈0.59 → phai-xuong
     *   WEST=198°: dx=sin(198)≈-0.31, dy=-cos(198)≈0.95 → trai-xuong
     *
     * Thuc ra spawn can dat o ria canvas de xe di vao:
     *   NORTH (xe di len): spawn tu duoi → (539, 900)
     *   SOUTH (xe di xuong): spawn tu tren → (461, -100)
     *   EAST  (xe di tu trai): spawn tu trai → (-100, 439)
     *   WEST  (xe di tu phai): spawn tu phai → (1100, 361)
     *   NORTHEAST: spawn tu goc duoi-trai → (-100, 850)
     */
    @Override
    public java.awt.Point getSpawnPoint(Direction direction) {
        switch (direction) {
            case SOUTH:     return new java.awt.Point(461,  -100);
            case NORTH:     return new java.awt.Point(539,  1100);
            case EAST:      return new java.awt.Point(-100,  439);
            case WEST:      return new java.awt.Point(1100,  361);
            case NORTHEAST: return new java.awt.Point(-100,  850);
            default:        return new java.awt.Point(CX, CY);
        }
    }

    @Override
    public double getQueueSpacing() {
        return 80.0;
    }
}
