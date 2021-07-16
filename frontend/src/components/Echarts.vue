<template>
  <div id="myChart" class="echartsBox"></div>
</template>
<script>
import * as echarts from 'echarts/core';
import { GridComponent } from 'echarts/components';
import { LineChart } from 'echarts/charts';
import { CanvasRenderer } from 'echarts/renderers';
import { onMounted, reactive } from '@vue/runtime-core';
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
    onMounted(() => {
      getehartsData(data);
    });
    function getehartsData(data) {
      getData(data.connectionid, data.storagegroupid, data.name, data.timeseries).then((res) => {
        let length = res.data.timeList.length / 5;
        for (let i = 0; i < 5; i++) {
          res.data.timeList[i * length] = formatDate(res.data.timeList[i * length]);
        }
        props.getDate(res.data.timeList[0], res.data.timeList[res.data.timeList.length - 1]);
        let max = Math.max.apply(null, res.data.valueList);
        let myChart = echarts.init(document.getElementById('myChart'));
        myChart.setOption({
          title: { text: '总用户量' },
          tooltip: {},
          grid: {
            left: '30px',
            top: '10px',
            width: '100%',
          },
          xAxis: {
            data: res.data.timeList,
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
            min: 0,
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
              type: 'line',
              data: res.data.valueList,
              symbol: 'none',
              itemStyle: {
                normal: {
                  lineStyle: {
                    color: '#0cc100',
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
    function formatDate(datec) {
      var date = new Date(datec);
      var YY = date.getFullYear() + '-';
      var MM = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
      var DD = date.getDate() < 10 ? '0' + date.getDate() : date.getDate();
      var hh = (date.getHours() < 10 ? '0' + date.getHours() : date.getHours()) + ':';
      var mm = (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()) + ':';
      var ss = date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds();
      return YY + MM + DD + ' ' + hh + mm + ss;
    }
    return {
      getehartsData,
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
