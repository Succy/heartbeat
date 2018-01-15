package com.benyun.heartbeat;

import cn.hutool.http.HttpException;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 心跳任务，每一个服务实例开启一个心跳线程去检查
 * @author Succy
 */
public class HeartBeatTask implements Runnable {
    private static final Log logger = LogFactory.get();
    private static final String HEART_BEAT_URL = "/benyun/health/heartbeat/ping";
    private AtomicInteger retryCounter;
    private String serverAddr;
    private String url;
    private static final int MAX_RETRY_COUNT = 2;

    public HeartBeatTask(String serverAddr) {
        this.serverAddr = serverAddr;
        this.url = this.serverAddr + HEART_BEAT_URL;
        this.retryCounter = new AtomicInteger(0);
    }

    @Override
    public void run() {
        try {
            long start = System.currentTimeMillis();
            int status = HttpUtil.createGet(url).execute().getStatus();
            long end = System.currentTimeMillis();
            if (status == HttpStatus.HTTP_OK) {
                logger.info("ping server: {}; status: {}; times: {}", serverAddr, "200 OK", (end - start) + "ms");
            } else {
                throw new HttpException("status not equal 200, status=%s", status);
            }
        } catch (Exception e) {
            // 此处应该做其他处理，例如重试和发送信息
            logger.warn("server: {} no response, it will be retry……", serverAddr);
            retry();
            if (retryCounter.get() == MAX_RETRY_COUNT) {
                logger.error("had been retry 2 times, but server: {} no response, cause: {}; maybe it was death, please checking", serverAddr, e.getMessage());
                //TODO 直接发送报警
            }
        }
    }

    /**
     * 重试
     */
    private void retry() {
        if (retryCounter.get() == MAX_RETRY_COUNT) {
            return;
        }
        try {
            int count = retryCounter.incrementAndGet();
            logger.info("retry {} time ping server: {}", count, serverAddr);
            long start = System.currentTimeMillis();
            int status = HttpUtil.createGet(url).execute().getStatus();
            long end = System.currentTimeMillis();
            if (status == HttpStatus.HTTP_OK) {
                logger.info("ping server: {}; status: {}; times: {}", serverAddr, "200 OK", (end - start) + "ms");
                return;
            } else {
                retry();
            }
        }catch (Exception e) {
            retry();
        }

    }
}
