<template>
  <div :id="id" class="echartsBox"></div>
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
  },
  setup(props) {
    const data = reactive(props.row);
    const eachrtsObj = reactive(props.echartsData);
    let eachrts = ref(null);
    const id = data.timeseries;
    onMounted(() => {
      setTimeout(() => {
        getehartsData(data);
      }, 500);
    });
    function getehartsData(data) {
      getData(eachrtsObj.connectionid, eachrtsObj.storagegroupid, eachrtsObj.name, data.timeseries).then((res) => {
        const timearr = res.data.timeList.reverse().slice(0, 20);
        const dataarr = res.data.valueList.reverse().slice(0, 20);
        let max = Math.max.apply(null, dataarr);
        let myChart = echarts.init(document.getElementById(data.timeseries));
        console.log(myChart.setOption);
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
            min: 0,
            show: false,
            splitNumber: 1,
          },
          series: [
            {
              type: 'line',
              data: dataarr,
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
    return {
      getehartsData,
      id,
      eachrts,
    };
  },
};
</script>

<style lang="scss" scoped>
.echartsBox {
  width: 150px;
  height: 70px;
  margin-left: -15px;
}
</style>
