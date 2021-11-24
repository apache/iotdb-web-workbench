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

//Get entity details
export function getDeviceDate(data) {
  return axios.get(`/servers/${data.connectionid}/storageGroups/${data.storagegroupid}/devices/${data.name}`);
}
//Get Measurement  list under entity
export function getList(data, params) {
  return axios.get(`/servers/${data.connectionid}/storageGroups/${data.storagegroupid}/devices/${data.name}/info`, { params: params });
}
//Obtain the trend of measuring point data
export function getData(serverId, groupName, deviceName, timeseriesName, params) {
  return axios.get(`/servers/${serverId}/storageGroups/${groupName}/devices/${deviceName}/timeseries/${timeseriesName}`, { params });
}
//Delete entity
export function deleteDevice(data) {
  return axios.delete(`/servers/${data.connectionid}/storageGroups/${data.storagegroupid}/devices/${data.name}`);
}
//Get data preview
export function getDataDeviceList(info, params, data) {
  return axios.post(`/servers/${info.connectionid}/storageGroups/${info.storagegroupid}/devices/${info.name}/data`, data, { params });
}
//Random import data
export function randomImport(info, data) {
  return axios.post(`/servers/${info.connectionid}/storageGroups/${info.storagegroupid}/devices/${info.name}/randomImport`, data);
}
//Edit Measurement  data
export function editData(info, data) {
  return axios.put(`/servers/${info.connectionid}/storageGroups/${info.storagegroupid}/devices/${info.name}/data`, data);
}
//delete Measurement
export function deleteDeviceData(info, data) {
  return axios.delete(`/servers/${info.connectionid}/storageGroups/${info.storagegroupid}/devices/${info.name}/data`, { data });
}
//Export Measurement data
export function exportDataCSV(info, data) {
  return axios.post(`/servers/${info.connectionid}/storageGroups/${info.storagegroupid}/devices/${info.name}/exportData`, data, { responseType: 'blob' });
}
//File import
export function importData(info, data) {
  return axios.post(`/servers/${info.connectionid}/importData`, data);
}
//Template download
export function downloadFile() {
  return axios.get(`/downloadFile/template`);
}
//List of physical quantities under the specified device
export function getTimeseiresList(serverId, groupName, deviceName) {
  return axios.get(`/servers/${serverId}/storageGroups/${groupName}/devices/${deviceName}/timeseries`);
}
