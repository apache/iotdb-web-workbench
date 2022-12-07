/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.iotdb.admin.controller;

import org.apache.iotdb.admin.model.entity.User;
import org.apache.iotdb.admin.tool.JJwtTool;

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

@SpringBootTest
class QueryControllerTest {

  private MockMvc mvc;

  @Autowired private WebApplicationContext wac;

  private String token = getToken();

  @Test
  void query() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    RequestBuilder request =
        MockMvcRequestBuilders.post("http://127.0.0.1:8080/api/servers/1/querySql")
            .contextPath("/api")
            .content(
                "{\n"
                    + "  \"sqls\": [\n"
                    + "    \"show storage group\",\n"
                    + "    \"show devices\"\n"
                    + "  ],\n"
                    + "  \"timestamp\": 0\n"
                    + "}")
            .header("Authorization", token)
            .header("Content-Type", "application/json");
    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Transactional
  @Rollback
  @Test
  void saveQuery() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    RequestBuilder request =
        MockMvcRequestBuilders.post("http://127.0.0.1:8080/api/servers/1/query")
            .contextPath("/api")
            .content(
                "{\n"
                    + "  \"connectionId\": 1,\n"
                    + "  \"queryName\": \"testQ\",\n"
                    + "  \"sqls\": \"show storage group\"\n"
                    + "}")
            .header("Authorization", token)
            .header("Content-Type", "application/json");
    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void getQueries() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    RequestBuilder request =
        MockMvcRequestBuilders.get("http://127.0.0.1:8080/api/servers/1/query")
            .contextPath("/api")
            .header("Authorization", token);
    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Transactional
  @Rollback
  @Test
  void deleteQuery() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    RequestBuilder request =
        MockMvcRequestBuilders.delete("http://127.0.0.1:8080/api/servers/1/query/4")
            .contextPath("/api")
            .header("Authorization", token);
    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void getQuery() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    RequestBuilder request =
        MockMvcRequestBuilders.get("http://127.0.0.1:8080/api/servers/1")
            .contextPath("/api")
            .header("Authorization", token);
    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
  }

  private String getToken() {
    try {
      User user = new User();
      user.setId(1);
      user.setName("root");
      return JJwtTool.generateToken(user);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
