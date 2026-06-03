package model.intersection;

import config.Constants;
import util.Direction;
import util.Lane;

import java.util.*;

/**
 * Mô tả hình học đầy đủ của một giao lộ.
 *
 * <p>Quy ước tọa độ làn (traffic bên phải):
 * <ul>
 *   <li>NORTH (đi lên, y giảm): làn phải = cột x = cx + half,
 *       làn trái (vượt) = cx + half + lw. Xe spawn từ y lớn (dưới màn hình).</li>
 *   <li>SOUTH (đi xuống, y tăng): làn phải = cx - half,
 *       làn trái = cx - half - lw. Spawn từ y âm (trên màn hình).</li>
 *   <li>EAST (đi phải, x tăng): làn phải = cy - half,
 *       làn trái = cy - half - lw. Spawn từ x âm (trái màn hình).</li>
 *   <li>WEST (đi trái, x giảm): làn phải = cy + half,
 *       làn trái = cy + half + lw. Spawn từ x lớn (phải màn hình).</li>
 *   <li>NORTHEAST (45°, đi theo vector (1,-1)/√2): offset làn vuông góc
 *       với hướng di chuyển theo chiều CW (sang phải khi nhìn theo hướng đi).</li>
 * </ul>
 * </p>
 *
 * <p>Mỗi IntersectionType tạo ra một IntersectionLayout thông qua factory.
 * Factory nhận (cx, cy) để hỗ trợ mạng lưới nhiều giao lộ tại vị trí tùy ý.</p>
 */
public class IntersectionLayout {

    // =========================================================
    // Arm — một nhánh đường
    // =========================================================

    /**
     * Một nhánh (arm) của giao lộ.
     *
     * <p>Với hướng NORTH/SOUTH: leftLaneCenterX và rightLaneCenterX có ý nghĩa;
     * leftLaneCenterY và rightLaneCenterY đặt = 0 (placeholder, không dùng).
     * Với hướng EAST/WEST: leftLaneCenterY và rightLaneCenterY có ý nghĩa;
     * X đặt = 0.
     * Với hướng NORTHEAST: cả X lẫn Y đều có ý nghĩa (điểm trên đường chéo).</p>
     */
    public static class Arm {
        public final Direction direction;
        public final int leftLaneCenterX;
        public final int leftLaneCenterY;
        public final int rightLaneCenterX;
        public final int rightLaneCenterY;
        public final double spawnX;
        public final double spawnY;

        public Arm(Direction dir,
                   int leftX, int leftY,
                   int rightX, int rightY,
                   double spawnX, double spawnY) {
            this.direction        = dir;
            this.leftLaneCenterX  = leftX;
            this.leftLaneCenterY  = leftY;
            this.rightLaneCenterX = rightX;
            this.rightLaneCenterY = rightY;
            this.spawnX           = spawnX;
            this.spawnY           = spawnY;
        }
    }

    // =========================================================
    // TurnZone — vùng kích hoạt rẽ
    // =========================================================

    public static class TurnZone {
        public final int left, right, top, bottom;

        public TurnZone(int left, int right, int top, int bottom) {
            this.left   = left;
            this.right  = right;
            this.top    = top;
            this.bottom = bottom;
        }

        /** Kiểm tra tâm xe (x,y) với nửa chiều rộng/cao hw,hh có nằm trong zone không. */
        public boolean contains(double x, double y, double hw, double hh) {
            return x + hw > left  && x - hw < right
                && y + hh > top   && y - hh < bottom;
        }
    }

    // =========================================================
    // Trường dữ liệu
    // =========================================================

    private final int cx, cy;
    private final Map<Direction, Arm> arms;
    private final TurnZone turnZone;
    private final int recoverLeft, recoverRight, recoverTop, recoverBottom;
    private final int checkLeft,   checkRight,   checkTop,   checkBottom;

