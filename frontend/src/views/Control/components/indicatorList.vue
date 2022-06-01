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
  <div>
    <div class="main-center-container-head">
      <div class="select-box">
        <el-select v-model="mode" class="m-2" placeholder="Select">
          <el-option v-for="item in modeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </div>
      <div v-if="showBtnGroup">
        <el-button type="primary" @click="handleChangePanel">{{ $t('controlPage.chartBtn') }}</el-button>
        <el-button @click="handleRefres">{{ $t('common.refresh') }}</el-button>
      </div>
    </div>
    <div class="main-center-container-center">
      <div class="main-center-container-center-left">
        <el-tabs v-model="activeName" tab-position="left" style="height: 200px" class="demo-tabs">
          <el-tab-pane v-for="(item, index) in tabPanelOptions" :key="index" v-bind="item"></el-tab-pane>
        </el-tabs>
      </div>
      <div class="main-center-container-center-right">
        <div class="search-way" v-if="activeName === 'search'">
          <span :class="{ active: searchWay === '0' }" @click="handleSearchWay('0')">Top Sql</span>
          <span :class="{ active: searchWay === '1' }" @click="handleSearchWay('1')">{{ $t('controlPage.slowSearch') }}</span>
        </div>
        <el-table class="iotdb-table" border style="width: 100%" :empty-text="$t('controlPage.nodata')" :data="tableData">
          <template v-for="(item, index) in tableColumn">
            <el-table-column v-if="!item.fixed" :key="'col' + index" v-bind="item"></el-table-column>

            <el-table-column v-else :key="'colfixed' + index" fixed="right" :label="$t('common.operation')" width="140">
              <template #default="scoped">
                <el-button v-if="scoped.row.detailAvailable !== 0" type="text" size="small" @click="handleDeatil(scoped)">{{ $t('common.detail') }}</el-button>
              </template>
            </el-table-column>
          </template>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script>
