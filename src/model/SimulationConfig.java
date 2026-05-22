package model;
import model.intersection.IntersectionType;

public class SimulationConfig {

    private IntersectionType intersectionType;

    private String trafficMode;

    private String lightType;

    private int vehicleCount;

    public SimulationConfig(
        IntersectionType intersectionType,
        String trafficMode,
        String lightType,
        int vehicleCount
) {

        this.intersectionType = intersectionType;
        this.trafficMode = trafficMode;
        this.lightType = lightType;
        this.vehicleCount = vehicleCount;
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

    public int getVehicleCount() {
        return vehicleCount;
    }
}