import axios from 'axios';
import { ElMessage } from 'element-plus';
import router from '../router';

const instance = axios.create({});
const headerUrls = ['/api/login'];
instance.defaults.withCredentials = true;
instance.defaults.timeout = 100000;
instance.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded';

function trim(s) {
  return s.replace(/(^\s*)|(\s*$)/g, '');
}

function isString(str) {
  return typeof str == 'string' && str.constructor == String;
}

function filter(data) {
  if (data) {
    for (let key in data) {
      data[key] === undefined && delete data[key];
      isString(data[key]) && (data[key] = trim(data[key]));
      if (data[key] && typeof data[key] === 'object') {
        for (let innerKey in data[key]) {
          data[key][innerKey] === undefined && delete data[key][innerKey];
          isString(data[key][innerKey]) && trim(data[key][innerKey]);
        }
      }
    }
  }
}

instance.interceptors.request.use((request) => {
  request.url = '/api' + request.url;
  request.headers.common.Authorization = localStorage.getItem('authorization') || '';
  filter(request.params || request.data);
  return request;
});

instance.interceptors.response.use(
  (response) => {
    if (response.data.code === 'USER-0009') {
      router.push('/login');
      return {};
    }
    if (headerUrls.indexOf(response.config.url) !== -1) {
      return response;
    }
    // if (response && response.data) {
    if (response && response.data && response.data.code == '0') {
      return response.data;
    } else {
      ElMessage.error(response.data.message);
      return {};
    }
  },
  (error) => {
    ElMessage.error('网络错误');
    return Promise.reject(error);
  }
);

export default instance;
