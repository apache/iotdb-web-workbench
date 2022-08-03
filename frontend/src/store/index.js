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
import { createStore } from 'vuex';
import moduleA from './moduleA';
import axios from '@/util/axios.js';
// import storage from './storage';
import dataBaseM from './dataBaseM';

export default createStore({
  state: () => ({
    isLogin: false,
    userInfo: {},
    firstPageLoad: true,
  }),
  mutations: {
    setFirstPageLoad(state, bool) {
      state.firstPageLoad = bool;
    },
    setLogin(state, islogin) {
      state.isLogin = islogin;
    },
    setUserInfo(state, info) {
      state.userInfo = info.data;
    },
  },
  actions: {
    async fetchIsLogin({ commit }) {
      axios.get('/get', {}).then((res) => {
        if (res?.code === '0') {
          commit('setLogin', true);
          commit('setUserInfo', {
            data: {
              name: res?.data?.name,
              userId: res?.data?.id,
            },
          });
        }
      });
    },
  },
  modules: {
    a: moduleA,
    // storage,
    dataBaseM: dataBaseM,
  },
});
