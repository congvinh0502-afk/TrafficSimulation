package system.collision;

import model.vehicle.Vehicle;

/**
 * Hộp chạm AABB (Axis-Aligned Bounding Box) cho phương tiện.
 *
 * <p>Được dùng để:
 * <ul>
 *   <li>Kiểm tra va chạm giữa các phương tiện.</li>
 *   <li>Kiểm tra phương tiện có vào vùng giao lộ chưa.</li>
 *   <li>Chạy song song với hệ thống giữ khoảng cách để phát hiện
 *       tình huống overlap thực sự.</li>
 * </ul>
 * </p>
 *
 * <p><b>Lưu ý:</b> AABB không xét góc xoay — dùng cho kiểm tra nhanh.
 * Khi xe đang rẽ, hitbox mở rộng thêm padding để bù cho góc xoay.</p>
 */
public class Hitbox {

    public final double x;
    public final double y;
    public final double width;
    public final double height;

    // --------------------------------------------------------
    // Padding bù cho các trạng thái đặc biệt
    // --------------------------------------------------------
    private static final double TURNING_PADDING = 8.0;

    // --------------------------------------------------------
    // Constructor
    // --------------------------------------------------------

    public Hitbox(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // --------------------------------------------------------
    // Factory từ Vehicle
    // --------------------------------------------------------

    /**
     * Tạo hitbox từ phương tiện, lấy tâm xe làm điểm gốc.
     *
     * @param v phương tiện cần tạo hitbox
     * @return hitbox tương ứng
     */
    public static Hitbox of(Vehicle v) {
        double padding = v.isTurning() ? TURNING_PADDING : 0;
        double w = v.getWidth() + padding * 2;
        double h = v.getHeight() + padding * 2;
        return new Hitbox(
                v.getX() - padding,
                v.getY() - padding,
                w, h
        );
    }

    /**
     * Tạo hitbox với padding tường minh.
     */
    public static Hitbox ofWithPadding(Vehicle v, double padding) {
        return new Hitbox(
                v.getX() - padding,
                v.getY() - padding,
                v.getWidth()  + padding * 2,
                v.getHeight() + padding * 2
        );
    }

    // --------------------------------------------------------
    // Kiểm tra giao nhau
    // --------------------------------------------------------

    /**
     * AABB overlap test.
     *
     * @param other hitbox cần so sánh
     * @return {@code true} nếu hai hộp chạm nhau hoặc chứa nhau
     */
    public boolean intersects(Hitbox other) {
        return x < other.x + other.width
            && x + width  > other.x
            && y < other.y + other.height
            && y + height > other.y;
    }

    /**
     * Điểm có nằm trong hitbox không.
     */
    public boolean contains(double px, double py) {
        return px >= x && px <= x + width
            && py >= y && py <= y + height;
    }

    /**
     * Khoảng cách tâm-tâm giữa hai hitbox.
     */
    public double centerDistance(Hitbox other) {
        double dx = (x + width / 2) - (other.x + other.width / 2);
        double dy = (y + height / 2) - (other.y + other.height / 2);
        return Math.sqrt(dx * dx + dy * dy);
    }

    /** Tâm X. */
    public double centerX() { return x + width  / 2; }
    /** Tâm Y. */
    public double centerY() { return y + height / 2; }

    @Override
    public String toString() {
        return String.format("Hitbox[%.1f,%.1f %.1f×%.1f]", x, y, width, height);
    }
}
