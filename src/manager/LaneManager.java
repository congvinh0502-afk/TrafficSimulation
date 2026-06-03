package manager;

import model.intersection.IntersectionLayout;
import util.Direction;
import util.Lane;

/**
 * Quản lý tọa độ trung tâm làn đường.
 * Nay delegate hoàn toàn sang IntersectionLayout — không còn hardcode.
 */
public final class LaneManager {

    private LaneManager() {}

    public static int getLaneCenterX(Direction direction, Lane lane, IntersectionLayout layout) {
        return layout.getLaneCenterX(direction, lane);
    }

    public static int getLaneCenterY(Direction direction, Lane lane, IntersectionLayout layout) {
        return layout.getLaneCenterY(direction, lane);
    }
}