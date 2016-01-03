package com.yhtye.gongjiao.entity;

import java.io.Serializable;

public class LineInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String firstTime;
    private String lastTime;
    private int stopsNum;
    private String startStopName;
    private String endStopName;
    private String lineId;
    private String lineName;
    private String lineNo;
    private int direction;
    
    public String getFirstTime() {
        return firstTime;
    }
    public void setFirstTime(String firstTime) {
        this.firstTime = firstTime;
    }
    public String getLastTime() {
        return lastTime;
    }
    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }
    public int getStopsNum() {
        return stopsNum;
    }
    public void setStopsNum(int stopsNum) {
        this.stopsNum = stopsNum;
    }
    public String getStartStopName() {
        return startStopName;
    }
    public void setStartStopName(String startStopName) {
        this.startStopName = startStopName;
    }
    public String getEndStopName() {
        return endStopName;
    }
    public void setEndStopName(String endStopName) {
        this.endStopName = endStopName;
    }
    public String getLineId() {
        return lineId;
    }
    public void setLineId(String lineId) {
        this.lineId = lineId;
    }
    public String getLineName() {
        return lineName;
    }
    public void setLineName(String lineName) {
        this.lineName = lineName;
    }
    public String getLineNo() {
        return lineNo;
    }
    public void setLineNo(String lineNo) {
        this.lineNo = lineNo;
    }
    public int getDirection() {
        return direction;
    }
    public void setDirection(int direction) {
        this.direction = direction;
    }
}
