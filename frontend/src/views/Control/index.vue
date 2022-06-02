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
  <div class="monitor">
    <el-container>
      <el-aside :width="dividerWidth + 'px'">
        <div class="aside-title">
          {{ $t('controlPage.dataList') }}<span> ({{ filterDataList.length || 0 }}) </span>
        </div>
        <el-input v-if="dataList.length" v-model="search" class="w-50 m-2" :placeholder="$t('common.placeHolder')" suffix-icon="el-icon-search" />
        <div class="aside-main">
          <div :class="{ 'data-item': true, 'active-item': checkedId === item.id }" v-for="(item, index) in filterDataList" :key="index" @click="handleSwitch(item)">
            <svg class="aside-icon" aria-hidden="true">
              <use xlink:href="#icon-shujulianjie1"></use></svg
            >{{ item.name }}
          </div>
          <div v-if="dataList.length === 0" class="datalist-empty">
            <span>{{ $t('controlPage.nodata') }}</span>
          </div>
        </div>
      </el-aside>
      <div class="divider" ref="dividerRef"></div>
      <el-main v-if="monitorInfo">
        <div class="main-top">
          <div class="main-head">
            <div class="main-title">
              {{ currentData.name }}
              <span v-if="monitorInfo.status" class="survive">{{ $t('common.survival') }}</span>
              <span v-else class="die">{{ $t('common.death') }}</span>
            </div>
            <div class="main-host format">
              <div>
                <svg class="main-icon" aria-hidden="true">
                  <use xlink:href="#icon-se-icon-ip"></use>
                </svg>
                {{ $t('controlPage.address') }}：<span>{{ formatInfo(monitorInfo?.url) }}</span>
              </div>
              <div>
                <svg class="main-icon" aria-hidden="true">
                  <use xlink:href="#icon-duankou"></use>
                </svg>
                {{ $t('common.port') }}： <span>{{ formatInfo(monitorInfo?.port) }}</span>
              </div>
            </div>
            <div class="main-info format">
              <div>
                <svg class="main-icon" aria-hidden="true">
                  <use xlink:href="#icon-cunchuzu1"></use></svg
                >{{ $t('controlPage.storage') }}： <span>{{ formatInfo(monitorInfo?.storageGroupCount) }}</span>
              </div>
              <div>
                <svg class="main-icon" aria-hidden="true">
                  <use xlink:href="#icon-shiti"></use></svg
                >{{ $t('controlPage.entity') }}： <span>{{ formatInfo(monitorInfo?.monitorCount) }}</span>
              </div>
              <div>
                <svg class="main-icon" aria-hidden="true">
                  <use xlink:href="#icon-wuliliang"></use></svg
                >{{ $t('controlPage.physics') }}： <span>{{ formatInfo(monitorInfo?.deviceCount) }}</span>
              </div>
              <div>
                <svg class="main-icon" aria-hidden="true">
                  <use xlink:href="#icon-shujuzongliang"></use></svg
                >{{ $t('controlPage.total') }}： <span>{{ formatInfo(monitorInfo?.dataCount) }}</span>
              </div>
            </div>
          </div>
          <div class="main-top-tabs">
            <el-tabs v-model="activeTab" @tab-click="handleChangeTab">
              <template v-for="(item, index) in tabPaneOptions" :key="index">
                <el-tab-pane v-bind="item" :disabled="item.name === 'Query' ? disabled : false"> </el-tab-pane>
              </template>
            </el-tabs>
          </div>
        </div>
        <router-view v-slot="{ Component }">
          <keep-alive>
            <component :is="Component" :data="monitorInfo" />
          </keep-alive>
        </router-view>
      </el-main>
    </el-container>
  </div>
</template>

<script>
// @ is an alias to /src
import { computed, onMounted, ref, watch } from 'vue';
import { ElTabs, ElContainer, ElAside, ElMain } from 'element-plus';
import { useRoute, useRouter } from 'vue-router';
import useElementResize from '@/hooks/useElementResize.js';
import useLanguageWatch from '@/hooks/useLanguageWatch';
import { useI18n } from 'vue-i18n';
import { getMonitorList, getMonitorInfo } from './api';

