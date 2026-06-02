package model.trafficlight;

/**
 * Chế độ hiển thị đếm ngược trên đèn giao thông.
 *
 * Thay thế các String magic "ALWAYS COUNTDOWN" / "NO COUNTDOWN" / "COUNT <= 10"
 * trước đây dùng trong SimulationConfig và TrafficLightRenderer.
 */
public enum LightDisplayMode {

    /** Không hiện số giây */
    NO_COUNTDOWN,

    /** Luôn hiện số giây */
    ALWAYS_COUNTDOWN,

    /** Chỉ hiện số giây khi còn <= 10 giây */
    LAST_10S_COUNTDOWN;

    /**
     * Parse từ String (cho ControlPanel backward-compatible).
     */
    public static LightDisplayMode fromString(String s) {
        switch (s) {
            case "ALWAYS COUNTDOWN": return ALWAYS_COUNTDOWN;
            case "COUNT <= 10":      return LAST_10S_COUNTDOWN;
            default:                 return NO_COUNTDOWN;
        }
    }
}
