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
  <div class="databasem">
    <el-container class="content-container">
      <el-aside :width="dividerWidth + 'px'">
        <data-list-tree
          :func="{
            removeTab,
            addTab,
            updateTree,
            expandByIds,
          }"
          :nodekey="nodekey"
          ref="treeRef"
          :handleNodeClick="handleNodeClick"
        ></data-list-tree>
      </el-aside>
      <div class="divider" ref="dividerRef"></div>
      <el-main>
        <template v-if="urlTabs.length !== 0 || route.path === `/databasem/empty`">
          <el-tabs v-model="urlTabsValue" type="card" @tab-click="handleClick" @tab-remove="removeTab" closable class="top-tab-content">
            <el-tab-pane v-for="item in urlTabs" :key="item.name" :name="item.name">
              <template #label>
                <span>
                  <icon-types :data="item" :nodekey="urlTabsValue" />
                  <span>{{ item.title }}</span>
                </span>
              </template>
            </el-tab-pane>
          </el-tabs>
          <div class="router-container">
            <router-view v-slot="{ Component, route }">
              <keep-alive>
                <component
                  :key="route.fullPath"
                  :is="Component"
                  :data="tabData"
                  :dividerWidth="dividerWidth"
                  :func="{
                    removeTab,
                    addTab,
                    updateTree,
                    expandByIds,
                    updateTreeByIds,
                  }"
                />
              </keep-alive>
            </router-view>
          </div>
        </template>
      </el-main>
    </el-container>
  </div>
</template>

<script>
// @ is an alias to /src
import { onMounted, ref, watch } from 'vue';
import { useStore } from 'vuex';
import { useRouter, useRoute } from 'vue-router';
import useElementResize from '@/hooks/useElementResize.js';
import DataListTree from './components/dataListTree.vue';
import IconTypes from './components/iconTypes.vue';
import { ElContainer, ElAside, ElMain, ElTabs, ElTabPane } from 'element-plus';

