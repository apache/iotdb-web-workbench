<template>
  <el-container>
    <el-container>
      <el-header class="sqlheader">
        <div class="title flex">
          <div>
            <span>{{ $t('device.dataconnection') }}：{{ routeData.obj.connectId }}</span>
          </div>
          <div class="rightIcon flex" style="width: 60px">
            <eltooltip label="sqlserch.save">
              <span>
                <svg class="icon icon-1" aria-hidden="true" @click="centerDialogVisible = true" v-icon="`#icon-baocun-color`">
                  <use xlink:href="#icon-baocun"></use>
                </svg>
              </span>
            </eltooltip>
            <eltooltip label="sqlserch.run">
              <span>
                <svg class="icon icon-1" aria-hidden="true" @click="querySqlRun" v-icon="`#icon-yunhang-color`">
                  <use xlink:href="#icon-yunhang"></use>
                </svg>
              </span>
            </eltooltip>
            <eltooltip label="sqlserch.stop">
              <i class="el-icon-video-pause stop" @click="stopquery"></i>
            </eltooltip>
          </div>
        </div>
      </el-header>
      <el-main class="backcolor">
        <div :style="{ height: `calc(100vh - ${143 + divwerHeight}px)` }">
          <codemirror ref="codemirror" :codes="code" :sqlheight="sqlheight" @getCode="getCode"></codemirror>
        </div>
      </el-main>
      <el-footer class="footer" :style="{ display: divwerHeight > 30 ? '' : 'none' }">
        <div class="divider" ref="dividerRef"></div>
        <div :style="{ height: divwerHeight + 'px', overflow: 'auto' }">
          <div class="tabs">
            <el-tabs v-model="activeName" @tab-click="handleClick" class="tabs_nav">
              <el-tab-pane name="first1">
                <template #label>
                  <span>{{ $t('standTable.running') }}<i class="el-icon-more iconmore green"></i> </span>
                </template>
                <div class="header_messge flex">
                  <div>
                    <span>
                      <svg class="icon icon-1 icon-color" aria-hidden="true" @click="btnClick1">
                        <use xlink:href="#icon-se-icon-download"></use>
                      </svg>
                      <span class="downloadchart">{{ $t('standTable.download') }}</span>
                    </span>
                    <span class="frist_span">{{ $t('standTable.maxdownload') }}</span>
                  </div>
                  <div>
                    <span class="frist_span">{{ $t('standTable.serchtime') }}：{{ time }}</span>
                    <span class="frist_span">{{ $t('standTable.queryline') }}：{{ line }}</span>
                  </div>
                </div>
                <div class="tab_table">
                  <stand-table ref="standTable" :column="column" :tableData="tableData" :lineHeight="5" :lineWidth="13" :maxHeight="400" :pagination="pagination"> </stand-table>
                </div>
              </el-tab-pane>
              <!-- <el-tab-pane name="second2">
                <template #label>
                  <span>{{ $t('standTable.running') }}2<i class="el-icon-more iconmore red"></i> </span>
                </template>
              </el-tab-pane> -->
            </el-tabs>
          </div>
        </div>
      </el-footer>
    </el-container>
    <el-aside width="300px">
      <div class="el_aside_div">
        <div class="tabgad">
          <el-tabs v-model="activeNameRight" @tab-click="handleClick" class="tabs_nav_aside">
            <el-tab-pane :label="$t('standTable.function')" name="first">
              <formserch :placeholder="'请输入函数名称'" @getFunction="getFunction"></formserch>
            </el-tab-pane>
            <el-tab-pane :label="$t('standTable.data')" name="second">
              <formserch-data :placeholder="'请输入测点名称'" @getFunction="getFunction" :id="routeData.obj.connectionid" :treeList="treeList"> </formserch-data>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </el-aside>
  </el-container>
  <el-dialog :title="$t('standTable.savequery')" v-model="centerDialogVisible" width="30%" center>
    <div class="dilog_div">
      <span>{{ $t('standTable.queryname') }}：</span><el-input style="width: 50%" v-model="sqlName"></el-input>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="centerDialog">{{ $t('device.cencel') }}</el-button>
        <el-button type="primary" @click="centerDialogOk">{{ $t('device.ok') }}</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script>
