package model;
import model.intersection.IntersectionType;
import util.TrafficDensity;

public class SimulationConfig {

    private IntersectionType intersectionType;

    private String trafficMode;

    private String lightType;

    //private int vehicleCount;
    private TrafficDensity trafficDensity;

    public SimulationConfig(
        IntersectionType intersectionType,
        String trafficMode,
        String lightType,
        //int vehicleCount,
        TrafficDensity trafficDensity
) {

        this.intersectionType = intersectionType;
        this.trafficMode = trafficMode;
        this.lightType = lightType;
        //this.vehicleCount = vehicleCount;
        this.trafficDensity = trafficDensity;
    }

    public IntersectionType getIntersectionType() {
    return intersectionType;
}

    public String getTrafficMode() {
        return trafficMode;
    }

    public String getLightType() {
        return lightType;
    }

    /*public int getVehicleCount() {
        return vehicleCount;
    }*/
    public TrafficDensity getTrafficDensity() {

    return trafficDensity;
}
}