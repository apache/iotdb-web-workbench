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

import axios from 'axios';
import { ElMessage } from 'element-plus';
import router from '../router';

const instance = axios.create({});
const headerUrls = ['/api/login', '/api/loginWithCasdoor', '/api/downloadFile/template', '/api/downloadQueryLogFile'];
const exportUrl = '/exportData';
const downUrl = '/downloadFile';
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
    if (response.config.url.indexOf(exportUrl) !== -1 || response.config.url.indexOf(downUrl) !== -1) {
      return response.data;
    }
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
