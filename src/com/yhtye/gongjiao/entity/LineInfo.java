package com.yhtye.gongjiao.entity;

import java.io.Serializable;
import java.util.List;

public class LineInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String start_earlytime;
    private String end_latetime;
    private String start_stop;
    private String end_stop;
    private String line_id;
    private String line_name;
    private String fangxiang;
    // 停靠站信息
    private List<StationInfo> stations;
    
    public String getEnd_latetime() {
        return end_latetime;
    }
    public void setEnd_latetime(String end_latetime) {
        this.end_latetime = end_latetime;
    }
    public String getEnd_stop() {
        return end_stop;
    }
    public void setEnd_stop(String end_stop) {
        this.end_stop = end_stop;
    }
    public String getLine_id() {
        return line_id;
    }
    public void setLine_id(String line_id) {
        this.line_id = line_id;
    }
    public String getLine_name() {
        return line_name;
    }
    public void setLine_name(String line_name) {
        this.line_name = line_name;
    }
    public String getStart_earlytime() {
        return start_earlytime;
    }
    public void setStart_earlytime(String start_earlytime) {
        this.start_earlytime = start_earlytime;
    }
    public String getStart_stop() {
        return start_stop;
    }
    public void setStart_stop(String start_stop) {
        this.start_stop = start_stop;
    }
    public String getFangxiang() {
        return fangxiang;
    }
    public void setFangxiang(String fangxiang) {
        this.fangxiang = fangxiang;
    }
    public List<StationInfo> getStations() {
        return stations;
    }
    public void setStations(List<StationInfo> stations) {
        this.stations = stations;
    }
}
