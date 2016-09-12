package com.example.filesharedapp.utils.md5;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5工具集，主要包括获取文件的MD5值和字符串进行MD5加密
 * Created by yanling on 2015/10/27.
 */
public class MD5Utils {

    protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    protected static MessageDigest messageDigest = null;
    static {
        try {
            //生成实现指定摘要算法的 MessageDigest 对象。
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将字符串进行MD5加密
     * @param sourceStr，源字符串
     * @return，返回32位的md5值
     */
    public static String getStrMD5String(String sourceStr) {
        String result = "";
        //使用指定的字节数组更新摘要。
        messageDigest.update(sourceStr.getBytes());
        //通过执行诸如填充之类的最终操作完成哈希计算。
        byte b[] = messageDigest.digest();
        int i;
        //生成具体的md5密码到buf数组
        StringBuffer buf = new StringBuffer("");
        for (int offset = 0; offset < b.length; offset++) {
            i = b[offset];
            if (i < 0)
                i += 256;
            if (i < 16)
                buf.append("0");
            buf.append(Integer.toHexString(i));
        }
        result = buf.toString();
        return result;
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
    public static String bytes2hex(byte[] bytes)
    {
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


}
