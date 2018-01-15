package com.benyun.heartbeat;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 心跳服务端点，切入到基于Servlet API的应用中，不侵入其程序
 * @author Succy
 */
@WebServlet(name = "heartBeatListener", urlPatterns = "/benyun/health/heartbeat/ping")
public class HeartBeatListener extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        resp.setStatus(200);
    }
}
