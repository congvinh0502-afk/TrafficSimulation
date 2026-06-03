package view.renderer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.vehicle.*;

/**
 * Renderer phương tiện (JavaFX).
 *
 * <p>Trước khi vẽ, xoay hệ tọa độ theo góc của xe.
 * Tất cả phương thức vẽ nội bộ vẽ xe hướng EAST (mặc định).</p>
 */
public class VehicleRenderer {

    public void render(GraphicsContext gc, Vehicle vehicle, boolean flash) {
        double x = vehicle.getX();
        double y = vehicle.getY();
        double w = vehicle.getWidth();
        double h = vehicle.getHeight();
        double cx = x + w / 2.0;
        double cy = y + h / 2.0;

        gc.save();
        gc.translate(cx, cy);
        gc.rotate(vehicle.getAngle()); // JavaFX dùng độ trực tiếp
        gc.translate(-cx, -cy);

        if      (vehicle instanceof Ambulance)  drawAmbulance(gc, x, y, (int)w, (int)h, flash);
        else if (vehicle instanceof FireTruck)  drawFireTruck(gc, x, y, (int)w, (int)h, flash);
        else if (vehicle instanceof Motorbike)  drawMotorbike(gc, x, y, (int)w, (int)h);
        else if (vehicle instanceof Bicycle)    drawBicycle(gc, x, y, (int)w, (int)h);
        else                                    drawCar(gc, x, y, (int)w, (int)h);

        gc.restore();
    }

    // --------------------------------------------------------
    // Ô tô
    // --------------------------------------------------------
    private void drawCar(GraphicsContext gc, double x, double y, int w, int h) {
        gc.setFill(Color.rgb(40, 120, 220));
        gc.fillRoundRect(x, y + 4, w, h - 8, 14, 14);

        gc.setFill(Color.rgb(180, 230, 255));
        gc.fillRoundRect(x + 10, y + 6, w - 20, h - 12, 10, 10);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeRoundRect(x, y + 4, w, h - 8, 14, 14);

        drawWheels(gc, x, y, w, h);
        drawHeadTailLights(gc, x, y, w, h);
    }

    // --------------------------------------------------------
    // Xe cứu thương
    // --------------------------------------------------------
    private void drawAmbulance(GraphicsContext gc, double x, double y, int w, int h, boolean flash) {
        gc.setFill(Color.WHITE);
        gc.fillRoundRect(x, y, w, h, 10, 10);

        gc.setFill(Color.RED);
        gc.fillRect(x, y + h / 2.0 - 3, w, 6);

        gc.setFill(Color.rgb(150, 220, 255));
        gc.fillRoundRect(x + 12, y + 4, w - 24, h - 8, 8, 8);

        // Dấu cộng y tế
        gc.setFill(Color.RED);
        double mcx = x + w / 2.0, mcy = y + h / 2.0;
        gc.fillRect(mcx - 2, mcy - 8, 4, 16);
        gc.fillRect(mcx - 8, mcy - 2, 16, 4);

        // Đèn ưu tiên nhấp nháy
        gc.setFill(flash ? Color.BLUE : Color.RED);
        gc.fillRect(mcx - 14, y + 2, 10, 4);
        gc.fillRect(mcx + 4,  y + 2, 10, 4);

        drawWheels(gc, x, y, w, h);
        drawHeadTailLights(gc, x, y, w, h);
    }

    // --------------------------------------------------------
    // Xe cứu hỏa
    // --------------------------------------------------------
    private void drawFireTruck(GraphicsContext gc, double x, double y, int w, int h, boolean flash) {
        gc.setFill(Color.rgb(190, 20, 20));
        gc.fillRoundRect(x, y, w, h, 10, 10);

        gc.setFill(Color.rgb(120, 200, 255));
        gc.fillRoundRect(x + 5, y + 4, 20, h - 8, 6, 6);

        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(x + 25, y + h / 2.0 - 2, w - 35, 4);

        gc.setFill(flash ? Color.BLUE : Color.RED);
        gc.fillRect(x + w / 2.0 - 5, y + 2, 10, 4);

        drawWheels(gc, x, y, w, h);
        drawHeadTailLights(gc, x, y, w, h);
    }

    // --------------------------------------------------------
    // Xe máy
    // --------------------------------------------------------
    private void drawMotorbike(GraphicsContext gc, double x, double y, int w, int h) {
        gc.setFill(Color.BLACK);
        gc.fillOval(x, y + h - 10, 10, 10);
        gc.fillOval(x + w - 10, y + h - 10, 10, 10);

        gc.setFill(Color.rgb(30, 144, 255));
        gc.fillRoundRect(x + 6, y + h / 2.0 - 3, w - 12, 6, 6, 6);

        gc.setFill(Color.DARKGRAY);
        gc.fillRect(x + w - 10, y + h / 2.0 - 6, 4, 8);

        gc.setFill(Color.GRAY);
        gc.fillRect(x + 10, y + h / 2.0 - 6, 10, 4);
    }

    // --------------------------------------------------------
    // Xe đạp
    // --------------------------------------------------------
    private void drawBicycle(GraphicsContext gc, double x, double y, int w, int h) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.5);
        gc.strokeOval(x, y + h - 12, 12, 12);
        gc.strokeOval(x + w - 12, y + h - 12, 12, 12);

        gc.setStroke(Color.rgb(50, 180, 50));
        gc.strokeLine(x + 6, y + h - 6, x + w / 2.0, y + h / 2.0);
        gc.strokeLine(x + w / 2.0, y + h / 2.0, x + w - 6, y + h - 6);
        gc.strokeLine(x + 6, y + h - 6, x + w - 6, y + h - 6);
        gc.strokeLine(x + w / 2.0, y + h / 2.0, x + w / 2.0 + 6, y + h / 2.0 - 6);
        gc.setLineWidth(1);
    }

    // --------------------------------------------------------
    // Thành phần chung
    // --------------------------------------------------------
    private void drawWheels(GraphicsContext gc, double x, double y, int w, int h) {
        gc.setFill(Color.BLACK);
        gc.fillRoundRect(x + 5, y - 2, 8, 4, 4, 4);
        gc.fillRoundRect(x + 5, y + h - 2, 8, 4, 4, 4);
        gc.fillRoundRect(x + w - 13, y - 2, 8, 4, 4, 4);
        gc.fillRoundRect(x + w - 13, y + h - 2, 8, 4, 4, 4);
    }

    private void drawHeadTailLights(GraphicsContext gc, double x, double y, int w, int h) {
        gc.setFill(Color.YELLOW);
        gc.fillRect(x + w - 3, y + 4, 3, 5);
        gc.fillRect(x + w - 3, y + h - 9, 3, 5);

        gc.setFill(Color.RED);
        gc.fillRect(x, y + 4, 3, 5);
        gc.fillRect(x, y + h - 9, 3, 5);
    }
}