    private IntersectionLayout(int cx, int cy,
                                Map<Direction, Arm> arms,
                                TurnZone turnZone,
                                int recoverLeft,  int recoverRight,
                                int recoverTop,   int recoverBottom,
                                int checkLeft,    int checkRight,
                                int checkTop,     int checkBottom) {
        this.cx           = cx;
        this.cy           = cy;
        this.arms         = Collections.unmodifiableMap(arms);
        this.turnZone     = turnZone;
        this.recoverLeft  = recoverLeft;
        this.recoverRight = recoverRight;
        this.recoverTop   = recoverTop;
        this.recoverBottom= recoverBottom;
        this.checkLeft    = checkLeft;
        this.checkRight   = checkRight;
        this.checkTop     = checkTop;
        this.checkBottom  = checkBottom;
    }

    // =========================================================
    // Getter
    // =========================================================

    public int getCx() { return cx; }
    public int getCy() { return cy; }

    public Collection<Arm> getArms()    { return arms.values(); }
    public Arm             getArm(Direction d) { return arms.get(d); }
    public boolean         hasDirection(Direction d) { return arms.containsKey(d); }

    public List<Direction> getDirections() { return new ArrayList<>(arms.keySet()); }

    public TurnZone getTurnZone() { return turnZone; }

    public int getLaneCenterX(Direction d, Lane lane) {
        Arm arm = arms.get(d);
        if (arm == null) return cx;
        return lane == Lane.LEFT ? arm.leftLaneCenterX : arm.rightLaneCenterX;
    }

    public int getLaneCenterY(Direction d, Lane lane) {
        Arm arm = arms.get(d);
        if (arm == null) return cy;
        return lane == Lane.LEFT ? arm.leftLaneCenterY : arm.rightLaneCenterY;
    }

    public double getSpawnX(Direction d) {
        Arm arm = arms.get(d);
        return arm != null ? arm.spawnX : Constants.SPAWN_OFFSCREEN_NEGATIVE;
    }

    public double getSpawnY(Direction d) {
        Arm arm = arms.get(d);
        return arm != null ? arm.spawnY : Constants.SPAWN_OFFSCREEN_NEGATIVE;
    }

    public boolean isOutsideRecover(double x, double y) {
        return x < recoverLeft || x > recoverRight
            || y < recoverTop  || y > recoverBottom;
    }

    public boolean isInsideCheck(double x, double y) {
        return x > checkLeft && x < checkRight
            && y > checkTop  && y < checkBottom;
    }

    public int getCheckLeft()   { return checkLeft; }
    public int getCheckRight()  { return checkRight; }
    public int getCheckTop()    { return checkTop; }
    public int getCheckBottom() { return checkBottom; }

    // =========================================================
    // Factory — helper chung
    // =========================================================

    /**
     * Tạo 4 arm chuẩn NORTH/SOUTH/EAST/WEST cho tâm (cx,cy).
     * Dùng chung cho fourWay và fiveWay.
     */
    private static void addCardinalArms(Map<Direction, Arm> arms, int cx, int cy) {
        int lw   = Constants.LANE_WIDTH;
        int half = lw / 2;

        // --- NORTH (đi lên, y giảm) ---
        // Làn phải (traffic bên phải): x = cx + half
        // Làn trái (vượt):             x = cx + half + lw
        int nRightX = cx + half;
        int nLeftX  = cx + half + lw;
        arms.put(Direction.NORTH, new Arm(Direction.NORTH,
                nLeftX,  0,
                nRightX, 0,
                nRightX, Constants.SPAWN_OFFSCREEN_POSITIVE));

        // --- SOUTH (đi xuống, y tăng) ---
        // Làn phải: x = cx - half
        // Làn trái: x = cx - half - lw
        int sRightX = cx - half;
        int sLeftX  = cx - half - lw;
        arms.put(Direction.SOUTH, new Arm(Direction.SOUTH,
                sLeftX,  0,
                sRightX, 0,
                sRightX, Constants.SPAWN_OFFSCREEN_NEGATIVE));

        // --- EAST (đi phải, x tăng) ---
        // Làn phải: y = cy - half  (phía trên trục ngang)
        // Làn trái: y = cy - half - lw
        int eRightY = cy - half;
        int eLeftY  = cy - half - lw;
        arms.put(Direction.EAST, new Arm(Direction.EAST,
                0, eLeftY,
                0, eRightY,
                Constants.SPAWN_OFFSCREEN_NEGATIVE, eRightY));

        // --- WEST (đi trái, x giảm) ---
        // Làn phải: y = cy + half  (phía dưới trục ngang)
        // Làn trái: y = cy + half + lw
        int wRightY = cy + half;
        int wLeftY  = cy + half + lw;
        arms.put(Direction.WEST, new Arm(Direction.WEST,
                0, wLeftY,
                0, wRightY,
                Constants.SPAWN_OFFSCREEN_POSITIVE, wRightY));
    }

