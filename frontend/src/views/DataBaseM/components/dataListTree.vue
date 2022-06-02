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

<template>
  <div class="data-list-tree">
    <div class="data-list-top">
      <span>{{ $t('rootPage.dataList') }}</span>
    </div>
    <div class="data-list-btn">
      <el-button @click="newSource">
        <svg class="icon" aria-hidden="true">
          <use xlink:href="#icon-add"></use>
        </svg>
        {{ $t('rootPage.newdatasource') }}
      </el-button>
      <el-button @click="sqlClick">
        <svg class="icon" aria-hidden="true">
          <use xlink:href="#icon-add"></use>
        </svg>
        {{ $t('rootPage.newQueryWindow') }}
      </el-button>
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
import { ElTree, ElButton } from 'element-plus';
import { reactive, ref, computed, nextTick } from 'vue';
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
      if (data.type === 'pre' || data.type === 'next') {
        const resolve = async (array) => {
          const childNodes = node.parent.childNodes.slice();
          props.handleNodeClick(node.parent.data, node.parent);
          array.forEach((element) => {
            nextTick(() => treeRef.value.append(element, node.parent));
          });
          await Promise.all(
            childNodes.map((element) => {
              return nextTick(() => treeRef.value.remove(element));
            })
          );
        };
        getData(node.parent, resolve, node);
      } else {
        props.handleNodeClick(data, node);
      }
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

    const recurseDeviceTree = (data, node, res) => {
      let newDevice = {
        id: node.data.id + ':newdevice',
        name: computed(() => t(`databasem.newDevice`)),
        type: 'newdevice',
        leaf: true,
        parent: node.data,
        connectionid: node.data.connectionid,
        storagegroupid: node.data.storagegroupid,
      };
      let isPage = res.data.total > res.data.pageSize;
      let prev = {
        id: node.data.id + ':pre',
        name: computed(() => t('sourcePage.prePage')),
        type: 'pre',
        leaf: true,
        parent: node.data,
        rawid: node.data.name,
        connectionid: node.data.connectionid,
        storagegroupid: node.data.storagegroupid,
        serverId: res.serverId,
        pageNum: res.data.pageNum,
        pageSize: res.data.pageSize,
        total: res.data.total,
      };
      let next = {
        id: node.data.id + ':next',
        name: computed(() => t('sourcePage.nextPage')),
        type: 'next',
        leaf: true,
        parent: node.data,
        rawid: node.data.name,
        connectionid: node.data.connectionid,
        storagegroupid: node.data.storagegroupid,
        serverId: res.serverId,
        pageNum: res.data.pageNum,
        pageSize: res.data.pageSize,
        total: res.data.total,
      };
      let childs = data.map((e) => {
        let child = {
          parent: node.data,
          name: e.name,
          id: node.data.id + e.name + 'device',
          type: 'device',
          // leaf: e.children === null ? true : false,
          leaf: false,
          rawid: e.name,
          storagegroupid: node.data.storagegroupid,
          connectionid: node.data.connectionid,
          deviceid: e.name,
          serverId: res.serverId,
        };
        if (e.children) {
          let innerChilds = recurseDeviceTree(e.children || [], { data: child }, res);
          child.zones = innerChilds;
        }
        return child;
      });
      isPage && childs.unshift(prev);
      childs.unshift(newDevice);
      isPage && childs.push(next);
      return childs;
    };
    const getData = (node, resolve, pagination) => {
      let groupName = node.data.rawid;
      let serverId = node.data.parent.rawid;
      if (node.data.type !== 'storageGroup') {
        serverId = node.data.serverId;
      }
      const params = { pageNum: (pagination && pagination.data.pageNum) || 1, pageSize: (pagination && pagination.data.pageSize) || 10 };
      if (pagination && pagination.data.type === 'pre') {
        params.pageNum = params.pageNum - 1 < 1 ? 1 : params.pageNum - 1;
      }
      if (pagination && pagination.data.type === 'next') {
        const max = Math.ceil((pagination.data.total || 1) / params.pageSize);
        params.pageNum = params.pageNum + 1 > max ? max : params.pageNum + 1;
      }
      axios
        .get(`/servers/${serverId}/storageGroups/${groupName}/devices/tree`, {
          params,
        })
        .then((res) => {
          if (res?.code === '0') {
            if (!res.data) {
              let newDevice = {
                id: node.data.id + ':newdevice',
                name: computed(() => t(`databasem.newDevice`)),
                type: 'newdevice',
                leaf: true,
                parent: node.data,
                connectionid: node.data.connectionid,
                storagegroupid: node.data.storagegroupid,
              };
              resolve([newDevice]);
              return;
            }
            let childs = recurseDeviceTree(res.data.children || [], node, { serverId, data: res.data });
            resolve(childs);
          } else {
            resolve([]);
          }
        })
        .catch(() => {
          resolve([]);
        });
    };

    const loadNode = (node, resolve) => {
      if (node?.data?.zones) {
        resolve(node.data.zones);
        return;
      }
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
      if ((node.level === 2 && node.data.type === 'storageGroup') || (node.data && node.data.type === 'device')) {
        getData(node, resolve);
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
    ElButton,
    // ElInput,
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
    color: #333;
    font-size: 12px;
    line-height: 20px;
    text-align: left;
    margin: 14px 20px;
    font-weight: 600;
  }
  .data-list-btn {
    text-align: left;
    padding: 0 0 14px;
    border-width: 0;
    border-bottom-width: 1px;
    border-style: solid;
    border-color: #f5f5f7;
    margin: 0 14px 14px;
    &::v-deep {
      .el-button {
        border-width: 0;
        background-color: #f9fbfc;
        color: #333;
        font-size: 12px;
        &:hover {
          background-color: #edf8f5;
          color: #16c493;
        }
        .icon {
          font-size: 14px;
          margin-right: 8px;
        }
      }
    }
  }
  .data-list-input {
    margin: 0 20px 15px;
  }
  .custom-tree-node {
    font-size: 12px;
    color: #333;
  }
  ::v-deep(.el-tree) {
    height: calc(100% - 105px);
    width: 100%;
    overflow: auto;
    .el-tree-node {
      min-width: fit-content;
      &.is-current > .el-tree-node__content > .custom-tree-node {
        & > span {
          color: $theme-color;
        }
      }
    }
    .el-tree-node__content {
      min-width: fit-content;
    }
  }
}
</style>
