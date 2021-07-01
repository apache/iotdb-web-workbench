<template>
  <div id="myChart" class="echartsBox"></div>
</template>
<script>
import * as echarts from "echarts/core";
import { GridComponent } from "echarts/components";
import { LineChart } from "echarts/charts";
import { CanvasRenderer } from "echarts/renderers";
import { onMounted } from "@vue/runtime-core";
echarts.use([GridComponent, LineChart, CanvasRenderer]);
export default {
  name: "Echarts",
  setup() {
    onMounted(() => {
      let myChart = echarts.init(document.getElementById("myChart"));
      myChart.setOption({
        title: { text: "总用户量" },
        tooltip: {},
        grid: {
          left: "30px",
          top: "10px",
          width: "100%",
        },
        xAxis: {
          data: ["12-3", "12-4", "12-5", "12-6", "12-7", "12-8"],
          boundaryGap: false,
          axisTick: {
            show: false,
          },
          axisLine: {
            show: true,
            onZero: true,
            lineStyle: { color: "#ebeef5" },
          },
          axisLabel: {
            show: true,
            textStyle: {
              color: "black",
            },
          },
        },
        yAxis: {
          type: "value",
          max: 150,
          min: 0,
          splitNumber: 3,
          axisTick: {
            show: false,
          },
          axisLabel: {
            show: true,
            textStyle: {
              color: "black",
            },
          },
          axisLine: {
            show: true,
            lineStyle: { color: "#ebeef5" },
          },
          splitLine: {
            show: false,
            interval: "auto",
          },
        },
        series: [
          {
            type: "line",
            data: [50, 120, 76, 100, 100, 120],
            symbol: "none",
            itemStyle: {
              normal: {
                lineStyle: {
                  color: "#0cc100",
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
