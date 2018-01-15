package com.benyun.heartbeat;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.StaticLog;
import cn.hutool.setting.Setting;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class HeartBeatSender {
    private static final Log logger = StaticLog.get();
    private Map<String, Setting> serverMap = new HashMap<String, Setting>();

    public HeartBeatSender() {
        Setting setting = new Setting("config/server.setting");
        LinkedList<String> groups = setting.getGroups();
        for (String group : groups) {
            Setting value = setting.getSetting(group);
            serverMap.put(group, value);
        }
    }

    public void send() {
        for (Map.Entry<String, Setting> entry : serverMap.entrySet()) {
            Setting setting = entry.getValue();
            String address = setting.getStr("address");
            if (StrUtil.isBlank(address)) {
                logger.warn("server address: {} is blank!");
                continue;
            }
            new Thread(new HeartBeatTask(address)).start();
        }
    }
}
