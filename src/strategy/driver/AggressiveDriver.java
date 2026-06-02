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

            // [FIX N-06] Logic cÅ©: Math.random() > 0.4  = 60% dá»«ng (SAI)
            // AggressiveDriver nÃªn VÆ¯á»¢T Ä‘Ã¨n nhiá»u hÆ¡n dá»«ng.
            // Fix: Math.random() > 0.6  = chá»‰ 40% dá»«ng, 60% vÆ°á»£t Ä‘Ã¨n Ä‘á».
            return Math.random() > 0.6;
        }

        return false;
    }

    @Override
    public int getSpeed() {
        return 7;
    }
}