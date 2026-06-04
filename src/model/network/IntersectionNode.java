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

    private long lastTime = 0;
    private int cycleTimer = 0;

    public void updateLights() {
        if (verticalLight == null || horizontalLight == null)
            return;

        // Lấy thời gian thực của hệ thống (mili-giây)
        long currentTime = System.currentTimeMillis();
        if (lastTime == 0) {
            lastTime = currentTime; // Bỏ qua sai số ở frame đầu tiên
        }
        int deltaTime = (int) (currentTime - lastTime);
        lastTime = currentTime;

        int greenTime = config.Constants.LIGHT_GREEN_DURATION;
        int yellowTime = config.Constants.LIGHT_YELLOW_DURATION;
        int totalCycle = (greenTime + yellowTime) * 2;

        // Cộng dồn thời gian thực đã trôi qua
        cycleTimer += deltaTime;
        if (cycleTimer >= totalCycle) {
            cycleTimer %= totalCycle; // Chia lấy dư để chu kỳ luôn mượt mà kể cả khi lag
        }

        int phase1End = greenTime;
        int phase2End = phase1End + yellowTime;
        int phase3End = phase2End + greenTime;
        int phase4End = totalCycle;

        if (cycleTimer < phase1End) {
            verticalLight.setColor(model.trafficlight.LightColor.GREEN);
            verticalLight.setTimer(phase1End - cycleTimer);
            horizontalLight.setColor(model.trafficlight.LightColor.RED);
            horizontalLight.setTimer(phase2End - cycleTimer);
        } else if (cycleTimer < phase2End) {
            verticalLight.setColor(model.trafficlight.LightColor.YELLOW);
            verticalLight.setTimer(phase2End - cycleTimer);
            horizontalLight.setColor(model.trafficlight.LightColor.RED);
            horizontalLight.setTimer(phase2End - cycleTimer);
        } else if (cycleTimer < phase3End) {
            verticalLight.setColor(model.trafficlight.LightColor.RED);
            verticalLight.setTimer(phase4End - cycleTimer);
            horizontalLight.setColor(model.trafficlight.LightColor.GREEN);
            horizontalLight.setTimer(phase3End - cycleTimer);
        } else {
            verticalLight.setColor(model.trafficlight.LightColor.RED);
            verticalLight.setTimer(phase4End - cycleTimer);
            horizontalLight.setColor(model.trafficlight.LightColor.YELLOW);
            horizontalLight.setTimer(phase4End - cycleTimer);
        }
    }

    @Override
    public String toString() {
        return String.format("Intersection[%s @ (%d,%d)]", type, cx, cy);
    }
}
