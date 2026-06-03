package config;

/**
 * Hằng số dùng chung trong toàn dự án.
 *
 * <p>
 * Tập trung tất cả giá trị hình học, vật lý và thời gian
 * vào một nơi duy nhất để dễ chỉnh sửa và tái sử dụng.
 * Không khởi tạo lớp này — chỉ truy cập các trường tĩnh.
 * </p>
 */
public final class Constants {

    private Constants() {
        /* không khởi tạo */ }

    // =========================================================
    // Kích thước màn hình / bản đồ
    // =========================================================

    /** Chiều rộng cửa sổ ứng dụng (pixel). */
    public static final int WINDOW_WIDTH = 1200;

    /** Chiều cao cửa sổ ứng dụng (pixel). */
    public static final int WINDOW_HEIGHT = 800;

    // =========================================================
    // Hình học giao lộ — tất cả tọa độ liên quan đến giao lộ
    // =========================================================

    /** Biên trái vùng giao lộ (x). */
    public static final int INTERSECTION_LEFT = 300;

    /** Biên phải vùng giao lộ (x). */
    public static final int INTERSECTION_RIGHT = 500;

    /** Biên trên vùng giao lộ (y). */
    public static final int INTERSECTION_TOP = 300;

    /** Biên dưới vùng giao lộ (y). */
    public static final int INTERSECTION_BOTTOM = 500;

    /** Tâm X của giao lộ. */
    public static final int INTERSECTION_CENTER_X = (INTERSECTION_LEFT + INTERSECTION_RIGHT) / 2;

    /** Tâm Y của giao lộ. */
    public static final int INTERSECTION_CENTER_Y = (INTERSECTION_TOP + INTERSECTION_BOTTOM) / 2;

    // =========================================================
    // Làn đường — vị trí trung tâm của từng làn
    // =========================================================

    /**
     * Khoảng cách từ trung tâm đường đến trung tâm mỗi làn.
     * Đường rộng 200px → mỗi làn 100px → offset ±50px từ vạch giữa.
     */
    public static final int LANE_OFFSET = 50;

    // Làn NORTH (xe đi từ Nam lên Bắc, chạy bên phải đường):
    // Đường dọc: x=300..500, trục giữa x=400.
    // Làn trái (gần giao lộ) = 400 - LANE_OFFSET + 30 = 430
    // Làn phải (xa giao lộ) = 400 + LANE_OFFSET - 30 = 470
    /** Trung tâm X làn trái, hướng NORTH. */
    public static final int LANE_NORTH_LEFT_X = 430;
    /** Trung tâm X làn phải, hướng NORTH. */
    public static final int LANE_NORTH_RIGHT_X = 470;

    // Làn SOUTH (xe đi từ Bắc xuống Nam, chạy bên trái đường):
    /** Trung tâm X làn trái, hướng SOUTH. */
    public static final int LANE_SOUTH_LEFT_X = 330;
    /** Trung tâm X làn phải, hướng SOUTH. */
    public static final int LANE_SOUTH_RIGHT_X = 370;

    // Làn EAST (xe đi từ Tây sang Đông):
    /** Trung tâm Y làn trái, hướng EAST. */
    public static final int LANE_EAST_LEFT_Y = 330;
    /** Trung tâm Y làn phải, hướng EAST. */
    public static final int LANE_EAST_RIGHT_Y = 370;

    // Làn WEST (xe đi từ Đông sang Tây):
    /** Trung tâm Y làn trái, hướng WEST. */
    public static final int LANE_WEST_LEFT_Y = 430;
    /** Trung tâm Y làn phải, hướng WEST. */
    public static final int LANE_WEST_RIGHT_Y = 470;

    // =========================================================
    // Vùng kiểm tra giao lộ (có thêm buffer để tránh bỏ sót)
    // =========================================================

    /** Buffer mở rộng vùng giao lộ khi kiểm tra va chạm/dừng. */
    public static final int INTERSECTION_BUFFER = 70;

    /** Biên trái có buffer. */
    public static final int INTERSECTION_CHECK_LEFT = INTERSECTION_LEFT - INTERSECTION_BUFFER;
    /** Biên phải có buffer. */
    public static final int INTERSECTION_CHECK_RIGHT = INTERSECTION_RIGHT + INTERSECTION_BUFFER;
    /** Biên trên có buffer. */
    public static final int INTERSECTION_CHECK_TOP = INTERSECTION_TOP - INTERSECTION_BUFFER;
    /** Biên dưới có buffer. */
    public static final int INTERSECTION_CHECK_BOTTOM = INTERSECTION_BOTTOM + INTERSECTION_BUFFER;

    // =========================================================
    // An toàn — khoảng cách giữ xe
    // =========================================================

    /** Khoảng cách (pixel) để kiểm tra xe phía trước (canEnterIntersection). */
    public static final int LOOKAHEAD_DISTANCE = 120;

    /** Khoảng cách ngang tối đa để coi hai xe là cùng làn. */
    public static final int SAME_LANE_TOLERANCE = 50;

    /** Khoảng cách an toàn tối thiểu khi đổi làn. */
    public static final int LANE_CHANGE_SAFE_DISTANCE = 120;

    /** Cooldown (frame) sau khi đổi làn xong trước khi được đổi tiếp. */
    public static final int LANE_CHANGE_COOLDOWN = 60;

    /**
     * Khoảng cách ngang tối đa để coi là đang cùng hàng dọc/ngang
     * (maintainDistance).
     */
    public static final int SAME_FILE_TOLERANCE = 25;

    // =========================================================
    // Spawn / xóa xe
    // =========================================================

    /** Tọa độ spawn ngoài màn hình theo chiều âm (trên/trái). */
    public static final int SPAWN_OFFSCREEN_NEGATIVE = -100;

