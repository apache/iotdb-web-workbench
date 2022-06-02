/*
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
 */
import { getChartData } from '../api';
import { useI18n } from 'vue-i18n';
import { get } from 'lodash';
const ChartMap = {
  GCEchart: 0,
  JVMClassEchart: 1,
  YGCEchart: 2,
  FGCEchart: 3,
  JAVATypeEchart: 4,
  JAVATimeEchart: 5,
  MemoryEchart: 6,
  BufferEchart: 7,
  CPUEchart: 8,
  IOEchart: 9,
  FileCountEchart: 10,
  FileSizeEchart: 11,
  WriteEchart: 12,
  SearchEchart: 13,
  ApiEchart: 14,
  ApiQPSEchart: 15,
};
let ChartCacheData = {};
let chartsTitle = null;
let t = null;
async function initMatchChart(serverId, type, refreshData = true) {
  !t && (t = useI18n().t);
  chartsTitle = {
    GCEchart: t('controlPage.GCEchart'),
    JVMClassEchart: t('controlPage.JVMClassEchart'),
    YGCEchart: t('controlPage.YGCEchart'),
    FGCEchart: t('controlPage.FGCEchart'),
    JAVATypeEchart: t('controlPage.JAVATypeEchart'),
    JAVATimeEchart: t('controlPage.JAVATimeEchart'),
    MemoryEchart: t('controlPage.MemoryEchart'),
    BufferEchart: t('controlPage.BufferEchart'),
    CPUEchart: t('controlPage.CPUEchart'),
    IOEchart: t('controlPage.IOEchart'),
    FileCountEchart: t('controlPage.FileCountEchart'),
    FileSizeEchart: t('controlPage.FileSizeEchart'),
    WriteEchart: t('controlPage.WriteEchart'),
    SearchEchart: t('controlPage.SearchEchart'),
    ApiEchart: t('controlPage.ApiEchart'),
    ApiQPSEchart: t('controlPage.ApiQPSEchart'),
  };
  let res;
  // Cache
  if (refreshData === '0') {
    res = ChartCacheData[type];
  } else {
    let result = await getChartData(serverId, ChartMap[type]);
    ChartCacheData[type] = result.data.chartData;
    res = result.data.chartData;
  }
  if (type === 'GCEchart') {
    let temp = {
      title: chartsTitle[type],
      options: {
        color: ['#379E7D', '#5776ED', '#F69823', '#FD6031'],
        legend: {
          type: 'scroll',
          data: res?.metricnameList,
          bottom: '12px',
        },
        xAxis: {
          type: 'category',
          axisTick: { show: false },
          axisLine: { show: true, lineStyle: { color: ' #eee', width: 1 } },
          axisLabel: { show: true, textStyle: { color: '#8E97AA' } },
          data: res ? res.timeList : [],
        },
        yAxis: {
          type: 'category',
        },
        series: res?.metricnameList.map((item, index) => {
          return {
            name: item,
            data: res?.dataList && get(res, `dataList.${get(res, `metricnameList.${index}`)}`),
            type: 'bar',
            barGap: '0',
          };
        }),
      },
    };
    return temp;
  } else if (type === 'JVMClassEchart') {
    let temp = {
      title: chartsTitle[type],
      options: {
        color: ['#5776ED', '#FD5C5C'],
        legend: {
          data: res?.metricnameList,
          bottom: '12px',
        },
        xAxis: {
          type: 'category',
          axisTick: { show: false },
          axisLine: { show: true, lineStyle: { color: ' #eee', width: 1 } },
          axisLabel: { show: true, textStyle: { color: '#8E97AA' } },
          data: res ? res.timeList : [],
        },
        yAxis: {
          axisLabel: {
            //这种做法就是在y轴的数据的值旁边拼接单位，貌似也挺方便的
            formatter: `{value}${get(res, 'unitList.0')}`,
          },
        },
        series: res?.metricnameList.map((item, index) => {
          return {
            name: item,
            data: res?.dataList && get(res, `dataList.${get(res, `metricnameList.${index}`)}`),
            type: 'line',
          };
        }),
      },
    };
    return temp;
  } else if (type === 'YGCEchart') {
    let temp = {
      title: chartsTitle[type],
      options: {
        color: ['#F69823'],
        tooltip: {},
        xAxis: {
          type: 'value',
          splitLine: { show: false },
          axisTick: { show: false },
          axisLine: { show: true, lineStyle: { color: ' #eee', width: 1 } },
          axisLabel: { show: true, textStyle: { color: '#8E97AA' }, formatter: `{value}${get(res, 'unitList.0')}` },
        },
        yAxis: {
          inverse: true,
          type: 'category',
          axisTick: { show: false },
          data: res ? res.metricnameList : [],
          axisLine: { show: true, lineStyle: { color: ' #eee', width: 1 } },
          axisLabel: { show: true, textStyle: { color: '#8E97AA' } },
        },
        grid: {
          top: '20px',
          left: '20px',
          right: '20px',
          bottom: '16px',
          containLabel: true,
        },
        series: [
          {
            type: 'bar',
            data: res?.metricnameList.map((item, index) => {
              return res?.dataList && get(res, `dataList.${get(res, `metricnameList.${index}`)}`).map((item) => parseFloat(item))[0];
            }),
          },
        ],
      },
    };
    return temp;
  } else if (type === 'FGCEchart') {
    let temp = {
      title: chartsTitle[type],
      options: {
        color: ['#5855DA'],
        tooltip: {},
        xAxis: {
          type: 'value',
          splitLine: { show: false },
          axisTick: { show: false },
          axisLine: { show: true, lineStyle: { color: ' #eee', width: 1 } },
          axisLabel: { show: true, textStyle: { color: '#8E97AA' }, formatter: `{value}${get(res, 'unitList.0')}` },
        },
        yAxis: {
          inverse: true,
          type: 'category',
          data: res ? res.metricnameList : [],
          axisTick: { show: false },
          axisLine: { show: true, lineStyle: { color: ' #eee', width: 1 } },
          axisLabel: { show: true, textStyle: { color: '#8E97AA' } },
        },
        grid: {
          top: '20px',
          left: '20px',
          right: '20px',
          bottom: '16px',
          containLabel: true,
        },
        series: [
          {
            type: 'bar',
            data: res?.metricnameList.map((item, index) => {
              return res?.dataList && get(res, `dataList.${get(res, `metricnameList.${index}`)}`).map((item) => parseFloat(item))[0];
            }),
          },
        ],
      },
    };
    return temp;
  } else if (type === 'JAVATypeEchart') {
    let temp = {
      title: chartsTitle[type],
      options: {
        color: ['#F69823', '#FD5C5C', '#5776ED'],
        legend: {
          data: res?.metricnameList,
          bottom: '12px',
        },
        xAxis: {
          type: 'category',
          axisTick: { show: false },
          axisLine: { show: true, lineStyle: { color: ' #eee', width: 1 } },
          axisLabel: { show: true, textStyle: { color: '#8E97AA' } },
          data: res?.timeList,
        },
        yAxis: {
          axisLabel: {
            //这种做法就是在y轴的数据的值旁边拼接单位，貌似也挺方便的
            formatter: `{value}${get(res, 'unitList.0')}`,
          },
        },
        series: res?.metricnameList.map((item, index) => {
          return {
            name: item,
            data: res?.dataList && get(res, `dataList.${get(res, `metricnameList.${index}`)}`),
            type: 'line',
          };
        }),
      },
    };
    return temp;
  } else if (type === 'JAVATimeEchart') {
    let temp = {
      title: chartsTitle[type],
      options: {
        color: ['#379E7D', '#66A5FF', '#5776ED', '#F69823', '#FD5C5C', '#50D6BB'],
        legend: {
          type: 'scroll',
          data: res?.metricnameList,
          bottom: '12px',
        },
        xAxis: {
          type: 'category',
          axisTick: { show: false },
          axisLine: { show: true, lineStyle: { color: ' #eee', width: 1 } },
          axisLabel: { show: true, textStyle: { color: '#8E97AA' } },
          data: res?.timeList,
        },
        yAxis: {
          axisLabel: {
            //这种做法就是在y轴的数据的值旁边拼接单位，貌似也挺方便的
            formatter: `{value}${get(res, 'unitList.0')}`,
          },
        },
        series: res?.metricnameList.map((item, index) => {
          return {
            name: item,
            data: res?.dataList && get(res, `dataList.${get(res, `metricnameList.${index}`)}`),
            type: 'line',
          };
        }),
      },
    };
    return temp;
  } else if (type === 'MemoryEchart') {
    let temp = {
      title: chartsTitle[type],
      options: {
        color: ['#379E7D', '#5776ED'],
        legend: {
          data: res?.metricnameList,
          bottom: '12px',
        },
        xAxis: {
          type: 'category',
          axisTick: { show: false },
          axisLine: { show: true, lineStyle: { color: ' #eee', width: 1 } },
          axisLabel: { show: true, textStyle: { color: '#8E97AA' } },
          data: res?.timeList,
        },
        yAxis: {
          axisLabel: {
            //这种做法就是在y轴的数据的值旁边拼接单位，貌似也挺方便的
            formatter: `{value}${get(res, 'unitList.0')}`,
          },
        },
        series: res?.metricnameList.map((item, index) => {
          return {
            name: item,
            data: res?.dataList && get(res, `dataList.${get(res, `metricnameList.${index}`)}`),
            type: 'line',
          };
        }),
      },
    };
    return temp;
  } else if (type === 'BufferEchart') {
    let temp = {
      title: chartsTitle[type],
      options: {
        color: ['#379E7D', '#5776ED'],
        legend: {
          data: res?.metricnameList,
          bottom: '12px',
        },
        xAxis: {
          type: 'category',
          axisTick: { show: false },
          axisLine: { show: true, lineStyle: { color: ' #eee', width: 1 } },
          axisLabel: { show: true, textStyle: { color: '#8E97AA' } },
          data: res?.timeList,
        },
        yAxis: {
          axisLabel: {
            //这种做法就是在y轴的数据的值旁边拼接单位，貌似也挺方便的
            formatter: `{value}${get(res, 'unitList.0')}`,
          },
        },
        series: res?.metricnameList.map((item, index) => {
          return {
            name: item,
            data: res?.dataList && get(res, `dataList.${get(res, `metricnameList.${index}`)}`),
            type: 'line',
          };
        }),
      },
    };
    return temp;
  } else if (type === 'CPUEchart') {
    let data = [];
    res?.metricnameList.map((item) => {
      data.push({ value: parseFloat(get(res, `dataList.${item}`)), name: item });
    });
    let temp = {
      title: chartsTitle[type],
      options: {
        color: ['#379E7D', '#5776ED', '#66A5FF', '#F69823', '#FD5C5C'],
        legend: {
          type: 'scroll',
          bottom: '12px',
        },
        tooltip: {
          trigger: 'item',
          textStyle: {
            align: 'left',
          },
          formatter: function (val) {
            console.log(val.name, val.value);
            return `${val.marker}${val.name}&nbsp;&nbsp;&nbsp;&nbsp;${val.value}%`;
          },
        },
        xAxis: null,
        yAxis: null,
        series: [
          {
            name: chartsTitle[type],
            type: 'pie',
            radius: ['45%', '70%'],
            avoidLabelOverlap: false,
            center: ['50%', '120px'],
            label: {
              show: false,
              position: 'center',
            },
            emphasis: {
              label: {
                show: false,
                fontSize: '40',
                fontWeight: 'bold',
              },
            },
            labelLine: {
              show: false,
            },
            data,
          },
        ],
      },
    };
    return temp;
  } else if (type === 'IOEchart') {
    let temp = {
      title: chartsTitle[type],
      options: {
        color: ['#379E7D'],
        legend: null,
        xAxis: {
          type: 'category',
          axisTick: { show: false },
          axisLine: { show: true, lineStyle: { color: ' #eee', width: 1 } },
          axisLabel: { show: true, textStyle: { color: '#8E97AA' } },
          data: res?.timeList,
        },
        yAxis: {
          axisLabel: {
            //这种做法就是在y轴的数据的值旁边拼接单位，貌似也挺方便的
            formatter: `{value}${get(res, 'unitList.0')}`,
          },
        },
        series: res?.metricnameList.map((item, index) => {
          return {
            name: item,
            data: res?.dataList && get(res, `dataList.${get(res, `metricnameList.${index}`)}`),
            type: 'line',
          };
        }),
      },
    };
    return temp;
  } else if (type === 'FileCountEchart') {
    let temp = {
      title: chartsTitle[type],
      options: {
        color: ['#5776ED', '#66A5FF', '#F69823', '#379E7D'],
        legend: {
          type: 'scroll',
          data: res?.metricnameList,
          bottom: '12px',
        },
        xAxis: {
          type: 'category',
          axisTick: { show: false },
          axisLine: { show: true, lineStyle: { color: ' #eee', width: 1 } },
          axisLabel: { show: true, textStyle: { color: '#8E97AA' } },
          data: res?.timeList,
        },
        yAxis: {
          axisLabel: {
            //这种做法就是在y轴的数据的值旁边拼接单位，貌似也挺方便的
            formatter: `{value}${get(res, 'unitList.0')}`,
          },
        },
        series: res?.metricnameList.map((item, index) => {
          return {
            name: item,
            data: res?.dataList && get(res, `dataList.${get(res, `metricnameList.${index}`)}`),
            type: 'line',
          };
        }),
      },
    };
    return temp;
  } else if (type === 'FileSizeEchart') {
    let temp = {
      title: chartsTitle[type],
      options: {
        color: ['#5776ED', '#66A5FF', '#F69823', '#379E7D'],
        legend: {
          type: 'scroll',
          data: res?.metricnameList,
          bottom: '12px',
        },
        xAxis: {
          type: 'category',
          axisTick: { show: false },
          axisLine: { show: true, lineStyle: { color: ' #eee', width: 1 } },
          axisLabel: { show: true, textStyle: { color: '#8E97AA' } },
          data: res?.timeList,
        },
        yAxis: {
          axisLabel: {
            //这种做法就是在y轴的数据的值旁边拼接单位，貌似也挺方便的
            formatter: `{value}${get(res, 'unitList.0')}`,
          },
        },
        series: res?.metricnameList.map((item, index) => {
          return {
            name: item,
            data: res?.dataList && get(res, `dataList.${get(res, `metricnameList.${index}`)}`),
            type: 'line',
          };
        }),
      },
    };
    return temp;
  } else if (type === 'WriteEchart') {
    let temp = {
      title: chartsTitle[type],
      options: {
        color: ['#379E7D', '#FD6031', '#5776ED'],
        legend: {
          data: res?.metricnameList,
          bottom: '12px',
        },
        xAxis: {
          type: 'category',
          axisTick: { show: false },
          axisLine: { show: true, lineStyle: { color: ' #eee', width: 1 } },
          axisLabel: { show: true, textStyle: { color: '#8E97AA' } },
          data: res?.timeList,
        },
        yAxis: {
          axisLabel: {
            //这种做法就是在y轴的数据的值旁边拼接单位，貌似也挺方便的
            formatter: `{value}${get(res, 'unitList.0')}`,
          },
        },
        series: res?.metricnameList.map((item, index) => {
          return {
            name: item,
            data: res?.dataList && get(res, `dataList.${get(res, `metricnameList.${index}`)}`),
            type: 'line',
          };
        }),
      },
    };

    return temp;
  } else if (type === 'SearchEchart') {
    let temp = {
      title: chartsTitle[type],
      options: {
        color: ['#379E7D', '#FD6031', '#5776ED'],
        legend: {
          data: res?.metricnameList,
          bottom: '12px',
        },
        xAxis: {
          type: 'category',
          axisTick: { show: false },
          axisLine: { show: true, lineStyle: { color: ' #eee', width: 1 } },
          axisLabel: { show: true, textStyle: { color: '#8E97AA' } },
          data: res?.timeList,
        },
        yAxis: {
          axisLabel: {
            //这种做法就是在y轴的数据的值旁边拼接单位，貌似也挺方便的
            formatter: `{value}${get(res, 'unitList.0')}`,
          },
        },
        series: res?.metricnameList.map((item, index) => {
          return {
            name: item,
            data: res?.dataList && get(res, `dataList.${get(res, `metricnameList.${index}`)}`),
            type: 'bar',
            barGap: '0',
          };
        }),
      },
    };
    return temp;
  } else if (type === 'ApiEchart') {
    let temp = {
      title: chartsTitle[type],
      options: {
        color: ['#379E7D', '#66A5FF', '#5776ED', '#F69823', '#FD5C5C', '#50D6BB'],
        legend: {
          type: 'scroll',
          data: res?.metricnameList,
          bottom: '12px',
          left: '20px',
        },
        xAxis: {
          type: 'category',
          axisTick: { show: false },
          axisLine: { show: true, lineStyle: { color: ' #eee', width: 1 } },
          axisLabel: { show: true, textStyle: { color: '#8E97AA' } },
          data: res?.timeList,
        },
        yAxis: {
          axisLabel: {
            //这种做法就是在y轴的数据的值旁边拼接单位，貌似也挺方便的
            formatter: `{value}${get(res, 'unitList.0')}`,
          },
        },
        series: res?.metricnameList.map((item, index) => {
          return {
            name: item,
            data: res?.dataList && get(res, `dataList.${get(res, `metricnameList.${index}`)}`),
            type: 'line',
          };
        }),
      },
    };
    return temp;
  } else if (type === 'ApiQPSEchart') {
    let temp = {
      title: chartsTitle[type],
      options: {
        color: ['#379E7D', '#66A5FF', '#5776ED', '#F69823', '#FD5C5C', '#50D6BB'],
        legend: {
          type: 'scroll',
          data: res?.metricnameList,
          bottom: '12px',
          left: '20px',
        },
        xAxis: {
          type: 'category',
          axisTick: { show: false },
          axisLine: { show: true, lineStyle: { color: ' #eee', width: 1 } },
          axisLabel: { show: true, textStyle: { color: '#8E97AA' } },
          data: res?.timeList,
        },
        yAxis: {
          axisLabel: {
            //这种做法就是在y轴的数据的值旁边拼接单位，貌似也挺方便的
            formatter: `{value}${get(res, 'unitList.0')}`,
          },
        },
        series: res?.metricnameList.map((item, index) => {
          return {
            name: item,
            data: res?.dataList && get(res, `dataList.${get(res, `metricnameList.${index}`)}`),
            type: 'line',
          };
        }),
      },
    };
    return temp;
  }
}
export default initMatchChart;
