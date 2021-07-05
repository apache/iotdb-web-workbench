import axios from '.axios';

//获取实体下物理量列表
export function getList(serverId, groupName, deviceName, data) {
  return axios.get(`/servers/${serverId}/storageGroups/${groupName}/devices/${deviceName}/info`, { params: data });
}