    /** Tọa độ spawn ngoài màn hình theo chiều dương (dưới/phải). */
    public static final int SPAWN_OFFSCREEN_POSITIVE = 1100;

    /** Khoảng cách tối thiểu giữa hai xe khi spawn. */
    public static final int SPAWN_MIN_DISTANCE = 150;

    /** Ngưỡng xóa xe khi ra ngoài bản đồ. */
    public static final int REMOVE_THRESHOLD_MIN = -200;

    /** Ngưỡng xóa xe khi ra ngoài bản đồ. */
    public static final int REMOVE_THRESHOLD_MAX = 1400;

    // =========================================================
    // Góc quay ban đầu của xe theo hướng
    // =========================================================

    /** Góc (độ) của xe hướng EAST. */
    public static final double ANGLE_EAST = 0;
    /** Góc (độ) của xe hướng SOUTH. */
    public static final double ANGLE_SOUTH = 90;
    /** Góc (độ) của xe hướng WEST. */
    public static final double ANGLE_WEST = 180;
    /** Góc (độ) của xe hướng NORTH. */
    public static final double ANGLE_NORTH = -90;
    /** Góc (độ) của xe hướng NORTHEAST. */
    public static final double ANGLE_NORTHEAST = -45;

    // =========================================================
    // Đèn giao thông — thời gian (frame, 60fps)
    // =========================================================

    /** Thời gian đèn XANH (frame). */
    public static final int LIGHT_GREEN_DURATION = 300;

    /** Thời gian đèn VÀNG (frame). */
    public static final int LIGHT_YELLOW_DURATION = 120;

    /** Thời gian đèn ĐỎ (frame). */
    public static final int LIGHT_RED_DURATION = 300;

    /** Thời gian đèn khi xe ưu tiên kích hoạt. */
    public static final int LIGHT_EMERGENCY_DURATION = 400;

    /** Số xe chênh lệch để kích hoạt điều chỉnh đèn thông minh. */
    public static final int SMART_LIGHT_VEHICLE_THRESHOLD = 3;

    /** Thời gian tối đa đèn khi điều chỉnh thông minh. */
    public static final int SMART_LIGHT_MAX_TIMER = 400;

    // =========================================================
    // Cảm biến đèn đỏ — khoảng cách gần vạch dừng
    // =========================================================

    /** Khoảng cách phía trước xe để kích hoạt kiểm tra đèn. */
    public static final int STOP_SENSOR_OFFSET = 60;

    // =========================================================
    // Vật lý xe — turning
    // =========================================================

    /** Ngưỡng góc (độ) để coi rẽ đã hoàn thành. */
    public static final double TURN_FINISH_TOLERANCE = 5.0;

    /** Tốc độ quay xe mỗi frame (độ). */
    public static final double ROTATE_SPEED = 2.0;

    /** Hệ số tốc độ tiến khi đang rẽ. */
    public static final double TURNING_SPEED_FACTOR = 0.35;

    /** Hệ số căn giữa làn (lane alignment smooth). */
    public static final double LANE_ALIGN_SMOOTH_FACTOR = 0.15;

    /** Tốc độ tối đa căn giữa làn (pixel/frame). */
    public static final double LANE_ALIGN_MAX_SMOOTH = 2.0;

    /** Tốc độ dịch chuyển khi đổi làn (pixel/frame). */
    public static final double LANE_CHANGE_SMOOTH = 0.6;

    /** Ngưỡng pixel để xác nhận đã vào đúng làn sau khi đổi. */
    public static final double LANE_CHANGE_SNAP_THRESHOLD = 3.0;

    // =========================================================
    // Phát hiện trong giao lộ (TurningSystem)
    // =========================================================

    /** Biên trái vùng kích hoạt rẽ. */
    public static final int TURN_TRIGGER_LEFT = 420;
    /** Biên phải vùng kích hoạt rẽ. */
    public static final int TURN_TRIGGER_RIGHT = 530;
    /** Biên trên vùng kích hoạt rẽ. */
    public static final int TURN_TRIGGER_TOP = 420;
    /** Biên dưới vùng kích hoạt rẽ. */
    public static final int TURN_TRIGGER_BOTTOM = 530;

    // =========================================================
    // Hồi phục sau giao lộ (recoverAfterIntersection)
    // =========================================================

    /** Biên trái vùng giao lộ mở rộng — xe ngoài vùng này mới reset. */
    public static final int RECOVER_LEFT = 360;
    /** Biên phải vùng giao lộ mở rộng. */
    public static final int RECOVER_RIGHT = 590;
    /** Biên trên vùng giao lộ mở rộng. */
    public static final int RECOVER_TOP = 360;
    /** Biên dưới vùng giao lộ mở rộng. */
    public static final int RECOVER_BOTTOM = 590;

    // =========================================================
    // Xe ưu tiên — bán kính nhường đường
    // =========================================================

    /** Khoảng cách (pixel) mà xe thường phải nhường xe ưu tiên. */
    public static final int EMERGENCY_YIELD_RADIUS = 150;

    // =========================================================
    // Đèn nháy xe ưu tiên
    // =========================================================

    /** Số frame mỗi lần đổi trạng thái nhấp nháy đèn xe ưu tiên. */
    public static final int FLASH_INTERVAL_FRAMES = 20;

    // =========================================================
    // Tỉ lệ tắc đường
    // =========================================================

    /** Tỉ lệ xe dừng để xếp loại tắc đường MỨC CAO. */
    public static final double JAM_HIGH_THRESHOLD = 0.6;

    /** Tỉ lệ xe dừng để xếp loại tắc đường MỨC TRUNG. */
    public static final double JAM_MEDIUM_THRESHOLD = 0.3;
}