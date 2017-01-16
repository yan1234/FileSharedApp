package com.yanling.fileshared.utils.common;

/**
 * 时间处理工具集
 * Created by yanling on 2015/11/4.
 */
public class DateUtils {

    /**
     * 将毫秒数转化为普通的按多少年:月:日....的时长
     * 注意：这里不是转化为当前时间，而是时长如：5分30s
     * @param millisecond
     * @return,返回年月日的时长，格式为：x年x月x日x时x分x秒x毫秒
     */
    public static long[] millisToTime(long millisecond){
        //实例化一个7位的数组来存储各个位数上的时间
        long[] time = new long[7];
        //年(这里不深究闰年，大小月的问题）
        time[0] = millisecond / (365*24*60*60*1000);
        millisecond = millisecond % (365*24*60*60*1000);
        //月
        time[1] = millisecond /(30*24*60*60*1000);
        millisecond = millisecond % (30*24*60*60*1000);
        //日
        time[2] = millisecond / (24*60*60*1000);
        millisecond = millisecond % (24*60*60*1000);
        //时
        time[3] = millisecond / (60*60*1000);
        millisecond = millisecond % (60*60*1000);
        //分
        time[4] = millisecond / (60*1000);
        millisecond = millisecond % (60*1000);
        //秒
        time[5] = millisecond / 1000;
        //毫秒
        time[6] = millisecond % 1000;
        return time;
    }
}