import { ElContainer, ElMain, ElAside, ElHeader, ElFooter, ElTabs, ElTabPane, ElDialog, ElButton, ElInput, ElMessage } from 'element-plus';
import StandTable from '@/components/StandTable';
import formserch from './components/formserch';
import formserchData from './components/formserchData';
import useElementResize from './hooks/useElementResize.js';
import codemirror from './components/codemirror';
import eltooltip from './components/eltooltip';
import { ref, computed, nextTick, reactive, onActivated } from 'vue';
import { querySql, saveQuery, getSql, queryStop, getGroup } from './api/index';
import { useRoute } from 'vue-router';
export default {
  name: 'Sqlserch',
  props: {
    func: Object,
    data: Object,
  },
  setup(props) {
    let centerDialogVisible = ref(false);
    let divwerHeight = ref(0);
    const route = useRoute();
    let timeNumber = ref(0);
    let dividerRef = ref(null);
    let sqlName = ref(null);
    let codemirror = ref(null);
    const standTable = ref(null);
    let activeName = ref('first1');
    let activeNameRight = ref('first');
    let runFlag = ref(true);
    let line = ref(null);
    let time = ref(null);
    let sqlheight = computed(() => {
      nextTick(() => {
        codemirror.value.codemrriorHeight(divwerHeight.value);
      });
      return divwerHeight.value;
    });
    let code = '';
    let codeArr = [];
    const routeData = reactive({
      obj: route.params,
    });
    const column = reactive({
      list: [],
    });
    const tableData = reactive({
      list: [],
    });
    const treeList = reactive({
      list: [],
    });
    function getFunction(val) {
      codemirror.value.onCmCodeChange(val);
    }
    function getCode(code) {
      codeArr = code.split('\n');
      codeArr = codeArr.filter((item) => {
        return item;
      });
      console.log(codeArr);
    }
    function querySqlRun() {
      if (runFlag.value) {
        runFlag.value = false;
        divwerHeight.value = 300;
        timeNumber.value = Number(new Date());
        useElementResize(dividerRef, divwerHeight);
        querySql(routeData.obj.connectionid, { sqls: codeArr, timestamp: timeNumber.value }).then((res) => {
          line.value = res.data.line;
          time.value = res.data.queryTime;
          column.list = res.data.metaDataList.map((item, index) => {
            return {
              label: item,
              prop: `t${index}`,
            };
          });
          tableData.list = res.data.valueList.map((item) => {
            const obj = {};
            for (let i = 0; i < item.length; i++) {
              obj[`t${i}`] = item[i];
            }
            return obj;
          });
          standTable.value.getColumn(column.list);
          runFlag.value = true;
        });
        setTimeout(() => {
          runFlag.value = true;
        }, 5000);
      } else {
        ElMessage.error('查询正在运行中，请勿重复操作');
      }
    }
    function centerDialog() {
      centerDialogVisible.value = false;
      ElMessage({
        type: 'info',
        message: '已取消保存',
      });
    }
    function centerDialogOk() {
      let codes = '';
      codeArr.forEach((item) => {
        codes += item + '\n';
      });
      const data = {
        connectionId: routeData.obj.connectionid * 1,
        id: routeData.obj.queryid || null,
        queryName: sqlName.value,
        sqls: codes,
      };
      saveQuery(routeData.obj.connectionid, data).then(() => {
        ElMessage({
          type: 'success',
          message: '保存成功!',
        });
        centerDialogVisible.value = false;
        props.func.updateTree();
      });
    }
    function getSqlCode() {
      let data = '';
      if (route.params.name !== '新建查询') {
        getSql(routeData.obj.connectionid, routeData.obj.queryid).then((res) => {
          sqlName.value = res.data.queryName;
          data = res.data.sqls;
          codeArr = res.data.sqls.split('\n');
          codemirror.value.setCode(data);
          setTimeout(() => {
            codemirror.value.setEvent(data);
          }, 1000);
        });
      } else {
        setTimeout(() => {
          codemirror.value.setEvent(data);
        }, 1000);
      }
    }
    function getGroupList() {
      getGroup(routeData.obj.connectionid).then((res) => {
        treeList.list = res.data.map((item) => {
          return {
            label: item.groupName,
            value: item.groupName,
            decr: '',
            type: '',
          };
        });
      });
    }
    function stopquery() {
      queryStop(routeData.obj.connectionid, { timestamp: timeNumber.value }).then((res) => {
        console.log(res);
      });
    }
    onActivated(() => {
      routeData.obj = route.params;
      if (route.params.forceupdate) {
        getSqlCode();
        getGroupList();
        useElementResize(dividerRef, divwerHeight);
      }
    });
    return {
      column,
      line,
      activeNameRight,
      treeList,
      centerDialogVisible,
      time,
      standTable,
      tableData,
      stopquery,
      code,
      sqlName,
      centerDialog,
      getCode,
      centerDialogOk,
      codemirror,
      divwerHeight,
      dividerRef,
      sqlheight,
      activeName,
      getFunction,
      querySqlRun,
      routeData,
    };
  },
  components: {
    eltooltip,
    codemirror,
    ElContainer,
    ElMain,
    ElAside,
    ElHeader,
    ElFooter,
    ElTabs,
    ElTabPane,
    StandTable,
    formserch,
    ElDialog,
    ElButton,
    ElInput,
    formserchData,
  },
};
</script>

