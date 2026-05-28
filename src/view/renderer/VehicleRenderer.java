package view.renderer;

import java.awt.*;
import model.vehicle.Ambulance;
import model.vehicle.Bicycle;
import model.vehicle.FireTruck;
import model.vehicle.Motorbike;
import model.vehicle.Vehicle;

public class VehicleRenderer {

    public void render(
            Graphics2D g2d,
            Vehicle vehicle,
            boolean flash
    ) {

        Graphics2D g = (Graphics2D) g2d.create();

        int x = (int) vehicle.getX();
        int y = (int) vehicle.getY();

        int w = (int) vehicle.getWidth();
        int h = (int) vehicle.getHeight();

        g.rotate(
                Math.toRadians(vehicle.getAngle()),
                x + w / 2.0,
                y + h / 2.0
        );

        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        if (vehicle instanceof Ambulance) {

            drawAmbulance(g, x, y, w, h, flash);

        } else if (vehicle instanceof FireTruck) {

            drawFireTruck(g, x, y, w, h, flash);

        } else if (vehicle instanceof Motorbike) {

            drawMotorbike(g, x, y, w, h);

        } else if (vehicle instanceof Bicycle) {

            drawBicycle(g, x, y, w, h);

        } else {

            drawCar(g, x, y, w, h);
        }

        g.dispose();
    }

    // =========================================================
    // CAR
    // =========================================================

    private void drawCar(Graphics2D g, int x, int y, int w, int h) {

    // thân xe
    g.setColor(new Color(40, 120, 220));
    g.fillRoundRect(x, y + 4, w, h - 8, 14, 14);

    // cabin kính
    g.setColor(new Color(180, 230, 255));
    g.fillRoundRect(x + 10, y + 6, w - 20, h - 12, 10, 10);

    // viền
    g.setColor(Color.BLACK);
    g.drawRoundRect(x, y + 4, w, h - 8, 14, 14);

    drawWheels(g, x, y, w, h);
    drawLights(g, x, y, w, h);
}

    // =========================================================
    // AMBULANCE
    // =========================================================

    private void drawAmbulance(
            Graphics2D g,
            int x,
            int y,
            int w,
            int h,
            boolean flash
    ) {

        // thân
        g.setColor(Color.WHITE);
        g.fillRoundRect(x, y, w, h, 10, 10);

        // sọc đỏ
        g.setColor(Color.RED);
        g.fillRect(x, y + h / 2 - 3, w, 6);

        // kính
        g.setColor(new Color(150, 220, 255));
        g.fillRoundRect(x + 12, y + 4, w - 24, h - 8, 8, 8);

        // dấu +
        g.setColor(Color.RED);

        int cx = x + w / 2;
        int cy = y + h / 2;

        g.fillRect(cx - 2, cy - 8, 4, 16);
        g.fillRect(cx - 8, cy - 2, 16, 4);

        // đèn ưu tiên hình chữ nhật ngang
        g.setColor(flash ? Color.BLUE : Color.RED);

        g.fillRect(x + w / 2 - 14, y + 2, 10, 4);
        g.fillRect(x + w / 2 + 4, y + 2, 10, 4);

        drawWheels(g, x, y, w, h);
        drawLights(g, x, y, w, h);
    }

    // =========================================================
    // FIRE TRUCK
    // =========================================================

    private void drawFireTruck(
            Graphics2D g,
            int x,
            int y,
            int w,
            int h,
            boolean flash
    ) {

        // thân chính
        g.setColor(new Color(190, 20, 20));
        g.fillRoundRect(x, y, w, h, 10, 10);

        // cabin
        g.setColor(new Color(120, 200, 255));
        g.fillRoundRect(x + 5, y + 4, 20, h - 8, 6, 6);

        // thang
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(x + 25, y + h / 2 - 2, w - 35, 4);

        // đèn
        g.setColor(flash ? Color.BLUE : Color.RED);
        g.fillRect(x + w / 2 - 5, y + 2, 10, 4);

        drawWheels(g, x, y, w, h);
        drawLights(g, x, y, w, h);
    }

    // =========================================================
    // MOTORBIKE
    // =========================================================

    private void drawMotorbike(Graphics2D g, int x, int y, int w, int h) {

        // bánh
        g.setColor(Color.BLACK);

        g.fillOval(x, y + h - 10, 10, 10);
        g.fillOval(x + w - 10, y + h - 10, 10, 10);

        // thân
        g.setColor(new Color(30, 144, 255));
        g.fillRoundRect(x + 6, y + h / 2 - 3, w - 12, 6, 6, 6);

        // đầu xe
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x + w - 10, y + h / 2 - 6, 4, 8);

        // yên xe
        g.setColor(Color.GRAY);
        g.fillRect(x + 10, y + h / 2 - 6, 10, 4);
    }

    // =========================================================
    // BICYCLE
    // =========================================================

    private void drawBicycle(Graphics2D g, int x, int y, int w, int h) {

        // bánh xe
        g.setColor(Color.BLACK);

        g.drawOval(x, y + h - 12, 12, 12);
        g.drawOval(x + w - 12, y + h - 12, 12, 12);

        // khung
        g.setColor(new Color(50, 180, 50));

        g.drawLine(x + 6, y + h - 6, x + w / 2, y + h / 2);
        g.drawLine(x + w / 2, y + h / 2, x + w - 6, y + h - 6);
        g.drawLine(x + 6, y + h - 6, x + w - 6, y + h - 6);

        // tay lái
        g.drawLine(x + w / 2, y + h / 2, x + w / 2 + 6, y + h / 2 - 6);
    }

    // =========================================================
    // WHEELS
    // =========================================================

    private void drawWheels(Graphics2D g, int x, int y, int w, int h) {

        g.setColor(Color.BLACK);

        g.fillRoundRect(x + 5, y - 2, 8, 4, 4, 4);
        g.fillRoundRect(x + 5, y + h - 2, 8, 4, 4, 4);

        g.fillRoundRect(x + w - 13, y - 2, 8, 4, 4, 4);
        g.fillRoundRect(x + w - 13, y + h - 2, 8, 4, 4, 4);
    }

    // =========================================================
    // LIGHTS
    // =========================================================

    private void drawLights(Graphics2D g, int x, int y, int w, int h) {

        // đèn trước
        g.setColor(Color.YELLOW);

        g.fillRect(x + w - 3, y + 4, 3, 5);
        g.fillRect(x + w - 3, y + h - 9, 3, 5);

        // đèn sau
        g.setColor(Color.RED);

        g.fillRect(x, y + 4, 3, 5);
        g.fillRect(x, y + h - 9, 3, 5);
    }
}