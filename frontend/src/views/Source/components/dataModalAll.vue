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
import { useRouter, useRoute } from 'vue-router';
import img1 from '../../../assets/storage.png';
var MyChart = '';
export default {
  name: 'DataModals',
  //   props: ['func'],
  //   setup(props) {
  setup() {
    const { t } = useI18n();
    const x = ref(0);
    const router = useRouter();
    const route = useRoute();

    const datas = ref({});

    const getModalTreeData = (func) => {
      axios.get(`/servers/${router.currentRoute.value.params.serverid}/dataModel`, {}).then((res) => {
        if (res && res.code == 0) {
          dealData(res.data, (res.data && res.data.children) || []);
          console.log(res.data);
          datas.value = res.data || {};
          func && func();
        }
      });
    };
    /**
     * data父级数据
     * children子集数据
     */
    const dealData = (data, children) => {
      if (children.length >= 10) {
        data.pageNum = 1;
        data.totalPage = Math.ceil(children.length / 10);
        data.childrensTemp = JSON.parse(JSON.stringify(children));
        // let temp = JSON.parse(JSON.stringify(data.childrensTemp));
        data.children = data.children.splice((data.pageNum - 1) * 10, 10);

        data.children.push({ name: '下一页', parentName: data.name, type: 'next', pageNum: 1, totalPage: Math.ceil(data.childrensTemp.length / 10) });
        data.children.unshift({ name: '上一页', parentName: data.name, type: 'pre', pageNum: 1, totalPage: Math.ceil(data.childrensTemp.length / 10) });
      } else {
        data.pageNum = 1;
        data.totalPage = 1;
      }
      for (let i = 0; i < data.children.length; i++) {
        if (data.children[i].children && data.children[i].children.length) {
          dealData(data.children[i], data.children[i].children || []);
        }
      }
    };
    const clickFunction = (params) => {
      let data = params.data || {};
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
      }
    };
    const circulateData = (data, type) => {
      let name = data.parentName;
      let dataAll = datas.value;

      deepSearch(dataAll, name, type);
    };
    const deepSearch = (data, name, type) => {
      console.log(name);
      if (data.name == name) {
        //找到
        let temp = JSON.parse(JSON.stringify(data.childrensTemp));

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
        setOption();
        return;
      } else {
        if (data.children && data.children.length) {
          for (let i = 0; i < data.children.length; i++) {
            deepSearch(data.children[i], name, type);
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
                style: {
                  padding: [0, 0, 0, 6],
                },
              },
              formatter: (params) => {
                return '{img|}' + '{style|' + `${params.data.name}` + '}';
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
      console.log(route);
      getModalTreeData(() => {
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
