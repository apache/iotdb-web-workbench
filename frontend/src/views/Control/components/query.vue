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
  <div class="query-main">
    <div class="query-main-container">
      <div class="container-left">
        <el-tabs v-model="activeType" tab-position="left" style="height: 200px" class="demo-tabs">
          <el-tab-pane v-for="(item, index) in tabPanelOptions" :key="index" :label="item.name" :name="item.id + ''">
            <template #label>
              <div class="tab-label" :key="index">{{ item.name }}</div>
            </template>
          </el-tab-pane>
        </el-tabs>
      </div>
      <div class="container-right">
        <div class="container-right-tip">
          <div>
            {{ $t('controlPage.lastTime') }} <span>{{ latestTime }}</span>
          </div>
          <div>
            {{ $t('controlPage.runCount') }}<span>{{ runTotal }}</span>
          </div>
        </div>
        <div class="container-right-operate">
          <div class="operate-item">
            <span class="input-label">{{ $t('controlPage.querySentence') }}：</span>
            <div class="input-box">
              <el-input v-model="searchValue" class="w-50 m-2" :placeholder="$t('common.placeHolder')" />
            </div>
          </div>
          <div class="operate-item">
            <span class="input-label">{{ $t('controlPage.runTime') }}：</span>
            <div class="date-box">
              <el-date-picker v-model="runTime" size="mini" type="datetimerange" :start-placeholder="$t('common.startTime')" :end-placeholder="$t('common.endTime')" />
            </div>
          </div>
          <div class="operate-item">
            <span class="input-label">{{ $t('controlPage.exeResult') }}：</span>
            <div class="select-box">
              <el-select v-model="runResult" class="m-2" :placeholder="$t('common.selectPlaceholder')">
                <el-option v-for="item in resultOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </div>
          </div>
          <el-button class="operate-btn" @click="handleSearch">{{ $t('common.query') }}</el-button>
        </div>
        <div class="container-right-table">
          <el-table class="iotdb-table" border style="width: 100%" :data="tableData">
            <template v-for="(item, index) in tableColumn">
              <el-table-column v-if="item.isSlowQuery" :key="'col' + index" label="" :width="item.maxWidth">
                <template #default="scope">
                  <div class="slow-query" v-if="scope.row.isSlowQuery">{{ '慢' }}</div>
                </template>
              </el-table-column>
              <el-table-column v-else-if="item.type === 'result'" :key="'col' + index" :label="$t('controlPage.exeResult')">
                <template #default="scope">
                  <div :class="+scope.row.executionResult === 1 ? 'success' : 'error'">{{ +scope.row.executionResult === 1 ? $t('common.success') : $t('common.fail') }}{{ scope.row.result }}</div>
                </template>
              </el-table-column>
              <el-table-column v-else :key="'col' + index" show-overflow-tooltip v-bind="item"></el-table-column>
            </template>
            <el-table-column fixed="right" :label="$t('common.operation')" width="100">
              <template #default="scoped">
                <el-button type="text" size="small" @click="handleDownload(scoped)">{{ $t('controlPage.downloadLog') }}</el-button>
              </template>
            </el-table-column>
          </el-table>
          <!-- :page-sizes="[10, 25,50, 100]" -->
          <div class="table-pagination">
            <el-pagination
              v-model:currentPage="currentPage"
              v-model:page-size="pageSize"
              layout="total, sizes, prev, pager, next"
              :total="totalCount"
              @size-change="handlePageSize"
              @current-change="handleCurrentPage"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
// @ is an alias to /src
import { computed, nextTick, onMounted, reactive, ref, toRefs, watch } from 'vue';
// import useLanguageWatch from '@/hooks/useLanguageWatch';
import { useI18n } from 'vue-i18n';
// import { ElTabs } from 'element-plus';
// import { useRouter } from 'vue-router';
import { getClassifyList, getClassifyData } from '../api';

