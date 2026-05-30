package util;

/**
 * Mật độ giao thông — xác định tần suất spawn xe mới.
 *
 * @see config.Constants#SPAWN_OFFSCREEN_NEGATIVE
 */
public enum TrafficDensity {

    /** Thưa — spawn chậm (khoảng 3 giây/xe). */
    LOW,

    /** Trung bình — spawn vừa (khoảng 2 giây/xe). */
    MEDIUM,

    /** Đông đúc — spawn nhanh (khoảng 1 giây/xe). */
    HIGH
}