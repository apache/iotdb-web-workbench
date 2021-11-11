<!--
  - Licensed to the Apache Software Foundation (ASF) under one
  - or more contributor license agreements.  See the NOTICE file
  - distributed with this work for additional information
  - regarding copyright ownership.  The ASF licenses this file
  - to you under the Apache License, Version 2.0 (the
  - "License"); you may not use this file except in compliance
  - with the License.  You may obtain a copy of the License at
  -
  -   http://www.apache.org/licenses/LICENSE-2.0
  -
  - Unless required by applicable law or agreed to in writing,
  - software distributed under the License is distributed on an
  - "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  - KIND, either express or implied.  See the License for the
  - specific language governing permissions and limitations
  - under the License.
  -->

<template>
  <div class="storage-container">
    <div class="base-info">
      <div class="btns">
        <svg class="icon" aria-hidden="true" @click="editGroup()">
          <use xlink:href="#icon-se-icon-f-edit"></use>
        </svg>
        <el-popconfirm placement="top" :title="$t('storagePage.deleteGroupConfirm')" @confirm="deleteGroup()">
          <template #reference>
            <span class="icon-del">
              <svg class="icon" aria-hidden="true">
                <use xlink:href="#icon-se-icon-delete"></use>
              </svg>
            </span>
          </template>
        </el-popconfirm>
      </div>
      <el-descriptions :title="baseInfo.groupName">
        <el-descriptions-item :label="$t('storagePage.alias') + ':'">{{ baseInfo.alias }}</el-descriptions-item>
        <el-descriptions-item :label="$t('storagePage.creator') + ':'">{{ baseInfo.creator }}</el-descriptions-item>
        <el-descriptions-item :label="$t('storagePage.createTime')">{{ baseInfo.createTime }}</el-descriptions-item>
        <el-descriptions-item v-if="baseInfo.ttl" :label="$t('storagePage.ttl')"> {{ baseInfo.ttl }}{{ ttlValue[baseInfo.ttiUnit] }} </el-descriptions-item>
        <el-descriptions-item v-else :label="$t('storagePage.ttl')"> ∞ </el-descriptions-item>
        <el-descriptions-item :label="$t('storagePage.description') + ':'">{{ baseInfo.description }}</el-descriptions-item>
      </el-descriptions>
    </div>
    <div class="device-content">
      <div class="search-panel clearfix">
        <span class="search-title">{{ $t('storagePage.deviceName') }}</span>
        <el-input v-model="searchVal" suffix-icon="el-icon-search" @blur="search()" @keyup.enter="search()"></el-input>
        <el-button type="primary" class="search-btn" @click="newDevice">{{ $t('storagePage.newDevice') }}</el-button>
      </div>
      <div class="device-list">
        <!-- @selection-change="handleSelectionChange" -->
        <el-table :data="tableData" style="width: 100%">
          <!-- <el-table-column type="selection" width="55"> </el-table-column> -->
          <el-table-column show-overflow-tooltip prop="deviceName" :label="$t('device.devicename')" width="180" sortable> </el-table-column>
          <el-table-column show-overflow-tooltip prop="description" :label="$t('device.description')"> </el-table-column>
          <el-table-column prop="line" :label="$t('storagePage.line')"> </el-table-column>
          <el-table-column prop="creator" :label="$t('storagePage.creator')"> </el-table-column>
          <el-table-column :label="$t('storagePage.operation')">
            <template #default="scope">
              <el-button type="text" size="small" @click="editDevice(scope.row)">{{ $t('common.edit') }}{{ scope.row.ttl }}</el-button>
              <el-popconfirm placement="top" :title="$t('storagePage.deleteDeviceConfirm')" @confirm="deleteDevice(scope)">
                <template #reference>
                  <el-button type="text" size="small" class="el-button-delete">{{ $t('common.delete') }}</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination @current-change="handleCurrentChange" :currentPage="currentPage" :page-size="pageSize" layout="total, prev, pager, next" :total="total"> </el-pagination>
      </div>
    </div>
  </div>
</template>

<script>
// @ is an alias to /src
import { onActivated, onMounted, ref, watch } from 'vue';
import { ElDescriptions, ElDescriptionsItem, ElInput, ElButton, ElTable, ElTableColumn, ElPagination, ElMessage, ElPopconfirm } from 'element-plus';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';
import axios from '@/util/axios.js';

