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
<!-- role list -->
<template>
  <div class="list-wrapper">
    <div class="user-title">
      <span class="title">{{ $t('sourcePage.roleList') }}</span>
      <div class="btn">
        <svg class="icon" aria-hidden="true" @click="addRole">
          <use xlink:href="#icon-add1"></use>
        </svg>
      </div>
    </div>
    <ul v-if="roleList?.length" class="role-list">
      <li v-for="item in roleList" :key="item" :class="[activeRole === item ? 'active-item' : '']" class="role-list-item" @click="clickRole(item)">
        <div :class="[activeRole === item ? 'circle active-circle' : 'circle']">
          <div class="small-circle"></div>
        </div>
        {{ item }}
        <div class="operate">
          <svg class="icon" aria-hidden="true" v-if="!isAdding && activeRole === item" @click.stop="editRole(item)">
            <use xlink:href="#icon-se-icon-f-edit"></use>
          </svg>
          <el-popconfirm placement="top" :confirmButtonText="$t('common.submit')" :cancelButtonText="$t('common.cancel')" :title="$t('sourcePage.deleteRoleConfirm')" @confirm="deleteRole(item)">
            <template #reference>
              <span class="icon-del del-user">
                <svg v-if="activeRole === item" class="icon delete" aria-hidden="true">
                  <use xlink:href="#icon-se-icon-delete"></use>
                </svg>
              </span>
            </template>
          </el-popconfirm>
        </div>
      </li>
    </ul>
    <div v-else class="no-data">{{ $t('common.noData') }}</div>
  </div>
</template>

<script>
import { useI18n } from 'vue-i18n';
import { ref, onActivated, onDeactivated, getCurrentInstance, watchEffect } from 'vue';
import { useRoute } from 'vue-router';
import api from '../../api/index';
import { ElMessage } from 'element-plus';
import { useStore } from 'vuex';

export default {
  name: 'RoleList',
  props: [],
  setup(props, { emit }) {
    const store = useStore();
    let canPrivilege = {};
    watchEffect(() => {
      canPrivilege = store.getters.canPrivilege;
    });
    const { t, locale } = useI18n();
    let roleList = ref([]);
    let activeRole = ref(1);
    let serverId = useRoute().params.serverid;
    const emitter = getCurrentInstance().appContext.config.globalProperties.emitter;
    let isAdding = ref(false);

    // Get all roles
    let getRoleList = async (roleName) => {
      isAdding.value = false;
      let result = await api.getRoles(serverId);
      roleList.value = result.data;
      if (!roleList.value?.length) {
        emit('changeCurrRole', { id: '', name: 'NEW' });
        activeRole.value = '';
      } else {
        clickRole(roleName ? roleName : result?.data[0]);
      }
      emit('roleList', roleList.value);
    };

    const addRole = () => {
      if (isAdding.value) {
        ElMessage.error(t('sourcePage.addRoleTip'));
        return;
      }
      if (!canPrivilege.canAddRole) {
        ElMessage.error(t('sourcePage.noAuthTip'));
        return;
      }
      roleList.value.unshift('NEW');
      activeRole.value = 'NEW';
      emit('changeCurrRole', { id: '', name: 'NEW', type: 'add' });
      isAdding.value = true;
    };
    const clickRole = async (item) => {
      if (isAdding.value) {
        ElMessage.error(t('sourcePage.addRoleTip'));
        return;
      }
      activeRole.value = item;
      let roleInfo = await api.getRoleInfo({ serverId, roleName: item });
      let privileges = await api.getAuthPrivilege({ serverId, roleName: item });
      emit('changeCurrRole', { ...roleInfo.data, roleName: item, privileges: privileges.data, type: 'view' });
    };
    const editRole = async (item) => {
      if (isAdding.value) {
        ElMessage.error(t('sourcePage.addRoleTip'));
        return;
      }
      activeRole.value = item;
      let roleInfo = await api.getRoleInfo({ serverId, roleName: item });
      let privileges = await api.getAuthPrivilege({ serverId, roleName: item });
      emit('changeCurrRole', { ...roleInfo.data, roleName: item, privileges: privileges.data, type: 'edit' });
    };
    const deleteRole = async (item) => {
      if (isAdding.value) {
        ElMessage.error(t('sourcePage.addRoleTip'));
        return;
      }
      if (!canPrivilege.canDeleteRole) {
        ElMessage.error(t('sourcePage.noAuthTip'));
        return;
      }

      await api.deleteRole({ serverId, roleName: item });
      ElMessage.success(t('common.deleteSuccess'));
      getRoleList();
    };
    const cancelAdd = () => {
      //When adding a new role, click Cancel to exit the new status and lock the first role
      if (isAdding.value) {
        roleList.value.splice(0, 1);
        isAdding.value = false;
        clickRole(roleList?.value[0]);
      } else {
        //When editing, click Cancel to exit the editing status
        clickRole(activeRole.value);
      }
    };
    const changeTab = () => {
      activeRole.value && clickRole(activeRole.value);
    };

    onActivated(() => {
      getRoleList();
      emitter.on('add-role', getRoleList);
      emitter.on('change-tab', changeTab);
      emitter.on('cancel-add-role', cancelAdd);
    });
    onDeactivated(() => {
      emitter.off('add-role', getRoleList);
      emitter.off('cancel-add-role', cancelAdd);
      emitter.off('change-tab', changeTab);
    });

    return {
      t,
      locale,
      roleList,
      getRoleList,
      activeRole,
      addRole,
      clickRole,
      deleteRole,
      editRole,
      cancelAdd,
    };
  },
  methods: {},
};
</script>
<style scoped lang="scss">
.list-wrapper {
  flex-basis: 220px;
  flex-grow: 0;
  flex-shrink: 0;
  height: 100%;
  font-size: 12px;
  text-align: center;

  .no-data {
    color: #7a859e;
    padding-top: 30px;
    height: calc(100% - 44px);
    width: 100%;
    background-color: #f9fbfc;
    margin-top: 4px;
    box-sizing: border-box;
  }

  .user-title {
    padding: 10px 20px;
    display: flex;
    justify-content: space-between;
    background-color: #f9fbfc;
    color: #808ba3;
    height: 40px;
    align-items: center;
    box-sizing: border-box;
    .btn {
      font-size: 14px;
      cursor: pointer;
    }
  }
  .role-list {
    background-color: #f9fbfc;
    margin-top: 4px;
    padding: 10px 0 10px 10px;
    box-sizing: border-box;
    color: #7a859e;
    height: calc(100% - 44px);
    overflow: auto;
    .active-item {
      background: #fff;
    }
    &-item {
      width: 100%;
      height: 40px;
      line-height: 20px;
      padding: 10px;
      box-sizing: border-box;
      display: flex;
      position: relative;
      cursor: pointer;
      border-radius: 30px 0 0 30px;

      .circle {
        width: 20px;
        height: 20px;
        background: #f9fbfc;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 10px;
      }

      .small-circle {
        width: 6px;
        height: 6px;
        background: #f9fbfc;
        border-radius: 50%;
        position: absolute;
      }

      &:hover {
        background: #fff;
        color: #333;
        .circle {
          background: #fff;
          .small-circle {
            background: #fff;
          }
        }
      }
      .circle.active-circle {
        background: #edf8f5;
        .small-circle {
          background: #13c393;
        }
      }
      .operate {
        position: absolute;
        right: 10px;
        .icon {
          cursor: pointer;
        }
        .delete {
          margin-left: 15px;
        }
      }
    }
  }
}
</style>
