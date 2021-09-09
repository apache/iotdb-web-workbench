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
  <el-container>
    <el-container>
      <el-header class="sqlheader">
        <div class="title flex">
          <div>
            <span>{{ $t('device.dataconnection') }}：{{ routeData.obj.connectId }}</span>
          </div>
          <div class="rightIcon flex">
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
            <eltooltip label="device.delete" v-if="routeData.obj.name !== '新建查询'">
              <i class="el-icon-delete stop" @click="deleteQuery"></i>
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
        <div :style="{ height: divwerHeight + 'px', overflow: 'hidden' }">
          <div class="tabs">
            <el-tabs v-model="activeName" @tab-click="handleClick" class="tabs_nav">
              <el-tab-pane v-for="(item, index) of column.list" :key="index" :name="`t${index}`">
                <template #label>
                  <span>{{ $t('standTable.running') }}{{ index + 1 }}<i class="el-icon-more iconmore green"></i> </span>
                </template>
                <div class="header_messge flex">
                  <div>
                    <!-- <span>
                      <svg class="icon icon-1 icon-color" aria-hidden="true" @click="btnClick1">
                        <use xlink:href="#icon-se-icon-download"></use>
                      </svg>
                      <span class="downloadchart">{{ $t('standTable.download') }}</span>
                    </span>
                    <span class="frist_span">{{ $t('standTable.maxdownload') }}</span> -->
                  </div>
                  <div>
                    <span class="frist_span">{{ $t('standTable.serchtime') }}：{{ time.list[index] }}</span>
                    <span class="frist_span">{{ $t('standTable.queryline') }}：{{ line.list[index] }}</span>
                  </div>
                </div>
                <div class="table_top_border"></div>
                <div class="tab_table" v-if="item">
                  <stand-table ref="standTable" :column="item" :tableData="tableData.list[index]" :lineHeight="5" :lineWidth="13" :maxHeight="divwerHeight" :pagination="pagination"> </stand-table>
                </div>
                <div class="tab_table" v-else>
                  <span>{{ $t('sqlserch.sqlserchText') }}</span>
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
              <formserch placeholder="device.inputfunction" @getFunction="getFunction"></formserch>
            </el-tab-pane>
            <el-tab-pane :label="$t('standTable.data')" name="second">
              <formserch-data @getFunction="getFunction" :id="routeData.obj.connectionid" :treeList="treeList"> </formserch-data>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </el-aside>
  </el-container>
  <div class="footer_button">
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
  </div>
</template>

<script>
import { ElContainer, ElMain, ElAside, ElHeader, ElFooter, ElTabs, ElTabPane, ElDialog, ElButton, ElInput, ElMessage, ElMessageBox } from 'element-plus';
import StandTable from '@/components/StandTable';
import formserch from './components/formserch';
import formserchData from './components/formserchData';
import useElementResize from './hooks/useElementResize.js';
import codemirror from './components/codemirror';
import eltooltip from './components/eltooltip';
import { ref, computed, nextTick, reactive, onActivated } from 'vue';
import { querySql, saveQuery, getSql, queryStop, getGroup, deleteQueryS } from './api/index';
import { useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
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
    const { t } = useI18n();
    let timeNumber = ref(0);
    let dividerRef = ref(null);
    let sqlName = ref(null);
    let codemirror = ref(null);
    let tabelNum = ref(0);
    const standTable = ref(null);
    let activeName = ref(0);
    let activeNameRight = ref('first');
    let runFlag = ref(true);
    let line = reactive({
      list: [],
    });
    let time = reactive({
      list: [],
    });
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
        divwerHeight.value = 400;
        timeNumber.value = Number(new Date());
        useElementResize(dividerRef, divwerHeight);
        querySql(routeData.obj.connectionid, { sqls: codeArr, timestamp: timeNumber.value }).then((res) => {
          activeName.value = 't0';
          column.list = [];
          tableData.list = [];
          tabelNum.value = res.data.length;
          res.data.forEach((item) => {
            time.list.push(item.queryTime);
            line.list.push(item.line);
            if (item.metaDataList) {
              column.list.push({
                list: item.metaDataList.map((eleitem, index) => {
                  return {
                    label: eleitem,
                    prop: `t${index}`,
                  };
                }),
              });
            } else {
              column.list.push(null);
            }
            if (item.valueList) {
              tableData.list.push({
                list: item.valueList.map((eleitem) => {
                  const obj = {};
                  for (let i = 0; i < eleitem.length; i++) {
                    obj[`t${i}`] = eleitem[i];
                  }
                  return obj;
                }),
              });
            } else {
              tableData.list.push(null);
            }
          });
          runFlag.value = true;
        });
        setTimeout(() => {
          runFlag.value = true;
        }, 5000);
      } else {
        ElMessage.error(`${t('sqlserch.sqlrun')}`);
      }
    }
    function centerDialog() {
      centerDialogVisible.value = false;
      ElMessage({
        type: 'info',
        message: t('device.cencel'),
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
      saveQuery(routeData.obj.connectionid, data).then((res) => {
        if (res.code) {
          ElMessage({
            type: 'success',
            message: t('device.savesuccess'),
          });
          centerDialogVisible.value = false;
          props.func.updateTree();
        }
      });
    }
    function getSqlCode() {
      let data = '';
      if (route.params.type !== 'newquery') {
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
    function deleteQuery() {
      ElMessageBox.confirm(`${t('device.deletecontent1')}"${routeData.obj.name}"？${t('device.deletecontent2')}`, `${t('device.tips')}`, {
        confirmButtonText: t('device.ok'),
        cancelButtonText: t('device.cencel'),
        type: 'warning',
      })
        .then(() => {
          deleteQueryS(routeData.obj.connectionid, routeData.obj.queryid).then((res) => {
            if (res.code === '0') {
              ElMessage({
                type: 'success',
                message: `${t('device.deletetitle')}!`,
              });
              props.func.updateTree();
              props.func.removeTab(routeData.obj.id);
            }
          });
        })
        .catch(() => {
          ElMessage({
            type: 'info',
            message: `${t('device.canceldeletion')}!`,
          });
        });
    }
    onActivated(() => {
      routeData.obj = route.params;
      console.log(11111);
      console.log(routeData.obj);
      if (route.params.forceupdate) {
        getSqlCode();
        getGroupList();
        useElementResize(dividerRef, divwerHeight);
      }
    });
    return {
      column,
      line,
      tabelNum,
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
      deleteQuery,
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
  color: rgb(156, 182, 246);
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
    background-color: #efefef;
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
    .table_top_border {
      height: 1px;
      background: #eff0f4;
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
  font-size: 16px;
  width: 100px;
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
.footer_button {
  .el-dialog__footer {
    text-align: right;
  }
}
</style>
