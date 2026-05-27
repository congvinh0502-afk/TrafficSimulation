package view.renderer;

import java.awt.*;
import model.vehicle.Ambulance;
import model.vehicle.FireTruck;
import model.vehicle.Motorbike;
import model.vehicle.Vehicle;

public class VehicleRenderer {

    private boolean flash = false;

    private int flashCounter = 0;

    public void render(
            Graphics2D g2d,
            Vehicle vehicle
    ) {

        Graphics2D g =
                (Graphics2D) g2d.create();

        int x = (int) vehicle.getX();
        int y = (int) vehicle.getY();

        int w = (int) vehicle.getWidth();
        int h = (int) vehicle.getHeight();

        updateFlash();

        // xoay xe
        g.rotate(
                Math.toRadians(vehicle.getAngle()),
                x + w / 2.0,
                y + h / 2.0
        );

        // anti alias
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        // ===== MOTORBIKE =====
        if (vehicle instanceof Motorbike) {

            drawMotorbike(g, x, y, w, h);

        }

        // ===== AMBULANCE =====
        else if (vehicle instanceof Ambulance) {

            drawAmbulance(g, x, y, w, h);

        }

        // ===== FIRE TRUCK =====
        else if (vehicle instanceof FireTruck) {

            drawFireTruck(g, x, y, w, h);

        }

        // ===== NORMAL CAR =====
        else {

            drawCar(g, x, y, w, h);
        }

        g.dispose();
    }

    private void updateFlash() {

        flashCounter++;

        if (flashCounter >= 20) {

            flash = !flash;

            flashCounter = 0;
        }
    }

    // ================= CAR =================

    private void drawCar(
            Graphics2D g,
            int x,
            int y,
            int w,
            int h
    ) {

        // body
        g.setColor(new Color(220, 50, 50));

        g.fillRoundRect(
                x,
                y,
                w,
                h,
                12,
                12
        );

        // window
        g.setColor(new Color(170, 220, 255));

        g.fillRoundRect(
                x + 10,
                y + 4,
                w - 20,
                h - 8,
                8,
                8
        );

        drawWheels(g, x, y, w, h);

        drawLights(g, x, y, w, h);
    }

    // ================= AMBULANCE =================

    private void drawAmbulance(
            Graphics2D g,
            int x,
            int y,
            int w,
            int h
    ) {

        g.setColor(Color.WHITE);

        g.fillRoundRect(
                x,
                y,
                w,
                h,
                10,
                10
        );

        // red stripe
        g.setColor(Color.RED);

        g.fillRect(
                x,
                y + h / 2 - 3,
                w,
                6
        );

        // window
        g.setColor(new Color(170, 220, 255));

        g.fillRoundRect(
                x + 12,
                y + 4,
                w - 24,
                h - 8,
                8,
                8
        );

        // flash lights
        if (flash) {

            g.setColor(Color.BLUE);

        } else {

            g.setColor(Color.RED);
        }

        g.fillOval(
                x + w / 2 - 12,
                y + 2,
                10,
                10
        );

        g.fillOval(
                x + w / 2 + 2,
                y + 2,
                10,
                10
        );

        drawWheels(g, x, y, w, h);

        drawLights(g, x, y, w, h);
    }

    // ================= FIRE TRUCK =================

    private void drawFireTruck(
            Graphics2D g,
            int x,
            int y,
            int w,
            int h
    ) {

        g.setColor(new Color(200, 20, 20));

        g.fillRoundRect(
                x,
                y,
                w,
                h,
                10,
                10
        );

        // ladder
        g.setColor(Color.LIGHT_GRAY);

        g.fillRect(
                x + 15,
                y + h / 2 - 3,
                w - 30,
                6
        );

        // cabin
        g.setColor(new Color(170, 220, 255));

        g.fillRoundRect(
                x + 5,
                y + 4,
                18,
                h - 8,
                6,
                6
        );

        // siren
        if (flash) {

            g.setColor(Color.BLUE);

        } else {

            g.setColor(Color.RED);
        }

        g.fillOval(
                x + w / 2 - 5,
                y + 2,
                10,
                10
        );

        drawWheels(g, x, y, w, h);

        drawLights(g, x, y, w, h);
    }

    // ================= MOTORBIKE =================

    private void drawMotorbike(
            Graphics2D g,
            int x,
            int y,
            int w,
            int h
    ) {

        g.setColor(new Color(50, 50, 50));

        g.fillOval(x, y, 10, 10);

        g.fillOval(
                x + w - 10,
                y,
                10,
                10
        );

        g.setColor(Color.BLUE);

        g.fillRoundRect(
                x + 5,
                y + 2,
                w - 10,
                h - 4,
                6,
                6
        );
    }

    // ================= WHEELS =================

    private void drawWheels(
            Graphics2D g,
            int x,
            int y,
            int w,
            int h
    ) {

        g.setColor(Color.BLACK);

        g.fillRect(x + 5, y - 2, 8, 4);

        g.fillRect(
                x + 5,
                y + h - 2,
                8,
                4
        );

        g.fillRect(
                x + w - 13,
                y - 2,
                8,
                4
        );

        g.fillRect(
                x + w - 13,
                y + h - 2,
                8,
                4
        );
    }

    // ================= LIGHTS =================

    private void drawLights(
            Graphics2D g,
            int x,
            int y,
            int w,
            int h
    ) {

        // front
        g.setColor(Color.YELLOW);

        g.fillRect(
                x + w - 4,
                y + 4,
                4,
                5
        );

        g.fillRect(
                x + w - 4,
                y + h - 9,
                4,
                5
        );

        // rear
        g.setColor(Color.RED);

        g.fillRect(
                x,
                y + 4,
                4,
                5
        );

        g.fillRect(
                x,
                y + h - 9,
                4,
                5
        );
    }
}