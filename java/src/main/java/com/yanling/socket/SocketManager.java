package com.yanling.socket;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 主要是通过线程池管理ServerSocket服务端（同一服务端支持多个客户端）
 * @author yanling
 * @date 2016-12-23
 */
public class SocketManager {

    //定义线程池的大小
    private static final int THREAD_COUNT = 5;

    //定义静态单例对象
    private static SocketManager instance;

    //定义线程池对象
    private ExecutorService executorService;

    private SocketManager(){

    }

    /**
     * 单例模式
     * @return
     */
    public static SocketManager getInstance(){
        if (instance == null){
            instance = new SocketManager();
        }
        return instance;
    }

    /**
     * 开启线程池
     *@param socketHandler, socket处理线程
     */
    public void start(BaseSocketHandler socketHandler){
        //初始化容量为5的固定大小线程池
        executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        //将socket处理添加到线程池
        executorService.execute(socketHandler);
    }

    /**
     * 释放线程池资源
     */
    public void close(){
        if (executorService != null && !executorService.isShutdown()){
            executorService.shutdown();
        }
    }

}
