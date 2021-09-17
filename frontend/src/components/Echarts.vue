/* * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements. See the NOTICE file * distributed with this work for additional information * regarding
copyright ownership. The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License. You may obtain a
copy of the License at * * http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, * software distributed under the License is distributed on an * "AS
IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY * KIND, either express or implied. See the License for the * specific language governing permissions and limitations * under the License. */

<template>
  <div id="myChart" class="echartsBox"></div>
</template>
<script>
import * as echarts from 'echarts';
import { GridComponent } from 'echarts/components';
import { LineChart } from 'echarts/charts';
import { CanvasRenderer } from 'echarts/renderers';
import { onMounted, reactive, ref } from '@vue/runtime-core';
import { getData } from '../views/DeviceMessage/api';
echarts.use([GridComponent, LineChart, CanvasRenderer]);
export default {
  name: 'Echarts',
  props: {
    echartsData: Object,
    getDate: Function,
  },
  setup(props) {
    const data = reactive(props.echartsData);
    const time = reactive({
      list: [],
    });
    const value = reactive({
      list: [],
    });
    const timeCopy = reactive({
      list: [],
    });
    const valueCopy = reactive({
      list: [],
    });
    let max = ref(0);
    let min = ref(0);
    onMounted(() => {
      getehartsData(data);
    });
    function setEchartsTime(times) {
      let startTime = Date.parse(times[0]);
      let endTime = Date.parse(times[1]);
      time.list = [];
      value.list = [];
      timeCopy.list.forEach((item, index) => {
        if (Date.parse(item) > startTime && Date.parse(item) < endTime) {
          time.list.push(timeCopy.list[index]);
          value.list.push(valueCopy.list[index]);
        }
      });
      setEcharts();
    }
    function setEcharts() {
      let length = Math.floor(time.list.length / 5);
      time.list.forEach((item, index) => {
        time.list[index] = formatDate(item);
      });
      let myChart = echarts.init(document.getElementById('myChart'));
      myChart.setOption({
        tooltip: {
          trigger: 'axis',
        },
        toolbox: {
          feature: {
            // saveAsImage: {},
          },
        },
        grid: {
          left: '100px',
          top: '10px',
          width: '100%',
        },
        xAxis: {
          type: 'category',
          data: time.list,
          boundaryGap: false,
          axisTick: {
            show: false,
          },
          axisLine: {
            show: true,
            onZero: true,
            lineStyle: { color: '#ebeef5' },
          },
          axisLabel: {
            show: true,
            align: 'left',
            textStyle: {
              color: 'black',
            },
            interval: length - 1,
          },
        },
        yAxis: {
          type: 'value',
          max: max,
          min: min,
          splitNumber: 1,
          axisTick: {
            show: false,
          },
          axisLabel: {
            show: true,
            textStyle: {
              color: 'black',
            },
          },
          axisLine: {
            show: true,
            lineStyle: { color: '#ebeef5' },
          },
          splitLine: {
            show: false,
            interval: 'auto',
          },
        },
        series: [
          {
            name: '值',
            type: 'line',
            data: value.list,
            symbol: 'none',
            itemStyle: {
              normal: {
                lineStyle: {
                  color: '#16C493',
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
    }
    function getehartsData(data) {
      getData(data.connectionid, data.storagegroupid, data.name, data.timeseries, { dataType: data.dataType }).then((res) => {
        time.list = res.data.timeList;
        timeCopy.list = JSON.parse(JSON.stringify(res.data.timeList.reverse()));
        value.list = res.data.valueList;
        valueCopy.list = JSON.parse(JSON.stringify(res.data.valueList.reverse()));
        props.getDate(res.data.timeList[0], res.data.timeList[res.data.timeList.length - 1]);
        max = Math.max(...res.data.valueList);
        min = Math.min(...res.data.valueList);
        setEcharts();
      });
    }
    function formatDate(datec) {
      let date = new Date(datec);
      let YY = date.getFullYear() + '-';
      let MM = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
      let DD = date.getDate() < 10 ? '0' + date.getDate() : date.getDate();
      let hh = (date.getHours() < 10 ? '0' + date.getHours() : date.getHours()) + ':';
      let mm = (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()) + ':';
      let ss = date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds();
      let hs = date.getMilliseconds();
      return YY + MM + DD + ' ' + hh + mm + ss + ':' + hs;
    }
    return {
      getehartsData,
      setEchartsTime,
    };
  },
};
</script>

<style lang="scss" scoped>
.echartsBox {
  height: 300px;
  padding-left: 30px;
  // width: 100%;
  // position: absolute;
  // top: 50px;
  // left: -30px;
}
</style>
