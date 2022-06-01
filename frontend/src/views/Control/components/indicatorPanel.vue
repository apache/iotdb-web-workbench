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
  <div class="indicator-list">
    <div class="list-head">
      <div class="select-box">
        <el-select v-model="modeValue" class="selece-box-mode" placeholder="请选择">
          <el-option v-for="item in modeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-select v-model="typeValue" class="selece-box-type" :placeholder="$t('controlPage.selectMetrics')">
          <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </div>
      <div>
        <el-button type="primary" @click="handleChangePanel">{{ $t('controlPage.listBtn') }}</el-button>
        <el-button @click="handleRefresh">{{ $t('common.refresh') }}</el-button>
      </div>
    </div>
    <div class="list-center">
      <div v-for="(item, index) in chartList" v-show="isShowChart(item.name)" :key="'chart' + index" :class="['chart-item', 'chart-' + item.size]" :tag="item.name">
        <IndicatorChart v-if="ChartObject[item.name]" :data="ChartObject[item.name]" />
      </div>
    </div>
  </div>
</template>

<script>
import { computed, nextTick, onMounted, onUnmounted, reactive, ref, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import IndicatorChart from './indicatorChart';
import useInitChart from '../hooks/useInitChart';
import { useI18n } from 'vue-i18n';
export default {
  name: 'IndicatorPanel',
  props: {
    currentData: {
      type: Object,
    },
  },
  components: {
    IndicatorChart,
  },
  setup(props) {
    const typeMap = {
      CPU: '4',
      memory: '5',
      store: '6',
      write: '7',
      search: '8',
    };
    const chartList = [
      {
        name: 'GCEchart',
        size: '50',
      },

      {
        name: 'JVMClassEchart',
        size: '50',
      },
      {
        name: 'YGCEchart',
        size: '100',
      },
      {
        name: 'FGCEchart',
        size: '100',
      },
      {
        name: 'JAVATypeEchart',
        size: '50',
      },
      {
        name: 'JAVATimeEchart',
        size: '50',
      },
      {
        name: 'MemoryEchart',
        size: '50',
      },
      {
        name: 'BufferEchart',
        size: '50',
      },
      {
        name: 'CPUEchart',
        size: '50',
      },
      {
        name: 'IOEchart',
        size: '50',
      },
      {
        name: 'FileCountEchart',
        size: '50',
      },
      {
        name: 'FileSizeEchart',
        size: '50',
      },
      {
        name: 'WriteEchart',
        size: '50',
      },
      {
        name: 'SearchEchart',
        size: '50',
      },
      {
        name: 'ApiEchart',
        size: '50',
      },
      {
        name: 'ApiQPSEchart',
        size: '50',
      },
    ];
    let DOMQueue = [];
    let { t, locale } = useI18n();
    let router = useRouter();
    let route = useRoute();
    let modeValue = ref(route.query?.mode || '0');
    const typeDefault = route.params.mode === 'JVM' ? route.query.type : typeMap[route.params.mode];
    let typeValue = ref(typeDefault);
    let ChartObject = reactive({
      GCEchart: null,
      JVMClassEchart: null,
      YGCEchart: null,
      FGCEchart: null,
      JAVATypeEchart: null,
      JAVATimeEchart: null,
      MemoryEchart: null,
      BufferEchart: null,
      CPUEchart: null,
      IOEchart: null,
      FileCountEchart: null,
      FileSizeEchart: null,
      WriteEchart: null,
      SearchEchart: null,
      ApiEchart: null,
      ApiQPSEchart: null,
    });

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
    let typeOptions = computed(() => {
      const JVM = [
        {
          label: t('controlPage.JVMThread'),
          value: '0',
        },
        {
          label: t('controlPage.JVMRecycle'),
          value: '1',
        },
        {
          label: t('controlPage.JVMMemory'),
          value: '2',
        },
        {
          label: t('controlPage.JVMClasses'),
          value: '3',
        },
      ];
      const CPU = {
        label: t('controlPage.cpu'),
        value: '4',
      };
      const memory = {
        label: t('controlPage.memory'),
        value: '5',
      };
      const store = {
        label: t('controlPage.store'),
        value: '6',
      };
      const write = {
        label: t('controlPage.write'),
        value: '7',
      };
      const search = {
        label: t('controlPage.isearch'),
        value: '8',
      };
      if (modeValue.value === '0') {
        return [...JVM, CPU, memory, store, write, search];
      } else if (modeValue.value === '1') {
        return [...JVM, write, search];
      } else {
        return [CPU, memory, store, write, search];
      }
    });
    watch(modeValue, () => {
      typeValue.value = '';
      nextTick(() => {
        lazyChart();
      });
    });
    watch(typeValue, () => {
      nextTick(() => {
        lazyChart();
      });
    });
    watch(
      () => props.currentData,
      () => {
        handleRefresh();
      }
    );
    watch(locale, async () => {
      handleRefresh('0');
    });
    onMounted(() => {
      DOMQueue = [...document.querySelectorAll('.chart-item')];
      let main = document.querySelectorAll('main')[0];
      main?.addEventListener('scroll', lazyChart);
      main && lazyChart();
    });
    onUnmounted(() => {
      document.querySelectorAll('main')[0]?.removeEventListener('scroll', lazyChart);
    });

    function isShowChart(name) {
      // JVM-线程
      let JVMThreadMap = {
        JAVATypeEchart: true,
        JAVATimeEchart: true,
      };
      // JVM-垃圾回收
      let JVMRecycleMap = {
        YGCEchart: true,
        FGCEchart: true,
        GCEchart: true,
      };
      // JVM-内存
      let JVMMemoryMap = {
        BufferEchart: true,
      };
      // JVM-classes
      let JVMclassesMap = {
        JVMClassEchart: true,
      };
      //   CPU
      let CPUMap = {
        CPUEchart: true,
      };
      //   内存
      let MemoryMap = {
        MemoryEchart: true,
      };
      //   存储
      let StoreMap = {
        IOEchart: true,
        FileCountEchart: true,
        FileSizeEchart: true,
      };
      //   写入
      let WriteMap = {
        WriteEchart: true,
      };
      //   查询
      let SearchMap = {
        SearchEchart: true,
        ApiEchart: true,
        ApiQPSEchart: true,
      };
      let devChartMap = {
        ...JVMThreadMap,
        ...JVMRecycleMap,
        ...JVMMemoryMap,
        ...JVMclassesMap,
      };
      let operatorMap = {
        ...CPUMap,
        ...MemoryMap,
        ...StoreMap,
        ...WriteMap,
        ...SearchMap,
      };
      let type2Map = {
        0: JVMThreadMap,
        1: JVMRecycleMap,
        2: JVMMemoryMap,
        3: JVMclassesMap,
        4: CPUMap,
        5: MemoryMap,
        6: StoreMap,
        7: WriteMap,
        8: SearchMap,
      };
      if (!typeValue.value) {
        if (modeValue.value === '0') {
          return true;
        } else if (modeValue.value === '1') {
          return devChartMap[name];
        } else if (modeValue.value === '2') {
          return operatorMap[name];
        }
      } else {
        return type2Map[typeValue.value][name];
      }
    }
    function handleChangePanel() {
      router.push({ path: `/control/indicator/list/${route.params.id}/${route.params.mode}`, query: { mode: modeValue.value } });
    }
    function lazyChart(refreshData) {
      DOMQueue.filter((item) => {
        let tag = item.getAttribute('tag');
        if (window.getComputedStyle(item).display !== 'none' && !ChartObject[tag]) {
          return true;
        }
      }).map((item) => {
        let rect = item.getBoundingClientRect();
        if (rect.top + 100 <= (window.innerHeight || document.documentElement.clientHeight) && rect.top > -300) {
          initMatchChart(item.getAttribute('tag'), refreshData);
        }
      });
    }
    async function initMatchChart(name, refreshData) {
      let result = await useInitChart(props.currentData.serverId, name, refreshData);
      ChartObject[name] = result;
    }
    function handleRefresh(refreshData) {
      let arr = [...document.querySelectorAll('.chart-item')];
      arr.map((item) => {
        if (window.getComputedStyle(item).display !== 'none') {
          let chartName = item.getAttribute('tag');
          ChartObject[chartName] = null;
        }
      });
      lazyChart(refreshData);
    }
    return {
      modeValue,
      chartList,
      typeValue,
      modeOptions,
      typeOptions,
      ChartObject,

      handleChangePanel,
      handleRefresh,
      isShowChart,
    };
  },
};
</script>

<style lang="scss" scoped>
.indicator-list {
  .list-head {
    display: flex;
    justify-content: space-between;
    padding: 12px 20px;
    border: 1px solid #eaecf0;
  }
  .list-center {
    display: grid;
    border-left: 1px solid #eaecf0;
    grid-template-columns: repeat(2, 50%);
  }
  .chart-item {
    box-sizing: border-box;
    height: 304px;
    border-right: 1px solid #eaecf0;
    border-bottom: 1px solid #eaecf0;
  }
  .chart-50 {
    width: 100%;
  }
  .chart-100 {
    grid-column: 1/3;
    width: 100%;
  }
}
.selece-box-mode {
  width: 120px;
}
.selece-box-type {
  margin-left: 20px;
  width: 200px;
}
:deep .el-select .el-input__inner {
  font-size: 12px;
}
</style>
