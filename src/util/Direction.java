package util;

import math.Vector2D;

/**
 * Hướng di chuyển của phương tiện.
 *
 * <p>Mỗi giá trị enum đi kèm một {@link Vector2D} đơn vị,
 * cho phép mã di chuyển không cần switch-case hướng.</p>
 *
 * <p>NORTHEAST dành riêng cho ngã năm (FIVE_WAY).</p>
 */
public enum Direction {

    /** Đi từ Nam lên Bắc (y giảm). */
    NORTH(Vector2D.NORTH, -90),

    /** Đi từ Bắc xuống Nam (y tăng). */
    SOUTH(Vector2D.SOUTH, 90),

    /** Đi từ Tây sang Đông (x tăng). */
    EAST(Vector2D.EAST, 0),

    /** Đi từ Đông sang Tây (x giảm). */
    WEST(Vector2D.WEST, 180),

    /** Hướng chéo Đông-Bắc 45° — chỉ dùng trong FIVE_WAY. */
    NORTHEAST(Vector2D.NORTHEAST, -45);

    // --------------------------------------------------------

    private final Vector2D vector;
    private final double angleDeg;

    Direction(Vector2D vector, double angleDeg) {
        this.vector = vector;
        this.angleDeg = angleDeg;
    }

    /**
     * Vectơ đơn vị tương ứng với hướng này.
     *
     * @return {@link Vector2D} đã normalize
     */
    public Vector2D toVector() {
        return vector;
    }

    /**
     * Góc hiển thị mặc định (độ) khi xe xuất phát theo hướng này.
     * Hệ tọa độ: 0° = phải, tăng chiều kim đồng hồ.
     *
     * @return góc tính bằng độ
     */
    public double toAngleDeg() {
        return angleDeg;
    }

    /**
     * Hướng ngược lại.
     *
     * @return hướng đối diện
     */
    public Direction opposite() {
        switch (this) {
            case NORTH:     return SOUTH;
            case SOUTH:     return NORTH;
            case EAST:      return WEST;
            case WEST:      return EAST;
            case NORTHEAST: return NORTHEAST; // không có cặp
            default:        return this;
        }
    }
}
