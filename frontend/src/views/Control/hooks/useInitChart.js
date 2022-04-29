import { getChartData } from '../api';
import { useI18n } from 'vue-i18n';
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
let chartsLegend = null;
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
  chartsLegend = {
    GCEchart: [t('controlPage.fgcCount'), t('controlPage.ygcCount'), t('controlPage.fgcTime'), t('controlPage.ygcTime')],
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
          data: chartsLegend.GCEchart,
          bottom: '12px',
        },
        xAxis: {
          type: 'category',
          axisTick: { show: false },
          axisLine: { show: true, lineStyle: { color: ' #eee', width: 1 } },
          axisLabel: { show: true, textStyle: { color: '#8E97AA' } },
          data: res ? res.timeList : [],
        },
        series: [
          {
            name: chartsLegend.GCEchart[0],
            data: res?.dataList && res?.dataList[0],
            type: 'bar',
            barGap: '0',
          },
          {
            name: chartsLegend.GCEchart[1],
            data: res?.dataList && res?.dataList[1],
            type: 'bar',
            barGap: '0',
          },
          {
            name: chartsLegend.GCEchart[2],
            data: res?.dataList && res?.dataList[2],
            type: 'line',
          },
          {
            name: chartsLegend.GCEchart[3],
            data: res?.dataList && res?.dataList[3],
            type: 'line',
          },
        ],
      },
    };
    return temp;
  } else if (type === 'JVMClassEchart') {
    let temp = {
      title: chartsTitle[type],
      options: {
        color: ['#5776ED', '#FD5C5C'],
        legend: {
          data: ['load', 'unload'],
          bottom: '12px',
        },
        xAxis: {
          type: 'category',
          axisTick: { show: false },
          axisLine: { show: true, lineStyle: { color: ' #eee', width: 1 } },
          axisLabel: { show: true, textStyle: { color: '#8E97AA' } },
          data: res ? res.timeList : [],
        },
        series: [
          {
            name: 'load',
            data: res?.dataList && res?.dataList[0],
            type: 'line',
          },
          {
            name: 'unload',
            data: res?.dataList && res?.dataList[1],
            type: 'line',
          },
        ],
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
          splitLine: { show: false },
          axisTick: { show: false },
          axisLine: { show: true, lineStyle: { color: ' #eee', width: 1 } },
          axisLabel: { show: true, textStyle: { color: '#8E97AA' } },
        },
        yAxis: {
          inverse: true,
          type: 'category',
          data: res?.timeList,
          axisTick: { show: false },
          axisLine: { show: true, lineStyle: { color: ' #eee', width: 1 } },
          axisLabel: { show: true, textStyle: { color: '#8E97AA' } },
        },
        grid: {
          top: '20px',
          left: '20px',
          right: '20px',
          bottom: '40px',
          containLabel: true,
        },
        series: [
          {
            type: 'bar',
            data: res?.dataList && res?.dataList[0],
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
          splitLine: { show: false },
          axisTick: { show: false },
          axisLine: { show: true, lineStyle: { color: ' #eee', width: 1 } },
          axisLabel: { show: true, textStyle: { color: '#8E97AA' } },
        },
        yAxis: {
          inverse: true,
          type: 'category',
          data: res?.timeList,
          axisTick: { show: false },
          axisLine: { show: true, lineStyle: { color: ' #eee', width: 1 } },
          axisLabel: { show: true, textStyle: { color: '#8E97AA' } },
        },
        grid: {
          top: '20px',
          left: '20px',
          right: '20px',
          bottom: '40px',
          containLabel: true,
        },
        series: [
          {
            type: 'bar',
            data: res?.dataList && res?.dataList[0],
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
          data: ['front', 'end', 'total'],
          bottom: '12px',
        },
        // grid: {
        //   //   top: '40px',
        //   right: '20px',
        //   bottom: '60px',
        // },
        xAxis: {
          type: 'category',
          axisTick: { show: false },
          axisLine: { show: true, lineStyle: { color: ' #eee', width: 1 } },
          axisLabel: { show: true, textStyle: { color: '#8E97AA' } },
          data: res?.timeList,
        },
        // yAxis: {
        //   name: '单位：个',
        // },
        series: [
          {
            name: 'front',
            data: res?.dataList && res?.dataList[0],
            type: 'line',
          },
          {
            name: 'end',
            data: res?.dataList && res?.dataList[1],
            type: 'line',
          },
          {
            name: 'total',
            data: res?.dataList && res?.dataList[2],
            type: 'line',
          },
        ],
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
          data: ['new', 'canrunning', 'running', 'block', 'die', 'dormancy'],
          bottom: '12px',
        },
        xAxis: {
          type: 'category',
          axisTick: { show: false },
          axisLine: { show: true, lineStyle: { color: ' #eee', width: 1 } },
          axisLabel: { show: true, textStyle: { color: '#8E97AA' } },
          data: res?.timeList,
        },
        series: [
          {
            name: 'new',
            data: res?.dataList && res?.dataList[0],
            type: 'line',
          },
          {
            name: 'canrunning',
            data: res?.dataList && res?.dataList[1],
            type: 'line',
          },
          {
            name: 'running',
            data: res?.dataList && res?.dataList[2],
            type: 'line',
          },
          {
            name: 'block',
            data: res?.dataList && res?.dataList[3],
            type: 'line',
          },
          {
            name: 'die',
            data: res?.dataList && res?.dataList[4],
            type: 'line',
          },
          {
            name: 'dormancy',
            data: res?.dataList && res?.dataList[5],
            type: 'line',
          },
        ],
      },
    };
    return temp;
  } else if (type === 'MemoryEchart') {
    let temp = {
      title: chartsTitle[type],
      options: {
        color: ['#379E7D', '#5776ED'],
        legend: {
          data: ['storage', 'max'],
          bottom: '12px',
        },
        xAxis: {
          type: 'category',
          axisTick: { show: false },
          axisLine: { show: true, lineStyle: { color: ' #eee', width: 1 } },
          axisLabel: { show: true, textStyle: { color: '#8E97AA' } },
          data: res?.timeList,
        },
        series: [
          {
            name: 'storage',
            data: res?.dataList && res?.dataList[0],
            type: 'line',
          },
          {
            name: 'max',
            data: res?.dataList && res?.dataList[1],
            type: 'line',
          },
        ],
      },
    };
    return temp;
  } else if (type === 'BufferEchart') {
    let temp = {
      title: chartsTitle[type],
      options: {
        color: ['#379E7D', '#5776ED'],
        legend: {
          data: ['buffer', 'max'],
          bottom: '12px',
        },
        xAxis: {
          type: 'category',
          axisTick: { show: false },
          axisLine: { show: true, lineStyle: { color: ' #eee', width: 1 } },
          axisLabel: { show: true, textStyle: { color: '#8E97AA' } },
          data: res?.timeList,
        },
        series: [
          {
            name: 'buffer',
            data: res?.dataList && res?.dataList[0],
            type: 'line',
          },
          {
            name: 'max',
            data: res?.dataList && res?.dataList[1],
            type: 'line',
          },
        ],
      },
    };
    return temp;
  } else if (type === 'CPUEchart') {
    let data = [];
    res?.timeList.map((item, index) => {
      data.push({ value: res?.dataList && res.dataList[0][index], name: item });
    });
    let temp = {
      title: chartsTitle[type],
      options: {
        color: ['#379E7D', '#5776ED', '#66A5FF', '#F69823', '#FD5C5C'],
        legend: {
          type: 'scroll',
          bottom: '12px',
        },
        tooltip: {},
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
        series: [
          {
            name: '磁盘 IO 吞吐',
            data: res?.dataList[0],
            type: 'line',
          },
        ],
      },
    };
    return temp;
  } else if (type === 'FileCountEchart') {
    let temp = {
      title: chartsTitle[type],
      options: {
        color: ['#5776ED', '#66A5FF', '#F69823', '#379E7D'],
        legend: {
          data: ['wal_size', 'tsfile_seq', 'tsfile_unseq', 'total'],
          bottom: '12px',
        },
        xAxis: {
          type: 'category',
          axisTick: { show: false },
          axisLine: { show: true, lineStyle: { color: ' #eee', width: 1 } },
          axisLabel: { show: true, textStyle: { color: '#8E97AA' } },
          data: res?.timeList,
        },
        series: [
          {
            name: 'wal_size',
            data: res?.dataList && res?.dataList[0],
            type: 'line',
          },
          {
            name: 'tsfile_seq',
            data: res?.dataList && res?.dataList[1],
            type: 'line',
          },
          {
            name: 'tsfile_unseq',
            data: res?.dataList && res?.dataList[2],
            type: 'line',
          },
          {
            name: 'total',
            data: res?.dataList && res?.dataList[3],
            type: 'line',
          },
        ],
      },
    };
    return temp;
  } else if (type === 'FileSizeEchart') {
    let temp = {
      title: chartsTitle[type],
      options: {
        color: ['#5776ED', '#66A5FF', '#F69823', '#379E7D'],
        legend: {
          data: ['wal_size', 'tsfile_seq', 'tsfile_unseq', 'total'],
          bottom: '12px',
        },
        xAxis: {
          type: 'category',
          axisTick: { show: false },
          axisLine: { show: true, lineStyle: { color: ' #eee', width: 1 } },
          axisLabel: { show: true, textStyle: { color: '#8E97AA' } },
          data: res?.timeList,
        },
        series: [
          {
            name: 'wal_size',
            data: res?.dataList && res?.dataList[0],
            type: 'line',
          },
          {
            name: 'tsfile_seq',
            data: res?.dataList && res?.dataList[1],
            type: 'line',
          },
          {
            name: 'tsfile_unseq',
            data: res?.dataList && res?.dataList[2],
            type: 'line',
          },
          {
            name: 'total',
            data: res?.dataList && res?.dataList[3],
            type: 'line',
          },
        ],
      },
    };
    return temp;
  } else if (type === 'WriteEchart') {
    let temp = {
      title: chartsTitle[type],
      options: {
        color: ['#379E7D', '#FD6031', '#5776ED'],
        legend: {
          data: ['success', 'fail', 'total'],
          bottom: '12px',
        },
        xAxis: {
          type: 'category',
          axisTick: { show: false },
          axisLine: { show: true, lineStyle: { color: ' #eee', width: 1 } },
          axisLabel: { show: true, textStyle: { color: '#8E97AA' } },
          data: res?.timeList,
        },
        series: [
          {
            name: 'success',
            data: res?.dataList && res?.dataList[0],
            type: 'line',
          },
          {
            name: 'fail',
            data: res?.dataList && res?.dataList[1],
            type: 'line',
          },
          {
            name: 'total',
            data: res?.dataList && res?.dataList[2],
            type: 'line',
          },
        ],
      },
    };

    return temp;
  } else if (type === 'SearchEchart') {
    let temp = {
      title: chartsTitle[type],
      options: {
        color: ['#379E7D', '#FD6031', '#5776ED'],
        legend: {
          data: ['success', 'fail', 'total'],
          bottom: '12px',
        },
        xAxis: {
          type: 'category',
          axisTick: { show: false },
          axisLine: { show: true, lineStyle: { color: ' #eee', width: 1 } },
          axisLabel: { show: true, textStyle: { color: '#8E97AA' } },
          data: res?.timeList,
        },
        series: [
          {
            name: 'success',
            data: res?.dataList && res?.dataList[0],
            type: 'bar',
            barGap: '0',
          },
          {
            name: 'fail',
            data: res?.dataList && res?.dataList[1],
            type: 'bar',
            barGap: '0',
          },
          {
            name: 'total',
            data: res?.dataList && res?.dataList[2],
            type: 'bar',
          },
        ],
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
          data: ['interface1', 'interface2', 'interface3', 'interface4', 'interface5', 'interface6'],
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
        series: [
          {
            name: 'interface1',
            data: res?.dataList && res?.dataList[0],
            type: 'line',
          },
          {
            name: 'interface2',
            data: res?.dataList && res?.dataList[1],
            type: 'line',
          },
          {
            name: 'interface3',
            data: res?.dataList && res?.dataList[2],
            type: 'line',
          },
          {
            name: 'interface4',
            data: res?.dataList && res?.dataList[3],
            type: 'line',
          },
          {
            name: 'interface5',
            data: res?.dataList && res?.dataList[4],
            type: 'line',
          },
          {
            name: 'interface6',
            data: res?.dataList && res?.dataList[5],
            type: 'line',
          },
        ],
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
          data: ['interface1', 'interface2', 'interface3', 'interface4', 'interface5', 'interface6'],
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
        series: [
          {
            name: 'interface1',
            data: res?.dataList && res?.dataList[0],
            type: 'line',
          },
          {
            name: 'interface2',
            data: res?.dataList && res?.dataList[1],
            type: 'line',
          },
          {
            name: 'interface3',
            data: res?.dataList && res?.dataList[2],
            type: 'line',
          },
          {
            name: 'interface4',
            data: res?.dataList && res?.dataList[3],
            type: 'line',
          },
          {
            name: 'interface5',
            data: res?.dataList && res?.dataList[4],
            type: 'line',
          },
          {
            name: 'interface6',
            data: res?.dataList && res?.dataList[5],
            type: 'line',
          },
        ],
      },
    };
    return temp;
  }
}
export default initMatchChart;