export default {
  name: 'Root',
  setup() {
    const dividerRef = ref(null);
    const store = useStore();
    let dividerWidth = ref(240);
    let urlTabsValue = ref('');
    let urlTabs = ref([]);
    let tabData = ref({});
    const nodekey = ref('');
    let treeRef = ref(null);
    const router = useRouter();
    const route = useRoute();

    const handleClick = (tab) => {
      let data = urlTabs.value[tab.index];
      urlTabsValue.value = data.name;
      if (data.twinTab) {
        treeRef.value.treeRef.setCurrentKey(data.node.id);
        nodekey.value = data.node.id;
      } else {
        treeRef.value.treeRef.setCurrentKey(data.id);
        nodekey.value = data.id;
      }
      urlSkipMap(data.node);
    };

    const updateTree = (params, clear) => {
      treeRef.value.updateTree(params, clear);
    };

    const updateTreeByIds = (ids) => {
      ids.forEach((id) => {
        let node = treeRef.value.treeRef.getNode(id);
        if (node) {
          node.loaded = false;
          node.loadData();
        }
      });
    };

    const expandByIds = (ids) => {
      ids.forEach((id) => {
        let count = 0;
        let stop = setInterval(() => {
          let node = treeRef.value.treeRef.getNode(id);
          count++;
          if (node) {
            node.expanded = true;
            clearInterval(stop);
          }
          if (count > 10) {
            clearInterval(stop);
          }
        }, 500);
      });
    };

    const addTab = (id, extraParams, notupdate) => {
      if (!notupdate) {
        updateTree();
      }
      let count = 0;
      let stop = setInterval(() => {
        let node = treeRef.value.treeRef.getNode(id);
        count++;
        if (node) {
          handleNodeClick({ ...node.data, extraParams: extraParams });
          clearInterval(stop);
        }
        if (count > 10) {
          clearInterval(stop);
        }
      }, 500);
    };

    watch(urlTabsValue, (newValue) => {
      urlTabs.value.forEach((e) => {
        if (e.name === newValue) {
          tabData.value = e.node;
        }
      });
    });

    const urlSkipMap = (data, forceupdateparams) => {
      let extraParams = data.extraParams;
      let forceupdate = forceupdateparams ? forceupdateparams : '';
      if (data.type === 'connection') {
        if (data.extraParams && data.extraParams.type == 'modal') {
          router.push({ name: 'DataModal', params: { ...data, serverid: data.connectionid, forceupdate, ...extraParams } });
        } else {
          //data connection
          router.push({ name: 'Source', params: { serverid: data.connectionid, forceupdate, ...extraParams } });
        }
      } else if (data.type === 'newstorageGroup') {
        router.push({ name: 'NewStorage', params: { serverid: data.connectionid, forceupdate, ...extraParams } });
      } else if (data.type === 'querylist') {
        //Query list
      } else if (data.type === 'storageGroup') {
        store.commit('setCurrRouteParams', { ...data, parentid: data.parent.id, parentids: data.parent?.parent?.name, forceupdate, ...extraParams });

        //Determine whether to enter the storage group details or edit the storage group
        if (data.extraParams && data.extraParams.type == 'edit') {
          router.push({ name: 'EditStorage', params: { serverid: data.connectionid, groupname: data.name, forceupdate, ...extraParams } });
        } else {
          //Storage group
          router.push({ name: 'Storage', params: { serverid: data.connectionid, groupname: data.name, forceupdate, ...extraParams } });
        }
      } else if (data.type === 'newdevice') {
        //New entity
        store.commit('setCurrRouteParams', { ...data, parentid: data.parent.id, parentids: data.parent.parent.name, forceupdate, ...extraParams });
        router.push({ name: 'Device', params: { ...data, parentid: data.parent.id, parentids: data.parent.parent.name, forceupdate, ...extraParams }, query: { id: data.id } });
      } else if (data.type === 'device') {
        store.commit('setCurrRouteParams', { ...data, parentid: data.parent.id, parentids: data.parent.parent.name, forceupdate, ...extraParams });
        router.push({ name: 'DeviceMessage', params: { ...data, parentid: data.parent.id, parentids: data.parent.parent.name, forceupdate, ...extraParams } });
      } else if (data.type === 'newquery') {
        //new query
        console.log(data);
        router.push({ name: 'SqlSerch', params: { ...data, connectId: data.parent.parent.name, forceupdate, ...extraParams } });
      } else if (data.type === 'query') {
        //new query
        router.push({ name: 'SqlSerch', params: { ...data, connectId: data.parent.parent.name, forceupdate, ...extraParams } });
      }
    };

    const handleNodeClick = (data) => {
      nodekey.value = data.id;
      treeRef.value.treeRef.setCurrentKey(data.id);
      if (data.type === 'querylist') {
        return;
      }
      urlTabsValue.value = data.id + '';
      let list = urlTabs.value;
      if (data.extraParams && data.extraParams.twinTab) {
        list.push({ node: data, title: data.extraParams.title, name: data.extraParams.id, id: data.extraParams.id, type: data.type, twinTab: true });
        urlTabs.value = list;
        urlTabsValue.value = data.extraParams.id;
        urlSkipMap(data, true);
      } else if (
        !list.some((e) => {
          return e.id === data.id + '';
        })
      ) {
        list.push({ node: data, title: data.name, name: data.id + '', id: data.id + '', type: data.type });
        urlTabs.value = list;
        urlSkipMap(data, true);
      } else {
        urlSkipMap(data);
      }
    };

    const removeTab = (targetName) => {
      let tabs = urlTabs.value;
      let activeName = urlTabsValue.value;
      if (activeName === targetName) {
        tabs.forEach((tab, index) => {
          if (tab.name === targetName) {
            let nextTab = tabs[index + 1] || tabs[index - 1];
            if (nextTab) {
              urlSkipMap(nextTab.node);
              activeName = nextTab.name;
              nodekey.value = nextTab.id;
              treeRef.value.treeRef.setCurrentKey(activeName);
            } else {
              nodekey.value = null;
              treeRef.value.treeRef.setCurrentKey(null);
            }
          }
        });
      }
      urlTabsValue.value = activeName;
      urlTabs.value = tabs.filter((tab) => tab.name !== targetName);
    };

    onMounted(() => {
      useElementResize(dividerRef, dividerWidth);
    });

    return {
      store,
      route,
      tabData,
      urlTabsValue,
      dividerRef,
      nodekey,
      dividerWidth,
      urlTabs,
      treeRef,
      handleClick,
      removeTab,
      handleNodeClick,
      updateTree,
      updateTreeByIds,
      expandByIds,
      addTab,
    };
  },
  components: {
    ElContainer,
    ElAside,
    ElMain,
    DataListTree,
    ElTabs,
    ElTabPane,
    IconTypes,
  },
};
</script>

<style scoped lang="scss">
.databasem {
  height: 100%;
  .divider {
    z-index: 10;
    width: 1px;
    height: 100%;
    background-color: #e0e0e0;
    &:hover {
      cursor: w-resize;
      background-color: $theme-color;
      width: 2px;
    }
  }
  .router-container {
    height: calc(100% - 42px);
    width: 100%;
    overflow: auto;
  }
  &::v-deep(.content-container) {
    height: 100%;
    & > .el-aside {
      height: 100%;
    }
    & > .el-main {
      padding: 0;
      height: 100%;
      .top-tab-content > .el-tabs__header {
        margin: 0;
        .el-tabs__nav-scroll {
          background-color: #f9fbfc;
        }
        .el-tabs__nav {
          border: 0;
          .el-tabs__item {
            box-sizing: content-box;
            border-width: 0;
            line-height: 42px;
            height: 42px;
            position: relative;
            &.is-active {
              background: rgba(69, 117, 246, 0.04);
              background-color: #fff;
              span {
                color: $theme-color !important;
              }
            }
          }
        }
      }
    }
  }
}
</style>
