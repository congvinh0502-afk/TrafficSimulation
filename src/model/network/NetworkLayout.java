package model.network;

/**
 * Nguồn sự thật duy nhất cho tọa độ world của toàn mạng lưới.
 *
 * <p>Hệ tọa độ world: gốc (0,0) = tâm màn hình.
 * Camera khởi tạo với offsetX = screenW/2, offsetY = screenH/2 nên
 * world(0,0) luôn hiển thị ở giữa màn hình.</p>
 *
 * <p>Luật đi lề PHẢI (right-hand traffic):
 * <ul>
 *   <li>Đường N-S: xe đi NORTH (lên) ở làn ĐÔNG (x = int_x + LANE_HALF).
 *                  xe đi SOUTH (xuống) ở làn TÂY (x = int_x - LANE_HALF).</li>
 *   <li>Đường E-W: xe đi EAST (phải) ở làn NAM (y = +LANE_HALF).
 *                  xe đi WEST (trái) ở làn BẮC (y = -LANE_HALF).</li>
 * </ul>
 * </p>
 */
public final class NetworkLayout {

    private NetworkLayout() {}

    // =========================================================
    // Tâm các giao lộ (tọa độ world)
    // =========================================================

    /** Ngã ba — T-junction mở sang ĐÔNG (có N, S, E arm). */
    public static final int TW_X = -400, TW_Y = 0;

    /** Ngã tư — crossroads đầy đủ (N, S, E, W). */
    public static final int FW_X = 0,    FW_Y = 0;

    /** Ngã năm — crossroads + nhánh chéo NE (N, S, E, W, NE). */
    public static final int VW_X = 400,  VW_Y = 0;

    // =========================================================
    // Kích thước đường
    // =========================================================

    /** Nửa chiều rộng đường (mỗi bên tâm = 1 làn = 36 px). */
    public static final int ROAD_HALF = 36;

    /** Tâm làn cách tâm đường (= ROAD_HALF / 2). */
    public static final int LANE_HALF = 18;

    /** Chiều rộng mỗi làn (= ROAD_HALF). */
    public static final int LANE_WIDTH = ROAD_HALF;

    // =========================================================
    // Tâm làn — N/S-bound dùng X; E/W-bound dùng Y
    // =========================================================

    // Đường N-S tại THREE_WAY (center x = TW_X)
    /** Làn xe đi NORTH tại ngã ba: phía ĐÔNG (lề phải). */
    public static final int TW_NORTH_LANE_X = TW_X + LANE_HALF;  // -382
    /** Làn xe đi SOUTH tại ngã ba: phía TÂY (lề phải). */
    public static final int TW_SOUTH_LANE_X = TW_X - LANE_HALF;  // -418

    // Đường N-S tại FOUR_WAY (center x = FW_X = 0)
    /** Làn xe đi NORTH tại ngã tư. */
    public static final int FW_NORTH_LANE_X = FW_X + LANE_HALF;  // +18
    /** Làn xe đi SOUTH tại ngã tư. */
    public static final int FW_SOUTH_LANE_X = FW_X - LANE_HALF;  // -18

    // Đường N-S tại FIVE_WAY (center x = VW_X = 400)
    /** Làn xe đi NORTH tại ngã năm. */
    public static final int VW_NORTH_LANE_X = VW_X + LANE_HALF;  // +418
    /** Làn xe đi SOUTH tại ngã năm. */
    public static final int VW_SOUTH_LANE_X = VW_X - LANE_HALF;  // +382

    // Đường E-W (ngang, tất cả ở y = 0)
    /** Làn xe đi EAST: phía NAM (lề phải). */
    public static final int EAST_LANE_Y = +LANE_HALF;  // +18
    /** Làn xe đi WEST: phía BẮC (lề phải). */
    public static final int WEST_LANE_Y = -LANE_HALF;  // -18

    // =========================================================
    // Chiều dài nhánh (từ tâm giao lộ ra ngoài màn hình)
    // =========================================================
    public static final int ARM_EXT = 550;