export default {
  name: 'Storage',
  props: ['data', 'func'],
  setup(props) {
    const { t, locale } = useI18n();

    const router = useRouter();
    let baseInfo = ref({});
    let searchVal = ref(null);
    let tableData = ref([]);
    let currentPage = ref(1);
    const pageSize = ref(10);
    let total = ref(0);
    let ttlMap = () => {
      return {
        second: t('storagePage.secondLabel'),
        minute: t('storagePage.minuteLabel'),
        hour: t('storagePage.hourLabel'),
        day: t('storagePage.dayLabel'),
        week: t('storagePage.weekLabel'),
        month: t('storagePage.monthLabel'),
        year: t('storagePage.yearLabel'),
      };
    };
    let ttlValue = ref({
      second: t('storagePage.secondLabel'),
      minute: t('storagePage.minuteLabel'),
      hour: t('storagePage.hourLabel'),
      day: t('storagePage.dayLabel'),
      week: t('storagePage.weekLabel'),
      month: t('storagePage.monthLabel'),
      year: t('storagePage.yearLabel'),
    });
    // const handleSelectionChange = (selection) => {
    //   console.log(selection);
    // };
    watch(locale, () => {
      ttlValue.value = ttlMap();
    });
    const handleCurrentChange = (val) => {
      currentPage.value = val;
      getDeviceList();
    };
    /**
     * 实体名称搜索
     */
    const search = () => {
      currentPage.value = 1;
      getDeviceList();
    };
    /**
     * 编辑存储组信息
     */
    const editGroup = () => {
      router.push({
        name: 'EditStorage',
        params: { serverid: router.currentRoute.value.params.serverid, groupname: baseInfo.value.groupName },
      });
    };
    /**
     * 删除存储组信息
     */
    const deleteGroup = () => {
      axios.delete(`/servers/${router.currentRoute.value.params.serverid}/storageGroups/${baseInfo.value.groupName}`).then((rs) => {
        if (rs && rs.code == 0) {
          ElMessage.success(t('sourcePage.deleteGroupLabel'));
          props.func.updateTree();
          props.func.removeTab(props.data.id);
        }
      });
    };
    /**
     * 获取存储组详情
     * serverid: 连接id
     * groupname:存储组名
     */
    const getGroupDetail = () => {
      axios.get(`/servers/${router.currentRoute.value.params.serverid}/storageGroups/${router.currentRoute.value.params.groupname}`, {}).then((res) => {
        if (res && res.code == 0) {
          baseInfo.value = res.data;
        }
      });
    };
    /**
     * 获取存储组下面的实体列表
     * serverid: 连接id
     * groupname:存储组名
     */
    const getDeviceList = () => {
      axios
        .get(`/servers/${router.currentRoute.value.params.serverid}/storageGroups/${router.currentRoute.value.params.groupname}/devices/info`, {
          params: {
            pageSize: pageSize.value,
            pageNum: currentPage.value,
            keyword: searchVal.value || null,
          },
        })
        .then((res) => {
          if (res && res.code == 0) {
            tableData.value = res.data.deviceInfos || [];
            total.value = res.data.totalCount || 0;
          } else {
            tableData.value = [];
            total.value = 0;
          }
        });
    };
    /**
     * 删除实体
     * scope:要被删除的实体的信息
     */
    const deleteDevice = (scope) => {
      axios.delete(`/servers/${props.data.connectionid}/storageGroups/${props.data.storagegroupid}/devices/${scope.row.deviceName}`).then((res) => {
        if (res.code === '0') {
          ElMessage({
            type: 'success',
            message: `${t('device.deleteSuccess')}!`,
          });
          getDeviceList();
        }
      });
    };
    /**
     * 编辑实体
     * row:要被删除的实体的信息
     */
    const editDevice = (row) => {
      console.log(row);
      console.log(props.data);
      router.push({
        name: 'Device',
        params: { name: row.deviceName, connectionid: props.data.connectionid, storagegroupid: props.data.storagegroupid, parentids: props.data.parent.name, dflag: true },
      });
      // props.func.updateTree([props.data.parent.id, props.data.id]);
      // props.func.expandByIds([props.data.parent.id, props.data.id, `${props.data.id}${row.deviceName}device`]);
      // props.func.addTab(`${props.data.id}${row.deviceName}device`);
    };
    /**
     * 新建实体
     */
    const newDevice = () => {
      router.push({
        name: 'Device',
        params: { name: '新建实体', connectionid: props.data.connectionid, storagegroupid: props.data.storagegroupid, parentids: props.data.parent.name, dflag: true, type: 'newdevice' },
      });
      // props.func.updateTree([props.data.parent.id, props.data.id]);
      // props.func.addTab(`${props.data.id}:newdevice`, { getList: getDeviceList, dflag: true });
      // props.func.removeTab(props.data.id);
    };
    onActivated(() => {
      getGroupDetail();
      getDeviceList();
    });
    onMounted(() => {
      getGroupDetail();
      getDeviceList();
    });

    return {
      t,
      baseInfo,
      searchVal,
      tableData,
      currentPage,
      pageSize,
      total,
      // handleSelectionChange,
      newDevice,
      handleCurrentChange,
      editGroup,
      editDevice,
      deleteGroup,
      getGroupDetail,
      getDeviceList,
      deleteDevice,
      search,
      ttlValue,
    };
  },
  components: {
    ElDescriptions,
    ElDescriptionsItem,
    ElInput,
    ElButton,
    ElTable,
    ElTableColumn,
    ElPagination,
    ElPopconfirm,
  },
};
</script>

<style scoped lang="scss">
.storage-container {
  height: 100%;
  text-align: left;
  .base-info,
  .device-content {
    padding: 20px;
  }
  .base-info {
    border-bottom: 1px solid #e0e0e0;
    position: relative;
    .btns {
      position: absolute;
      right: 20px;
      .icon {
        cursor: pointer;
      }

      .icon:first-child {
        margin-right: 20px;
        color: $theme-color;
      }
      .icon:last-child {
        margin-right: 0;
        color: #d32d2fff;
      }
    }
  }
  .device-content {
    .search-panel {
      .search-title {
        font-size: 14px;
      }
      .el-input {
        width: 300px;
        margin-left: 10px;
      }
      .search-btn {
        float: right;
      }
    }
    .device-list {
      .el-button--text {
        padding-left: 0 !important;
      }
    }
    .el-pagination {
      text-align: right;
      margin-top: 16px;
    }
  }
}
</style>
