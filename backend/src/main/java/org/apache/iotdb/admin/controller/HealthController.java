package org.apache.iotdb.admin.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 运维平台通过存活、就绪探针表示应用是否存活，就绪。这个类大家最好不好删除，url地址也不要改变
 *
 * @author fanli
 */
@Controller
public class HealthController {

    /**
     * 存活探针，运维平台会每格一段时间，请求一下这个接口，如果这个接口返回200，那么表示存活，
     * 如果多次返回500，则表示这个程序已经死了，会重启这个程序。
     */
    @GetMapping(value = "/healthz/liveness")
    public void liveness(HttpServletResponse response) throws IOException {
        // boolean isAlive = true;

        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * 就绪探针，运维平台会每格一段时间，请求一下这个接口，如果这个接口返回200，那么表示程序已经可用，
     * 如果多次返回500，则表示这个程序未启动成功，会重启这个程序。
     */
    @GetMapping(value = "/healthz/readiness")
    public void readiness(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
