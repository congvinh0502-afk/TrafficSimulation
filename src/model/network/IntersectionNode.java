package model.network;

import model.intersection.IntersectionType;
import model.trafficlight.TrafficLight;

/**
 * Một nút giao lộ trong mạng lưới giao thông.
 *
 * <p>Lưu:
 * <ul>
 *   <li>Tọa độ world tâm giao lộ.</li>
 *   <li>Loại giao lộ ({@link IntersectionType}).</li>
 *   <li>Đèn dọc (N/S-bound) và đèn ngang (E/W-bound) — null nếu không có đèn.</li>
 * </ul>
 * </p>
 *
 * <p>THREE_WAY không có đèn (cả hai đèn = null).</p>
 */
public class IntersectionNode {

    public final int cx, cy;           // tâm giao lộ (world)
    public final IntersectionType type;

    /** Đèn điều khiển xe đi NORTH và SOUTH. Null nếu THREE_WAY. */
    public TrafficLight verticalLight;

    /** Đèn điều khiển xe đi EAST và WEST. Null nếu THREE_WAY. */
    public TrafficLight horizontalLight;

    public IntersectionNode(int cx, int cy, IntersectionType type,
                            TrafficLight vertical, TrafficLight horizontal) {
        this.cx              = cx;
        this.cy              = cy;
        this.type            = type;
        this.verticalLight   = vertical;
        this.horizontalLight = horizontal;
    }

    /** Giao lộ này có đèn giao thông không. */
    public boolean hasTrafficLights() {
        return verticalLight != null;
    }

    /** Cập nhật chu kỳ đèn mỗi frame (chỉ gọi nếu AUTO mode). */
    public void updateLights() {
        if (verticalLight == null) return;
        verticalLight.update();
        // Đồng bộ đèn ngang ngược với đèn dọc
        syncHorizontal();
    }

    private void syncHorizontal() {
        if (horizontalLight == null) return;
        switch (verticalLight.getColor()) {
            case GREEN:
            case YELLOW:
                horizontalLight.setColor(model.trafficlight.LightColor.RED);
                break;
            case RED:
                if (horizontalLight.getColor() != model.trafficlight.LightColor.GREEN) {
                    horizontalLight.setColor(model.trafficlight.LightColor.GREEN);
                    horizontalLight.setTimer(config.Constants.LIGHT_GREEN_DURATION);
                }
                break;
        }
    }

    @Override
    public String toString() {
        return String.format("Intersection[%s @ (%d,%d)]", type, cx, cy);
    }
}
