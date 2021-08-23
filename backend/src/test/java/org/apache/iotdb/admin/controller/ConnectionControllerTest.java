package org.apache.iotdb.admin.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.net.InetAddress;
import java.util.Calendar;

@SpringBootTest
class ConnectionControllerTest {
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext wac;

    private String token = getToken();

    @Transactional
    @Rollback
    @Test
    void saveOrUpdateConnection() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        RequestBuilder request = MockMvcRequestBuilders
                .post("http://127.0.0.1:8080/api/servers")
                .contextPath("/api")
                .content("{\n" +
                        "  \"alias\": \"tttt\",\n" +
                        "  \"host\": \"localhost\",\n" +
                        "  \"password\": \"root\",\n" +
                        "  \"port\": 6667,\n" +
                        "  \"userId\": 1,\n" +
                        "  \"username\": \"root\"\n" +
                        "}")
                .header("Authorization", token)
                .header("Content-Type", "application/json");
        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Transactional
    @Rollback
    @Test
    void deleteConnection() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        RequestBuilder request = MockMvcRequestBuilders
                .delete("http://127.0.0.1:8080/api/servers/1")
                .contextPath("/api")
                .header("Authorization", token);
        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getConnection() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        RequestBuilder request = MockMvcRequestBuilders
                .get("http://127.0.0.1:8080/api/servers/1")
                .contextPath("/api")
                .header("Authorization", token);
        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getAllConnections() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        RequestBuilder request = MockMvcRequestBuilders
                .get("http://127.0.0.1:8080/api/servers")
                .contextPath("/api")
                .param("userId","1")
                .header("Authorization", token);
        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    private String getToken() {
        Calendar instance = Calendar.getInstance();
        try {
            instance.add(Calendar.HOUR, 24);
            String token = JWT.create()
                    .withClaim("userId", 1)
                    .withClaim("name", "root")
                    .withExpiresAt(instance.getTime())
                    .sign(Algorithm.HMAC256("IOTDB:" + InetAddress.getLocalHost().getHostAddress()));
            return token;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}