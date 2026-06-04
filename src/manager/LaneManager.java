package manager;

import config.Constants;
import model.network.NetworkLayout;
import util.Direction;
import util.Lane;

/**
 * Cung cấp tọa độ trung tâm làn.
 * Dùng Constants.LANE_WIDTH để tương thích với v0.1.2 (lane_half = LANE_WIDTH/2 = 25px).
 */
public final class LaneManager {
    private LaneManager() {}

    /** Nửa chiều rộng làn — khớp với IntersectionLayout của v0.1.2. */
    private static final int LANE_HALF = Constants.LANE_WIDTH / 2;

    /**
     * Tâm X làn cho xe N/S-bound.
     * NORTH: lề phải = phía ĐÔNG → cx + LANE_HALF
     * SOUTH: lề phải = phía TÂY  → cx - LANE_HALF
     */
    public static int getLaneCenterX(Direction direction, int homeIntersectionX) {
        switch (direction) {
            case NORTH: return homeIntersectionX + LANE_HALF;
            case SOUTH: return homeIntersectionX - LANE_HALF;
            default:    return homeIntersectionX;
        }
    }

    /**
     * Tâm Y làn cho xe E/W-bound.
     * EAST: lề phải = phía NAM → iy + LANE_HALF
     * WEST: lề phải = phía BẮC → iy - LANE_HALF
     * @param homeIntersectionY tâm Y của giao lộ mà xe thuộc về
     */
    public static int getLaneCenterY(Direction direction, int homeIntersectionY) {
        switch (direction) {
            case EAST: return homeIntersectionY + LANE_HALF;
            case WEST: return homeIntersectionY - LANE_HALF;
            default:   return homeIntersectionY;
        }
    }

    /** Overload không có homeIntersectionY — fallback về NetworkLayout (dùng cho network_v2). */
    public static int getLaneCenterY(Direction direction) {
        switch (direction) {
            case EAST: return NetworkLayout.EAST_LANE_Y;
            case WEST: return NetworkLayout.WEST_LANE_Y;
            default:   return 0;
        }
    }

    // Legacy overloads
    public static int getLaneCenterX(Direction direction, Lane lane, int homeIntersectionX) {
        return getLaneCenterX(direction, homeIntersectionX);
    }

    public static int getLaneCenterY(Direction direction, Lane lane) {
        return getLaneCenterY(direction);
    }

    public static int getLaneCenterX(Direction direction, model.intersection.IntersectionLayout layout) {
        return layout.getLaneCenterX(direction, Lane.RIGHT);
    }
}
