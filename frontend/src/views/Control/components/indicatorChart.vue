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
