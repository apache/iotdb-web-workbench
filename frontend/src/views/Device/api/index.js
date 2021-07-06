import axios from '@/util/axios.js';

//获取实体详情
export function getDeviceDate(deviceData) {
  return axios.get(`/servers/${deviceData.id}/storageGroups/${deviceData.groupName}/devices/${deviceData.deviceName}`);
}
//获取实体下物理量列表
export function getList(deviceData, data) {
  return axios.get(`/servers/${deviceData.id}/storageGroups/${deviceData.groupName}/devices/${deviceData.deviceName}/info`, { params: data });
}
//删除物理量
export function deleteData(deviceData, timeseriesName) {
  return axios.delete(`/servers/${deviceData.id}/storageGroups/${deviceData.groupName}/devices/${deviceData.deviceName}/timeseries/${timeseriesName}`);
}
//新增/编辑实体信息
export function deviceAddEdite(serverId, groupName, data) {
  return axios.post(`/servers/${serverId}/storageGroups/${groupName}/devices`, data);
}
