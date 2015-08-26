package com.example.filesharedapp.common.util;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.ScanResult;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by yanling on 2015/8/24.
 * 主要是对android端wifi操作的工具类
 */
public class WifiUtils {


    public static void connectWifi(String ssid, String preSharedKey, Context context){
        //得到wifi管理器
        WifiManager wifiMag = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        //得到扫描wifi列表
        List<ScanResult> wifiList = wifiMag.getScanResults();
        //遍历所有的wifi
        for (ScanResult result : wifiList){
            //判断扫描到的wifi是否是当前要连接的wifi
            if (result.SSID.equals(ssid)){
                //设置对应的连接参数
                WifiConfiguration wifiConfig = setWifiConfig(ssid, preSharedKey);
                //将设置添加到网络
                int wifiID = wifiMag.addNetwork(wifiConfig);
                boolean flag = wifiMag.enableNetwork(wifiID, true);
                break;
            }
        }
    }


    /**
     * 设置wifi连接的参数
     * @param ssid，ssid
     * @param preSharedKey, wifi连接的密码
     * @return， 返回设置的参数
     */
    public static WifiConfiguration setWifiConfig(String ssid, String preSharedKey){
        //定义wifi配置参数
        WifiConfiguration wifiConfig = new WifiConfiguration();
        //设置ssid
        wifiConfig.SSID = "\""+ssid+"\"";
        //设置密码
        wifiConfig.preSharedKey = "\""+preSharedKey+"\"";
        wifiConfig.hiddenSSID = true;
        wifiConfig.status = WifiConfiguration.Status.ENABLED;
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);

        return wifiConfig;
    }


    /**
     * 设置wifi热点
     * @param ssid，热点名
     * @param preSharedKey，热点密码
     * @param context，上下文
     * @return，返回设置的状态，true:设置成功，false:设置失败
     */
    public static boolean startWifiAp(String ssid, String preSharedKey, Context context){
        //得到wifi管理器
        WifiManager wifiMag = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        //由于wifi热点和wifi不能同时开启，所以需要关闭wifi连接
        wifiMag.setWifiEnabled(false);
        try{
            //配置wifi热点
            WifiConfiguration apConfig = new WifiConfiguration();
            //配置热点的名称
            apConfig.SSID = ssid;
            //配置密码
            apConfig.preSharedKey = preSharedKey;
            //配置加密类型（默认使用wpa2_psk)
            apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            //通过反射调用机制设置热点
            Method method = wifiMag.getClass().getMethod(
                    "startWifiAp",
                    WifiConfiguration.class,
                    String.class,
                    String.class,
                    Context.class);
            //返回热点打开状态
            //return（Boolean)method.invoke(apConfig, ssid, preSharedKey, context);
        }catch (Exception e){
            return false;
        }
        return true;

    }


    /**
     * 判断wifi是否连接
     * @param context，上下文
     * @return，返回连接状态，true:连接；false:未连接
     */
    public boolean isWifiEnabled(Context context){
        //得到wifi管理器
        WifiManager wifiMag = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);

        //返回wifi连接状态
        return wifiMag.isWifiEnabled();
    }

    /**
     * 得到系统连接的wifi的ip地址（xxx.xxx.xxx.xxx的形式）
     * @param context，上下文
     * @return，返回连接的ip
     */
    public static String getWifiAddress(Context context){
        //得到系统的wifi管理器
        WifiManager wifiMag = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        //得到连接wifi的信息
        WifiInfo wifiInfo = wifiMag.getConnectionInfo();
        //获取连接的ip地址，需要进行转化
        int ipAddress = wifiInfo.getIpAddress();
        //将int型的ip地址转化为String类型
        String ip = intToIp(ipAddress);
        return ip;
    }

    /**
     * 将int型ip地址转化为xx.xx.xx.xx字符串的形式
     * @param ipAddress，int类型的ip地址
     * @return，返回字符串类型的ip地址
     */
    private static String intToIp(int ipAddress){
        return (ipAddress & 0xFF) + "."
                +((ipAddress >> 8) & 0xFF) + "."
                +((ipAddress >> 16) & 0xFF) + "."
                +((ipAddress >> 24) & 0xFF);
    }
}
