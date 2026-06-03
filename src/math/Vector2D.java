package math;

/**
 * Vectơ 2D cho hướng và chuyển động toán học.
 *
 * <p>Thay thế enum {@code Direction} bằng vectơ thực, cho phép:
 * <ul>
 *   <li>Phương tiện di chuyển theo vectơ (kể cả hướng chéo 45°).</li>
 *   <li>Tính toán khoảng cách theo vectơ (dot product, projection).</li>
 *   <li>Xoay, cộng, nhân vô hướng trơn tru.</li>
 * </ul>
 * </p>
 *
 * <p>Đây là immutable class – mọi thao tác trả về instance mới.</p>
 */
public final class Vector2D {

    public final double x;
    public final double y;

    // --------------------------------------------------------
    // Hướng chuẩn (đơn vị)
    // --------------------------------------------------------

    /** Đi lên (y giảm). */
    public static final Vector2D NORTH     = new Vector2D(0, -1);
    /** Đi xuống (y tăng). */
    public static final Vector2D SOUTH     = new Vector2D(0,  1);
    /** Đi sang phải (x tăng). */
    public static final Vector2D EAST      = new Vector2D(1,  0);
    /** Đi sang trái (x giảm). */
    public static final Vector2D WEST      = new Vector2D(-1, 0);
    /** Hướng chéo Đông-Bắc 45° (chỉ dùng FIVE_WAY). */
    public static final Vector2D NORTHEAST = new Vector2D(Math.sqrt(2)/2, -Math.sqrt(2)/2);
    /** Hướng chéo Tây-Nam (ngược NORTHEAST). */
    public static final Vector2D SOUTHWEST = new Vector2D(-Math.sqrt(2)/2, Math.sqrt(2)/2);

    /** Vectơ không (dừng lại). */
    public static final Vector2D ZERO      = new Vector2D(0, 0);

    // --------------------------------------------------------
    // Constructor
    // --------------------------------------------------------

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // --------------------------------------------------------
    // Toán tử
    // --------------------------------------------------------

    /** Cộng hai vectơ. */
    public Vector2D add(Vector2D v) {
        return new Vector2D(x + v.x, y + v.y);
    }

    /** Trừ hai vectơ. */
    public Vector2D sub(Vector2D v) {
        return new Vector2D(x - v.x, y - v.y);
    }

    /** Nhân vô hướng (scale). */
    public Vector2D scale(double s) {
        return new Vector2D(x * s, y * s);
    }

    /** Tích vô hướng (dot product). */
    public double dot(Vector2D v) {
        return x * v.x + y * v.y;
    }

    /** Độ dài (magnitude). */
    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    /** Vectơ đơn vị (normalize). Trả về ZERO nếu độ dài = 0. */
    public Vector2D normalize() {
        double len = length();
        return len > 1e-9 ? new Vector2D(x / len, y / len) : ZERO;
    }

    /** Góc theo độ (0 = phải, tăng chiều kim đồng hồ). */
    public double angleDeg() {
        return Math.toDegrees(Math.atan2(y, x));
    }

    /** Quay vectơ một góc (độ). */
    public Vector2D rotate(double deg) {
        double rad = Math.toRadians(deg);
        double cos = Math.cos(rad), sin = Math.sin(rad);
        return new Vector2D(x * cos - y * sin, x * sin + y * cos);
    }

    /**
     * Hình chiếu vectơ này lên vectơ {@code axis}.
     * Dùng để xác định phương tiện phía trước trong một làn.
     */
    public double projectOnto(Vector2D axis) {
        return this.dot(axis.normalize());
    }

    // --------------------------------------------------------
    // Tiện ích
    // --------------------------------------------------------

    /** So sánh xấp xỉ với epsilon. */
    public boolean approxEquals(Vector2D v, double eps) {
        return Math.abs(x - v.x) < eps && Math.abs(y - v.y) < eps;
    }

    @Override
    public String toString() {
        return String.format("(%.3f, %.3f)", x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Vector2D)) return false;
        Vector2D v = (Vector2D) o;
        return Double.compare(x, v.x) == 0 && Double.compare(y, v.y) == 0;
    }

    @Override
    public int hashCode() {
        return 31 * Double.hashCode(x) + Double.hashCode(y);
    }
}
