package org.apache.iotdb.admin.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.InetAddress;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTest {
  private MockMvc mvc;

  @Autowired private WebApplicationContext wac;

  private String token = getToken();

  @Test
  void login() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    RequestBuilder request =
        MockMvcRequestBuilders.post("http://127.0.0.1:8080/api/login")
            .param("name", "root")
            .param("password", "123456")
            .contextPath("/api");
    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void getUser() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    RequestBuilder request =
        MockMvcRequestBuilders.get("http://127.0.0.1:8080/api/get")
            .contextPath("/api")
            .header("Authorization", token);
    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
  }

  private String getToken() {
    Calendar instance = Calendar.getInstance();
    try {
      instance.add(Calendar.HOUR, 24);
      String token =
          JWT.create()
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
