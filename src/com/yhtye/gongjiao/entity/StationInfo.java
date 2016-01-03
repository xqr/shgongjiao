package com.yhtye.gongjiao.entity;

import java.io.Serializable;

public class StationInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String stopId;
    private String stopName;
    private String stopNo;
    private double weidu;
    private double jingdu;
    private int order; 
    
    public StationInfo() {
        
    }

    public String getStopId() {
        return stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public String getStopNo() {
        return stopNo;
    }

    public void setStopNo(String stopNo) {
        this.stopNo = stopNo;
    }

    public double getWeidu() {
        return weidu;
    }

    public void setWeidu(double weidu) {
        this.weidu = weidu;
    }

    public double getJingdu() {
        return jingdu;
    }

    public void setJingdu(double jingdu) {
        this.jingdu = jingdu;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
