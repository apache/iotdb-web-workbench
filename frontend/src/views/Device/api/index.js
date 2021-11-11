/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import axios from '@/util/axios.js';

//获取实体详情
export function getDeviceDate(deviceData) {
  return axios.get(`/servers/${deviceData.connectionid}/storageGroups/${deviceData.storagegroupid}/devices/${deviceData.name}`);
}
//获取实体下物理量列表
export function getList(deviceData, data) {
  return axios.get(`/servers/${deviceData.connectionid}/storageGroups/${deviceData.storagegroupid}/devices/${deviceData.name}/info`, { params: data });
}
//删除物理量
export function deleteData(deviceData, timeseriesName) {
  return axios.delete(`/servers/${deviceData.connectionid}/storageGroups/${deviceData.storagegroupid}/devices/${deviceData.name}/timeseries/${timeseriesName}`);
}
//新增/编辑实体信息
export function deviceAddEdite(serverId, groupName, data) {
  return axios.post(`/servers/${serverId}/storageGroups/${groupName}/devices`, data);
}
