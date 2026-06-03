package util;

/**
 * Hướng di chuyển của phương tiện trên bản đồ.
 *
 * <p>
 * NORTHEAST là hướng thứ 5 dành riêng cho ngã năm (FIVE_WAY).
 * Các hướng còn lại dùng chung cho tất cả loại ngã rẽ.
 * </p>
 */
public enum Direction {

    /** Đi từ Nam lên Bắc (y giảm dần). */
    NORTH,

    /** Đi từ Bắc xuống Nam (y tăng dần). */
    SOUTH,

    /** Đi từ Tây sang Đông (x tăng dần). */
    EAST,

    /** Đi từ Đông sang Tây (x giảm dần). */
    WEST,

    /** Hướng chéo Đông-Bắc — chỉ dùng trong FIVE_WAY. */
    NORTHEAST
}