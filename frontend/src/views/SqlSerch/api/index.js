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

// sql query
export function querySql(serverId, data) {
  return axios.post(`/servers/${serverId}/querySql`, data);
}
//save sql
export function saveQuery(serverId, data) {
  return axios.post(`/servers/${serverId}/query`, data);
}
//Get storage group list
export function getGroup(serverId) {
  return axios.get(`/servers/${serverId}/storageGroups`);
}
//Get entity list
export function getDevice(serverId, groupName) {
  return axios.get(`/servers/${serverId}/storageGroups/${groupName}/devices`);
}
//Get physical quantity list under entity
export function getCList(serverId, groupName, deviceName, params) {
  return axios.get(`/servers/${serverId}/storageGroups/${groupName}/devices/${deviceName}/info`, { params: params });
}
//Gets the specified query
export function getSql(serverId, queryId) {
  return axios.get(`/servers/${serverId}/query/${queryId}`);
}
//Terminate query
export function queryStop(serverId, params) {
  return axios.get(`/servers/${serverId}/stop`, { params: params });
}
// Get data connection
export function getlink(params) {
  return axios.get(`/servers`, { params: params });
}
// Delete query
export function deleteQueryS(serverId, queryId) {
  return axios.delete(`/servers/${serverId}/query/${queryId}`);
}
// Import query
export function exportDataSql(serverId, params) {
  return axios.get(`/servers/${serverId}/exportData`, { params });
}
