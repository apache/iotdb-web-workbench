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
  <div class="data-list-tree">
    <div class="data-list-top">
      <span>{{ $t('rootPage.dataList') }}</span>
      <el-tooltip :content="$t('rootPage.newQueryWindow')" :visible-arrow="false" effect="light">
        <div class="icon-1">
          <svg class="icon" @click="sqlClick" aria-hidden="true" v-icon="`#icon-xinjianchaxun-color`">
            <use xlink:href="#icon-xinjianchaxun"></use>
          </svg>
        </div>
      </el-tooltip>
      <el-tooltip :content="$t('rootPage.newdatasource')" :visible-arrow="false" effect="light">
        <div class="icon-2">
          <svg v-icon="`#icon-xinzengshujulianjie-color`" class="icon" aria-hidden="true" @click="newSource">
            <use xlink:href="#icon-xinzengshujulianjie"></use>
          </svg>
        </div>
      </el-tooltip>
    </div>
    <!-- <div class="data-list-input">
      <el-input size="small" placeholder="" v-model="searchVal">
        <template #suffix>
          <i @click="searchClick" class="el-icon-search"></i>
        </template>
      </el-input>
    </div> -->
    <el-tree
      :expand-on-click-node="true"
      :default-expanded-keys="treeExpandKey"
      v-if="store.state?.userInfo?.userId !== undefined"
      ref="treeRef"
      highlight-current
      node-key="id"
      :indent="20"
      :props="treeProps"
      @node-click="nodeClick"
      :load="loadNode"
      lazy
      :current-node-key="nodekey"
      :key="treeKey"
      @node-expand="expandNode"
      @node-collapse="collapseNode"
    >
      <template #default="{ node, data }">
        <span class="custom-tree-node">
          <icon-types :data="data" :nodekey="nodekey" />
          <span>{{ node.label }}</span>
        </span>
      </template>
    </el-tree>
    <NewSource v-if="showDialog" :func="func" :serverId="null" :showDialog="showDialog" :types="types" @close="close()" @successFunc="successFunc(data)" />
    <SqlDrawer v-if="showDrawer" :func="funcdata" @coloseDrawer="coloseDrawer"></SqlDrawer>
  </div>
</template>

<script>
import { ElTree, ElTooltip } from 'element-plus';
import { reactive, ref, computed } from 'vue';
import { useStore } from 'vuex';
import IconTypes from './iconTypes.vue';
import axios from '@/util/axios.js';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import NewSource from '../../Source/components/newSource';
import SqlDrawer from '../../SqlSerch/components/sqlDrawer';

