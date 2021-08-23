package org.apache.iotdb.admin.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
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

import java.net.InetAddress;
import java.util.Calendar;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IotDBControllerTest {
    private MockMvc mvc;
    @Autowired
    private WebApplicationContext wac;


    private String token = getToken();

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

    @Order(1)
    @Test
    void saveStorageGroup() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        RequestBuilder request = MockMvcRequestBuilders
                .post("http://127.0.0.1:8080/api/servers/1/storageGroups")
                .contextPath("/api")
                .content("{\n" +
                        "  \"description\": \"a group\",\n" +
                        "  \"groupName\": \"group1\",\n" +
                        "  \"ttl\": 1,\n" +
                        "  \"ttlUnit\": \"day\"\n" +
                        "}")
                .header("Authorization", token)
                .header("Content-Type", "application/json");
        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
        request = MockMvcRequestBuilders
                .post("http://127.0.0.1:8080/api/servers/1/storageGroups")
                .contextPath("/api")
                .content("{\n" +
                        "  \"description\": \"a group2\",\n" +
                        "  \"groupName\": \"group2\",\n" +
                        "  \"ttl\": 2,\n" +
                        "  \"ttlUnit\": \"day\"\n" +
                        "}")
                .header("Authorization", token)
                .header("Content-Type", "application/json");
        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Order(2)
    @Test
    void getAllStorageGroupsInfo() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        RequestBuilder request = MockMvcRequestBuilders
                .get("http://127.0.0.1:8080/api/servers/1/storageGroups/info")
                .contextPath("/api")
                .header("Authorization", token);
        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Order(3)
    @Test
    void getAllStorageGroups() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        RequestBuilder request = MockMvcRequestBuilders
                .get("http://127.0.0.1:8080/api/servers/1/storageGroups")
                .contextPath("/api")
                .header("Authorization", token);
        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Order(4)
    @Test
    void getStorageGroup() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        RequestBuilder request = MockMvcRequestBuilders
                .get("http://127.0.0.1:8080/api/servers/1/storageGroups/group1")
                .contextPath("/api")
                .header("Authorization", token);
        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Order(5)
    @Test
    void deleteStorageGroup() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        RequestBuilder request = MockMvcRequestBuilders
                .delete("http://127.0.0.1:8080/api/servers/1/storageGroups/group2")
                .contextPath("/api")
                .header("Authorization", token);
        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Order(6)
    @Test
    void saveOrUpdateDevice() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        RequestBuilder request = MockMvcRequestBuilders
                .post("http://127.0.0.1:8080/api/servers/1/storageGroups/group1/devices")
                .contextPath("/api")
                .content("{\n" +
                        "  \"description\": \"a device1\",\n" +
                        "  \"deviceDTOList\": [\n" +
                        "    {\n" +
                        "      \"dataType\": \"INT64\",\n" +
                        "      \"description\": \"a dev1\",\n" +
                        "      \"encoding\": \"PLAIN\",\n" +
                        "      \"timeseries\": \"s1\"\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"deviceName\": \"dev1\"\n" +
                        "}")
                .header("Authorization", token)
                .header("Content-Type", "application/json");
        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
        request = MockMvcRequestBuilders
                .post("http://127.0.0.1:8080/api/servers/1/storageGroups/group1/devices")
                .contextPath("/api")
                .content("{\n" +
                        "  \"description\": \"a device1\",\n" +
                        "  \"deviceDTOList\": [\n" +
                        "    {\n" +
                        "      \"dataType\": \"INT64\",\n" +
                        "      \"description\": \"a dev1\",\n" +
                        "      \"encoding\": \"PLAIN\",\n" +
                        "      \"timeseries\": \"s1\"\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"deviceName\": \"dev2\"\n" +
                        "}")
                .header("Authorization", token)
                .header("Content-Type", "application/json");
        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Order(7)
    @Test
    void getDeviceInfo() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        RequestBuilder request = MockMvcRequestBuilders
                .get("http://127.0.0.1:8080/api/servers/1/storageGroups/group1/devices/dev1")
                .contextPath("/api")
                .header("Authorization", token);
        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Order(8)
    @Test
    void getDevicesInfoByGroupName() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        RequestBuilder request = MockMvcRequestBuilders
                .get("http://127.0.0.1:8080/api/servers/1/storageGroups/group1/devices/info")
                .param("pageSize","10")
                .param("pageNum","1")
                .param("keyword","d")
                .contextPath("/api")
                .header("Authorization", token);
        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Order(9)
    @Test
    void getDevicesByGroupName() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        RequestBuilder request = MockMvcRequestBuilders
                .get("http://127.0.0.1:8080/api/servers/1/storageGroups/group1/devices")
                .contextPath("/api")
                .header("Authorization", token);
        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Order(10)
    @Test
    void deleteDevice() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        RequestBuilder request = MockMvcRequestBuilders
                .delete("http://127.0.0.1:8080/api/servers/1/storageGroups/group1/devices/dev2")
                .contextPath("/api")
                .header("Authorization", token);
        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Order(11)
    @Test
    void getMeasurementsByDeviceName() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        RequestBuilder request = MockMvcRequestBuilders
                .get("http://127.0.0.1:8080/api/servers/1/storageGroups/group1/devices/dev1/info")
                .param("pageSize","10")
                .param("pageNum","1")
                .param("keyword","s")
                .contextPath("/api")
                .header("Authorization", token);
        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Order(12)
    @Test
    void getMeasurementInfo() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        RequestBuilder request = MockMvcRequestBuilders
                .get("http://127.0.0.1:8080/api/servers/1/storageGroups/group1/devices/dev1")
                .contextPath("/api")
                .header("Authorization", token);
        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Order(13)
    @Test
    void getTimeseries() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        RequestBuilder request = MockMvcRequestBuilders
                .get("http://127.0.0.1:8080/api/servers/1/storageGroups/group1/devices/dev1/timeseries")
                .contextPath("/api")
                .header("Authorization", token);
        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Order(14)
    @Test
    void deleteTimeseries() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        RequestBuilder request = MockMvcRequestBuilders
                .delete("http://127.0.0.1:8080/api/servers/1/storageGroups/group1/devices/dev1/timeseries/s1")
                .contextPath("/api")
                .header("Authorization", token);
        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Order(15)
    @Test
    void setIotDBUser() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        RequestBuilder request = MockMvcRequestBuilders
                .post("http://127.0.0.1:8080/api/servers/1/users")
                .contextPath("/api")
                .content("{\n" +
                        "  \"password\": \"1234\",\n" +
                        "  \"userName\": \"lisi\"\n" +
                        "}")
                .header("Authorization", token)
                .header("Content-Type", "application/json");
        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Order(16)
    @Test
    void getIotDBUserList() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        RequestBuilder request = MockMvcRequestBuilders
                .get("http://127.0.0.1:8080/api/servers/1/users")
                .contextPath("/api")
                .header("Authorization", token);
        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Order(17)
    @Test
    void setUserPrivileges() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        RequestBuilder request = MockMvcRequestBuilders
                .post("http://127.0.0.1:8080/api/servers/1/users/lisi")
                .contextPath("/api")
                .content("{\n" +
                        "  \"cancelPrivileges\": [],\n" +
                        "  \"delDevicePaths\": [],\n" +
                        "  \"delGroupPaths\": [],\n" +
                        "  \"delTimeseriesPaths\": [],\n" +
                        "  \"devicePaths\": [],\n" +
                        "  \"groupPaths\": [\n" +
                        "    \"group1\"\n" +
                        "  ],\n" +
                        "  \"privileges\": [\n" +
                        "    \"CREATE_TIMESERIES\"\n" +
                        "  ],\n" +
                        "  \"timeseriesPaths\": [],\n" +
                        "  \"type\": 1\n" +
                        "}")
                .header("Authorization", token)
                .header("Content-Type", "application/json");
        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Order(18)
    @Test
    void getIotDBUser() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        RequestBuilder request = MockMvcRequestBuilders
                .get("http://127.0.0.1:8080/api/servers/1/users/lisi")
                .contextPath("/api")
                .header("Authorization", token);
        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Order(19)
    @Test
    void updatePassword() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        RequestBuilder request = MockMvcRequestBuilders
                .post("http://127.0.0.1:8080/api/servers/1/users/pwd")
                .contextPath("/api")
                .content("{\n" +
                        "  \"password\": \"2222\",\n" +
                        "  \"userName\": \"lisi\"\n" +
                        "}")
                .header("Authorization", token)
                .header("Content-Type", "application/json");
        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Order(20)
    @Test
    void deleteIotDBUser() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        RequestBuilder request = MockMvcRequestBuilders
                .delete("http://127.0.0.1:8080/api/servers/1/users/lisi")
                .contextPath("/api")
                .header("Authorization", token);
        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Order(21)
    @Test
    void deleteStorageGroup2() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        RequestBuilder request = MockMvcRequestBuilders
                .delete("http://127.0.0.1:8080/api/servers/1/storageGroups/group1")
                .contextPath("/api")
                .header("Authorization", token);
        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

}