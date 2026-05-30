package util;

/**
 * Làn đường mà một phương tiện đang đi.
 *
 * <p>
 * LEFT và RIGHT được xác định theo chiều di chuyển của xe,
 * không phải theo hướng nhìn của người quan sát.
 * </p>
 */
public enum Lane {

    /** Làn bên trái theo chiều di chuyển. */
    LEFT,

    /** Làn bên phải theo chiều di chuyển (mặc định khi spawn). */
    RIGHT
}