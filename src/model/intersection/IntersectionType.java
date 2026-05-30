package model.intersection;

import util.Direction;

import java.util.ArrayList;
import java.util.List;

/**
 * Loại ngã rẽ — xác định những hướng nào có đường vào/ra giao lộ.
 *
 * <p>
 * Gọi {@link #getDirections()} để lấy danh sách hướng hợp lệ
 * tại loại ngã rẽ này. Dùng để:
 * <ul>
 * <li>Lọc xe không thuộc hướng đang hiển thị.</li>
 * <li>Kiểm tra hướng rẽ có hợp lệ không (TurningSystem).</li>
 * <li>Spawn xe đúng hướng (VehicleSpawnManager).</li>
 * </ul>
 * </p>
 */
public enum IntersectionType {

    /** Ngã ba — NORTH, EAST, WEST (không có SOUTH). */
    THREE_WAY,

    /** Ngã tư — NORTH, SOUTH, EAST, WEST. */
    FOUR_WAY,

    /** Ngã năm — NORTH, SOUTH, EAST, WEST, NORTHEAST. */
    FIVE_WAY;

    /**
     * Trả về danh sách hướng hợp lệ tại loại ngã rẽ này.
     *
     * @return list hướng (không null, không rỗng)
     */
    public List<Direction> getDirections() {
        List<Direction> directions = new ArrayList<>();
        switch (this) {
            case THREE_WAY:
                directions.add(Direction.NORTH);
                directions.add(Direction.EAST);
                directions.add(Direction.WEST);
                break;
            case FIVE_WAY:
                directions.add(Direction.NORTH);
                directions.add(Direction.SOUTH);
                directions.add(Direction.EAST);
                directions.add(Direction.WEST);
                directions.add(Direction.NORTHEAST);
                break;
            default: // FOUR_WAY
                directions.add(Direction.NORTH);
                directions.add(Direction.SOUTH);
                directions.add(Direction.EAST);
                directions.add(Direction.WEST);
                break;
        }
        return directions;
    }
}