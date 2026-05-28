package view.renderer;

import java.awt.*;

public class BaseRenderer {
    
    public void drawGrass(Graphics2D g2d) {
        g2d.setColor(new Color(65, 142, 65));
        g2d.fillRect(0, 0, 1200, 800);
        g2d.setColor(new Color(58, 132, 58));
        for (int i = 0; i < 1200; i += 50) {
            for (int j = 0; j < 800; j += 50) {
                if ((i + j) % 3 == 0) g2d.fillOval(i + 15, j + 10, 6, 4);
            }
        }
    }

    public void drawStreetLights(Graphics2D g2d) {
        int[][] lightPositions = {{250, 250}, {550, 250}, {250, 550}, {550, 550}};
        for (int[] pos : lightPositions) {
            g2d.setColor(new Color(255, 255, 150, 45));
            g2d.fillOval(pos[0] - 20, pos[1] - 20, 50, 50);
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillOval(pos[0] - 4, pos[1] - 4, 8, 8);
            g2d.setColor(Color.WHITE);
            g2d.fillOval(pos[0] - 2, pos[1] - 2, 4, 4);
        }
    }
}