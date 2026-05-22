package view.renderer;

import java.awt.Color;
import java.awt.Graphics2D;

import model.vehicle.Vehicle;

public class VehicleRenderer {

   public void render(Graphics2D g2d, Vehicle vehicle) {

    Graphics2D g =
            (Graphics2D) g2d.create();

    int x = (int) vehicle.getX();
    int y = (int) vehicle.getY();

    int w = (int) vehicle.getWidth();
    int h = (int) vehicle.getHeight();

    g.rotate(
            Math.toRadians(
                    vehicle.getAngle()
            ),
            x + w / 2,
            y + h / 2
    );

    if (vehicle instanceof model.vehicle.Ambulance) {

        g.setColor(Color.WHITE);

    } else if (vehicle instanceof model.vehicle.FireTruck) {

        g.setColor(Color.RED);

    } else {

        g.setColor(Color.BLUE);
    }

    g.fillRect(x, y, w, h);

    g.dispose();
}
    
}