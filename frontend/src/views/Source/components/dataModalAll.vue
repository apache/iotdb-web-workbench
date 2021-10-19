<template>
  <div id="mains" class="mains-contain"></div>
</template>

<script>
// @ is an alias to /src
import { onMounted, ref, onActivated } from 'vue';
import * as echarts from 'echarts';
// import { ElButton } from 'element-plus';
import { useI18n } from 'vue-i18n';
import axios from '@/util/axios.js';
// import { useStore } from 'vuex';
//, useRoute
import { useRouter } from 'vue-router';
import img1 from '../../../assets/storage.png';
import img2 from '../../../assets/data.png';
import img3 from '../../../assets/device.png';
var MyChart = '';
export default {
  name: 'DataModals',
  //   props: ['func'],
  //   setup(props) {
  setup() {
    const { t } = useI18n();
    const x = ref(0);
    const router = useRouter();
    // const route = useRoute();

    const datas = ref({});

    const getModalTreeData = (func) => {
      axios.get(`/servers/${router.currentRoute.value.params.serverid}/dataModel`, {}).then((res) => {
        if (res && res.code == 0) {
          // dealData(res.data, (res.data && res.data.children) || [], 0);
          dealData([res.data] || {}, 0);
          datas.value = res.data || {};
          func && func();
        }
      });
    };
    /**
     * data父级数据
     * children子集数据
     */
    const dealData = (data, index) => {
      for (let i = 0; i < data.length; i++) {
        if (index < initialTreeDepth.value) {
          data[i].collapsed = false;
        } else {
          data[i].collapsed = true;
        }
        if (data[i].children && data[i].children.length) {
          if (data[i].children.length > 10) {
            data[i].pageNum = 1;
            data[i].totalPage = Math.ceil(data[i].children.length / 10);
            data[i].childrensTemp = JSON.parse(JSON.stringify(data[i].children));
            // let temp = JSON.parse(JSON.stringify(data[i].childrensTemp));
            data[i].children = data[i].children.splice((data[i].pageNum - 1) * 10, 10);

            data[i].children.push({ name: t('sourcePage.nextPage'), parentName: data[i].path, type: 'next', pageNum: 1, totalPage: Math.ceil(data[i].childrensTemp.length / 10) });
            data[i].children.unshift({ name: t('sourcePage.prePage'), parentName: data[i].path, type: 'pre', pageNum: 1, totalPage: Math.ceil(data[i].childrensTemp.length / 10) });
          } else {
            data[i].pageNum = 1;
            data[i].totalPage = 1;
          }

          index += 1;
          dealData(data[i].children, index);
        }
      }
    };
    // const dealData = (data, children, index) => {
    //   // for (let i = 0; i < children.length; i++) {
    //   //   if (index <= initialTreeDepth.value) {
    //   //     children[i].collapsed = false;
    //   //     console.log(111);
    //   //     console.log(children[i].name);
    //   //   } else {
    //   //     children[i].collapsed = true;
    //   //     console.log(children[i].name);

    //   //     console.log(222);
    //   //   }
    //   // }
    //   if (children.length > 10) {
    //     data.pageNum = 1;
    //     data.totalPage = Math.ceil(children.length / 10);
    //     data.childrensTemp = JSON.parse(JSON.stringify(children));
    //     // let temp = JSON.parse(JSON.stringify(data.childrensTemp));
    //     data.children = data.children.splice((data.pageNum - 1) * 10, 10);

    //     data.children.push({ name: t('sourcePage.nextPage'), parentName: data.name, type: 'next', pageNum: 1, totalPage: Math.ceil(data.childrensTemp.length / 10) });
    //     data.children.unshift({ name: t('sourcePage.prePage'), parentName: data.name, type: 'pre', pageNum: 1, totalPage: Math.ceil(data.childrensTemp.length / 10) });
    //   } else {
    //     data.pageNum = 1;
    //     data.totalPage = 1;
    //   }

    //   for (let i = 0; i < data.children.length; i++) {
    //     if (children[i].children && children[i].children.length) {
    //       console.log(children[i], 1111);
    //       console.log(children[i].children, 1111);
    //       index += 1;
    //       dealData(children[i], children[i].children || [], index);
    //     }
    //   }
    // };

    const clickFunction = (params) => {
      console.log(params);
      let data = params.data || {};
      console.log(datas.value);
      if (data.type) {
        //do next if has 'type' key;
        if (data.type == 'pre') {
          if (data.pageNum == 1) {
            return;
          } else {
            circulateData(data, 'pre');
          }
        } else if (data.type == 'next') {
          if (data.pageNum == data.totalPage) {
            return;
          } else {
            circulateData(data, 'next');
          }
        }
      } else {
        if (params.data.children && params.data.children.length) {
          circulateDataSelf(data);
        }
      }
    };
    const circulateDataSelf = (data) => {
      let name = data.path;
      // let dataAll = datas.value;
      let index = 1;
      deepSearchSelf(datas.value, name, index);
    };
    const deepSearchSelf = (data, name, index) => {
      if (data.path == name) {
        debugger;
        //找到
        data.collapsed = !data.collapsed;
        MyChart.clear();
        setOption();
      } else {
        index++;
        if (data.children && data.children.length) {
          for (let i = 0; i < data.children.length; i++) {
            deepSearchSelf(data.children[i], name, index);
          }
        }
      }
    };
    const circulateData = (data, type) => {
      let name = data.parentName;
      let dataAll = datas.value;
      let index = 1;
      deepSearch(dataAll, name, type, index);
    };
    const initialTreeDepth = ref(1);
    const deepSearch = (data, name, type, index) => {
      if (data.path == name) {
        //找到
        // data.collapsed = false;
        let temp = JSON.parse(JSON.stringify(data.childrensTemp));
        console.log(index, 11111);
        initialTreeDepth.value = index;
        if (type == 'next') {
          data.pageNum += 1;
          temp = temp.splice((data.pageNum - 1) * 10, 10);
          data.children[0].pageNum += 1;
          data.children[data.children.length - 1].pageNum += 1;
        } else {
          data.pageNum -= 1;
          temp = temp.splice((data.pageNum - 1) * 10, 10);
          data.children[0].pageNum -= 1;
          data.children[data.children.length - 1].pageNum -= 1;
        }
        temp.unshift(data.children[0]);
        temp.push(data.children[data.children.length - 1]);
        data.children = temp;
        for (let i = 0; i < temp.length; i++) {
          if (temp[i].children && temp[i].children.length) {
            dealData(temp[i], temp[i].children || []);
          }
        }
        // let dataTemp = datas.value;
        console.log(datas.value);
        // datas.value = [];
        // datas.value = dataTemp;
        MyChart.clear();
        setOption();
        return;
      } else {
        index++;
        if (data.children && data.children.length) {
          for (let i = 0; i < data.children.length; i++) {
            deepSearch(data.children[i], name, type, index);
          }
        }
      }
    };
    const initCharts = () => {
      // 基于准备好的dom，初始化echarts实例
      MyChart = echarts.init(document.getElementById('mains'));
      MyChart.on('click', function (params) {
        clickFunction(params);
      });
      // 指定图表的配置项和数据
      setOption();
    };
    const setOption = () => {
      let option = {
        tooltip: {
          trigger: 'item',
          triggerOn: 'mousemove',
        },
        series: [
          {
            type: 'tree',
            edgeShape: 'polyline',
            data: [datas.value],
            left: '2%',
            // right: '2%',
            top: '8%',
            bottom: '8%',
            roam: true,
            symbol: 'emptyCircle',
            symbolSize: 0,
            // orient: 'vertical',
            // rootLocation: { x: 'center', y: '6%' },
            nodePadding: 80,
            // layerPadding: 33,
            expandAndCollapse: true,
            initialTreeDepth: initialTreeDepth.value,
            itemStyle: {
              normal: {
                color: '#fff', //圆圈颜色
                borderWidth: 0,
                borderColor: '#000',
                lineStyle: {
                  color: '#DBDFEAFF',
                  width: 1,
                  type: 'broken', // 'curve'|'broken'|'solid'|'dotted'|'dashed'
                },
              },
            },
            label: {
              position: 'bottom',
              rotate: 0,
              verticalAlign: 'middle',
              align: 'center',
              fontSize: 9,
              borderWidth: 1,
              borderColor: '#EAEDF2FF',
              padding: [4, 10, 4, 10],

              textStyle: {
                color: '#000',
                backgroundColor: '#fff',
                fontSize: 12,
                fontWeight: 500,
                baseline: 'middle',
              },
              rich: {
                img: {
                  backgroundColor: {
                    image: img1,
                  },
                },
                img1: {
                  backgroundColor: {
                    image: img2,
                  },
                },
                img2: {
                  backgroundColor: {
                    image: img3,
                  },
                },
                style: {
                  padding: [0, 0, 0, 6],
                },
              },
              formatter: (params) => {
                if (params.data.isGroup) {
                  return '{img|}' + '{style|' + `${params.data.name}` + '}';
                } else if (params.data.isDevice) {
                  return '{img2|}' + '{style|' + `${params.data.name}` + '}';
                } else if (params.data.isMeasurement) {
                  return '{img1|}' + '{style|' + `${params.data.name}` + '}';
                }
              },
            },

            leaves: {
              label: {
                position: 'bottom',
                rotate: 0,
                verticalAlign: 'middle',
                align: 'center',
              },
            },

            animationDurationUpdate: 750,
          },
        ],
      };

      // 使用刚指定的配置项和数据显示图表。
      MyChart.setOption(option);
    };
    // const resize = () => {
    //   let MyChart = echarts.init(document.getElementById('main'));
    // let eleArr = Array.from(new Set(MyChart._chartViews[0]._data._graphicEls));
    // let dep = MyChart._chartViews[0]._data.tree.root.height;
    // let layer_height = 100;
    // let currentHeight = layer_height * (dep + 1) || layer_height;
    // let newHeight = Math.max(currentHeight, layer_height);

    // }
    onMounted(() => {
      getModalTreeData(() => {
        initCharts();
      });
    });
    onActivated(() => {
      getModalTreeData(() => {
        console.log(datas.value);
        initCharts();
      });
    });
    return {
      t,
      x,
      datas,
      getModalTreeData,
      initCharts,
    };
  },
  components: {
    // ElButton,
  },
};
</script>

<style scoped lang="scss">
.mains-contain {
  width: 100%;
  height: 100%;
  overflow: auto;
}
</style>
