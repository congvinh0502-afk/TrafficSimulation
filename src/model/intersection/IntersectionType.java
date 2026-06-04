package model.intersection;

import util.Direction;

import java.util.List;

/**
 * Loại ngã rẽ — factory cho {@link IntersectionLayout} tương ứng.
 *
 * <p>Tâm giao lộ mặc định (400, 400) dùng cho chế độ single-intersection.
 * Với mạng lưới đường, gọi {@link #createLayout(int, int)} với tọa độ tùy ý.</p>
 *
 * <h3>Cách mở rộng thêm loại giao lộ mới:</h3>
 * <ol>
 *   <li>Thêm giá trị enum (vd. {@code SIX_WAY}) vào đây.</li>
 *   <li>Thêm factory method {@code IntersectionLayout.sixWay(cx, cy)} trong
 *       {@link IntersectionLayout}.</li>
 *   <li>Thêm hướng mới vào {@link util.Direction} nếu cần thêm trục mới.</li>
 *   <li>Thêm {@code case SIX_WAY} trong {@link #createLayout(int, int)}.</li>
 *   <li>Thêm renderer trong {@code view.renderer.EnvironmentRenderer} và
 *       {@code view.renderer.RoadRenderer}.</li>
 *   <li>Thêm option trong {@code view.controller.MenuController} và menu.fxml.</li>
 * </ol>
 *
 * <h3>Cách xây dựng mạng lưới đường (Road Network):</h3>
 * <pre>
 * // Ví dụ: lưới 2×2 ngã tư, cách nhau 600px
 * IntersectionLayout tl = FOUR_WAY.createLayout(300, 300);
 * IntersectionLayout tr = FOUR_WAY.createLayout(900, 300);
 * IntersectionLayout bl = FOUR_WAY.createLayout(300, 700);
 * IntersectionLayout br = FOUR_WAY.createLayout(900, 700);
 * // Tạo RoadNetwork(List.of(tl, tr, bl, br)) và render từng cái
 * </pre>
 */
public enum IntersectionType {

    /** Ngã ba — NORTH, EAST, WEST (không có SOUTH). */
    THREE_WAY,

    /** Ngã tư — NORTH, SOUTH, EAST, WEST. */
    FOUR_WAY,

    /** Ngã năm — NORTH, FW_NE, FW_SE, FW_SW, FW_NW (5 nhánh cách đều 72 độ). */
    FIVE_WAY;

    /** Tâm giao lộ mặc định cho chế độ single-intersection. */
    public static final int DEFAULT_CX = 400;
    public static final int DEFAULT_CY = 400;

    /**
     * Tạo {@link IntersectionLayout} với tâm mặc định (400, 400).
     */
    public IntersectionLayout createLayout() {
        return createLayout(DEFAULT_CX, DEFAULT_CY);
    }

    /**
     * Tạo {@link IntersectionLayout} với tâm tùy chỉnh.
     * Dùng cho mạng lưới đường nhiều giao lộ.
     *
     * @param cx tọa độ X tâm giao lộ (px)
     * @param cy tọa độ Y tâm giao lộ (px)
     */
    public IntersectionLayout createLayout(int cx, int cy) {
        switch (this) {
            case THREE_WAY: return IntersectionLayout.threeWay(cx, cy);
            case FIVE_WAY:  return IntersectionLayout.fiveWay(cx, cy);
            default:        return IntersectionLayout.fourWay(cx, cy);
        }
    }

    /**
     * Danh sách hướng hợp lệ — lấy từ layout mặc định.
     * Chỉ dùng khi không cần tọa độ cụ thể.
     */
    public List<Direction> getDirections() {
        return createLayout().getDirections();
    }
}