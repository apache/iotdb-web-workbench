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
  <div id="mains" class="mains-contain"></div>
</template>

<script>
// @ is an alias to /src
import { onMounted, ref, onActivated, onDeactivated, reactive } from 'vue';
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
    const pagination = reactive({
      pageSize: 10,
      pageNum: 1,
    });
    const datas = ref({});

    const getModalTreeData = (func) => {
      axios
        .get(`/servers/${router.currentRoute.value.params.serverid}/dataModel/detail`, {
          params: pagination,
        })
        .then((res) => {
          if (res && res.code == 0) {
            res.data.pageSize = pagination.pageSize;
            res.data.pageNum = pagination.pageNum;
            dealData([res.data] || {}, 0);
            res.data.collapsed = false;
            datas.value = res.data || {};
            func && func();
          }
        });
    };
    /**
     * data current level data
     * index  level num
     */
    const dealData = (data) => {
      for (let i = 0; i < data.length; i++) {
        data[i].collapsed = true;
        const isNext = data[i].total > data[i].pageNum * data[i].pageSize;
        const isPrev = data[i].pageNum !== 1;
        if (data[i].children && data[i].children.length) {
          isNext && data[i].children.push({ name: t('sourcePage.nextPage'), path: data[i].path, type: 'next', pageNum: data[i].pageNum, pageSize: data[i].pageSize });
          isPrev && data[i].children.unshift({ name: t('sourcePage.prePage'), path: data[i].path, type: 'pre', pageNum: data[i].pageNum, pageSize: data[i].pageSize });
        }
      }
    };
    let loading = false;
    const clickFunction = (params) => {
      if (loading) return;
      loading = true;
      let data = params.data || {};
      if (data.type === 'next') {
        data.pageNum += 1;
      } else if (data.type === 'pre' && data.pageNum >= 1) {
        data.pageNum -= 1;
      } else {
        data.pageNum = 1;
        data.pageSize = 10;
      }
      axios
        .get(`/servers/${router.currentRoute.value.params.serverid}/dataModel/detail`, {
          params: { pageNum: data.pageNum, pageSize: data.pageSize, path: data.path },
        })
        .then((res) => {
          if (res && res.code == 0) {
            dealData([res.data] || [], 0);
            params.data.children = res.data.children || [];
            if (params.data.children && params.data.children.length) {
              circulateDataSelf(data, res.data || {}); //第二个参数为动态请求回来的数据
            }
          }
        })
        .finally(() => {
          loading = false;
        });
    };
    const circulateDataSelf = (data, levelData) => {
      let name = data.path;
      // let dataAll = datas.value;
      let index = 1;
      deepSearchSelf(datas.value, name, index, levelData);
    };
    const deepSearchSelf = (data, name, index, levelData) => {
      if (data.path == name) {
        //do it
        data.collapsed = levelData.collapsed;
        data.collapsed = !data.collapsed;
        data.children = levelData.children || [];
        data.childrensTemp = levelData.childrensTemp || [];
        data.pageNum = levelData.pageNum;
        data.totalPage = levelData.totalPage;
        initialTreeDepth.value = index;
        MyChart.clear();
        setOption();
        return;
      } else {
        index++;
        if (data.children && data.children.length) {
          for (let i = 0; i < data.children.length; i++) {
            deepSearchSelf(data.children[i], name, index, levelData);
          }
        }
      }
    };
    const initialTreeDepth = ref(1);
    const initCharts = () => {
      //  init charts
      MyChart = echarts.init(document.getElementById('mains'));
      MyChart.on('click', function (params) {
        clickFunction(params);
      });
      // set option
      setOption();
    };
    const setOption = () => {
      let option = {
        tooltip: {
          trigger: 'item',
          triggerOn: 'mousemove',
          formatter: (params) => {
            let data = params.data;
            if (data.name == 'root') {
              //root
              return t('sourcePage.storageNum') + ':' + data.groupCount || 0;
            } else if (data.isGroup) {
              return t('sourcePage.entityNum') + ':' + data.deviceCount || 0;
            } else if (data.isDevice) {
              return t('sourcePage.physicalNum') + ':' + data.measurementCount || 0;
            } else if (data.isMeasurement) {
              return (
                t('device.datatype') + ':' + data.dataInfo.dataType + '</Br>' + t('sourcePage.dataNum') + ':' + data.dataInfo.dataCount + '</Br>' + t('device.newValue') + ':' + data.dataInfo.newValue
              );
            } else {
              return data.name;
            }
          },
        },
        series: [
          {
            type: 'tree',
            edgeShape: 'polyline',
            data: [datas.value],
            left: '4%',
            // right: '2%',
            top: '8%',
            bottom: '8%',
            roam: true,
            scaleLimit: {
              min: 0.5,
              max: 3,
            },
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
                color: '#fff',
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
                if (params.data.isGroup && params.data.isDevice) {
                  return '{img|}{img2|}' + '{style|' + `${params.data.name}` + '}';
                } else if (params.data.isGroup) {
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

      // show echart
      MyChart.setOption(option);
    };

    onMounted(() => {
      // getModalTreeData(() => {
      //   initCharts();
      // });
    });
    onActivated(() => {
      initialTreeDepth.value = 1;
      getModalTreeData(() => {
        initCharts();
      });
    });
    onDeactivated(() => {
      MyChart && MyChart.dispose();
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
