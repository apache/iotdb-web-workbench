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
  <div class="chart-box">
    <div class="chart-title">{{ title }}</div>
    <div class="chart-con">
      <div class="chart" ref="chartDom"></div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts';
import { onMounted, ref, watch } from 'vue';
export default {
  name: 'IndicatorChart',
  props: {
    data: {
      type: Object,
    },
  },
  setup(props) {
    let chartDom = ref();
    let Chart;
    onMounted(() => {
      let option;
      console.log(chartDom.value);
      Chart = echarts.init(chartDom.value);
      option = {
        tooltip: {
          trigger: 'axis',
          textStyle: {
            align: 'left',
          },
        },
        grid: {
          top: '20px',
          left: '20px',
          right: '20px',
          bottom: '44px',
          containLabel: true,
        },
        xAxis: {
          type: 'category',
          axisTick: { show: false },
          axisLine: { show: true, lineStyle: { color: ' #eee', width: 1 } },
          axisLabel: { show: true, textStyle: { color: '#8E97AA' } },
        },
        yAxis: {
          type: 'value',
          axisLine: { show: true, lineStyle: { color: ' #eee', width: 1 } },
          axisLabel: { show: true, textStyle: { color: '#8E97AA' } },
          splitLine: { show: false },
        },
      };
      Object.assign(option, props.data.options);
      option && Chart.setOption(option);
    });
    watch(
      () => props.data,
      (newValue) => {
        if (newValue) {
          Chart.setOption(newValue.options);
        }
      },
      {
        deep: true,
      }
    );

    return {
      title: props.data.title,
      chartDom,
    };
  },
};
</script>

<style lang="scss" scoped>
.chart-box {
  display: flex;
  flex-direction: column;
  height: 100%;
}
.chart-con {
  flex: 1;
}
.chart {
  height: 100%;
}
.chart-title {
  padding: 20px 0 0 20px;
  font-size: 14px;
  font-weight: 500;
  color: #333;
  line-height: 22px;
  text-align: left;
}
</style>
