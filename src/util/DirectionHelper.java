package util;

/**
 * Tiện ích tính hướng rẽ dựa trên hướng hiện tại.
 *
 * <p>
 * Quy tắc: nhìn từ trên xuống theo chiều đi của xe.
 * <ul>
 * <li>Rẽ trái NORTH → WEST, SOUTH → EAST, EAST → NORTH, WEST → SOUTH</li>
 * <li>Rẽ phải NORTH → EAST, SOUTH → WEST, EAST → SOUTH, WEST → NORTH</li>
 * </ul>
 * NORTHEAST không hỗ trợ rẽ — trả về chính nó.
 * </p>
 */
public final class DirectionHelper {

    private DirectionHelper() {
        /* tiện ích tĩnh */ }

    /**
     * Trả về hướng rẽ trái từ hướng hiện tại.
     *
     * @param direction hướng hiện tại của xe
     * @return hướng sau khi rẽ trái, hoặc chính {@code direction} nếu không xác
     *         định
     */
    public static Direction getLeftDirection(Direction direction) {
        switch (direction) {
            case NORTH:
                return Direction.WEST;
            case SOUTH:
                return Direction.EAST;
            case EAST:
                return Direction.NORTH;
            case WEST:
                return Direction.SOUTH;
            default:
                return direction;
        }
    }

    /**
     * Trả về hướng rẽ phải từ hướng hiện tại.
     *
     * @param direction hướng hiện tại của xe
     * @return hướng sau khi rẽ phải, hoặc chính {@code direction} nếu không xác
     *         định
     */
    public static Direction getRightDirection(Direction direction) {
        switch (direction) {
            case NORTH:
                return Direction.EAST;
            case SOUTH:
                return Direction.WEST;
            case EAST:
                return Direction.SOUTH;
            case WEST:
                return Direction.NORTH;
            default:
                return direction;
        }
    }
}