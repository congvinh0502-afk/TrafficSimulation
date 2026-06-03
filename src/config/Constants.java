package config;

/**
 * Hằng số toàn dự án — tập trung tại một nơi để dễ chỉnh sửa.
 */
public final class Constants {

    private Constants() {}

    // =========================================================
    // Màn hình / bản đồ
    // =========================================================
    public static final int WINDOW_WIDTH  = 1200;
    public static final int WINDOW_HEIGHT = 800;

    // =========================================================
    // Hình học giao lộ
    // =========================================================
    public static final int INTERSECTION_LEFT   = 300;
    public static final int INTERSECTION_RIGHT  = 500;
    public static final int INTERSECTION_TOP    = 300;
    public static final int INTERSECTION_BOTTOM = 500;
    public static final int INTERSECTION_CENTER_X = (INTERSECTION_LEFT + INTERSECTION_RIGHT) / 2;
    public static final int INTERSECTION_CENTER_Y = (INTERSECTION_TOP + INTERSECTION_BOTTOM) / 2;

    // =========================================================
    // Làn đường
    // =========================================================
    public static final int LANE_OFFSET = 50;

    public static final int LANE_NORTH_LEFT_X  = 430;
    public static final int LANE_NORTH_RIGHT_X = 470;
    public static final int LANE_SOUTH_LEFT_X  = 330;
    public static final int LANE_SOUTH_RIGHT_X = 370;
    public static final int LANE_EAST_LEFT_Y   = 330;
    public static final int LANE_EAST_RIGHT_Y  = 370;
    public static final int LANE_WEST_LEFT_Y   = 430;
    public static final int LANE_WEST_RIGHT_Y  = 470;

    // =========================================================
    // Vùng kiểm tra giao lộ (có buffer)
    // =========================================================
    public static final int INTERSECTION_BUFFER       = 70;
    public static final int INTERSECTION_CHECK_LEFT   = INTERSECTION_LEFT   - INTERSECTION_BUFFER;
    public static final int INTERSECTION_CHECK_RIGHT  = INTERSECTION_RIGHT  + INTERSECTION_BUFFER;
    public static final int INTERSECTION_CHECK_TOP    = INTERSECTION_TOP    - INTERSECTION_BUFFER;
    public static final int INTERSECTION_CHECK_BOTTOM = INTERSECTION_BOTTOM + INTERSECTION_BUFFER;

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
    // Góc ban đầu (độ) theo hướng
    // =========================================================
    public static final double ANGLE_EAST      =   0;
    public static final double ANGLE_SOUTH     =  90;
    public static final double ANGLE_WEST      = 180;
    public static final double ANGLE_NORTH     = -90;
    public static final double ANGLE_NORTHEAST = -45;

    // =========================================================
    // Đèn giao thông (frame @ 60fps)
    // =========================================================
    public static final int LIGHT_GREEN_DURATION    = 300;
    public static final int LIGHT_YELLOW_DURATION   = 120;
    public static final int LIGHT_RED_DURATION      = 300;
    public static final int LIGHT_EMERGENCY_DURATION= 400;
    public static final int SMART_LIGHT_THRESHOLD   = 3;
    public static final int SMART_LIGHT_MAX_TIMER   = 400;
    public static final int STOP_SENSOR_OFFSET      = 60;

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

    /**
     * Gia tốc dương mặc định (px/frame²) — tăng tốc dần về maxSpeed.
     */
    public static final double DEFAULT_ACCELERATION     = 0.08;

    /**
     * Tốc độ tối đa phanh (gia tốc âm) khi gần đèn đỏ / xe trước.
     * Giá trị âm được nhân với hệ số khoảng cách.
     */
    public static final double MAX_BRAKE_DECEL         = 0.35;

    /**
     * Khoảng cách bắt đầu giảm tốc trước đèn đỏ hoặc xe phía trước (px).
     */
    public static final double BRAKE_START_DISTANCE    = 130.0;

    /**
     * Khoảng cách tối thiểu giữ cách xe phía trước khi đứng yên (px).
     */
    public static final double MIN_FOLLOW_DISTANCE     = 10.0;

    /**
     * Hệ số phản hồi tốc độ — xe điều chỉnh tốc độ nhanh hay chậm.
     * Lớn hơn → phản ứng nhanh hơn nhưng có thể giật.
     */
    public static final double SPEED_RESPONSE_FACTOR   = 0.06;

    // =========================================================
    // Vùng phát hiện rẽ
    // =========================================================
    public static final int TURN_TRIGGER_LEFT   = 420;
    public static final int TURN_TRIGGER_RIGHT  = 530;
    public static final int TURN_TRIGGER_TOP    = 420;
    public static final int TURN_TRIGGER_BOTTOM = 530;

    // =========================================================
    // Hồi phục sau giao lộ
    // =========================================================
    public static final int RECOVER_LEFT   = 360;
    public static final int RECOVER_RIGHT  = 590;
    public static final int RECOVER_TOP    = 360;
    public static final int RECOVER_BOTTOM = 590;

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