    // =========================================================
    // Vùng giao lộ (collision / traffic light check)
    // =========================================================

    /** Bán kính vùng kiểm tra giao lộ (px từ tâm). */
    public static final int INTER_HALF  = ROAD_HALF + 4;   // 40 px
    /** Khoảng cách bắt đầu phanh trước giao lộ. */
    public static final int BRAKE_DIST  = 140;
    /** Vị trí vạch dừng: cách tâm giao lộ bao nhiêu px. */
    public static final int STOP_LINE   = ROAD_HALF + 10;  // 46 px

    // =========================================================
    // Vị trí đèn giao thông (offset từ tâm giao lộ, world px)
    // — luôn ở phía PHẢI của làn xe, ngoài mặt đường —
    // =========================================================
    public static final int LIGHT_BOX_W  = 22;   // hiển thị nhỏ gọn
    public static final int LIGHT_BOX_H  = 58;
    public static final int LIGHT_MARGIN = 10;   // khoảng cách từ mép đường

    /**
     * Tọa độ góc trên-trái của hộp đèn CHO xe đi NORTH tại giao lộ (ix, iy).
     * Đặt ở phía ĐÔNG (right-hand side of N-bound lane), ngoài mép đường.
     */
    public static double[] northLightPos(int ix, int iy) {
        // Right side of N-bound: EAST of road → x > ix + ROAD_HALF
        double lx = ix + ROAD_HALF + LIGHT_MARGIN;
        double ly = iy + ROAD_HALF - LIGHT_BOX_H;  // bottom at stop line
        return new double[]{lx, ly};
    }

    /** Đèn cho xe đi SOUTH tại giao lộ (ix, iy) — phía TÂY, ngoài mép đường. */
    public static double[] southLightPos(int ix, int iy) {
        double lx = ix - ROAD_HALF - LIGHT_MARGIN - LIGHT_BOX_W;
        double ly = iy - ROAD_HALF;  // top at stop line
        return new double[]{lx, ly};
    }

    /** Đèn cho xe đi EAST tại giao lộ (ix, iy) — phía NAM, ngoài mép đường. */
    public static double[] eastLightPos(int ix, int iy) {
        double lx = ix - ROAD_HALF - LIGHT_BOX_W;
        double ly = iy + ROAD_HALF + LIGHT_MARGIN;
        return new double[]{lx, ly};
    }

    /** Đèn cho xe đi WEST tại giao lộ (ix, iy) — phía BẮC, ngoài mép đường. */
    public static double[] westLightPos(int ix, int iy) {
        double lx = ix + ROAD_HALF;
        double ly = iy - ROAD_HALF - LIGHT_MARGIN - LIGHT_BOX_H;
        return new double[]{lx, ly};
    }

    // =========================================================
    // Tiện ích
    // =========================================================

    /**
     * Tìm tâm X của giao lộ N-S gần nhất với x của xe.
     * Dùng để biết xe N/S-bound đang ở nhánh của giao lộ nào.
     */
    public static int nearestIntersectionX(double vehicleX) {
        double d3 = Math.abs(vehicleX - TW_X);
        double d4 = Math.abs(vehicleX - FW_X);
        double d5 = Math.abs(vehicleX - VW_X);
        if (d3 <= d4 && d3 <= d5) return TW_X;
        if (d4 <= d5)              return FW_X;
        return VW_X;
    }

    /** Tâm X làn NORTH tương ứng với một giao lộ X. */
    public static int northLaneX(int intX) { return intX + LANE_HALF; }

    /** Tâm X làn SOUTH tương ứng với một giao lộ X. */
    public static int southLaneX(int intX) { return intX - LANE_HALF; }

    /**
     * Kiểm tra điểm (px, py) có nằm trong vùng giao lộ tại (ix, iy) không.
     */
    public static boolean inIntersectionBox(double px, double py, int ix, int iy) {
        return Math.abs(px - ix) < INTER_HALF && Math.abs(py - iy) < INTER_HALF;
    }
}
