package com.yhtye.shgongjiao.tools;

import com.yhtye.shgongjiao.entity.PositionInfo;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetUtil {
    /**
     * 检查用户设备联网情况
     * 
     * @param context
     * @return
     */
    public static boolean checkNet(Context context) {
        // 获取手机所以连接管理对象（包括wi-fi，net等连接的管理）
        ConnectivityManager conn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conn != null) {
            // 网络管理连接对象
            NetworkInfo info = conn.getActiveNetworkInfo();
            
            if(info != null && info.isConnected()) {
                // 判断当前网络是否连接
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }

        }
        return false;
    }
    
    /**
     * 通过GPS定位
     * 
     * @param context
     * @return
     */
    public static PositionInfo checkGps(Context context) {
        LocationManager locationManager = (LocationManager)context
                .getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            return null;
        }
        if (locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
          //根据当前provider对象获取最后一次位置信息
            Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            //如果位置信息为null，则请求更新位置信息
            if(currentLocation != null) {
                return new PositionInfo(currentLocation.getLatitude(), currentLocation.getLongitude());
            }
        }
        
        // 查找到服务信息
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗
        String provider =locationManager.getBestProvider(criteria, true); // 获取GPS信息
        Location location =locationManager.getLastKnownLocation(provider); // 通过GPS获取位置
        if (location == null) {
            return null;
        }
        return new PositionInfo(location.getLatitude(), location.getLongitude());
//        
//        if (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
//            // 未开启GPS
//            
//        } else {
//            //根据设置的Criteria对象，获取最符合此标准的provider对象
////            String currentProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER).getName();
//            
//            //根据当前provider对象获取最后一次位置信息
//            Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            //如果位置信息为null，则请求更新位置信息
//            if(currentLocation == null){
//                return null;
//            }
//            return new PositionInfo(currentLocation.getLatitude(), currentLocation.getLongitude());
//        }
    }
}
