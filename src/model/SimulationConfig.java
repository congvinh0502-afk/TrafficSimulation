package model;

import model.intersection.IntersectionType;
import model.trafficlight.LightDisplayMode;
import util.TrafficDensity;

/**
 * SimulationConfig — cấu hình một phiên mô phỏng.
 *
 * Thay đổi so với phiên bản cũ:
 *   - lightType giờ là LightDisplayMode enum thay vì String.
 *   - Thêm constructor nhận String để ControlPanel không cần sửa ngay.
 */
public class SimulationConfig {

    private final IntersectionType intersectionType;
    private final String           trafficMode;
    private final LightDisplayMode lightDisplayMode;
    private final TrafficDensity   trafficDensity;

    // ─────────────────────────────────────────────────────────────
    // CONSTRUCTOR (enum — dùng từ bây giờ)
    // ─────────────────────────────────────────────────────────────

    public SimulationConfig(
            IntersectionType intersectionType,
            String           trafficMode,
            LightDisplayMode lightDisplayMode,
            TrafficDensity   trafficDensity
    ) {
        this.intersectionType = intersectionType;
        this.trafficMode      = trafficMode;
        this.lightDisplayMode = lightDisplayMode;
        this.trafficDensity   = trafficDensity;
    }

    // ─────────────────────────────────────────────────────────────
    // CONSTRUCTOR (String lightType — backward-compatible)
    // ─────────────────────────────────────────────────────────────

    public SimulationConfig(
            IntersectionType intersectionType,
            String           trafficMode,
            String           lightType,
            TrafficDensity   trafficDensity
    ) {
        this(
            intersectionType,
            trafficMode,
            LightDisplayMode.fromString(lightType),
            trafficDensity
        );
    }

    // ─────────────────────────────────────────────────────────────
    // GETTERS
    // ─────────────────────────────────────────────────────────────

    public IntersectionType getIntersectionType() {
        return intersectionType;
    }

    public String getTrafficMode() {
        return trafficMode;
    }

    /** Trả về enum — dùng trong TrafficLightRenderer mới */
    public LightDisplayMode getLightDisplayMode() {
        return lightDisplayMode;
    }

    /** Backward-compatible getter — giữ để code cũ không lỗi */
    public String getLightType() {
        switch (lightDisplayMode) {
            case ALWAYS_COUNTDOWN:  return "ALWAYS COUNTDOWN";
            case LAST_10S_COUNTDOWN: return "COUNT <= 10";
            default:                return "NO COUNTDOWN";
        }
    }

    public TrafficDensity getTrafficDensity() {
        return trafficDensity;
    }
}
