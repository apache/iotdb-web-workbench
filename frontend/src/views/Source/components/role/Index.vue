<!--
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
-->
<!-- 用户角色 -->
<template>
  <div class="user-role-wrapper">
    <role-list :server-id="serverId" @changeCurrRole="changeRole" @roleList="refreshList"></role-list>
    <power-manage :current="currentRole" :role-list="roleList" :base-info="baseInfo"></power-manage>
  </div>
</template>

<script>
import { ref } from 'vue';
import RoleList from './RoleList.vue';
import PowerManage from './PowerManage.vue';
export default {
  name: 'RoleInfo',
  props: {
    baseInfo: {
      type: Object,
      default: () => {},
    },
  },
  components: {
    RoleList,
    PowerManage,
  },
  setup() {
    let currentRole = ref({});
    let roleList = ref([]);
    const changeRole = (role) => {
      currentRole.value = role;
    };

    const refreshList = (list) => {
      roleList.value = list;
    };
    return {
      changeRole,
      currentRole,
      refreshList,
      roleList,
    };
  },
};
</script>
<style lang="scss" scoped>
.user-role-wrapper {
  width: 100%;
  height: 100%;
  display: flex;
}
</style>
