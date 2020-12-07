package com.zs.easy.imgcompress.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EasyThreadPoolUtil {
    private ExecutorService pool;

    private static volatile EasyThreadPoolUtil instance;

    public static EasyThreadPoolUtil getInstance() {
        if (instance == null) {
            synchronized (EasyThreadPoolUtil.class) {
                if (instance == null) {
                    instance = new EasyThreadPoolUtil();
                }
            }
        }
        return instance;
    }


    private EasyThreadPoolUtil() {
        this.initThreadPool(5);
    }

    public EasyThreadPoolUtil(int threadLength) {
        this.initThreadPool(threadLength);
    }

    public synchronized void poolExecute(Runnable runnable) {
        if (this.pool != null) {
            this.pool.execute(runnable);
        }

    }

    public synchronized void poolClose() {
        if (this.pool != null) {
            this.pool.shutdownNow();
        }

        this.pool = null;
    }

    private void initThreadPool(int threadLength) {
        if (threadLength < 1) {
            threadLength = 1;
        }

        if (this.pool != null) {
            this.poolClose();
        }

        this.pool = Executors.newFixedThreadPool(threadLength);
    }
}
