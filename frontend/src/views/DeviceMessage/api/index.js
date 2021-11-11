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
export function getDeviceDate(data) {
  return axios.get(`/servers/${data.connectionid}/storageGroups/${data.storagegroupid}/devices/${data.name}`);
}
//获取实体下物理量列表
export function getList(data, params) {
  return axios.get(`/servers/${data.connectionid}/storageGroups/${data.storagegroupid}/devices/${data.name}/info`, { params: params });
}
//获取测点数据趋势
export function getData(serverId, groupName, deviceName, timeseriesName, params) {
  return axios.get(`/servers/${serverId}/storageGroups/${groupName}/devices/${deviceName}/timeseries/${timeseriesName}`, { params });
}
//删除实体
export function deleteDevice(data) {
  return axios.delete(`/servers/${data.connectionid}/storageGroups/${data.storagegroupid}/devices/${data.name}`);
}
//获取数据预览
export function getDataDeviceList(info, params, data) {
  return axios.post(`/servers/${info.connectionid}/storageGroups/${info.storagegroupid}/devices/${info.name}/data`, data, { params });
}
//随机导入数据
export function randomImport(info, data) {
  return axios.post(`/servers/${info.connectionid}/storageGroups/${info.storagegroupid}/devices/${info.name}/randomImport`, data);
}
//编辑物理量数据
export function editData(info, data) {
  return axios.put(`/servers/${info.connectionid}/storageGroups/${info.storagegroupid}/devices/${info.name}/data`, data);
}
//删除物理量
export function deleteDeviceData(info, data) {
  return axios.delete(`/servers/${info.connectionid}/storageGroups/${info.storagegroupid}/devices/${info.name}/data`, { data });
}
//导出物理量数据
export function exportDataCSV(info, data) {
  return axios.post(`/servers/${info.connectionid}/storageGroups/${info.storagegroupid}/devices/${info.name}/exportData`, data, { responseType: 'blob' });
}
//文件导入
export function importData(info, data) {
  return axios.post(`/servers/${info.connectionid}/importData`, data);
}
//模板下载
export function downloadFile() {
  return axios.get(`/downloadFile/template`);
}
//指定设备下的物理量列表
export function getTimeseiresList(serverId, groupName, deviceName) {
  return axios.get(`/servers/${serverId}/storageGroups/${groupName}/devices/${deviceName}/timeseries`);
}
