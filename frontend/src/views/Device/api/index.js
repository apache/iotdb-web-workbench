import axios from "@/util/axios.js";

export function getDeviceDate(serverId, groupName) {
  return axios.post(`/servers/${serverId}/storageGroups/${groupName}/devices`);
}
