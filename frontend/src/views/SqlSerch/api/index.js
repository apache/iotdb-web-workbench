import axios from '@/util/axios.js';

//用于查询器查询
export function querySql(serverId, data) {
  return axios.post(`/servers/${serverId}/querySql`, data);
}
