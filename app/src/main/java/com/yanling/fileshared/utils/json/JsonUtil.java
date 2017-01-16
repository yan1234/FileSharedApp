package com.yanling.fileshared.utils.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Json解析工具集
 * 目前主要采用Gson进行数据解析，以后便于扩展，直接替换解析方式即可
 * @author yanling
 * @date 2015-10-27
 */
public class JsonUtil {

    //定义静态的Gson对象,并设置时间Date类型字段解析方式
    private static Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss").create();


    /**
     * 将json串反序列化为对应的对象
     * @param json，json串
     * @param clazz， 待反序列化的对象
     * @return T，返回序列化的对象
     * @throws
     */
    public static <T> T jsonToObject(String json , Class<T> clazz){
        return gson.fromJson(json, clazz);
    }

    /**
     * 将Object对象序列化为json串
     * @param obj，待序列化的对象
     * @return String，序列化的json串
     * @throws
     */
    public static String objToJson(Object obj){
        return gson.toJson(obj);
    }

}
