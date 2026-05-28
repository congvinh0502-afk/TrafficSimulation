package view.renderer;

import java.awt.*;
import java.util.Random;

public class ParkingRenderer {
    private final Color[] carColors = {
        new Color(40, 115, 210), new Color(230, 185, 15), 
        new Color(45, 155, 85), new Color(225, 225, 230), 
        new Color(35, 35, 40), new Color(235, 95, 30)
    };

    public void drawParkingLotWithRealCars(Graphics2D g2d, int x, int y, int w, int h) {
        g2d.setColor(new Color(75, 75, 80));
        g2d.fillRect(x, y, w, h);
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawRect(x, y, w, h);

        int slotWidth = 45; int slotHeight = 65; int index = 0;
        Random colorPicker = new Random((long) x * y);

        for (int currX = x + 15; currX < x + w - slotWidth; currX += slotWidth + 10) {
            g2d.setColor(new Color(255, 255, 255, 180));
            g2d.drawRect(currX, y + 5, slotWidth, slotHeight);
            g2d.drawRect(currX, y + h - slotHeight - 5, slotWidth, slotHeight);

            Color topCarColor = carColors[colorPicker.nextInt(carColors.length)];
            Color bottomCarColor = carColors[colorPicker.nextInt(carColors.length)];

            if (index % 2 == 0) {
                drawParkedCar(g2d, currX + (slotWidth - 24) / 2, y + 5 + (slotHeight - 38) / 2, topCarColor, true);
            }
            if (index % 3 == 0 || index == 1) {
                drawParkedCar(g2d, currX + (slotWidth - 24) / 2, (y + h - slotHeight - 5) + (slotHeight - 38) / 2, bottomCarColor, false);
            }
            index++;
        }
    }

    private void drawParkedCar(Graphics2D g, int x, int y, Color carColor, boolean headingSouth) {
        int carW = 24; int carH = 38;
        g.setColor(Color.BLACK);
        g.fillRoundRect(x - 2, y + 5, 4, 8, 2, 2);       
        g.fillRoundRect(x + carW - 2, y + 5, 4, 8, 2, 2); 
        g.fillRoundRect(x - 2, y + carH - 13, 4, 8, 2, 2); 
        g.fillRoundRect(x + carW - 2, y + carH - 13, 4, 8, 2, 2); 

        g.setColor(carColor);
        g.fillRoundRect(x, y, carW, carH, 6, 6);
        g.setColor(carColor.darker());
        g.drawRoundRect(x, y, carW, carH, 6, 6);

        g.setColor(new Color(50, 50, 60)); 
        if (headingSouth) {
            g.fillRect(x + 3, y + carH - 15, carW - 6, 8); 
            g.setColor(new Color(255, 255, 255, 200));   
            g.fillRect(x + 3, y + carH - 2, 5, 2);
            g.fillRect(x + carW - 8, y + carH - 2, 5, 2);
        } else {
            g.fillRect(x + 3, y + 8, carW - 6, 8);
            g.setColor(new Color(255, 255, 255, 200));   
            g.fillRect(x + 3, y, 5, 2);
            g.fillRect(x + carW - 8, y, 5, 2);
        }
    }
}