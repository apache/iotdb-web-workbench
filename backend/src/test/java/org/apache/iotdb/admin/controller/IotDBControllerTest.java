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

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IotDBControllerTest {
  private MockMvc mvc;
  @Autowired private WebApplicationContext wac;

  private String token = getToken();

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

  @Order(1)
  @Test
  void saveStorageGroup() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    RequestBuilder request =
        MockMvcRequestBuilders.post("http://127.0.0.1:8080/api/servers/1/storageGroups")
            .contextPath("/api")
            .content(
                "{\n"
                    + "  \"description\": \"a group\",\n"
                    + "  \"groupName\": \"group1\",\n"
                    + "  \"ttl\": 1,\n"
                    + "  \"ttlUnit\": \"day\"\n"
                    + "}")
            .header("Authorization", token)
            .header("Content-Type", "application/json");
    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    request =
        MockMvcRequestBuilders.post("http://127.0.0.1:8080/api/servers/1/storageGroups")
            .contextPath("/api")
            .content(
                "{\n"
                    + "  \"description\": \"a group2\",\n"
                    + "  \"groupName\": \"group2\",\n"
                    + "  \"ttl\": 2,\n"
                    + "  \"ttlUnit\": \"day\"\n"
                    + "}")
            .header("Authorization", token)
            .header("Content-Type", "application/json");
    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Order(2)
  @Test
  void getAllStorageGroupsInfo() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    RequestBuilder request =
        MockMvcRequestBuilders.get("http://127.0.0.1:8080/api/servers/1/storageGroups/info")
            .contextPath("/api")
            .header("Authorization", token);
    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Order(3)
  @Test
  void getAllStorageGroups() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    RequestBuilder request =
        MockMvcRequestBuilders.get("http://127.0.0.1:8080/api/servers/1/storageGroups")
            .contextPath("/api")
            .header("Authorization", token);
    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Order(4)
  @Test
  void getStorageGroup() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    RequestBuilder request =
        MockMvcRequestBuilders.get("http://127.0.0.1:8080/api/servers/1/storageGroups/group1")
            .contextPath("/api")
            .header("Authorization", token);
    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Order(5)
  @Test
  void deleteStorageGroup() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    RequestBuilder request =
        MockMvcRequestBuilders.delete("http://127.0.0.1:8080/api/servers/1/storageGroups/group2")
            .contextPath("/api")
            .header("Authorization", token);
    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Order(6)
  @Test
  void saveOrUpdateDevice() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    RequestBuilder request =
        MockMvcRequestBuilders.post(
                "http://127.0.0.1:8080/api/servers/1/storageGroups/group1/devices")
            .contextPath("/api")
            .content(
                "{\n"
                    + "  \"description\": \"a device1\",\n"
                    + "  \"deviceDTOList\": [\n"
                    + "    {\n"
                    + "      \"dataType\": \"INT64\",\n"
                    + "      \"description\": \"a dev1\",\n"
                    + "      \"encoding\": \"PLAIN\",\n"
                    + "      \"timeseries\": \"s1\"\n"
                    + "    }\n"
                    + "  ],\n"
                    + "  \"deviceName\": \"dev1\"\n"
                    + "}")
            .header("Authorization", token)
            .header("Content-Type", "application/json");
    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    request =
        MockMvcRequestBuilders.post(
                "http://127.0.0.1:8080/api/servers/1/storageGroups/group1/devices")
            .contextPath("/api")
            .content(
                "{\n"
                    + "  \"description\": \"a device1\",\n"
                    + "  \"deviceDTOList\": [\n"
                    + "    {\n"
                    + "      \"dataType\": \"INT64\",\n"
                    + "      \"description\": \"a dev1\",\n"
                    + "      \"encoding\": \"PLAIN\",\n"
                    + "      \"timeseries\": \"s1\"\n"
                    + "    }\n"
                    + "  ],\n"
                    + "  \"deviceName\": \"dev2\"\n"
                    + "}")
            .header("Authorization", token)
            .header("Content-Type", "application/json");
    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Order(7)
  @Test
  void getDeviceInfo() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    RequestBuilder request =
        MockMvcRequestBuilders.get(
                "http://127.0.0.1:8080/api/servers/1/storageGroups/group1/devices/dev1")
            .contextPath("/api")
            .header("Authorization", token);
    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Order(8)
  @Test
  void getDevicesInfoByGroupName() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    RequestBuilder request =
        MockMvcRequestBuilders.get(
                "http://127.0.0.1:8080/api/servers/1/storageGroups/group1/devices/info")
            .param("pageSize", "10")
            .param("pageNum", "1")
            .param("keyword", "d")
            .contextPath("/api")
            .header("Authorization", token);
    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Order(9)
  @Test
  void getDevicesByGroupName() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    RequestBuilder request =
        MockMvcRequestBuilders.get(
                "http://127.0.0.1:8080/api/servers/1/storageGroups/group1/devices")
            .contextPath("/api")
            .header("Authorization", token);
    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Order(10)
  @Test
  void deleteDevice() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    RequestBuilder request =
        MockMvcRequestBuilders.delete(
                "http://127.0.0.1:8080/api/servers/1/storageGroups/group1/devices/dev2")
            .contextPath("/api")
            .header("Authorization", token);
    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Order(11)
  @Test
  void getMeasurementsByDeviceName() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    RequestBuilder request =
        MockMvcRequestBuilders.get(
                "http://127.0.0.1:8080/api/servers/1/storageGroups/group1/devices/dev1/info")
            .param("pageSize", "10")
            .param("pageNum", "1")
            .param("keyword", "s")
            .contextPath("/api")
            .header("Authorization", token);
    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Order(12)
  @Test
  void getMeasurementInfo() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    RequestBuilder request =
        MockMvcRequestBuilders.get(
                "http://127.0.0.1:8080/api/servers/1/storageGroups/group1/devices/dev1")
            .contextPath("/api")
            .header("Authorization", token);
    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Order(13)
  @Test
  void getTimeseries() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    RequestBuilder request =
        MockMvcRequestBuilders.get(
                "http://127.0.0.1:8080/api/servers/1/storageGroups/group1/devices/dev1/timeseries")
            .contextPath("/api")
            .header("Authorization", token);
    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Order(14)
  @Test
  void deleteTimeseries() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    RequestBuilder request =
        MockMvcRequestBuilders.delete(
                "http://127.0.0.1:8080/api/servers/1/storageGroups/group1/devices/dev1/timeseries/s1")
            .contextPath("/api")
            .header("Authorization", token);
    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Order(15)
  @Test
  void setIotDBUser() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    RequestBuilder request =
        MockMvcRequestBuilders.post("http://127.0.0.1:8080/api/servers/1/users")
            .contextPath("/api")
            .content("{\n" + "  \"password\": \"1234\",\n" + "  \"userName\": \"lisi\"\n" + "}")
            .header("Authorization", token)
            .header("Content-Type", "application/json");
    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Order(16)
  @Test
  void getIotDBUserList() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    RequestBuilder request =
        MockMvcRequestBuilders.get("http://127.0.0.1:8080/api/servers/1/users")
            .contextPath("/api")
            .header("Authorization", token);
    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Order(17)
  @Test
  void setUserPrivileges() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    RequestBuilder request =
        MockMvcRequestBuilders.post("http://127.0.0.1:8080/api/servers/1/users/lisi")
            .contextPath("/api")
            .content(
                "{\n"
                    + "  \"cancelPrivileges\": [],\n"
                    + "  \"delDevicePaths\": [],\n"
                    + "  \"delGroupPaths\": [],\n"
                    + "  \"delTimeseriesPaths\": [],\n"
                    + "  \"devicePaths\": [],\n"
                    + "  \"groupPaths\": [\n"
                    + "    \"group1\"\n"
                    + "  ],\n"
                    + "  \"privileges\": [\n"
                    + "    \"CREATE_TIMESERIES\"\n"
                    + "  ],\n"
                    + "  \"timeseriesPaths\": [],\n"
                    + "  \"type\": 1\n"
                    + "}")
            .header("Authorization", token)
            .header("Content-Type", "application/json");
    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Order(18)
  @Test
  void getIotDBUser() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    RequestBuilder request =
        MockMvcRequestBuilders.get("http://127.0.0.1:8080/api/servers/1/users/lisi")
            .contextPath("/api")
            .header("Authorization", token);
    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Order(19)
  @Test
  void updatePassword() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    RequestBuilder request =
        MockMvcRequestBuilders.post("http://127.0.0.1:8080/api/servers/1/users/pwd")
            .contextPath("/api")
            .content("{\n" + "  \"password\": \"2222\",\n" + "  \"userName\": \"lisi\"\n" + "}")
            .header("Authorization", token)
            .header("Content-Type", "application/json");
    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Order(20)
  @Test
  void deleteIotDBUser() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    RequestBuilder request =
        MockMvcRequestBuilders.delete("http://127.0.0.1:8080/api/servers/1/users/lisi")
            .contextPath("/api")
            .header("Authorization", token);
    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Order(21)
  @Test
  void deleteStorageGroup2() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    RequestBuilder request =
        MockMvcRequestBuilders.delete("http://127.0.0.1:8080/api/servers/1/storageGroups/group1")
            .contextPath("/api")
            .header("Authorization", token);
    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
  }
}
