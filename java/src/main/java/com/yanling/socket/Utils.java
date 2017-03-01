package com.yanling.socket;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

/**
 * 工具类
 * @author yanling
 * @date 2016-12-21
 */
public class Utils {

    /**
     * 根据源直接数组获取新的有效字节数组（主要是把源数组中的空字节'0'去掉）
     * @param src，源字节数组
     * @param placeholder, 待去除的占位符
     * @return，返回新的直接数组,若原数组为null或lenght为0则返回null
     */
    public static byte[] getAvailabByte(byte[] src, char placeholder){
        if (src == null || src.length <= 0){
            return null;
        }
        int availabLen = 0;
        //首先获取有效字节长度
        for (byte b : src){
            if (b != placeholder){
                availabLen ++;
            }else{
                break;
            }
        }
        byte[] dest = new byte[availabLen];
        System.arraycopy(src, 0, dest, 0, dest.length);
        return dest;
    }

    /**
     * 获取文件的md5值
     * @param file，文件对象
     * @return
     */
    public static String getFileMD5(File file){
        if (!file.isFile()){
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bytes2hex(digest.digest()).toUpperCase();
    }

    /**
     * 将byte转化为16进制数据
     * @param bytes
     * @return
     */
    public static String bytes2hex(byte[] bytes) {
        final String HEX = "0123456789abcdef";
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes)
        {
            // 取出这个字节的高4位，然后与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
            sb.append(HEX.charAt((b >> 4) & 0x0f));
            // 取出这个字节的低位，与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
            sb.append(HEX.charAt(b & 0x0f));
        }
        return sb.toString();
    }

    /**
     * 获取kmp算法目标串的next数组（即该串从长度为1-length的前缀和后缀相等的最长公共部分）
     * @param target，目标字符数组
     * @return，int[]数组，数组每一位表示当前下标个长度的子串匹配到的长度
     */
    public static int[] getNext(char[] target){
        //定义next数组存储子字符串的长度为i时的前缀和后缀的最长公共部分
        //由于字符串长度为0没有意义，所以next数组的起始位置为1，标示当前匹配的字符串的长度
        int[] next = new int[target.length + 1];
        //设置初始值
        next[0] = 0;
        next[1] = 0;
        //遍历目标数组
        for (int i = 1; i < target.length; i++){
            //记录上一个长度的next的值
            int before_next = next[i+1 - 1];
            //一步步缩小范围进行迭代
            //迭代结束有两种情况
            //1、找到相同的，那么当前字符串的长度比迭代到的长度+1
            //2、迭代到公共长度为0
            while (before_next > 0 && target[i] != target[before_next]){
                before_next = next[before_next];
            }
            //判断迭代结束后是否相等，如果相等，则长度+1
            if (target[i] == target[before_next]){
                before_next ++;
            }
            next[i + 1] = before_next;
        }
        return next;
    }

}
