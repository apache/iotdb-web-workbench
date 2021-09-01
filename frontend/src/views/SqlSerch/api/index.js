import axios from '@/util/axios.js';

//用于查询器查询
export function querySql(serverId, data) {
  return axios.post(`/servers/${serverId}/querySql`, data);
}
//保存查询
export function saveQuery(serverId, data) {
  return axios.post(`/servers/${serverId}/query`, data);
}
//获取存储组列表
export function getGroup(serverId) {
  return axios.get(`/servers/${serverId}/storageGroups`);
}
//获取实体列表
export function getDevice(serverId, groupName) {
  return axios.get(`/servers/${serverId}/storageGroups/${groupName}/devices`);
}
//获取实体下物理量列表
export function getCList(serverId, groupName, deviceName, params) {
  return axios.get(`/servers/${serverId}/storageGroups/${groupName}/devices/${deviceName}/info`, { params: params });
}
//获取指定查询
export function getSql(serverId, queryId) {
  return axios.get(`/servers/${serverId}/query/${queryId}`);
}
//终止查询
export function queryStop(serverId, params) {
  return axios.get(`/servers/${serverId}/stop`, { params: params });
}
// 获取数据连接
export function getlink(params) {
  return axios.get(`/servers`, { params: params });
}

// 删除查询
export function deleteQueryS(serverId, queryId) {
  return axios.delete(`/servers/${serverId}/query/${queryId}`);
}
