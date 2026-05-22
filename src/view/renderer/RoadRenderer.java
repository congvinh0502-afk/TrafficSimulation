package view.renderer;

import java.awt.Color;
import java.awt.Graphics2D;

public class RoadRenderer {

    public void renderFourWay(Graphics2D g2d) {

        // đường dọc
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(300, 0, 200, 800);

        // đường ngang
        g2d.fillRect(0, 300, 1200, 200);

        // lane line dọc
        g2d.setColor(Color.WHITE);

        for (int i = 0; i < 800; i += 40) {

            g2d.fillRect(395, i, 10, 20);
        }

        // lane line ngang
        for (int i = 0; i < 1200; i += 40) {

            g2d.fillRect(i, 395, 20, 10);
        }
    }
    public void renderThreeWay(Graphics2D g2d) {

    g2d.setColor(Color.DARK_GRAY);

    // dọc

    g2d.fillRect(300, 0, 200, 800);

    // ngang trái

    g2d.fillRect(0, 300, 500, 200);
}
    public void renderFiveWay(Graphics2D g2d) {

    renderFourWay(g2d);

    Graphics2D g = (Graphics2D) g2d.create();

    g.rotate(
            Math.toRadians(45),
            600,
            400
    );

    g.fillRect(500, 350, 400, 120);

    g.dispose();
}
}