package com.yhtye.gongjiao.entity;

import java.io.Serializable;
import java.util.List;

public class BusLineInfo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private LineInfo line;
    private List<StationInfo> stops;
    private List<BusInfo> bus;
    private String searchLineName;
    
    public LineInfo getLine() {
        return line;
    }
    public void setLine(LineInfo line) {
        this.line = line;
    }
    public List<StationInfo> getStops() {
        return stops;
    }
    public void setStops(List<StationInfo> stops) {
        this.stops = stops;
    }
    public List<BusInfo> getBus() {
        return bus;
    }
    public void setBus(List<BusInfo> bus) {
        this.bus = bus;
    }
    public String getSearchLineName() {
        return searchLineName;
    }
    public void setSearchLineName(String searchLineName) {
        this.searchLineName = searchLineName;
    }
}
