package com.yhtye.shgongjiao.entity;

import java.util.List;

public class LineStationInfo {
    private List<StationInfo> trueDirection;
    private List<StationInfo> falseDirection;
    public List<StationInfo> getTrueDirection() {
        return trueDirection;
    }
    public void setTrueDirection(List<StationInfo> trueDirection) {
        this.trueDirection = trueDirection;
    }
    public List<StationInfo> getFalseDirection() {
        return falseDirection;
    }
    public void setFalseDirection(List<StationInfo> falseDirection) {
        this.falseDirection = falseDirection;
    }
}
