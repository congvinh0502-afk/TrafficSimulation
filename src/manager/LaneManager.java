package manager;

import model.network.NetworkLayout;
import util.Direction;
import util.Lane;

/**
 * Cung cấp tọa độ trung tâm làn dựa trên NetworkLayout (right-hand traffic).
 */
public final class LaneManager {
    private LaneManager() {}

    /**
     * Tâm X làn cho xe N/S-bound, theo giao lộ mà xe đó thuộc.
     * @param direction NORTH hoặc SOUTH
     * @param homeIntersectionX tâm X của giao lộ (TW_X, FW_X, hoặc VW_X)
     */
    public static int getLaneCenterX(Direction direction, int homeIntersectionX) {
        switch (direction) {
            case NORTH: return NetworkLayout.northLaneX(homeIntersectionX);
            case SOUTH: return NetworkLayout.southLaneX(homeIntersectionX);
            default:    return homeIntersectionX; // fallback
        }
    }

    /**
     * Tâm Y làn cho xe E/W-bound (không phụ thuộc giao lộ).
     */
    public static int getLaneCenterY(Direction direction) {
        switch (direction) {
            case EAST: return NetworkLayout.EAST_LANE_Y;
            case WEST: return NetworkLayout.WEST_LANE_Y;
            default:   return 0;
        }
    }

    // Legacy overload — dùng Lane enum (RIGHT = tâm phải / đi bình thường)
    public static int getLaneCenterX(Direction direction, Lane lane, int homeIntersectionX) {
        return getLaneCenterX(direction, homeIntersectionX);
    }

    public static int getLaneCenterY(Direction direction, Lane lane) {
        return getLaneCenterY(direction);
    }
}
