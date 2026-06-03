package config;

/**
 * Hằng số toàn dự án.
 * - Physics/timing/UI: dùng ở mọi nơi.
 * - Hình học giao lộ cụ thể (lane centers, spawn, turn zones): thuộc IntersectionLayout.
 * - Hình học mặc định map 800×800 (tâm 400,400): dùng cho Road factory và renderer.
 */
public final class Constants {

    private Constants() {}

    // =========================================================
    // Màn hình / bản đồ
    // =========================================================
    public static final int WINDOW_WIDTH  = 1200;
    public static final int WINDOW_HEIGHT = 800;

    // =========================================================
    // Kích thước làn đường
    // =========================================================
    /** Chiều rộng mỗi làn đơn (px). */
    public static final int LANE_WIDTH = 50;

    // =========================================================
    // Hình học giao lộ mặc định (tâm 400,400 trên canvas 800×800)
    // Dùng cho Road.createVertical/Horizontal và renderer hardcode.
    // IntersectionLayout tính riêng từ cx,cy được truyền vào.
    // =========================================================
    /** Cạnh trái vùng giao lộ mặc định (cx=400, lw=50 → 400-50=350, nhưng vẽ từ 300). */
    public static final int INTERSECTION_LEFT   = 300;
    /** Cạnh phải vùng giao lộ mặc định. */
    public static final int INTERSECTION_RIGHT  = 500;
    /** Cạnh trên vùng giao lộ mặc định. */
    public static final int INTERSECTION_TOP    = 300;
    /** Cạnh dưới vùng giao lộ mặc định. */
    public static final int INTERSECTION_BOTTOM = 500;

    // =========================================================
    // Khoảng cách / safety
    // =========================================================
    public static final int    LOOKAHEAD_DISTANCE     = 120;
    public static final int    SAME_LANE_TOLERANCE    = 50;
    public static final int    LANE_CHANGE_SAFE_DIST  = 120;
    public static final int    LANE_CHANGE_COOLDOWN   = 60;
    public static final int    SAME_FILE_TOLERANCE    = 25;

    // =========================================================
    // Spawn / xóa xe
    // =========================================================
    public static final int SPAWN_OFFSCREEN_NEGATIVE = -100;
    public static final int SPAWN_OFFSCREEN_POSITIVE = 1100;
    public static final int SPAWN_MIN_DISTANCE       = 150;
    public static final int REMOVE_THRESHOLD_MIN     = -300;
    public static final int REMOVE_THRESHOLD_MAX     = 1500;

    // =========================================================
    // Đèn giao thông (frame @ 60fps)
    // =========================================================
    public static final int LIGHT_GREEN_DURATION     = 300;
    public static final int LIGHT_YELLOW_DURATION    = 120;
    public static final int LIGHT_RED_DURATION       = 300;
    public static final int LIGHT_EMERGENCY_DURATION = 400;
    public static final int SMART_LIGHT_THRESHOLD    = 3;
    public static final int SMART_LIGHT_MAX_TIMER    = 400;
    public static final int STOP_SENSOR_OFFSET       = 60;

    // =========================================================
    // Vật lý xe — turning
    // =========================================================
    public static final double TURN_FINISH_TOLERANCE    = 5.0;
    public static final double ROTATE_SPEED             = 2.5;
    public static final double TURNING_SPEED_FACTOR     = 0.2;
    public static final double LANE_ALIGN_SMOOTH_FACTOR = 0.12;
    public static final double LANE_ALIGN_MAX_SMOOTH    = 2.0;
    public static final double LANE_CHANGE_SMOOTH       = 0.7;
    public static final double LANE_CHANGE_SNAP_THRESHOLD  = 3.0;
    public static final double POST_TURN_ALIGN_FACTOR   = 0.18;
    public static final double POST_TURN_SNAP_THRESHOLD = 2.5;

    // =========================================================
    // Vật lý xe — acceleration
    // =========================================================
    public static final double DEFAULT_ACCELERATION  = 0.08;
    public static final double MAX_BRAKE_DECEL       = 0.35;
    public static final double BRAKE_START_DISTANCE  = 130.0;
    public static final double MIN_FOLLOW_DISTANCE   = 10.0;
    public static final double SPEED_RESPONSE_FACTOR = 0.06;

    // =========================================================
    // Xe ưu tiên
    // =========================================================
    public static final int    EMERGENCY_YIELD_RADIUS = 150;
    public static final int    FLASH_INTERVAL_FRAMES  = 20;

    // =========================================================
    // Mức tắc đường
    // =========================================================
    public static final double JAM_HIGH_THRESHOLD   = 0.6;
    public static final double JAM_MEDIUM_THRESHOLD = 0.3;

    // =========================================================
    // Camera mặc định
    // =========================================================
    public static final double CAMERA_DEFAULT_ZOOM = 1.0;
}
