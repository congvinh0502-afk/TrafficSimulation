package model.road;

import math.Vector2D;
import util.Direction;

/**
 * Đại diện một làn đường vật lý trên bản đồ.
 *
 * <p>Mỗi làn lưu:
 * <ul>
 *   <li>Vị trí và kích thước (pixel).</li>
 *   <li>{@link Direction} (enum) để tương thích các hệ thống cũ.</li>
 *   <li>{@link Vector2D} hướng toán học — xe di chuyển theo vectơ này.</li>
 *   <li>Tọa độ trung tâm làn ({@code centerX}, {@code centerY})
 *       cho alignment và spawn.</li>
 * </ul>
 * </p>
 *
 * <p>Không nhầm với {@link util.Lane} (LEFT/RIGHT) — lớp này là
 * thực thể hình học còn enum kia chỉ là vị trí tương đối.</p>
 */
public class Lane {

    private final double x;
    private final double y;
    private final double width;
    private final double height;

    /** Hướng enum — dùng cho logic đèn, spawn, va chạm. */
    private final Direction direction;

    /** Vectơ hướng toán học — xe di chuyển theo vectơ này. */
    private final Vector2D directionVector;

    /** Tọa độ trung tâm theo chiều ngang của làn (dùng để alignment). */
    private final double centerX;

    /** Tọa độ trung tâm theo chiều dọc của làn (dùng để alignment). */
    private final double centerY;

    // --------------------------------------------------------
    // Constructor
    // --------------------------------------------------------

    /**
     * Tạo làn đường với tất cả thông số.
     *
     * @param x         góc trên-trái X
     * @param y         góc trên-trái Y
     * @param width     chiều rộng (px)
     * @param height    chiều cao (px)
     * @param direction hướng xe di chuyển trong làn
     */
    public Lane(double x, double y, double width, double height, Direction direction) {
        this.x         = x;
        this.y         = y;
        this.width     = width;
        this.height    = height;
        this.direction = direction;
        this.directionVector = direction.toVector();
        this.centerX   = x + width  / 2;
        this.centerY   = y + height / 2;
    }

    // --------------------------------------------------------
    // Getter
    // --------------------------------------------------------

    public double    getX()               { return x; }
    public double    getY()               { return y; }
    public double    getWidth()           { return width; }
    public double    getHeight()          { return height; }
    public Direction getDirection()       { return direction; }
    public Vector2D  getDirectionVector() { return directionVector; }
    public double    getCenterX()         { return centerX; }
    public double    getCenterY()         { return centerY; }

    // --------------------------------------------------------
    // Tiện ích
    // --------------------------------------------------------

    /**
     * Kiểm tra điểm (px, py) có nằm trong làn này không.
     */
    public boolean contains(double px, double py) {
        return px >= x && px <= x + width
            && py >= y && py <= y + height;
    }

    @Override
    public String toString() {
        return String.format("Lane[%s center=(%.0f,%.0f)]", direction, centerX, centerY);
    }
}
