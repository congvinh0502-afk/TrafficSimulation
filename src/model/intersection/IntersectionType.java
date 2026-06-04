package model.intersection;

import util.Direction;
import java.util.Arrays;
import java.util.List;

/**
 * Loại giao lộ — xác định các hướng arm có trong giao lộ.
 *
 * <p>THREE_WAY: ngã ba T-junction với nhánh NORTH, SOUTH, EAST (mở phía Đông).
 * Không có nhánh WEST — xe đến từ Đông phải rẽ N hoặc S.</p>
 * <p>FOUR_WAY: crossroads đầy đủ 4 hướng.</p>
 * <p>FIVE_WAY: crossroads + nhánh chéo NE.</p>
 */
public enum IntersectionType {

    /** Ngã ba — N, S, E arm (không có W). Không có đèn giao thông. */
    THREE_WAY,

    /** Ngã tư — N, S, E, W arm. Có đèn giao thông. */
    FOUR_WAY,

    /** Ngã năm — N, S, E, W + NE diagonal arm. Có đèn giao thông. */
    FIVE_WAY;

    public List<Direction> getDirections() {
        switch (this) {
            case THREE_WAY:
                return Arrays.asList(Direction.NORTH, Direction.SOUTH, Direction.EAST);
            case FIVE_WAY:
                return Arrays.asList(Direction.NORTH, Direction.SOUTH,
                                     Direction.EAST,  Direction.WEST, Direction.NORTHEAST);
            default: // FOUR_WAY
                return Arrays.asList(Direction.NORTH, Direction.SOUTH,
                                     Direction.EAST,  Direction.WEST);
        }
    }
}
