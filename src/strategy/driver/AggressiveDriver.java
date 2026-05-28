package strategy.driver;

import model.trafficlight.LightColor;
import model.trafficlight.TrafficLight;
import model.vehicle.Vehicle;

import java.util.List;

public class AggressiveDriver
        implements DriverBehavior {

    @Override
    public boolean shouldStop(
            Vehicle self,
            List<Vehicle> vehicles,
            TrafficLight relevantLight
    ) {

        if (relevantLight.getColor()
                == LightColor.RED) {

            // [FIX N-06] Logic cũ: Math.random() > 0.4  = 60% dừng (SAI)
            // AggressiveDriver nên VƯỢT đèn nhiều hơn dừng.
            // Fix: Math.random() > 0.6  = chỉ 40% dừng, 60% vượt đèn đỏ.
            return Math.random() > 0.6;
        }

        return false;
    }

    @Override
    public int getSpeed() {
        return 7;
    }
}