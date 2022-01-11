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
import axios from '@/util/axios.js';

const dataBaseM = {
  state: () => ({
    privilegeListAll: [],
    currRouteParams: null,
  }),

  getters: {
    canPrivilege(state) {
      return {
        canViewRole: state.privilegeListAll.includes('LIST_ROLE'),
        canAddRole: state.privilegeListAll.includes('CREATE_ROLE'),
        canDeleteRole: state.privilegeListAll.includes('DELETE_ROLE'),
        canGrantRolePrivilege: state.privilegeListAll.includes('GRANT_ROLE_PRIVILEGE'),
        canCancelRolePrivilege: state.privilegeListAll.includes('REVOKE_ROLE_PRIVILEGE'),
        canGrantUserRole: state.privilegeListAll.includes('GRANT_USER_ROLE'),
        canCancelUserRole: state.privilegeListAll.includes('REVOKE_USER_ROLE'),
      };
    },
  },

  mutations: {
    setPrivilegeListAll(state, data) {
      state.privilegeListAll = data;
    },
    setCurrRouteParams(state, data) {
      state.currRouteParams = data;
    },
  },
  actions: {
    async fetchAllPrivileges({ commit }, params) {
      axios.get(`/servers/${params.serverId}/users/${params.userName}/allAuthorityPrivilege`, {}).then((res) => {
        commit('setPrivilegeListAll', res?.code === '0' ? res.data || [] : []);
      });
    },
  },
};
export default dataBaseM;
