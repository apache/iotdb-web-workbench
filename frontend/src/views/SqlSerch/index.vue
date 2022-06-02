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
                <svg class="icon icon-1" aria-hidden="true" @click="centerDialogVisible = true">
                  <use xlink:href="#icon-se-icon-save"></use>
                </svg>
              </span>
            </eltooltip>
            <eltooltip label="sqlserch.run">
              <span>
                <svg class="icon icon-1 stop" aria-hidden="true" @click="querySqlRun">
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
                <div class="table_top_border"></div>
                <div class="tab_table" v-if="item && display">
                  <stand-table
                    :key="key"
                    ref="standTable"
                    :column="item"
                    :tableData="tableDataPagination[index]"
                    :lineHeight="5"
                    :celineHeight="5"
                    :maxHeight="divwerHeight - 78"
                    :pagination="pagination"
                    :total="total[index]"
                    :getList="getList(index)"
                    backColor="#E7EAF2"
                  >
                  </stand-table>
                </div>
                <div class="tab_table" v-else>
                  <span v-if="display">{{ $t('sqlserch.sqlserchText') }}</span>
                </div>
                <div class="header_messge flex">
                  <div>
                    <span @click="exportSql(index)">
                      <svg class="icon icon-1 icon-color" aria-hidden="true">
                        <use xlink:href="#icon-se-icon-download"></use>
                      </svg>
                      <span class="downloadchart">{{ $t('standTable.download') }}</span>
                    </span>
                    <span class="frist_span">{{ $t('standTable.maxdownload') }}</span>
                  </div>
                  <div>
                    <span class="frist_span">{{ $t('standTable.serchtime') }}：{{ time.list[index] }}</span>
                    <span class="frist_span">{{ $t('standTable.queryline') }}：{{ line.list[index] }}</span>
                  </div>
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>
        </div>
      </el-footer>
    </el-container>
    <el-aside width="240px">
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
    <el-dialog :title="$t('standTable.savequery')" v-model="centerDialogVisible" width="400px">
      <div class="dilog_div">
        <span>{{ $t('standTable.queryname') }}：</span><el-input style="width: 70%" v-model="sqlName"></el-input>
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
import { querySql, saveQuery, getSql, queryStop, getGroup, deleteQueryS, exportDataSql } from './api/index';
import { useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { handleExport } from '@/util/export';
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
    let display = ref(false);
    let codemirror = ref(null);
    let tabelNum = ref(0);
    const standTable = ref(null);
    let key = ref('1');
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
    const total = computed(() => tableData.list.map((item) => item?.list?.length));
    const pagination = reactive({
      pageSize: 10,
      pageNum: 1,
    });
    const pageNums = reactive([]);
    function getList(index) {
      return (value) => {
        pageNums[index] = value;
      };
    }
    const tableDataPagination = computed(() =>
      tableData.list.map((item, index) => {
        nextTick(() => {
          key.value = Math.random() + Date.now() + '';
        });
        return {
          ...item,
          list: item?.list?.slice(((pageNums[index] || 1) - 1) * pagination.pageSize, (pageNums[index] || 1) * pagination.pageSize),
        };
      })
    );

    function getFunction(val) {
      codemirror.value.onCmCodeChange(val);
    }
    function getCode(code) {
      codeArr = code.split('\n');
      codeArr = codeArr.filter((item) => {
        return item;
      });
    }
    function querySqlRun() {
      if (runFlag.value) {
        let cd = codemirror.value.getSelecValue();
        if (cd) {
          getCode(cd);
        }
        display.value = false;
        runFlag.value = false;
        divwerHeight.value = 400;
        timeNumber.value = Number(new Date());
        useElementResize(dividerRef, divwerHeight);
        querySql(routeData.obj.connectionid, { sqls: codeArr, timestamp: timeNumber.value })
          .then((res) => {
            activeName.value = 't0';
            column.list = [];
            tableData.list = [];
            let lengthArry = [];
            time.list = [];
            line.list = [];
            tabelNum.value = res.data.length;
            res.data.forEach((item) => {
              let length = [];
              time.list.push(item.queryTime);
              line.list.push(item.line);
              if (item.metaDataList) {
                column.list.push({
                  list: item.metaDataList.map((eleitem, index) => {
                    return {
                      label: eleitem,
                      prop: `t${index}`,
                      width: 'auto',
                      fixed: index === 0 ? 'left' : index === item.metaDataList.length - 1 ? 'right' : false,
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
                      if (eleitem[i].length > length[i] || !length[i]) {
                        length[i] = eleitem[i].length;
                      }
                      obj[`t${i}`] = eleitem[i];
                    }
                    return obj;
                  }),
                });
              } else {
                tableData.list.push(null);
              }
              lengthArry.push(length);
            });
            // lengthArry.forEach((element, i) => {
            //   element.forEach((item, index) => {
            //     if (index === element.length - 1) {
            //       return false;
            //     }
            //     column.list[i].list[index].width = column.list[i].list[index].label.length < item ? item * 12 : column.list[i].list[index].label.length * 12;
            //   });
            // });
            display.value = true;
            runFlag.value = true;
            console.log(line.list);
          })
          .finally(() => {
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
          sqlName.value = null;
          centerDialogVisible.value = false;
          props.func.updateTree();
          let locationId = '';
          if (routeData.obj.id.endsWith('newquery')) {
            locationId = routeData.obj.id.substring(0, routeData.obj.id.length - 9) + res.data + 'query';
          } else {
            locationId = route.params.connectionid + 'connection:querylist' + res.data.id + 'query';
          }
          props.func.updateTree();
          props.func.addTab(locationId, {}, true);
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
      queryStop(routeData.obj.connectionid, { timestamp: timeNumber.value }).then(() => {});
    }
    function exportSql(i) {
      exportDataSql(routeData.obj.connectionid, { sql: codeArr[i] }).then((res) => {
        if (res) {
          ElMessage({
            type: 'success',
            message: `导出成功!`,
          });
          handleExport(res, '查询导出.CSV');
        } else {
          ElMessage({
            type: 'error',
            message: res.message,
          });
        }
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
      if (route.params.forceupdate) {
        getSqlCode();
        getGroupList();
        useElementResize(dividerRef, divwerHeight);
      }
    });
    return {
      exportSql,
      column,
      line,
      display,
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
      total,
      pagination,
      getList,
      pageNums,
      tableDataPagination,
      key,
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
  align-items: center;
}
.el_aside_div {
  width: 240px;
  height: calc(100vh - 106px);
  box-sizing: border-box;
  position: absolute;
  border-left: 1px solid #ebeef5;
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
    height: 0 !important;
  }
  .divider {
    // width: 1px;
    height: 3px;
    background-color: #f9fafc;
    cursor: n-resize;
    &:hover {
      background-color: $theme-color !important;
      height: 2px;
    }
  }
  :deep(.tabs) {
    height: 40px;
    background: #fff;
    box-shadow: 0 0 2px #d2d2d2;
    .el-tabs {
      .el-tabs__header {
        margin-bottom: 0;
      }
    }
    .frist_span {
      color: #ccc;
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
.tabgad {
  .el-tabs__nav-wrap::after {
    height: 1px;
    bottom: 1px;
  }
}
.backcolor.el-main {
  padding: 0;
  height: 100%;
}
.tabs_nav .el-tabs__item {
  padding: 5px 0 !important;
  width: 100px;
  font-size: 11px !important;
}
.tabs_nav_aside .el-tabs__item {
  padding: 7px 0 !important;
  width: 60px;
  font-size: 11px !important;
}
.tabs_nav .iconmore {
  font-size: 25px;
  width: 10px;
  overflow: hidden;
  line-height: 15px;
  position: absolute;
  top: 0;
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
