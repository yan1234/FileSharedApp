package com.example.filesharedapp.utils.systemutils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * App工具类，用于获取系统已安装的app
 * Created by yanling on 2015/10/30.
 */
public class AppUtils {

    //定义系统和用户标志常量
    public static final String ALL = "ALL";
    public static final String SYSTEM = "SYSTEM";
    public static final String CUSTOMER = "CUSTOMER";


    /**
     * 获取对应类型的app（系统，用户）
     * @param context，上下文
     * @param type，获取app的类型，ALL：所有，SYSTEM：系统，CUSTOMER：用户
     * @return
     */
    public static List<ResolveInfo> getApps(Context context, String type){
        //定义获取app列表
        List<ResolveInfo> list = new ArrayList<ResolveInfo>();
        //定义需要获取类型的app列表
        List<ResolveInfo> requestApps = new ArrayList<ResolveInfo>();
        //得到包管理器
        PackageManager pm = context.getPackageManager();
        //设置获取应用的条件（即带启动的activity)
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        //获取所有的应用
        list = pm.queryIntentActivities(intent, 0);
        //判断是否是获取全部的应用
        if (ALL.equals(type)){
            return list;
        }
        //区分系统/用户应用
        for (int i=0; i < list.size(); i++){
            ResolveInfo resolveInfo = list.get(i);
            //表示需要获取用户app
            //通过标志判断是否是用户app
            if (CUSTOMER.equals(type) &&
                    (resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0){
                //添加用户app
                requestApps.add(resolveInfo);
            }//表示需要获取系统app
            else if (SYSTEM.equals(type) &&
                    (resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0){
                //添加系统app
                requestApps.add(resolveInfo);
            }
        }
        return requestApps;
    }
}