export default {
  name: 'ComtrolIndex',
  components: {
    ElTabs,
    ElContainer,
    ElAside,
    ElMain,
  },
  setup() {
    let { t } = useI18n();
    const router = useRouter();
    const route = useRoute();
    let routerParams = {};
    let dividerRef = ref();
    let dividerWidth = ref(240);
    let search = ref();
    let activeTab = ref('Indicator');
    let currentData = ref(null);
    let dataList = ref([]);
    let monitorInfo = ref();
    let show = ref(false);
    let disabled = ref(false);
    let tabPaneOptions = ref([
      { name: 'Indicator', label: t('controlPage.monitor') },
      { name: 'Query', label: t('controlPage.search') },
    ]);
    //数据列表选中id
    let checkedId = computed(() => currentData.value && currentData.value.id);
    let filterDataList = computed(() => {
      let reg = new RegExp(search.value);
      return dataList.value.filter((item) => {
        if (reg.test(item.name)) {
          return true;
        }
      });
    });
    watch(currentData, async (newValue) => {
      if (newValue) {
        let res = await getMonitorInfo(newValue.id);
        monitorInfo.value = res.data;
        if (monitorInfo.value?.status) {
          disabled.value = false;
          handleChangeTab({ paneName: activeTab.value });
        } else {
          handleChangeTab({ paneName: 'Indicator' });
          disabled.value = true;
        }
      }
    });

    onMounted(() => {
      initFun();
      useElementResize(dividerRef, dividerWidth);
      useLanguageWatch(tabPaneOptions, () => [
        { name: 'Indicator', label: t('controlPage.monitor') },
        { name: 'Query', label: t('controlPage.search') },
      ]);
    });

    function urlSkip(routerName, id, params) {
      let panelMode = monitorInfo.value?.status ? params?.panel || 'list' : 'list';
      if (routerName == 'Indicator') {
        router.push({ path: `/control/indicator/${panelMode}/${id}/${params.mode}`, query: { ...route.query } });
      } else if (routerName === 'Query') {
        router.push({ path: `/control/query/${id}` });
      }
    }
    async function initFun() {
      // init tabs
      activeTab.value = routerNameLimit(route.name);
      await getData();
      //   routerParamId to data
      filterCurrentData(route.params.id);
    }
    async function getData() {
      let res = await getMonitorList();
      dataList.value = res.data || [];
    }
    function routerNameLimit(name) {
      for (let i = 0, len = tabPaneOptions.value.length; i < len; i++) {
        if (tabPaneOptions.value[i].name === name) {
          return name;
        }
      }
      return tabPaneOptions.value[0].name;
    }
    function filterCurrentData(id) {
      let len = dataList.value.length;
      for (let i = 0; i < len; i++) {
        let item = dataList.value[i];
        if (item.id === +id) {
          currentData.value = { ...item };
          return;
        }
      }
      currentData.value = dataList.value[0];
    }
    const handleChangeTab = (tabs) => {
      routerParams[route.name] = route.params;
      activeTab.value = tabs.paneName;
      let id = currentData.value?.id;
      let params = { ...routerParams['Indicator'] };
      params.mode = routerParams['Indicator']?.mode || 'JVM';
      urlSkip(tabs.paneName, id, params);
    };
    function handleSwitch(data) {
      currentData.value = data;
    }
    function formatInfo(val) {
      return val || '-';
    }
    return {
      dividerRef,
      dividerWidth,
      activeTab,
      search,
      dataList,
      currentData,
      monitorInfo,
      filterDataList,
      show,
      disabled,

      checkedId,
      tabPaneOptions,

      handleChangeTab,
      handleSwitch,
      formatInfo,
    };
  },
};
</script>

<style scoped lang="scss">
.monitor {
  height: 100%;
  .datalist-empty {
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 14px;
    color: #909399;
  }
  .el-container {
    height: 100%;
  }
  .el-aside {
    text-align: left;
    padding: 0 20px;
  }
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
  .aside-title {
    height: 20px;
    font-size: 14px;
    font-weight: 500;
    color: #333;
    line-height: 20px;
    margin: 20px 0;
    span {
      font-weight: 400;
      color: #808a9f;
    }
  }
  .aside-main {
    margin: 20px 0 10px 0;
    height: calc(100% - 118px);
    font-size: 12px;
    font-weight: 400;
    overflow-y: auto;

    .data-item {
      margin-top: 10px;
      height: 30px;
      line-height: 30px;
      border-radius: 4px;
      display: flex;
      align-items: center;
      cursor: pointer;
      &:hover {
        background: #edf8f5;
        color: $theme-color;
      }
      &:first-child {
        margin-top: 0;
      }
    }
    .active-item {
      background: #edf8f5;
      color: $theme-color;
    }
    .aside-icon {
      width: 14px;
      height: 14px;
      margin: 0 10px;
    }
  }
  .main-top {
    padding: 20px 20px 0 20px;
    &:deep .el-tabs {
      .el-tabs__nav-wrap::after {
        display: none;
      }
      .el-tabs__item.is-disabled {
        cursor: not-allowed;
        &:hover {
          color: #8e97aaff !important;
        }
      }
      .el-tabs__header {
        margin: 0;
      }
      .el-tabs__item {
        &.is-active {
          color: #15c294 !important;
        }
      }
    }
    .main-top-tabs {
      padding-left: 20px;
    }
  }
  .main-head {
    margin-bottom: 10px;
    padding: 20px;
    border-radius: 4px;
    border: 1px solid #eaecf0;
    text-align: left;
    .main-title {
      display: flex;
      align-items: center;
      height: 24px;
      line-height: 24px;
      color: #333;
      font-size: 16px;
      font-weight: 500;
      span {
        margin-left: 26px;
        font-size: 12px;
        padding: 0 12px;
        height: 20px;
        line-height: 20px;
        border-radius: 2px;
        &.survive {
          color: #00a950;
          background: rgba(0, 169, 80, 0.1);
        }
        &.die {
          color: #f33f2b;
          background: rgba(243, 63, 43, 0.1);
        }
      }
    }
    .format {
      display: flex;
      margin-top: 12px;
      height: 20px;
      line-height: 20px;
      font-size: 12px;
      color: #828ca1;
      font-weight: 400;
      > div {
        display: flex;
        margin-left: 38px;
        align-items: center;
        span {
          color: #333;
        }
        &:first-child {
          margin-left: 0;
        }
      }
    }

    .main-icon {
      margin-right: 8px;
      width: 14px;
      height: 14px;
    }
  }
  .main-tab {
    display: inline-block;
    padding: 12px 20px;
  }

  &:deep .el-main {
    padding: 0;
  }
  &:deep td,
  &:deep th {
    height: 40px;
  }
  &:deep .el-input .el-input__inner {
    font-size: 12px;
  }
}
</style>