<style lang="scss" scoped>
.stop:hover {
  color: rgb(84, 95, 255);
}
.dilog_div {
  display: flex;
  justify-content: center;
  align-items: center;
}
.el_aside_div {
  width: 298px;
  height: calc(100vh - 106px);
  // overflow: hidden;
  position: absolute;
  border-left: 1px solid #ebeef5;
  .tabgad {
    background: #efefef;
  }
}
.downloadchart {
  font-size: 11px;
  color: $theme-color;
  margin-left: 5px;
  cursor: pointer;
}
.sqlheader {
  &.el-header {
    height: 35px !important;
    padding: 0;
  }
}
.footer {
  &.el-footer {
    padding: 0;
    height: 0px !important;
  }
  .divider {
    // width: 1px;
    height: 1px;
    background-color: #f0f0f0;
    cursor: n-resize;
    &:hover {
      background-color: $theme-color !important;
      height: 2px;
    }
  }
  .tabs {
    height: 30px;
    background: #efefef;
    box-shadow: 0px 0px 2px #d2d2d2;
    .frist_span {
      color: #cccccc;
      font-size: 11px;
      margin-left: 20px;
    }
    .header_messge {
      padding: 10px 23px;
    }
    .tab_table {
      padding-top: 5px;
    }
  }
}
.title {
  padding: 10px 25px;
  color: black;
  font-size: 14px;
  border-bottom: 1px solid #ebeef5;
}
.rightIcon {
  width: 40px;
}
.flex {
  display: flex;
  justify-content: space-between;
}
.container {
  position: relative;
}
</style>
<style lang="scss">
.backcolor.el-main {
  padding: 0;
  height: 100%;
}
.tabs_nav .el-tabs__item {
  padding: 5px 0px !important;
  width: 100px;
  font-size: 11px !important;
}
.tabs_nav_aside .el-tabs__item {
  padding: 7px 0px !important;
  width: 60px;
  font-size: 11px !important;
}
.tabs_nav .iconmore {
  font-size: 25px;
  width: 10px;
  overflow: hidden;
  line-height: 15px;
  position: absolute;
  top: 0px;
  &.green {
    color: #00c300;
  }
  &.red {
    color: rgb(255, 33, 33);
  }
}
.icon.icon-color {
  color: $theme-color;
  vertical-align: -0.2em !important;
}
</style>
