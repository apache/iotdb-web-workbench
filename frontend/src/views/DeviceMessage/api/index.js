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
export function getData(serverId, groupName, deviceName, timeseriesName) {
  return axios.get(`/servers/${serverId}/storageGroups/${groupName}/devices/${deviceName}/timeseries/${timeseriesName}`);
}
//删除实体
export function deleteDevice(data) {
  return axios.delete(`/servers/${data.connectionid}/storageGroups/${data.storagegroupid}/devices/${data.name}`);
}