export default {
  name: 'Query',
  props: {
    data: {
      type: Object,
    },
  },
  setup(props) {
    let { t } = useI18n();
    let tabPanelOptions = ref();
    let tableColumnType = ref(true);
    let activeType = ref();
    let runTime = ref('');
    let tableData = ref();
    let runResult = ref('0');
    let searchValue = ref('');
    let pageReactive = reactive({
      latestTime: '',
      runTotal: '',
    });
    let tablePage = reactive({
      totalCount: 0,
      currentPage: 1,
      pageSize: 10,
    });
    let resultOptions = computed(() => [
      {
        label: t('common.all'),
        value: '0',
      },
      {
        label: t('common.success'),
        value: '1',
      },
      {
        label: t('common.fail'),
        value: '2',
      },
    ]);

    let tableColumn = computed(() => {
      let temp = [
        {
          label: '',
          maxWidth: '30',
          isSlowQuery: true,
        },
        {
          label: t('controlPage.runTime'),
          prop: 'runningTime',
          minWidth: '150px',
        },
        {
          label: t('controlPage.querySentence'),
          prop: 'statement',
          minWidth: '150px',
        },
        {
          label: t('controlPage.totalUseTime'),
          prop: 'totalTime',
          minWidth: '100px',
        },
      ];
      if (tableColumnType.value === 0) {
        temp = [
          ...temp,
          {
            label: t('controlPage.garmmarUseTime'),
            prop: 'analysisTime',
            minWidth: '120px',
          },
          {
            label: t('controlPage.ybyUseTime'),
            prop: 'precompiledTime',
            minWidth: '120px',
          },
          {
            label: t('controlPage.yhbyUseTime'),
            prop: 'optimizedTime',
            minWidth: '120px',
          },
          {
            label: t('controlPage.exeUseTime'),
            prop: 'executionTime',
            minWidth: '150px',
          },
        ];
      }
      temp.push({
        label: t('controlPage.exeResult'),
        prop: 'executionResult',
        minWidth: '80px',
        type: 'result',
      });
      return temp;
    });
    watch(activeType, (newValue) => {
      if (newValue) {
        QueryDataInit();
        tabPanelOptions.value.every((item) => {
          if (item.id + '' === newValue + '') {
            tableColumnType.value = item.flag;
            return false;
          }
          return true;
        });
        getCurrentQueryData(props.data.serverId, newValue);
      }
    });
    watch(
      () => props.data,
      (newValue) => {
        console.log(newValue);
        getClassifyList(newValue.serverId).then((res) => {
          console.log(res);
          tabPanelOptions.value = res.data.classificationList;
          activeType.value = '';
          nextTick(() => {
            activeType.value = tabPanelOptions.value[0]?.id + '';
          });
        });
      },
      { immediate: true }
    );

    onMounted(() => {});
    function QueryDataInit() {
      pageReactive.latestTime = '';
      pageReactive.runTotal = '';
      tablePage.currentPage = 1;
      tablePage.pageSize = 10;
      runResult.value = '0';
      searchValue.value = '';
    }
    function handlePageSize() {
      tablePage.currentPage = 1;
      getCurrentQueryData();
    }
    function handleCurrentPage() {
      getCurrentQueryData();
    }
    function getCurrentQueryData() {
      let query = {
        pageSize: tablePage.pageSize,
        pageNum: tablePage.currentPage,
        filterString: searchValue.value || undefined,
        startTime: (runTime.value && runTime.value[0]?.getTime()) || undefined,
        endTime: (runTime.value && runTime.value[1]?.getTime()) || undefined,
        executionResult: runResult.value,
      };
      console.log('接口参数', query);
      getClassifyData(props.data.serverId, activeType.value, query).then((res) => {
        console.log(res);
        let { data } = res;
        pageReactive.latestTime = data.latestRunningTime;
        pageReactive.runTotal = data.totalCount;
        tableData.value = data.filteredQueryDataStrVOSList;
        tablePage.totalCount = data.totalCount;
      });
    }
    function handleSearch() {
      getCurrentQueryData();
    }
    function handleDownload({ row }) {
      console.log('下载', row, props.data.serverId, activeType.value);
    }

    return {
      searchValue,
      activeType,
      tabPanelOptions,
      runTime,
      runResult,
      tableData,
      ...toRefs(tablePage),
      ...toRefs(pageReactive),
      //   ...toRefs(tablePage),

      resultOptions,
      tableColumn,

      handleSearch,
      //   handlePaginateChange,
      handleDownload,
      handleCurrentPage,
      handlePageSize,
    };
  },
  components: {
    // ElTabs,
  },
};
</script>

<style scoped lang="scss">
.query-main {
  min-height: calc(100% - 243px);
  padding: 20px;
  background: #f9fbfc;
  &-container {
    display: flex;
    background: #fff;
    border-radius: 4px;
    border: 1px solid #eaecf0;
    padding: 20px 20px 20px 0;
  }
  .container-left {
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
  .container-right {
    padding-left: 10px;
    width: 500px;
    flex: 1;
    &-tip {
      display: flex;
      align-items: center;
      height: 48px;
      padding: 0 20px;
      background: #fafafa;
      font-weight: 500;
      font-size: 12px;
      color: #808ba3;
      div {
        margin-left: 50px;
        display: flex;
        align-items: center;
      }
      div:first-child {
        margin-left: 0;
      }
      span {
        padding-left: 12px;
        font-size: 16px;
        font-weight: 400;
        color: #333;
        line-height: 24px;
      }
    }
    &-operate {
      font-size: 12px;
      display: flex;
      flex-wrap: wrap;
      align-items: center;
      .operate-item {
        margin-top: 20px;
        display: flex;
        align-items: center;
      }
      .operate-btn {
        margin-left: 20px;
        margin-top: 20px;
      }
      .input-box {
        width: 160px;
      }
      .select-box {
        width: 120px;
      }
      .input-label {
        padding: 0 10px;
      }
      &:deep .el-date-editor--datetimerange.el-input__inner {
        width: 320px;
      }
    }
    &-table {
      .slow-query {
        display: inline-block;
        background: #f33f2b;
        color: #fff;
        height: 18px;
        line-height: 18px;
        padding: 0 2px;
      }

      margin-top: 20px;
      .success {
        font-weight: 400;
        color: #5776ed;
      }
      .error {
        font-weight: 400;
        color: #fd5c5c;
      }
      .table-pagination {
        padding: 12px 0;
        text-align: right;
        &:deep button:hover {
          color: #15c294;
        }
        &:deep li {
          outline: none;
          &:hover {
            color: #15c294;
          }
          &.active {
            color: #15c294;
          }
        }
      }
    }
  }
  &:deep .el-input .el-input__inner {
    font-size: 12px;
  }
}
</style>