    // =========================================================
    // Factory — FOUR_WAY
    // =========================================================

    /**
     * Ngã tư chuẩn: NORTH + SOUTH + EAST + WEST.
     * Tâm tại (cx, cy), mỗi làn rộng LANE_WIDTH px.
     */
    public static IntersectionLayout fourWay(int cx, int cy) {
        int lw     = Constants.LANE_WIDTH;
        int tzPad  = 15;   // padding turn zone
        int recPad = 50;   // padding vùng recover
        int buf    = 70;   // padding vùng check blocking

        Map<Direction, Arm> arms = new LinkedHashMap<>();
        addCardinalArms(arms, cx, cy);

        TurnZone tz = new TurnZone(
                cx - lw - tzPad, cx + lw + tzPad,
                cy - lw - tzPad, cy + lw + tzPad);

        return new IntersectionLayout(cx, cy, arms, tz,
                cx - lw - recPad, cx + lw + recPad,
                cy - lw - recPad, cy + lw + recPad,
                cx - lw - buf,    cx + lw + buf,
                cy - lw - buf,    cy + lw + buf);
    }

    // =========================================================
    // Factory — THREE_WAY
    // =========================================================

    /**
     * Ngã ba: NORTH + EAST + WEST (không có SOUTH).
     *
     * <p>Đường dọc chỉ đi từ trên (y=0) xuống tâm giao lộ; không có nhánh xuống.
     * Xe NORTH spawn từ phía dưới, đến giao lộ thì chỉ có thể rẽ EAST hoặc WEST.
     * Xe EAST/WEST chạy qua toàn bộ đường ngang như bình thường.</p>
     *
     * <p>Logic này KHÔNG phụ thuộc vào fourWay — được tính độc lập.</p>
     */
    public static IntersectionLayout threeWay(int cx, int cy) {
        int lw = Constants.LANE_WIDTH;
        int half = lw / 2;
        int tzPad = 15;
        int recPad = 50;
        int buf = 70;

        Map<Direction, Arm> arms = new LinkedHashMap<>();

        // NORTH (đi lên, y giảm): spawn từ phía TRÊN giao lộ (y âm/nhỏ),
        // vì đường dọc chỉ có từ trên xuống tâm — không có nhánh xuống.
        // Xe NORTH đi từ trên xuống tâm rồi rẽ EAST hoặc WEST.
        int nRightX = cx + half;
        int nLeftX = cx + half + lw;
        arms.put(Direction.NORTH, new Arm(Direction.NORTH,
                nLeftX, 0,
                nRightX, 0,
                nRightX, Constants.SPAWN_OFFSCREEN_NEGATIVE)); // spawn từ trên, đi xuống

        // EAST (đi phải, x tăng): spawn từ trái màn hình, full width
        int eRightY = cy - half;
        int eLeftY = cy - half - lw;
        arms.put(Direction.EAST, new Arm(Direction.EAST,
                0, eLeftY,
                0, eRightY,
                Constants.SPAWN_OFFSCREEN_NEGATIVE, eRightY));

        // WEST (đi trái, x giảm): spawn từ phải màn hình, full width
        int wRightY = cy + half;
        int wLeftY = cy + half + lw;
        arms.put(Direction.WEST, new Arm(Direction.WEST,
                0, wLeftY,
                0, wRightY,
                Constants.SPAWN_OFFSCREEN_POSITIVE, wRightY));

        // Turn zone: vùng kích hoạt rẽ tại giao điểm trục dọc và ngang
        TurnZone tz = new TurnZone(
                cx - lw - tzPad, cx + lw + tzPad,
                cy - lw - tzPad, cy + lw + tzPad);

        // recoverLeft/Right/Top/Bottom: vùng reset trạng thái xe sau giao lộ
        // recoverTop phải đủ xa để xe NORTH không bị reset sớm khi còn trên đường
        return new IntersectionLayout(cx, cy, arms, tz,
                cx - lw - recPad, cx + lw + recPad,
                cy - lw - recPad, cy + lw + recPad, // recoverTop = cy-lw-recPad (~250)
                cx - lw - buf, cx + lw + buf,
                cy - lw - buf, cy + lw + buf);
    }

