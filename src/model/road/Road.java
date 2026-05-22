package model.road;

import java.util.ArrayList;
import java.util.List;

public class Road {

    private List<Lane> lanes;

    public Road() {

        lanes = new ArrayList<>();
    }

    public void addLane(Lane lane) {

        lanes.add(lane);
    }

    public List<Lane> getLanes() {

        return lanes;
    }
}