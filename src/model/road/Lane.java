package model.road;

import util.Direction;

/**
 * Đại diện một làn đường vật lý trên bản đồ.
 *
 * <p>
 * Lưu vị trí, kích thước và hướng di chuyển của làn.
 * Dùng bởi {@link Road} để tổ chức cấu trúc đường.
 * </p>
 *
 * <p>
 * Không nhầm lẫn với {@link util.Lane} (LEFT/RIGHT) —
 * lớp này là thực thể hình học, còn {@code util.Lane} là enum vị trí.
 * </p>
 */
public class Lane {

    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final Direction direction;

    /**
     * @param x         tọa độ X góc trên-trái
     * @param y         tọa độ Y góc trên-trái
     * @param width     chiều rộng làn (pixel)
     * @param height    chiều cao làn (pixel)
     * @param direction hướng xe di chuyển trong làn này
     */
    public Lane(int x, int y, int width, int height, Direction direction) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.direction = direction;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Direction getDirection() {
        return direction;
    }
}