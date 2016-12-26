package com.yanling.socket;


/**
 * Socket处理输入输出流回调（主要用于处理文件上传下载的进度）
 * @author yanling
 * @date 2016-12-22
 */
public interface SocketCallback {

    /**
     * 开始处理
     * @param name，待传送的数据名称（一般为文件名）
     * @param totalSize，待传输的数据总大小
     * @param isIn，表示是输入处理还是输出处理，True:输入，False:输出
     */
    public void start(String name, long totalSize, boolean isIn);

    /**
     * 处理socket数据传输的回调
     * @param name，待传送的数据名称（一般为文件名）
     * @param totalSize，待传输的数据总大小
     * @param transSize，已经传输的大小
     * @param isIn，表示是输入处理还是输出处理，True:输入，False输出
     */
    public void handlerProgress(String name, long totalSize, long transSize, boolean isIn);


    /**
     * 结束处理
     * @param name，待传送的数据名称（一般为文件名）
     * @param isIn，表示是输入处理还是输出处理，True:输入，False输出
     */
    public void end(String name, boolean isIn);

    /**
     * 异常处理
     * @param e, 异常类
     */
    public void error(Exception e);

}
