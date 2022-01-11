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

export default {
  //get role list
  getRoles(serverId) {
    return axios.get(`/servers/${serverId}/roles`);
  },
  //new / edit role
  editRole(serverId, payload) {
    return axios.post(`/servers/${serverId}/roles`, payload);
  },
  //grant privilege to user
  grantUserRole(params, payload) {
    let { serverId, roleName } = params;
    return axios.post(`/servers/${serverId}/roles/${roleName}/grant`, payload);
  },
  //modify role authorityPrivilege
  editRolePrivilegePower(params, payload) {
    let { serverId, roleName } = params;
    return axios.post(`/servers/${serverId}/roles/${roleName}/authorityPrivilege`, payload);
  },
  //get role authorityPrivilege
  getRolePrivilegePower(params) {
    let { serverId, roleName } = params;
    return axios.get(`/servers/${serverId}/roles/${roleName}/authorityPrivilege`);
  },
  //get role info && user list
  getRoleInfo(params) {
    let { serverId, roleName } = params;
    return axios.get(`/servers/${serverId}/roles/${roleName}`);
  },
  //get user list
  getUsers(serverId) {
    return axios.get(`/servers/${serverId}/users`);
  },
  //delete role
  deleteRole(params) {
    let { serverId, roleName } = params;
    return axios.delete(`/servers/${serverId}/roles/${roleName}`);
  },
  //get role authorityPrivilege
  getAuthPrivilege(params) {
    let { serverId, roleName } = params;
    return axios.get(`/servers/${serverId}/roles/${roleName}/authorityPrivilege`);
  },
  //modify role authorityPrivilege
  editAuthPrivilege(params, payload) {
    let { serverId, roleName } = params;
    return axios.post(`/servers/${serverId}/roles/${roleName}/authorityPrivilege`, payload);
  },
  //get role dataPrivilege
  getDataPrivilege(params) {
    let { serverId, roleName } = params;
    return axios.get(`/servers/${serverId}/roles/${roleName}/dataPrivilege`);
  },
  //modify role dataPrivilege
  editDataPrivilege(params, payload) {
    let { serverId, roleName } = params;
    return axios.post(`/servers/${serverId}/roles/${roleName}/dataPrivilege`, payload);
  },
  // get storage list (tree structure)
  getStorageGroupTree(params) {
    let { serverId } = params;
    return axios.get(`/servers/${serverId}/storageGroups/nodeTree`);
  },
  //get storage list (list)
  getStorageGroup(params) {
    let { serverId } = params;
    return axios.get(`/servers/${serverId}/storageGroups`);
  },
  //get device list(tree structure)
  getDeviceTreeByGroup(params) {
    let { serverId, groupName } = params;
    return axios.get(`/servers/${serverId}/storageGroups/${groupName}/devices/nodeTree`);
  },
  //get device list(list)
  getDeviceByGroup(params) {
    let { serverId, groupName } = params;
    return axios.get(`/servers/${serverId}/storageGroups/${groupName}/devices`);
  },
  //get timeseries
  getTimeseries(params) {
    let { serverId, groupName, deviceName } = params;
    return axios.get(`/servers/${serverId}/storageGroups/${groupName}/devices/${deviceName}/timeseries`);
  },
  //modify user dataPrivilege
  editUserDataPrivilege(params, payload) {
    let { serverId, userName } = params;
    return axios.post(`/servers/${serverId}/users/${userName}/dataPrivilege`, payload);
  },
};
