import axios from '@/util/axios.js';

//获取实体详情
export function getDeviceDate(serverId, groupName, deviceName) {
  return axios.get(`/servers/${serverId}/storageGroups/${groupName}/devices/${deviceName}`);
}
//获取实体下物理量列表
export function getList(serverId, groupName, deviceName, data) {
  return axios.get(`/servers/${serverId}/storageGroups/${groupName}/devices/${deviceName}/info`, { params: data });
}
//获取测点数据趋势
export function getData(serverId, groupName, deviceName, timeseriesName) {
  return axios.get(`/servers/${serverId}/storageGroups/${groupName}/devices/${deviceName}/timeseries/${timeseriesName}`);
}