import { computed, onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { getMetricsData, getSearchMetricsData } from '../api';

export default {
  name: 'IndicatorList',
  props: {
    currentData: {
      type: Object,
    },
  },
  setup(props) {
    let { t } = useI18n();
    let router = useRouter();
    let route = useRoute();
    let mode = ref(route.query.mode || '0');
    let activeName = ref(route.params.mode || 'JVM');
    let searchWay = ref('0');
    let tableData = ref();

    let modeOptions = computed(() => [
      {
        label: t('controlPage.allMode'),
        value: '0',
      },
      {
        label: t('controlPage.devMode'),
        value: '1',
      },
      {
        label: t('controlPage.opeMode'),
        value: '2',
      },
    ]);
    let tableColumn = computed(() => {
      const temp = [
        {
          prop: 'name',
          label: t('controlPage.iName'),
          minWidth: '160px',
        },
        {
          prop: 'latestScratchTime',
          label: t('controlPage.zxjg'),
          minWidth: '160px',
        },
        {
          prop: 'latestResult',
          label: t('controlPage.zxzb'),
          minWidth: '160px',
        },
        { fixed: true },
      ];
      if (activeName.value === 'JVM') {
        let formatCol = [...temp];
        formatCol.splice(1, 0, {
          prop: 'metricType',
          label: t('controlPage.iType'),
          minWidth: '120px',
        });
        return formatCol;
      } else if (activeName.value === 'search') {
        return [
          {
            prop: 'sqlstatement',
            minWidth: '372',
            label: t('controlPage.sql'),
          },
          {
            prop: 'runningTime',
            minWidth: '160',
            label: t('controlPage.runTime'),
          },
          {
            prop: 'executionTime',
            sortable: true,
            minWidth: '140',
            label: t('controlPage.exeTime'),
          },
        ];
      } else if (activeName.value === 'memory') {
        let formatCol = [...temp];
        formatCol.pop();
        return formatCol;
      }
      return temp;
    });
    let targetType = computed(() => [
      {
        label: t('controlPage.jvm'),
        name: 'JVM',
        type: '1',
      },
      {
        label: t('controlPage.cpu'),
        name: 'CPU',
        type: '2',
      },
      {
        label: t('controlPage.memory'),
        name: 'memory',
        type: '2',
      },
      {
        label: t('controlPage.store'),
        name: 'store',
        type: '2',
      },
      {
        label: t('controlPage.write'),
        name: 'write',
        type: '0',
      },
      {
        label: t('controlPage.isearch'),
        name: 'search',
        type: '0',
      },
    ]);
    let tabPanelOptions = computed(() => {
      let filterData = targetType.value.filter(
        (item) => {
          if (mode.value === '0' || item.type === mode.value || item.type === '0') {
            return true;
          }
        },
        { immediate: true }
      );
      checkedTarget(filterData, activeName.value);
      return filterData;
    });
    let showBtnGroup = computed(() => props.currentData?.status);
    watch(
      activeName,
      (newVlaue) => {
        initTableData(props.currentData, newVlaue);
        router.push({
          path: `/control/indicator/${route.params?.panel || 'list'}/${props.currentData.serverId}/${newVlaue}`,
          query: { ...route.query },
        });
      },
      {
        immediate: true,
      }
    );
    watch(
      () => route.params.mode,
      (newValue) => {
        newValue && (activeName.value = newValue);
      },
      {
        immediate: true,
      }
    );
    watch(
      () => route.query.mode,
      (newValue) => {
        newValue && (mode.value = newValue);
      }
    );
    watch(
      () => props.currentData,
      (newValue) => {
        initTableData(newValue, activeName.value);
      }
    );
    onMounted(() => {});
    function handleRefres() {
      initTableData(props.currentData, activeName.value);
    }
    function handleSearchWay(way) {
      if (searchWay.value !== way) {
        searchWay.value = way;
        initTableData(props.currentData, activeName.value);
      }
    }
    function checkedTarget(data, type) {
      for (let i = 0, len = data.length; i < len; i++) {
        if (data[i].name === type) {
          return false;
        }
      }
      data[0]?.name && (activeName.value = data[0].name);
    }
    function handleDeatil({ row }) {
      let modeMap = {
        JVM: {
          1: 0,
          2: 1,
          3: 2,
          4: 3,
        },
      };
      let type = activeName.value === 'JVM' ? modeMap[activeName.value][row.detailAvailable] : undefined;
      router.push({ path: `/control/indicator/chart/${route.params.id}/${route.params.mode}`, query: { mode: mode.value, type } });
    }
    function initTableData(data, mode) {
      const modeMap = {
        JVM: 0,
        CPU: 1,
        memory: 2,
        store: 3,
        write: 4,
      };
      if (!data?.status) {
        tableData.value = [];
        return;
      }
      // 数据列表信息
      if (mode !== 'search') {
        getMetricsData(data.serverId, modeMap[mode]).then((res) => {
          tableData.value = res.data.listInfo;
        });
      } else {
        getSearchMetricsData(data.serverId, searchWay.value).then((res) => {
          tableData.value = res.data.queryMetricsVOs;
        });
      }
    }
    function handleChangePanel() {
      router.push({ path: `/control/indicator/chart/${route.params.id}/${route.params.mode}`, query: { mode: mode.value } });
    }
    return {
      mode,
      activeName,
      modeOptions,
      tabPanelOptions,
      tableData,
      tableColumn,
      searchWay,
      showBtnGroup,

      handleDeatil,
      handleSearchWay,
      handleChangePanel,
      handleRefres,
    };
  },
};
</script>

<style lang="scss" scoped>
.main-center-container {
  background: #fff;
  border-radius: 4px;
  &-head {
    display: flex;
    justify-content: space-between;
    padding: 12px 20px;
    border: 1px solid #eaecf0;
  }
  &-center {
    padding: 20px 20px 20px 0;
    display: flex;
    border: 1px solid #eaecf0;
    border-top: none;
    &-left {
      margin-right: 15px;
      &:deep .el-tabs__active-bar {
        min-width: 0 !important;
      }
      &:deep .el-tabs__item {
        height: 24px;
        line-height: 24px;
        color: #8e97aa;
        font-size: 12px !important;
        &.is-active {
          color: $theme-bj-color !important;
        }
      }
      &:deep .el-tabs__nav-scroll,
      &:deep .el-tabs__nav-wrap {
        height: auto;
      }
    }
    &-right {
      width: 100px;
      flex: 1;
      .search-way {
        display: flex;
        font-size: 12px;
        margin-bottom: 20px;
        span {
          padding: 8px 21px;
          color: #8e97aa;
          border: 1px solid #eaecf0;
          cursor: pointer;
          &.active {
            background: #edf8f5;
            color: #15c294;
          }
        }
        span:first-child {
          border-radius: 4px 0 0 4px;
          border-right: none;
        }
        span:last-child {
          border-radius: 0 4px 4px 0;
        }
      }
    }
  }
  .select-box {
    width: 120px;
    &:deep .el-input__inner {
      font-size: 12px;
    }
  }
}
</style>
