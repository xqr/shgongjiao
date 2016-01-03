package com.yhtye.gongjiao.entity;

import java.io.Serializable;

public class BusInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String stopId;
    private int order;
    private int busNum;
    private int arrived;
    
    public String getStopId() {
        return stopId;
    }
    public void setStopId(String stopId) {
        this.stopId = stopId;
    }
    public int getOrder() {
        return order;
    }
    public void setOrder(int order) {
        this.order = order;
    }
    public int getBusNum() {
        return busNum;
    }
    public void setBusNum(int busNum) {
        this.busNum = busNum;
    }
    public int getArrived() {
        return arrived;
    }
    public void setArrived(int arrived) {
        this.arrived = arrived;
    }
}
