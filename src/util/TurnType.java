package util;

/**
 * Loại rẽ mà phương tiện sẽ thực hiện khi qua giao lộ.
 *
 * <p>
 * Được gán ngẫu nhiên khi xe spawn và không thay đổi
 * trong suốt hành trình của xe đó.
 * </p>
 */
public enum TurnType {

    /** Đi thẳng qua giao lộ. */
    STRAIGHT,

    /** Rẽ trái. */
    LEFT,

    /** Rẽ phải. */
    RIGHT
}