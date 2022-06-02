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
<!-- Role data management permission -->
<template>
  <div class="data-manage">
    <el-button class="add-btn" type="primary" @click="addPermit">{{ $t('sourcePage.addAuthBtn') }}</el-button>
    <el-table :data="tableData" style="width: 100%">
      <el-table-column show-overflow-tooltip :label="$t('sourcePage.path')" width="80">
        <template #default="{ row }">
          <span>{{ pathMap[row.type] }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('sourcePage.range')" width="280">
        <template #default="scope">
          <div v-if="scope.row.type == 0">-</div>
          <div v-else-if="scope.row.type == 1">
            <span>{{ $t('sourcePage.groupNameLabel') }} {{ scope.row.groupPaths.length || 0 }} 个</span>
            <el-popover placement="top" width="180" trigger="hover">
              <div style="margin: 0">
                {{ $t('sourcePage.groupNameLabel') }}
                <div v-for="item in scope.row.groupPaths" :key="item" class="device-path">{{ item }}</div>
              </div>
              <template v-slot:reference>
                <div class="popover-btns">
                  <svg class="icon" aria-hidden="true">
                    <use xlink:href="#icon-se-icon-caret-bottom"></use>
                  </svg>
                </div>
              </template>
            </el-popover>
          </div>
          <div v-else-if="scope.row.type == 2">
            <span>{{ $t('sourcePage.groupNameLabel') }} 1 个</span> &nbsp;&nbsp;| &nbsp;&nbsp;
            <span>{{ $t('sourcePage.deviceNameLabel') }} {{ scope.row.devicePaths.length || 0 }} 个</span>
            <el-popover placement="top" width="180" trigger="hover">
              <div style="margin: 0">
                <p>{{ $t('sourcePage.groupNameLabel') }}</p>
                <div class="device-path">{{ scope.row.groupPaths[0] }}</div>
                <p>{{ $t('sourcePage.deviceNameLabel') }}</p>
                <div v-for="item in scope.row.devicePaths" :key="item" class="device-path">{{ item }}</div>
              </div>
              <template v-slot:reference>
                <div class="popover-btns">
                  <svg class="icon" aria-hidden="true">
                    <use xlink:href="#icon-se-icon-caret-bottom"></use>
                  </svg>
                </div>
              </template>
            </el-popover>
            <!-- </div> -->
          </div>
          <div v-else-if="scope.row.type == 3">
            <span>{{ $t('sourcePage.groupNameLabel') }} 1 个</span> &nbsp;&nbsp;| &nbsp;&nbsp; <span>{{ $t('sourcePage.deviceNameLabel') }} 1 个</span> &nbsp;&nbsp;| &nbsp;&nbsp;
            <span>{{ $t('sourcePage.timeNameLabel') }} {{ scope.row.timeseriesPaths.length || 0 }} 个</span>
            <el-popover placement="top" width="180" trigger="hover">
              <div style="margin: 0">
                <p>{{ $t('sourcePage.groupNameLabel') }}</p>
                <div class="device-path">{{ scope.row.groupPaths[0] }}</div>
                <p>{{ $t('sourcePage.deviceNameLabel') }}</p>
                <div class="device-path">{{ scope.row.devicePaths[0] }}</div>
                <p>{{ $t('sourcePage.timeNameLabel') }}</p>
                <div v-for="item in scope.row.timeseriesPaths" :key="item" class="device-path">{{ item }}</div>
              </div>
              <template v-slot:reference>
                <div class="popover-btns">
                  <svg class="icon" aria-hidden="true">
                    <use xlink:href="#icon-se-icon-caret-bottom"></use>
                  </svg>
                </div>
              </template>
            </el-popover>
          </div>
        </template>
      </el-table-column>
      <el-table-column :label="$t('sourcePage.func')">
        <template #default="scope">
          <div class="privilege-name">
            <span class="privilege-item" v-for="(item, index) in scope.row.privileges" :key="item">
              {{ privilegeMap[item] }}
              <span v-if="index !== scope.row.privileges.length - 1" class="divide"></span>
            </span>
          </div>
        </template>
      </el-table-column>

      <el-table-column :label="$t('common.operation')">
        <template #default="{ row }">
          <el-button type="text" size="small" @click="editPrivilege(row)">{{ $t('common.edit') }}</el-button>
          <el-popconfirm placement="top" :confirmButtonText="$t('common.submit')" :cancelButtonText="$t('common.cancel')" :title="$t('sourcePage.deleteAuthConfirm')" @confirm="deletePrivilege(row)">
            <template #reference>
              <el-button type="text" size="small" class="el-button-delete">{{ $t('common.delete') }}</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <permit-dialog ref="permitDialogRef" :name="roleInfo.roleName" dialog-origin="role" @submit="handleSubmit"></permit-dialog>
  </div>
</template>

<script>
import { ref, watchEffect, watch, getCurrentInstance, onActivated, onDeactivated } from 'vue';
import PermitDialog from '../permitDialog';
import api from '../../api/index';
import { useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useI18n } from 'vue-i18n';
import { useStore } from 'vuex';

export default {
  name: 'DataManage',
  props: {
    roleInfo: {
      type: Object,
      default: () => {},
    },
  },
  setup(props) {
    const { t, locale } = useI18n();
    const store = useStore();
    let canPrivilege = {};
    watchEffect(() => {
      canPrivilege = store.getters.canPrivilege;
    });
    let permitDialogRef = ref(null);
    let serverId = useRoute().params.serverid;
    let oldValue = ref({});
    let tableData = ref([]);
    const emitter = getCurrentInstance().appContext.config.globalProperties.emitter;

    let pathMap = ref({
      0: t('sourcePage.selectAlias'),
      1: t('sourcePage.selectGroup'),
      2: t('sourcePage.selectDevice'),
      3: t('sourcePage.selectTime'),
    });
    let privilegeMap = ref({
      SET_STORAGE_GROUP: t('sourcePage.createGroup'),
      CREATE_TIMESERIES: t('sourcePage.createTimeSeries'),
      INSERT_TIMESERIES: t('sourcePage.insertTimeSeries'),
      READ_TIMESERIES: t('sourcePage.readTimeSeries'),
      DELETE_TIMESERIES: t('sourcePage.deleteTimeSeries'),
    });
    let dataPrivileges = ref({
      0: [
        { id: 'SET_STORAGE_GROUP', label: t('sourcePage.createGroup') },
        {
          id: 'CREATE_TIMESERIES',
          label: t('sourcePage.createTimeSeries'),
        },
        {
          id: 'INSERT_TIMESERIES',
          label: t('sourcePage.insertTimeSeries'),
        },
        { id: 'READ_TIMESERIES', label: t('sourcePage.readTimeSeries') },
        {
          id: 'DELETE_TIMESERIES',
          label: t('sourcePage.deleteTimeSeries'),
        },
      ],
      1: [
        { id: 'SET_STORAGE_GROUP', label: t('sourcePage.createGroup') },
        {
          id: 'CREATE_TIMESERIES',
          label: t('sourcePage.createTimeSeries'),
        },
        {
          id: 'INSERT_TIMESERIES',
          label: t('sourcePage.insertTimeSeries'),
        },
        { id: 'READ_TIMESERIES', label: t('sourcePage.readTimeSeries') },
        {
          id: 'DELETE_TIMESERIES',
          label: t('sourcePage.deleteTimeSeries'),
        },
      ],
      2: [
        {
          id: 'CREATE_TIMESERIES',
          label: t('sourcePage.createTimeSeries'),
        },
        {
          id: 'INSERT_TIMESERIES',
          label: t('sourcePage.insertTimeSeries'),
        },
        { id: 'READ_TIMESERIES', label: t('sourcePage.readTimeSeries') },
        {
          id: 'DELETE_TIMESERIES',
          label: t('sourcePage.deleteTimeSeries'),
        },
      ],
      3: [
        {
          id: 'INSERT_TIMESERIES',
          label: t('sourcePage.insertTimeSeries'),
        },
        { id: 'READ_TIMESERIES', label: t('sourcePage.readTimeSeries') },
        {
          id: 'DELETE_TIMESERIES',
          label: t('sourcePage.deleteTimeSeries'),
        },
      ],
    });
    const handleSubmit = () => {
      getData();
    };
    const addPermit = () => {
      if (!canPrivilege.canGrantRolePrivilege) {
        ElMessage.error(t('sourcePage.noAuthTip'));
        return;
      }
      permitDialogRef.value.open({ type: 'add' });
    };

    const editPrivilege = (row) => {
      if (!canPrivilege.canGrantRolePrivilege) {
        ElMessage.error(t('sourcePage.noAuthTip'));
        return;
      }
      oldValue.value = row;
      permitDialogRef.value.open({ type: 'edit', data: row });
    };
    const getData = async () => {
      if (!props.roleInfo.roleName) return;
      let result = await api.getDataPrivilege({ serverId, roleName: props.roleInfo.roleName });
      tableData.value = result.data;
    };
    const deletePrivilege = async (row) => {
      if (!canPrivilege.canCancelRolePrivilege) {
        ElMessage.error(t('sourcePage.noAuthTip'));
        return;
      }
      let params = {
        serverId,
        roleName: props.roleInfo.roleName,
      };
      let { type } = row;
      let payload = { type: row.type };
      payload.cancelPrivileges = row.privileges;
      if ([1, 2, 3].includes(type)) {
        payload.delGroupPaths = row.groupPaths;
      }
      if ([2, 3].includes(type)) {
        payload.delDevicePaths = row.devicePaths;
      }
      if (type === 3) {
        payload.delTimeseriesPaths = row.timeseriesPaths;
      }
      let result = await api.editDataPrivilege(params, payload);
      if (result.code === '0') {
        ElMessage.success(t('common.deleteSuccess'));
        getData();
      }
    };
    onActivated(() => {
      emitter.on('change-tab', changeTab);
    });

    onDeactivated(() => {
      emitter.off('change-tab', changeTab);
    });
    const changeTab = () => {
      getData();
    };
    watch(
      () => props.roleInfo.roleName,
      (val) => {
        val && getData();
      },
      { immediate: true }
    );

    watch(locale, () => {
      pathMap.value = {
        0: t('sourcePage.selectAlias'),
        1: t('sourcePage.selectGroup'),
        2: t('sourcePage.selectDevice'),
        3: t('sourcePage.selectTime'),
      };
      (dataPrivileges.value = {
        0: [
          { id: 'SET_STORAGE_GROUP', label: t('sourcePage.createGroup') },
          {
            id: 'CREATE_TIMESERIES',
            label: t('sourcePage.createTimeSeries'),
          },
          {
            id: 'INSERT_TIMESERIES',
            label: t('sourcePage.insertTimeSeries'),
          },
          { id: 'READ_TIMESERIES', label: t('sourcePage.readTimeSeries') },
          {
            id: 'DELETE_TIMESERIES',
            label: t('sourcePage.deleteTimeSeries'),
          },
        ],
        1: [
          { id: 'SET_STORAGE_GROUP', label: t('sourcePage.createGroup') },
          {
            id: 'CREATE_TIMESERIES',
            label: t('sourcePage.createTimeSeries'),
          },
          {
            id: 'INSERT_TIMESERIES',
            label: t('sourcePage.insertTimeSeries'),
          },
          { id: 'READ_TIMESERIES', label: t('sourcePage.readTimeSeries') },
          {
            id: 'DELETE_TIMESERIES',
            label: t('sourcePage.deleteTimeSeries'),
          },
        ],
        2: [
          {
            id: 'CREATE_TIMESERIES',
            label: t('sourcePage.createTimeSeries'),
          },
          {
            id: 'INSERT_TIMESERIES',
            label: t('sourcePage.insertTimeSeries'),
          },
          { id: 'READ_TIMESERIES', label: t('sourcePage.readTimeSeries') },
          {
            id: 'DELETE_TIMESERIES',
            label: t('sourcePage.deleteTimeSeries'),
          },
        ],
        3: [
          {
            id: 'INSERT_TIMESERIES',
            label: t('sourcePage.insertTimeSeries'),
          },
          { id: 'READ_TIMESERIES', label: t('sourcePage.readTimeSeries') },
          {
            id: 'DELETE_TIMESERIES',
            label: t('sourcePage.deleteTimeSeries'),
          },
        ],
      }),
        (privilegeMap.value = {
          SET_STORAGE_GROUP: t('sourcePage.createGroup'),
          CREATE_TIMESERIES: t('sourcePage.createTimeSeries'),
          INSERT_TIMESERIES: t('sourcePage.insertTimeSeries'),
          READ_TIMESERIES: t('sourcePage.readTimeSeries'),
          DELETE_TIMESERIES: t('sourcePage.deleteTimeSeries'),
        });
    });
    return {
      t,
      locale,
      permitDialogRef,
      addPermit,
      pathMap,
      tableData,
      editPrivilege,
      deletePrivilege,
      dataPrivileges,
      privilegeMap,
      getData,
      handleSubmit,
    };
  },
  components: {
    PermitDialog,
  },
};
</script>
<style scoped lang="scss">
.data-manage {
  overflow: auto;
  &:deep(.add-btn) {
    margin-bottom: 15px;
  }

  .popover-btns {
    display: inline-block;
  }
}
.privilege-name {
  display: flex;
  flex-wrap: wrap;

  .privilege-item {
    display: flex;
    align-items: center;
    line-height: 24px;

    .divide {
      background: $border-color;
      width: 1px;
      margin: 0 10px;
      height: 16px;
      display: inline-block;
    }
  }
}
.show-only {
  &:deep(.el-checkbox) {
    display: none;
  }
  &:deep(.is-checked) {
    display: inline-block !important;
    .is-checked {
      display: none !important;
    }
  }
  &:deep(.el-checkbox__input) {
    display: none;
  }
}
</style>
