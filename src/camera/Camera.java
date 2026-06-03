package camera;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.ScrollEvent;

/**
 * Camera 2D — quản lý pan (kéo màn hình) và zoom (phóng to/thu nhỏ).
 *
 * <p>Cách dùng trong game loop:
 * <pre>
 *   camera.applyTransform(gc);   // trước khi vẽ world
 *   // ... vẽ tất cả đối tượng thế giới ...
 *   camera.restoreTransform(gc); // sau khi vẽ xong
 *   // ... vẽ HUD (tọa độ màn hình) ...
 * </pre>
 * </p>
 *
 * <p>Chuột:
 * <ul>
 *   <li>Kéo (drag) → pan.</li>
 *   <li>Scroll → zoom vào điểm chuột.</li>
 *   <li>Double-click / Reset → về mặc định.</li>
 * </ul>
 * </p>
 */
public class Camera {

    // --------------------------------------------------------
    // Giới hạn zoom
    // --------------------------------------------------------
    public static final double MIN_ZOOM = 0.25;
    public static final double MAX_ZOOM = 4.0;
    private static final double ZOOM_STEP = 1.12;

    // --------------------------------------------------------
    // Trạng thái
    // --------------------------------------------------------
    private double offsetX  = 0;
    private double offsetY  = 0;
    private double zoom     = 1.0;

    // Trạng thái kéo
    private boolean dragging     = false;
    private double  dragStartX   = 0;
    private double  dragStartY   = 0;
    private double  dragOffsetX0 = 0;
    private double  dragOffsetY0 = 0;

    // --------------------------------------------------------
    // Áp dụng / khôi phục transform
    // --------------------------------------------------------

    /**
     * Áp dụng transform lên {@code GraphicsContext} trước khi vẽ.
     * Gọi {@link #restoreTransform} để hoàn tác.
     */
    public void applyTransform(GraphicsContext gc) {
        gc.save();
        gc.translate(offsetX, offsetY);
        gc.scale(zoom, zoom);
    }

    /**
     * Hoàn tác transform sau khi vẽ xong world.
     */
    public void restoreTransform(GraphicsContext gc) {
        gc.restore();
    }

    // --------------------------------------------------------
    // Chuyển đổi tọa độ
    // --------------------------------------------------------

    /** Tọa độ màn hình → tọa độ world X. */
    public double toWorldX(double screenX) {
        return (screenX - offsetX) / zoom;
    }

    /** Tọa độ màn hình → tọa độ world Y. */
    public double toWorldY(double screenY) {
        return (screenY - offsetY) / zoom;
    }

    /** Tọa độ world → tọa độ màn hình X. */
    public double toScreenX(double worldX) {
        return worldX * zoom + offsetX;
    }

    /** Tọa độ world → tọa độ màn hình Y. */
    public double toScreenY(double worldY) {
        return worldY * zoom + offsetY;
    }

    // --------------------------------------------------------
    // Pan (kéo)
    // --------------------------------------------------------

    /** Bắt đầu drag. Gọi khi MousePressed. */
    public void startDrag(double screenX, double screenY) {
        dragging     = true;
        dragStartX   = screenX;
        dragStartY   = screenY;
        dragOffsetX0 = offsetX;
        dragOffsetY0 = offsetY;
    }

    /** Tiếp tục drag. Gọi khi MouseDragged. */
    public void drag(double screenX, double screenY) {
        if (!dragging) return;
        offsetX = dragOffsetX0 + (screenX - dragStartX);
        offsetY = dragOffsetY0 + (screenY - dragStartY);
    }

    /** Kết thúc drag. */
    public void stopDrag() {
        dragging = false;
    }

    // --------------------------------------------------------
    // Zoom (cuộn chuột)
    // --------------------------------------------------------

    /**
     * Xử lý sự kiện cuộn — zoom vào điểm con trỏ.
     *
     * @param event sự kiện scroll JavaFX
     */
    public void handleScroll(ScrollEvent event) {
        double factor = event.getDeltaY() > 0 ? ZOOM_STEP : (1.0 / ZOOM_STEP);
        zoomAtPoint(factor, event.getX(), event.getY());
    }

    /**
     * Phóng to/thu nhỏ với tâm tại điểm màn hình (pivotX, pivotY).
     * Điểm world dưới con trỏ không dịch chuyển.
     */
    public void zoomAtPoint(double factor, double pivotX, double pivotY) {
        double newZoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoom * factor));
        if (newZoom == zoom) return;

        // Giữ world-point dưới con trỏ cố định
        double worldX = toWorldX(pivotX);
        double worldY = toWorldY(pivotY);

        zoom    = newZoom;
        offsetX = pivotX - worldX * zoom;
        offsetY = pivotY - worldY * zoom;
    }

    // --------------------------------------------------------
    // Reset
    // --------------------------------------------------------

    /** Đặt lại về trạng thái mặc định (zoom=1, offset=0). */
    public void reset() {
        offsetX = 0;
        offsetY = 0;
        zoom    = 1.0;
    }

    /**
     * Căn giữa màn hình vào điểm world (worldX, worldY).
     *
     * @param screenW chiều rộng màn hình
     * @param screenH chiều cao màn hình
     */
    public void centerOn(double worldX, double worldY, double screenW, double screenH) {
        offsetX = screenW / 2 - worldX * zoom;
        offsetY = screenH / 2 - worldY * zoom;
    }

    // --------------------------------------------------------
    // Getter
    // --------------------------------------------------------

    public double getOffsetX() { return offsetX; }
    public double getOffsetY() { return offsetY; }
    public double getZoom()    { return zoom; }
    public boolean isDragging(){ return dragging; }
}
