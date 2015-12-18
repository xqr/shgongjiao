package com.yhtye.gongjiao.entity;

/**
 * 换乘路线推荐
 *
 */
public class SchemeSteps {
    /**
     * 类型 5:步行；3:乘车
     */
    private int type;
    
    private String stepInstruction;
    /**
     * 路段距离    单位：米
     */
    private int distance;
    
    /**
     * 路段耗时    单位：秒
     */
    private int duration;
    
    private String vehicleName;
    
    private String vehicleStartName;
    
    private String vehicleEndName;
    
    private int vehicleStopNum;
    
    private int vehicleType;
    
    private String sname;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStepInstruction() {
        return stepInstruction;
    }

    public void setStepInstruction(String stepInstruction) {
        this.stepInstruction = stepInstruction;
    }

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

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getVehicleStartName() {
        return vehicleStartName;
    }

    public void setVehicleStartName(String vehicleStartName) {
        this.vehicleStartName = vehicleStartName;
    }

    public String getVehicleEndName() {
        return vehicleEndName;
    }

    public void setVehicleEndName(String vehicleEndName) {
        this.vehicleEndName = vehicleEndName;
    }

    public int getVehicleStopNum() {
        return vehicleStopNum;
    }

    public void setVehicleStopNum(int vehicleStopNum) {
        this.vehicleStopNum = vehicleStopNum;
    }

    public int getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(int vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }
}
