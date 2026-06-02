package layout;

import model.intersection.IntersectionType;

/**
 * Factory tạo IntersectionLayout từ IntersectionType.
 *
 * Khi thêm loại ngã rẽ mới:
 *   1. Tạo class implements IntersectionLayout (vd: SixWayLayout)
 *   2. Thêm case vào factory này
 *   → Không cần sửa bất kỳ system nào khác.
 */
public class IntersectionLayoutFactory {

    public static IntersectionLayout create(IntersectionType type) {
        switch (type) {
            case THREE_WAY: return new ThreeWayLayout();
            case FIVE_WAY:  return new FiveWayLayout();
            default:        return new FourWayLayout();
        }
    }
}
