package com.yhtye.gongjiao.entity;

import java.util.List;

public class RoutesScheme {
    private int distance;
    private int duration;
    private List<String> vehicleNames;
    private int walkDistance;
    private List<SchemeSteps> steps;
    
    public int getDistance() {
        return distance;
    }
    public void setDistance(int distance) {
        this.distance = distance;
    }
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public List<String> getVehicleNames() {
        return vehicleNames;
    }
    public void setVehicleNames(List<String> vehicleNames) {
        this.vehicleNames = vehicleNames;
    }
    public int getWalkDistance() {
        return walkDistance;
    }
    public void setWalkDistance(int walkDistance) {
        this.walkDistance = walkDistance;
    }
    public List<SchemeSteps> getSteps() {
        return steps;
    }
    public void setSteps(List<SchemeSteps> steps) {
        this.steps = steps;
    }
}
