package com.yanling.fileshared.framework.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.lang.reflect.Method;
import java.util.List;

/**
 * wifi管理核心类
 * Created by yanling on 2015/10/27.
 */
public class WifiController {

    //定义上下文
    private Context mContext;
    //定义wifi管理器
    public WifiManager wifiManager;

    //定义wifi的ssid和密码
    private String ssid;
    private String preSharedKey;

    public WifiController(Context context){
        this.mContext = context;
        //初始化系统的wifi管理器
        wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
    }


    /**
     * 连接对应的wifi
     * @param ssid，wifi名
     * @param preSharedKey，wifi密码
     */
    public void connectWifi(String ssid, String preSharedKey){

        //开启wifi
        setWifiStatus(true);
        ////开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
        //状态变成WifiManager.WIFI_STATE_ENABLED的时候才能执行下面的语句
        while (!(wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED)){
            try{
                //休眠100毫秒检测一次
                Thread.currentThread();
                Thread.sleep(100);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        //开始扫描wifi列表
        List<ScanResult> wifiList = wifiManager.getScanResults();
        //遍历所有的wifi
        for (ScanResult result : wifiList){
            //判断扫描到的wifi是否是当前要连接的wifi
            if (result.SSID.equals(ssid)){
                //设置对应的连接参数
                WifiConfiguration wifiConfig = setWifiConfig(ssid, preSharedKey);
                //将设置添加到网络
                int wifiID = wifiManager.addNetwork(wifiConfig);
                boolean flag = wifiManager.enableNetwork(wifiID, true);
                if (flag){
                    wifiManager.saveConfiguration();
                }
                break;
            }
        }
    }

    /**
     * 根据ssid和密码设置对应的wifi热点
     * @param ssid
     * @param preSharedKey
     * @param enable, true:开启，false:关闭
     * @return true: 设置成功，false：设置失败
     */
    public boolean startWifiAp(String ssid, String preSharedKey, boolean enable){
        //设置wifi名和密码
        this.ssid = ssid;
        this.preSharedKey = preSharedKey;
        //开启/关闭wifi热点
        return setWifiApEnabled(enable);
    }

    /**
     * 通过反射设置wifi热点
     * @return，返回设置的状态，true:设置成功，false:设置失败
     */
    private boolean setWifiApEnabled(boolean enable){
        //由于wifi热点和wifi不能同时开启，所以需要关闭wifi连接
        if (enable){
            this.setWifiStatus(false);
        }else{
            this.setWifiStatus(true);
        }
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
            Method method = wifiManager.getClass().getMethod(
                    "setWifiApEnabled",
                    WifiConfiguration.class,
                    Boolean.TYPE);
            //返回热点打开状态
            return(Boolean)method.invoke(wifiManager, apConfig, enable);
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 设置wifi连接的参数
     * @param ssid，ssid
     * @param preSharedKey, wifi连接的密码
     * @return， 返回设置的参数
     */
    private WifiConfiguration setWifiConfig(String ssid, String preSharedKey){
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
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);

        return wifiConfig;
    }

    /**
     * 得到系统连接的wifi的ip地址（xxx.xxx.xxx.xxx的形式）
     * @return，返回连接的ip
     */
    public String getWifiAddress(){
        //得到连接wifi的信息
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
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

    /**
     * 根据标志开启/关闭wifi连接
     * @param flag，true：开启wifi连接，false:关闭wifi连接
     */
    public void setWifiStatus(boolean flag){
        //当前wifi为连接，并且需要开启wifi时
        if (flag && !isWifiConnected()){
            wifiManager.setWifiEnabled(true);
        }
        //当前wifi已连接，并且需要关闭wifi时
        if (!flag && isWifiConnected()){
            wifiManager.setWifiEnabled(false);
        }
    }

    /**
     * 判断wifi是否连接上
     * @return
     */
    public boolean isWifiConnected(){
        return wifiManager.isWifiEnabled();
    }

    /**
     * 获取当前连接的wifi信息
     * 注意这里获取的SSID会多一对双引号""SSID""
     * @return
     */
    public WifiInfo getWifiInfo(){
        return wifiManager.getConnectionInfo();
    }
}
