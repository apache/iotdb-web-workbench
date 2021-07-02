import axios from '@/util/axios.js';

export function getDeviceDate(serverId, groupName, deviceName) {
  return axios.get(`/servers/${serverId}/storageGroups/${groupName}/devices/${deviceName}`);
}
