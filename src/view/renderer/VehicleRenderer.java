package view.renderer;

import model.vehicle.Ambulance;
import model.vehicle.Bicycle;
import model.vehicle.FireTruck;
import model.vehicle.Motorbike;
import model.vehicle.Vehicle;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Renderer phương tiện — vẽ hình ảnh xe xoay theo góc di chuyển.
 *
 * <p>
 * Phương thức {@link #render} xoay hệ tọa độ theo góc của xe
 * trước khi vẽ, nên các phương thức vẽ nội bộ không cần
 * xử lý góc — luôn vẽ xe hướng sang phải (EAST) như mặc định.
 * </p>
 *
 * <p>
 * Trạng thái {@code flash} (đèn nhấp nháy xe ưu tiên) được
 * truyền từ {@code SimulationPanel} — chỉ toggle một lần mỗi
 * {@link config.Constants#FLASH_INTERVAL_FRAMES} frame.
 * </p>
 */
public class VehicleRenderer {

    /**
     * Vẽ một phương tiện với góc xoay và kiểu dáng tương ứng loại xe.
     *
     * @param g2d     đối tượng đồ họa
     * @param vehicle xe cần vẽ
     * @param flash   trạng thái nhấp nháy hiện tại (true = đèn xanh, false = đèn
     *                đỏ)
     */
    public void render(Graphics2D g2d, Vehicle vehicle, boolean flash) {
        Graphics2D g = (Graphics2D) g2d.create();

        int x = (int) vehicle.getX();
        int y = (int) vehicle.getY();
        int w = (int) vehicle.getWidth();
        int h = (int) vehicle.getHeight();

        // Xoay hệ tọa độ quanh tâm xe
        g.rotate(Math.toRadians(vehicle.getAngle()), x + w / 2.0, y + h / 2.0);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (vehicle instanceof Ambulance)
            drawAmbulance(g, x, y, w, h, flash);
        else if (vehicle instanceof FireTruck)
            drawFireTruck(g, x, y, w, h, flash);
        else if (vehicle instanceof Motorbike)
            drawMotorbike(g, x, y, w, h);
        else if (vehicle instanceof Bicycle)
            drawBicycle(g, x, y, w, h);
        else
            drawCar(g, x, y, w, h);

        g.dispose();
    }

    // ==========================================================
    // Vẽ từng loại xe
    // ==========================================================

    /** Vẽ ô tô màu xanh với cabin kính. */
    private void drawCar(Graphics2D g, int x, int y, int w, int h) {
        // Thân xe
        g.setColor(new Color(40, 120, 220));
        g.fillRoundRect(x, y + 4, w, h - 8, 14, 14);

        // Cabin kính
        g.setColor(new Color(180, 230, 255));
        g.fillRoundRect(x + 10, y + 6, w - 20, h - 12, 10, 10);

        // Viền thân
        g.setColor(Color.BLACK);
        g.drawRoundRect(x, y + 4, w, h - 8, 14, 14);

        drawWheels(g, x, y, w, h);
        drawHeadAndTailLights(g, x, y, w, h);
    }

    /**
     * Vẽ xe cứu thương trắng với sọc đỏ và đèn ưu tiên nhấp nháy.
     *
     * @param flash trạng thái đèn ưu tiên (xanh / đỏ xen kẽ)
     */
    private void drawAmbulance(Graphics2D g, int x, int y, int w, int h, boolean flash) {
        // Thân
        g.setColor(Color.WHITE);
        g.fillRoundRect(x, y, w, h, 10, 10);

        // Sọc đỏ ngang thân
        g.setColor(Color.RED);
        g.fillRect(x, y + h / 2 - 3, w, 6);

        // Kính
        g.setColor(new Color(150, 220, 255));
        g.fillRoundRect(x + 12, y + 4, w - 24, h - 8, 8, 8);

        // Dấu cộng y tế
        g.setColor(Color.RED);
        int cx = x + w / 2, cy = y + h / 2;
        g.fillRect(cx - 2, cy - 8, 4, 16);
        g.fillRect(cx - 8, cy - 2, 16, 4);

        // Đèn ưu tiên nhấp nháy
        g.setColor(flash ? Color.BLUE : Color.RED);
        g.fillRect(x + w / 2 - 14, y + 2, 10, 4);
        g.fillRect(x + w / 2 + 4, y + 2, 10, 4);

        drawWheels(g, x, y, w, h);
        drawHeadAndTailLights(g, x, y, w, h);
    }

    /**
     * Vẽ xe cứu hỏa đỏ với thang và đèn ưu tiên.
     *
     * @param flash trạng thái đèn ưu tiên
     */
    private void drawFireTruck(Graphics2D g, int x, int y, int w, int h, boolean flash) {
        // Thân chính
        g.setColor(new Color(190, 20, 20));
        g.fillRoundRect(x, y, w, h, 10, 10);

        // Cabin
        g.setColor(new Color(120, 200, 255));
        g.fillRoundRect(x + 5, y + 4, 20, h - 8, 6, 6);

        // Thang
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(x + 25, y + h / 2 - 2, w - 35, 4);

        // Đèn ưu tiên
        g.setColor(flash ? Color.BLUE : Color.RED);
        g.fillRect(x + w / 2 - 5, y + 2, 10, 4);

        drawWheels(g, x, y, w, h);
        drawHeadAndTailLights(g, x, y, w, h);
    }

    /** Vẽ xe máy nhỏ gọn. */
    private void drawMotorbike(Graphics2D g, int x, int y, int w, int h) {
        // Bánh xe
        g.setColor(Color.BLACK);
        g.fillOval(x, y + h - 10, 10, 10);
        g.fillOval(x + w - 10, y + h - 10, 10, 10);

        // Thân
        g.setColor(new Color(30, 144, 255));
        g.fillRoundRect(x + 6, y + h / 2 - 3, w - 12, 6, 6, 6);

        // Đầu xe (tay lái)
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x + w - 10, y + h / 2 - 6, 4, 8);

        // Yên xe
        g.setColor(Color.GRAY);
        g.fillRect(x + 10, y + h / 2 - 6, 10, 4);
    }

    /** Vẽ xe đạp với khung và bánh xe. */
    private void drawBicycle(Graphics2D g, int x, int y, int w, int h) {
        // Bánh xe
        g.setColor(Color.BLACK);
        g.drawOval(x, y + h - 12, 12, 12);
        g.drawOval(x + w - 12, y + h - 12, 12, 12);

        // Khung xe
        g.setColor(new Color(50, 180, 50));
        g.drawLine(x + 6, y + h - 6, x + w / 2, y + h / 2);
        g.drawLine(x + w / 2, y + h / 2, x + w - 6, y + h - 6);
        g.drawLine(x + 6, y + h - 6, x + w - 6, y + h - 6);

        // Tay lái
        g.drawLine(x + w / 2, y + h / 2, x + w / 2 + 6, y + h / 2 - 6);
    }

    // ==========================================================
    // Thành phần dùng chung
    // ==========================================================

    /** Vẽ 4 bánh xe quanh thân xe. */
    private void drawWheels(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(Color.BLACK);
        g.fillRoundRect(x + 5, y - 2, 8, 4, 4, 4);
        g.fillRoundRect(x + 5, y + h - 2, 8, 4, 4, 4);
        g.fillRoundRect(x + w - 13, y - 2, 8, 4, 4, 4);
        g.fillRoundRect(x + w - 13, y + h - 2, 8, 4, 4, 4);
    }

    /** Vẽ đèn pha (vàng, phía trước) và đèn hậu (đỏ, phía sau). */
    private void drawHeadAndTailLights(Graphics2D g, int x, int y, int w, int h) {
        // Đèn pha trước
        g.setColor(Color.YELLOW);
        g.fillRect(x + w - 3, y + 4, 3, 5);
        g.fillRect(x + w - 3, y + h - 9, 3, 5);

        // Đèn hậu
        g.setColor(Color.RED);
        g.fillRect(x, y + 4, 3, 5);
        g.fillRect(x, y + h - 9, 3, 5);
    }
}