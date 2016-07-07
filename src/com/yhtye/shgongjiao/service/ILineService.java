package com.yhtye.shgongjiao.service;

import java.util.List;

import com.yhtye.shgongjiao.entity.CarInfo;
import com.yhtye.shgongjiao.entity.LineInfo;
import com.yhtye.shgongjiao.entity.LineStationInfo;

public interface ILineService {

    LineInfo getLineInfo(String lineName, int retryTimes);

    LineStationInfo getLineStation(String lineName, String lineId);

    List<CarInfo> getStationCars(String lineName, String lineId, String stopId,
            boolean direction);

}
