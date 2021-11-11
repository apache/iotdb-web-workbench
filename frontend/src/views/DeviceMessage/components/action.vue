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
  <div :id="id" :class="{ echartsBox: !str, echartsBox1: str }"></div>
</template>
<script>
import * as echarts from 'echarts/core';
import { GridComponent } from 'echarts/components';
import { LineChart } from 'echarts/charts';
import { CanvasRenderer } from 'echarts/renderers';
import { onMounted, reactive, ref } from '@vue/runtime-core';
import { getData } from '@/views/DeviceMessage/api';
echarts.use([GridComponent, LineChart, CanvasRenderer]);
export default {
  name: 'action',
  props: {
    echartsData: Object,
    getDate: Function,
    row: Object,
    scope: Object,
    idCopyStr: Number,
  },
  setup(props) {
    const data = reactive(props.row);
    const eachrtsObj = reactive(props.echartsData);
    const str = reactive(props.idCopyStr);
    let eachrts = ref(null);
    let id = data.timeseries + (str || '');
    onMounted(() => {
      setTimeout(() => {
        getehartsData(data);
      }, 500);
    });
    function getehartsData(data) {
      getData(eachrtsObj.connectionid, eachrtsObj.storagegroupid, eachrtsObj.name, data.timeseries, { dataType: data.dataType }).then((res) => {
        const timearr = res.data.timeList.reverse().slice(0, 20);
        const dataarr = res.data.valueList;
        let copy = res.data.valueList
          .sort(function (m, n) {
            if (m * 1 < n * 1) return 1;
            else if (m * 1 > n * 1) return -1;
            else return 0;
          })
          .slice(res.data.valueList.length / 2 - 10, res.data.valueList.length / 2 + 10);
        copy.sort(function () {
          return Math.random() > 0.5 ? -1 : 1;
        });
        let max = Math.max.apply(null, dataarr);
        let min = Math.min.apply(null, dataarr);
        let myChart = echarts.init(document.getElementById(id));
        myChart.setOption({
          grid: {
            width: 'auto',
            height: 'auto',
          },
          xAxis: {
            show: false,
            data: timearr,
          },
          yAxis: {
            type: 'value',
            max: max,
            min: min,
            show: false,
            splitNumber: 1,
          },
          series: [
            {
              type: 'line',
              data: copy,
              symbol: 'none',
              itemStyle: {
                normal: {
                  lineStyle: {
                    color: !str ? '#16C493' : '#D1D6E6',
                  },
                },
              },
            },
          ],
        });
        window.onresize = function () {
          //自适应大小
          myChart.resize();
        };
      });
    }
    return {
      getehartsData,
      str,
      id,
      eachrts,
    };
  },
};
</script>

<style lang="scss" scoped>
.echartsBox {
  width: 150px;
  height: 30px;
  margin-left: -15px;
}
.echartsBox1 {
  width: 100%;
  height: 30px;
  margin-left: -15px;
}
</style>