    // =========================================================
    // Factory — FIVE_WAY
    // =========================================================

    /**
     * Ngã năm: NORTH + SOUTH + EAST + WEST + NORTHEAST.
     *
     * <p>Nhánh NORTHEAST là đường chéo 45° đi từ góc Tây-Nam (SW) lên Đông-Bắc (NE).
     * Hướng di chuyển của xe NORTHEAST: vector đơn vị (1/√2, -1/√2) trong hệ canvas
     * (y tăng xuống), tức là đi về phía phải-trên.</p>
     *
     * <p><b>Tính offset làn cho NORTHEAST:</b><br>
     * - Hướng đi: d = (1, -1)/√2<br>
     * - Vuông góc sang PHẢI (CW 90°): perp_right = (-d.y, d.x)/normalize = (1, 1)/√2<br>
     *   Vì quay (x,y) 90° CW → (y, -x), nên quay (1/√2, -1/√2) → (-1/√2, -1/√2).
     *   Chú ý: trong canvas y tăng xuống, "phải" khi đi NE là về phía (x giảm, y giảm).<br>
     * - Làn RIGHT (xe đi, bên phải chiều NORTHEAST):
     *   center = tâm bùng binh + half * perp_right<br>
     *   perp_right = (-1/√2, -1/√2) → offset ≈ (-half/√2, -half/√2)<br>
     * - Làn LEFT (làn ngược chiều / vượt):
     *   center = tâm + (half + lw) * perp_right</p>
     *
     * <p>Spawn: xe NORTHEAST xuất phát từ góc SW, ngoài màn hình.</p>
     */
    /**
     * Ngã năm đối xứng: 5 nhánh cách đều nhau 72°.
     *
     * <p>
     * Góc các nhánh (0° = phải, tăng CW theo canvas):
     * <ul>
     * <li>Nhánh 0: 270° → NORTH (thẳng lên)</li>
     * <li>Nhánh 1: 342° → NE (~phải-trên)</li>
     * <li>Nhánh 2: 54° → SE (~phải-dưới)</li>
     * <li>Nhánh 3: 126° → SW (~trái-dưới)</li>
     * <li>Nhánh 4: 198° → NW (~trái-trên)</li>
     * </ul>
     * </p>
     *
     * <p>
     * Mỗi nhánh có 2 làn (RIGHT = chiều xe đến, LEFT = chiều xe đi ra).
     * Spawn cách tâm 650px theo hướng ngược lại của nhánh.
     * </p>
     *
     * <p>
     * Map từ góc sang Direction gần nhất để tương thích hệ thống cũ:
     * </p>
     * 
     * <pre>
     *   270° → NORTH
     *   342° → NORTHEAST  (góc gần nhất có sẵn)
     *   54°  → EAST        (tạm dùng, xe đi hướng SE)
     *   126° → SOUTH       (tạm dùng, xe đi hướng SW)
     *   198° → WEST        (tạm dùng, xe đi hướng NW)
     * </pre>
     * 
     * TODO: Bước 2 — thay Direction enum bằng ArmAngle(double) để xe
     * di chuyển chính xác theo góc 72°.
     */
    public static IntersectionLayout fiveWay(int cx, int cy) {
        int lw = Constants.LANE_WIDTH;
        int half = lw / 2;
        int tzPad = 20;
        int recPad = 80;
        int buf = 90;

        // Bán kính bùng binh — xe cần vào vùng này mới trigger rẽ
        int roundaboutR = 170;

        Map<Direction, Arm> arms = new LinkedHashMap<>();

        // 5 góc nhánh (độ, 0°=phải, CW). Nhánh 0 = NORTH = 270°
        double[] branchAngles = { 270, 342, 54, 126, 198 };

        // Direction map tương ứng (tạm thời dùng Direction gần nhất)
        Direction[] dirs = {
                Direction.NORTH,
                Direction.NORTHEAST,
                Direction.EAST,
                Direction.SOUTH,
                Direction.WEST
        };

        double spawnDist = 700.0;

        for (int i = 0; i < 5; i++) {
            double angleDeg = branchAngles[i];
            double rad = Math.toRadians(angleDeg);

            // Vector hướng nhánh (từ tâm ra ngoài)
            double dx = Math.cos(rad); // x tăng sang phải
            double dy = Math.sin(rad); // y tăng xuống (canvas)

            // Vector vuông góc sang PHẢI khi nhìn theo hướng đi (CW 90°)
            // rotate(dx,dy) 90° CW → (dy, -dx)
            double perpX = dy;
            double perpY = -dx;

            // Làn RIGHT: offset nửa làn sang phải
            int rightX = cx + (int) Math.round(perpX * half);
            int rightY = cy + (int) Math.round(perpY * half);

            // Làn LEFT: offset 1.5 làn sang phải (= half + lw)
            int leftX = cx + (int) Math.round(perpX * (half + lw));
            int leftY = cy + (int) Math.round(perpY * (half + lw));

            // Spawn: từ bên ngoài màn hình, đi ngược chiều nhánh vào
            double spawnX = cx - dx * spawnDist;
            double spawnY = cy - dy * spawnDist;

            arms.put(dirs[i], new Arm(dirs[i],
                    leftX, leftY,
                    rightX, rightY,
                    spawnX, spawnY));
        }

        // TurnZone = hình tròn bùng binh, xấp xỉ bằng bounding box
        TurnZone tz = new TurnZone(
                cx - roundaboutR, cx + roundaboutR,
                cy - roundaboutR, cy + roundaboutR);

        // Vùng recover và check rộng hơn vì 5 nhánh tỏa nhiều hướng
        return new IntersectionLayout(cx, cy, arms, tz,
                cx - roundaboutR - recPad, cx + roundaboutR + recPad,
                cy - roundaboutR - recPad, cy + roundaboutR + recPad,
                cx - roundaboutR - buf, cx + roundaboutR + buf,
                cy - roundaboutR - buf, cy + roundaboutR + buf);
    }

    // =========================================================
    // Factory — Mở rộng: có thể thêm sixWay, tWay, custom... ở đây
    // =========================================================

    /**
     * Giao lộ tùy chỉnh — skeleton để nhóm mở rộng sau này.
     *
     * <p>Để thêm loại giao lộ mới:
     * <ol>
     *   <li>Thêm giá trị enum vào {@link IntersectionType}.</li>
     *   <li>Thêm factory method static ở đây.</li>
     *   <li>Thêm hướng mới vào {@link util.Direction} nếu cần.</li>
     *   <li>Thêm case vào {@code IntersectionType.createLayout()}.</li>
     *   <li>Thêm renderer vào {@code EnvironmentRenderer} và {@code RoadRenderer}.</li>
     * </ol>
     * </p>
     */
    // public static IntersectionLayout sixWay(int cx, int cy) { ... }
}