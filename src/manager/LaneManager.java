package manager;

import layout.IntersectionLayout;
import util.Direction;
import util.Lane;

/**
 * LaneManager — cầu nối giữa các system cần tọa độ làn đường
 * và IntersectionLayout hiện tại.
 *
 * Thay đổi so với phiên bản cũ:
 *   - Không còn hardcode tọa độ trong switch-case.
 *   - Tất cả tọa độ lấy từ layout được inject vào.
 *   - setLayout() được gọi từ SimulationPanel khi START.
 *
 * Các system cũ (LaneAlignmentSystem, LaneChangeSystem, TurningSystem,
 * VehicleSpawnManager) gọi LaneManager.getLaneCenterX/Y() như cũ,
 * không cần sửa signature → backward compatible.
 */
public class LaneManager {

    // Layout hiện tại — mặc định FourWay để không null khi khởi động
    private static IntersectionLayout currentLayout =
            new layout.FourWayLayout();

    /**
     * Cập nhật layout khi user chọn loại ngã rẽ mới.
     * Gọi từ SimulationPanel.applyConfig().
     */
    public static void setLayout(IntersectionLayout layout) {
        currentLayout = layout;
    }

    public static IntersectionLayout getLayout() {
        return currentLayout;
    }

    // ─────────────────────────────────────────────────────────────
    // API GIỮ NGUYÊN — các system gọi vẫn compile không cần sửa
    // ─────────────────────────────────────────────────────────────

    public static int getLaneCenterX(Direction direction, Lane lane) {
        return currentLayout.getLaneCenterX(direction, lane);
    }

    public static int getLaneCenterY(Direction direction, Lane lane) {
        return currentLayout.getLaneCenterY(direction, lane);
    }
}
