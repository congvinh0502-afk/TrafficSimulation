package system.emergency;

import java.util.List;
import model.vehicle.Vehicle;
import model.vehicle.Ambulance;
import model.vehicle.FireTruck;

public class EmergencyVehicleSystem {

    public void updateEmergencyVehicles(List<Vehicle> vehicles) {

    for (Vehicle vehicle : vehicles) {

        if (!(vehicle instanceof Ambulance)
                && !(vehicle instanceof FireTruck)) {
            continue;
        }

        // ép xe thường dừng lại nếu Emergency đang đến gần
        for (Vehicle other : vehicles) {

            if (other == vehicle) continue;
            if (other instanceof Ambulance) continue;
            if (other instanceof FireTruck) continue;

            double dx = vehicle.getX() - other.getX();
            double dy = vehicle.getY() - other.getY();
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist < 150 && other.getDirection() == vehicle.getDirection()) {
    other.setStopped(true);
}
        }
    }
}
}