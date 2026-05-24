package view.renderer;

import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;
import model.vehicle.Ambulance;
import model.vehicle.FireTruck;
import model.vehicle.Vehicle;

public class VehicleRenderer {

    private Image carImage;
    private Image ambulanceImage;
    private Image fireTruckImage;

public VehicleRenderer() {

        // Đã sửa lại đường dẫn từ "/assets/image/..." thành "/image/..."
        carImage =
                new ImageIcon(
                        getClass().getResource("/image/Car.png")
                ).getImage();

        ambulanceImage =
                new ImageIcon(
                        getClass().getResource("/image/Ambulance.png")
                ).getImage();

        fireTruckImage =
                new ImageIcon(
                        getClass().getResource("/image/FireTruck.png")
                ).getImage();
    }

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

        g.rotate(
                Math.toRadians(
                        vehicle.getAngle()
                ),
                x + w / 2,
                y + h / 2
        );

        Image image;

        if (vehicle instanceof Ambulance) {

            image = ambulanceImage;

        } else if (vehicle instanceof FireTruck) {

            image = fireTruckImage;

        } else {

            image = carImage;
        }

        g.drawImage(
                image,
                x,
                y,
                w,
                h,
                null
        );

        g.dispose();
    }
}