package com.yhtye.shgongjiao.entity;

import java.io.Serializable;
import java.util.List;

public class LineStationInfo implements Serializable {
    private static final long serialVersionUID = 1L;
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