export default {
  name: 'DataListTree',
  props: ['handleNodeClick', 'nodekey', 'func'],
  setup(props) {
    const treeProps = reactive({
      label: 'name',
      children: 'zones',
      isLeaf: 'leaf',
      disabled: 'disabled',
    });
    const searchVal = ref('');
    const treeRef = ref(null);
    const treeExpandKey = ref([]);
    const store = useStore();
    const showDialog = ref(false);
    const showDrawer = ref(false);
    const types = ref(null);
    const treeKey = ref(1);
    const funcdata = reactive(props.func);
    const router = useRouter();
    const { t } = useI18n();

    const searchClick = () => {
      console.log('jj');
    };

    const nodeClick = (data, node) => {
      props.handleNodeClick(data, node);
    };

    /**
     * 新建数据连接
     */
    const newSource = () => {
      showDialog.value = true;
      types.value = 0;
    };
    /**
     * 关闭或者取消新增/编辑数据连接操作
     */
    const close = () => {
      showDialog.value = false;
      types.value = 0;
    };
    /**
     * 新增或编辑数据源成功回调
     */
    const successFunc = () => {
      showDialog.value = false;
      types.value = 0;
    };

    const sqlClick = () => {
      showDrawer.value = true;
    };

    const updateTree = (params, clear) => {
      if (params) {
        let arr = treeExpandKey.value;
        if (clear) {
          arr = [];
        }
        arr = arr.concat(params);
        treeExpandKey.value = arr;
      }
      treeKey.value += 1;
    };

    const coloseDrawer = () => {
      showDrawer.value = false;
    };

    const expandNode = (data) => {
      let arr = treeExpandKey.value;
      arr.push(data.id);
      treeExpandKey.value = arr;
    };

    const collapseNode = (data) => {
      let arr = treeExpandKey.value;
      let index = arr.indexOf(data.id);
      if (index !== -1) {
        arr.splice(index, 1);
      }
      treeExpandKey.value = arr;
    };

    const loadNode = (node, resolve) => {
      if (node.level === 0) {
        axios
          .get('/servers', { params: { userId: store.state?.userInfo?.userId } })
          .then((res) => {
            if (res?.code === '0') {
              let data = (res.data.aliasList || []).map((e) => {
                return {
                  name: e.alias,
                  id: e.id + 'connection',
                  type: 'connection',
                  rawid: e.id,
                  connectionid: e.id,
                };
              });
              if (data.length === 0) {
                router.push({ name: 'Empty' });
              }
              if (data.length > 0 && store.state.firstPageLoad) {
                // router.push({ name: 'Root' });
                props.func.addTab(data[0].id, {}, true);
              }
              store.commit('setFirstPageLoad', false);
              return resolve(data);
            } else {
              resolve([]);
            }
          })
          .catch(() => {
            resolve([]);
          });
      }
      if (node.level === 1) {
        axios
          .get(`/servers/${node.data.rawid}/storageGroups`, {})
          .then((res) => {
            let newStorageGroup = {
              id: node.data.id + ':newstoragegroup',
              name: computed(() => t(`databasem.newStoreGroup`)),
              parent: node.data,
              type: 'newstorageGroup',
              leaf: true,
              connectionid: node.data.connectionid,
            };
            let queryList = {
              id: node.data.id + ':querylist',
              name: computed(() => t(`databasem.query`)),
              parent: node.data,
              type: 'querylist',
              connectionid: node.data.connectionid,
            };
            if (res?.code === '0') {
              let data = (res.data || []).map((e) => {
                return {
                  parent: node.data,
                  name: e.groupName,
                  id: node.data.id + e.groupName + 'storageGroup',
                  type: 'storageGroup',
                  rawid: e.groupName,
                  storagegroupid: e.groupName,
                  connectionid: node.data.connectionid,
                };
              });
              data.unshift(queryList);
              data.unshift(newStorageGroup);
              return resolve(data);
            } else {
              resolve([]);
            }
          })
          .catch(() => {
            resolve([]);
          });
      }
      if (node.level === 2 && node.data.type === 'storageGroup') {
        let groupName = node.data.rawid;
        let serverId = node.data.parent.rawid;
        axios
          .get(`/servers/${serverId}/storageGroups/${groupName}/devices`, {})
          .then((res) => {
            let newDevice = {
              id: node.data.id + ':newdevice',
              name: computed(() => t(`databasem.newDevice`)),
              type: 'newdevice',
              leaf: true,
              parent: node.data,
              connectionid: node.data.connectionid,
              storagegroupid: node.data.storagegroupid,
            };
            if (res?.code === '0') {
              let data = (res.data || []).map((e) => {
                return {
                  parent: node.data,
                  name: e,
                  id: node.data.id + e + 'device',
                  type: 'device',
                  leaf: true,
                  rawid: e,
                  storagegroupid: node.data.storagegroupid,
                  connectionid: node.data.connectionid,
                  deviceid: e,
                };
              });
              data.unshift(newDevice);
              return resolve(data);
            } else {
              resolve([]);
            }
          })
          .catch(() => {
            resolve([]);
          });
      }
      if (node.level === 2 && node.data.type === 'querylist') {
        let serverId = node.data.parent.rawid;
        axios
          .get(`/servers/${serverId}/query`, {})
          .then((res) => {
            let newQuery = {
              id: node.data.id + ':newquery',
              name: computed(() => t(`databasem.newQuery`)),
              type: 'newquery',
              leaf: true,
              parent: node.data,
              storagegroupid: node.data.storagegroupid,
              connectionid: node.data.connectionid,
            };
            if (res?.code === '0') {
              let data = (res.data || []).map((e) => {
                return {
                  parent: node.data,
                  name: e.queryName,
                  id: node.data.id + e.id + 'query',
                  type: 'query',
                  leaf: true,
                  rawid: e.id,
                  storagegroupid: node.data.storagegroupid,
                  connectionid: node.data.connectionid,
                  queryid: e.id,
                };
              });
              data.unshift(newQuery);
              return resolve(data);
            } else {
              resolve([]);
            }
          })
          .catch(() => {
            resolve([]);
          });
      }
    };

    return {
      collapseNode,
      expandNode,
      treeExpandKey,
      treeKey,
      store,
      loadNode,
      searchClick,
      nodeClick,
      newSource,
      treeProps,
      searchVal,
      treeRef,
      close,
      successFunc,
      showDialog,
      types,
      showDrawer,
      updateTree,
      sqlClick,
      coloseDrawer,
      funcdata,
    };
  },
  components: {
    ElTree,
    IconTypes,
    // ElInput,
    ElTooltip,
    NewSource,
    SqlDrawer,
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">
.data-list-tree {
  height: 100%;
  overflow: auto;
  .data-list-top {
    color: rgba(34, 34, 34, 0.65);
    font-size: 12px;
    line-height: 20px;
    text-align: left;
    margin: 15px 20px;
    position: relative;
    .icon {
      font-size: 16px;
      cursor: pointer;
    }
    .icon-1 {
      top: 2px;
      right: 0;
      position: absolute;
    }
    .icon-2 {
      top: 2px;
      right: 30px;
      position: absolute;
    }
  }
  .data-list-input {
    margin: 0 20px 15px;
  }
  &:deep(.el-tree) {
    height: calc(100% - 50px);
    width: 100%;
    overflow: auto;
    .el-tree-node {
      min-width: fit-content;
    }
    .el-tree-node__content {
      min-width: fit-content;
    }
  }
}
</style>
