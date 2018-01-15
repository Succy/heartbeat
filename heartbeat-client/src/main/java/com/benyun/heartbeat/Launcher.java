package com.benyun.heartbeat;

import cn.hutool.cron.CronUtil;

public class Launcher {
    public static void main(String[] args) {
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }
}
