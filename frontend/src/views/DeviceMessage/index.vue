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
  <div class="content_message">
    <div class="content_padding">
      <div class="headerbox">
        <div class="flexBox" style="padding: 15px 0 10px 0">
          <div class="headerSpan">{{ routeData.obj.name }}</div>
          <div class="flexBox headerIcon">
            <el-button @click="editDevce">
              <svg class="icon edit" aria-hidden="true">
                <use xlink:href="#icon-se-icon-f-edit"></use></svg
              >&nbsp;编辑</el-button
            >
            <el-button @click="deleteData">
              <svg class="icon delete" aria-hidden="true">
                <use xlink:href="#icon-se-icon-delete"></use></svg
              >&nbsp;删除</el-button
            >
          </div>
        </div>
        <div class="messageBox">
          <span>{{ $t('device.dataconnection') }}：</span>
          <span>{{ routeData.obj.parentids }}</span>
          <!-- <span class="spanmargin">{{ $t('device.group') }}：</span>
        <span>{{ routeData.obj.storagegroupid }}</span> -->
          <span class="spanmargin">{{ $t('device.description') }}：</span>
          <span>{{ deviceObj.deviceData.description }}</span>
          <span class="spanmargin">
            <svg class="icon adminIcon" aria-hidden="true">
              <use xlink:href="#icon-yh"></use>
            </svg>
            {{ $t('device.creator') }}：
          </span>
          <span>{{ deviceObj.deviceData.creator }}</span>
          <span class="spanmargin">{{ $t('device.createTime') }}：</span>
          <span>{{ deviceObj.deviceData.time }}</span>
        </div>
      </div>
    </div>
    <el-tabs v-model="activeName" @tab-click="handleClick" class="tab_class">
      <el-tab-pane label="实体结构" name="first">
        <div class="frist_div">
          <div style="padding: 0px 0px 10px 0px" class="flexBox">
            <form-table :form="form" @serchFormData="serchFormData"></form-table>
            <!-- <el-button type="primary" class="newButton" @click="creatDevice">{{ $t('storagePage.newDevice') }}</el-button> -->
          </div>
          <stand-table
            :column="column"
            :tableData="tableData"
            :getList="getListData"
            :total="totalCount"
            :lineHeight="10"
            :lineWidth="21"
            :celineWidth="23"
            :maxHeight="450"
            :pagination="pagination"
            @getPagintions="getPagintions"
          >
            <template #default="{ scope }">
              <div @click="searchRow(scope.row)" v-if="scope.row.newValue * 1">
                <action :echartsData="routeData.obj" :row="scope.row" :scope="scope"></action>
              </div>
              <div v-else class="actionSpan">
                <span>——</span>
              </div>
            </template>
          </stand-table>
        </div>
      </el-tab-pane>
      <el-tab-pane label="数据预览" name="second">
        <div class="frist_div">
          <div style="padding: 0px 0px 10px 0px" class="flexBox">
            <div class="button_div">
              <el-dropdown trigger="click">
                <el-button type="primary">新增数据</el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="dialogFormVisible.flag = true">随机生成</el-dropdown-item>
                    <el-dropdown-item @click="dialogVisible1.flag = true">批量导入</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
              <el-button @click="exportData">导出数据</el-button>
            </div>
            <div>
              <form-table :form="form1" @serchFormData="serchFormData1"></form-table>
            </div>
          </div>
          <stand-table
            v-if="tableflag.flag"
            :column="column1"
            :tableData="tableData1"
            :getList="getPview"
            :total="totalCount1"
            :lineHeight="10"
            :celineHeight="10"
            :maxHeight="450"
            :pagination="pagination1"
            :selectData="selectData"
            :deleteArry="deleteArry"
            @getPagintions="getPagintions"
          >
            <template #default="{ scope }">
              <span class="table2 edit" @click="editRow(scope.row)">编辑</span>
              <span class="table2 delete" @click="deleteRow([scope.row], scope.$index)">{{ $t('device.delete') }}</span>
            </template>
          </stand-table>
        </div>
      </el-tab-pane>
    </el-tabs>
    <div class="drawer" v-if="drawerFlag" :style="{ height: drawer + 'px', width: widths - dividerWidth + 'px' }">
      <div class="drawertitle">
        <div>{{ routeData.obj.timeseries }}{{ $t('device.datatrend') }}</div>
        <div>
          <i class="el-icon-close" style="cursor: pointer" @click="closeDrawer"></i>
        </div>
      </div>
      <div class="formtable">
        <form-table :form="formdate"></form-table>
      </div>
      <div>
        <echarts ref="drawerRef" :echartsData="routeData.obj" :getDate="getDate"></echarts>
      </div>
    </div>
    <el-dialog title="随机数据" v-model="dialogFormVisible.flag" width="500px">
      <div>
        <span style="color: red">提示：同时间戳的数据将会被覆盖</span>
        <div style="padding-top: 20px">
          <form-table ref="formTable" :form="newData"></form-table>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogFormVisible.flag = false">取 消</el-button>
          <el-button type="primary" @click="randomImData">确 定</el-button>
        </span>
      </template>
    </el-dialog>
    <el-dialog title="导入" v-model="dialogVisible1.flag" width="500px" @close="closeEd" class="export_form">
      <div class="div_children">
        <div>
          <input ref="filesd" type="file" multiple="multiple" style="display: none" @change="getfile" />
          <el-form ref="ruleForm" label-width="100%">
            <el-form-item label="模板下载：">
              <el-button size="small" @click="downloadEx"
                >下载模板
                <i class="iconfont se-icon-download"></i>
              </el-button>
            </el-form-item>
            <el-form-item label="选择文件：" prop="categoryName">
              <el-button type="primary" size="small" @click="selectFile"
                >选择文件
                <i class="iconfont se-icon-upload2"></i>
              </el-button>
              <span class="export_notice">仅支持csv格式文件</span>
            </el-form-item>
          </el-form>
        </div>
      </div>
      <div v-if="false" class="div_children">
        <div>
          <el-progress :text-inside="true" :stroke-width="12" :percentage="50" color="#16C493">
            <span></span>
          </el-progress>
          <div class="info_div">上传中：45%</div>
        </div>
      </div>
      <div v-if="false" class="div_children">
        <div>
          <stand-table :column="column2" :tableData="tableData2" :lineHeight="10" :celineHeight="10">
            <template #default="{ scope }">
              <span class="table2 edit" @click="editRow(scope.row)">下载</span>
            </template>
          </stand-table>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible1.flag = false">取 消</el-button>
          <el-button type="primary" @click="dialogVisible1.flag = false">确 定</el-button>
        </span>
      </template>
    </el-dialog>
    <el-dialog title="编辑数据" v-model="dialogFormVisible.editflag" width="500px">
      <div>
        <form-table v-if="dialogFormVisible.editflag" ref="formTable" :form="editFormData"></form-table>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogFormVisible.editflag = false">取 消</el-button>
          <el-button type="primary" @click="saveData">确 定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ElMessageBox, ElMessage, ElButton, ElTabs, ElTabPane, ElDropdown, ElDropdownMenu, ElDropdownItem, ElDialog, ElForm, ElFormItem, ElProgress } from 'element-plus';
import StandTable from '@/components/StandTable';
import FormTable from '@/components/FormTable';
import { reactive, ref, onActivated } from 'vue';
import { getList, getDeviceDate, deleteDevice, getDataDeviceList, randomImport, editData, deleteDeviceData, exportDataCSV, downloadFile } from './api';
import Echarts from '@/components/Echarts';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import action from './components/action.vue';
import { handleExport } from '@/util/export';
export default {
  name: 'DeviceMessage',
  props: {
    func: Object,
    data: Object,
    dividerWidth: Object,
  },
  setup(props) {
    const { t } = useI18n();
    const funcs = reactive(props.func);
    const router = useRouter();
    const route = useRoute();
    const dialogFormVisible = reactive({
      flag: false,
      editflag: false,
    });
    const dialogVisible1 = reactive({
      flag: false,
    });
    let widths = ref(window.screen.width);
    let drawer = ref(0);
    let loading = ref(true);
    let drawerFlag = ref(false);
    let activeName = ref('first');
    let connection = ref('');
    let totalCount = ref(0);
    let totalCount1 = ref(0);
    let timestamp = ref(null);
    let measurementList = ref([]);
    let valueList = ref([]);
    let handleChange = ref(null);
    const filesd = ref(null);
    const pagination = reactive({
      pageSize: 10,
      pageNum: 1,
    });
    const pagination1 = reactive({
      pageSize: 10,
      pageNum: 1,
    });
    const routeData = reactive({
      obj: route.params,
    });
    const drawerRef = ref(null);
    const formTable = ref(null);
    let deviceObj = reactive({
      deviceData: 0,
    });
    const tableflag = reactive({
      flag: false,
    });
    const editFormData = reactive({
      formData: {},
      formItem: [],
    });
    const form = reactive({
      inline: true, //横向
      formData: {},
      formItem: [
        {
          label: '', //名称
          type: 'INPUT', //控件类型
          size: 'small', //element尺寸
          width: '400px',
          itemID: 'keyword', //数据字段名
          suffixIcon: 'el-icon-search', // 后图标样式
          placeholder: 'device.serchPy', //灰色提示文字
        },
      ],
    });
    const form1 = reactive({
      inline: true, //横向
      formData: {
        time: [],
      },
      formItem: [
        {
          label: 'device.time', //名称
          type: 'DATE', //控件类型
          size: 'small', //element尺寸
          width: '400px',
          itemID: 'time', //数据字段名
          // suffixIcon: "el-icon-search", // 后图标样式
          startPlaceholder: '开始日期', //灰色提示文字
          endPlaceholder: '结束日期', //灰色提示文字
        },
        {
          label: '物理量筛选', //名称
          type: 'SELECT', //控件类型
          size: 'small', //element尺寸
          width: '150px',
          itemID: 'keyword', //数据字段名
          placeholder: 'device.serchPy', //灰色提示文字
          options: [
            { label: '全部', value: '1' },
            { label: '启动', value: '1' },
          ],
        },
      ],
    });
    const newData = reactive({
      formData: {
        startTime: new Date(),
        stepSize: null,
        totalLine: null,
      },
      formItem: [
        {
          label: '开始时间', //名称
          type: 'DATETIME', //控件类型
          size: 'small', //element尺寸
          width: '100%',
          itemID: 'startTime', //数据字段名
          placeholder: '选择时间', //灰色提示文字
          required: true,
          rules: [{ required: true, message: '请选择开始时间', trigger: 'blur' }],
        },
        {
          label: '步长', //名称
          type: 'INPUTNUM', //控件类型
          size: 'small', //element尺寸
          width: '100%',
          itemID: 'stepSize', //数据字段名
          placeholder: '请输入步长', //灰色提示文字
          unit: 'ms',
          required: true,
          rules: [
            { required: true, message: '请填写步长', trigger: 'blur' },
            {
              trigger: 'change',
              validator: async (rule, value, callback) => {
                if (!/^[0-9]*$/.test(value) || value < 0) {
                  callback(new Error('请输入正整数'));
                  return;
                }
              },
            },
          ],
        },
        {
          label: '生成数量', //名称
          type: 'INPUTNUM', //控件类型
          size: 'small', //element尺寸
          width: '100%',
          itemID: 'totalLine', //数据字段名
          placeholder: '请输入生成数量', //灰色提示文字
          required: true,
          rules: [
            { required: true, message: '请输入生成数量', trigger: 'blur' },
            {
              trigger: 'change',
              validator: async (rule, value, callback) => {
                if (!/^[0-9]*$/.test(value)) {
                  callback(new Error('请输入正整数'));
                  return;
                }
                if (value > 1000000) {
                  callback(new Error('限制100万条以内'));
                  return;
                }
              },
            },
          ],
        },
      ],
    });
    const formdate = reactive({
      inline: true, //横向
      formData: {
        time: [],
      },
      formItem: [
        {
          label: 'device.time', //名称
          type: 'DATE', //控件类型
          size: 'small', //element尺寸
          width: '400px',
          itemID: 'time', //数据字段名
          // suffixIcon: "el-icon-search", // 后图标样式
          startPlaceholder: '开始日期', //灰色提示文字
          endPlaceholder: '结束日期', //灰色提示文字
          Event: checkDateValue,
        },
      ],
    });
    const column = reactive({
      list: [
        {
          label: 'device.physicalname',
          prop: 'timeseries',
          value: '——', //默认值，该项如果没有数据显示
        },
        {
          label: '别名',
          prop: 'alias',
          value: '——',
        },
        {
          label: 'device.datatype',
          prop: 'dataType',
          value: '——', //默认值，该项如果没有数据显示
        },
        {
          label: 'device.codingmode',
          prop: 'encoding',
          value: '——', //默认值，该项如果没有数据显示
        },
        {
          label: '压缩方式',
          prop: 'compression',
          value: '——',
        },
        {
          label: '数据总量(条)',
          prop: 'dataCount',
          value: '——',
        },
        {
          label: 'device.newValue',
          prop: 'newValue',
          value: '——', //默认值，该项如果没有数据显示
        },
        {
          label: 'device.datatrends',
          prop: 'action',
        },
        {
          label: 'device.physicaldescr',
          prop: 'description',
          value: '——', //默认值，该项如果没有数据显示
        },
        {
          label: '标签',
          prop: 'tags',
          type: 'TAGS',
          value: '——',
        },
        {
          label: '属性',
          prop: 'attributes',
          type: 'TAGS',
          value: '——',
        },
      ],
    });
    const column1 = reactive({
      list: [
        {
          label: 'device.action',
          prop: 'action',
          width: 100,
        },
      ],
    });
    const column2 = reactive({
      list: [
        {
          label: '上传总数',
          prop: 'totalCount',
        },
        {
          label: '成功数',
          prop: 'SCount',
        },
        {
          label: '失败数',
          prop: 'failCount',
        },
        {
          label: 'device.action',
          prop: 'action',
          width: 100,
        },
      ],
    });
    const tableData = reactive({
      list: [],
    });
    const tableData1 = reactive({
      list: [],
    });
    const tableData2 = reactive({
      list: [],
    });
    function selectFile() {
      filesd.value.click();
    }
    function getDataLook() {
      formTable.value.checkData(dialogFormVisible);
    }
    function downloadEx() {
      downloadFile().then((res) => {
        if (res.size) {
          handleExport(res, `模板.CSV`);
        }
      });
    }
    function searchRow(val) {
      routeData.obj.timeseries = val.timeseries;
      if (!drawerFlag.value) {
        drawerFlag.value = true;
      } else {
        drawerFlag.value = false;
        setTimeout(() => {
          drawerFlag.value = true;
          drawerRef.value.getehartsData(routeData.obj);
        }, 10);
      }
      setTimeout(() => {
        drawer.value = 400;
      }, 10);
    }
    function closeDrawer() {
      drawer.value = 0;
      setTimeout(() => {
        drawerFlag.value = false;
      }, 400);
    }
    function selectData(val) {
      handleChange.value = val;
    }
    function deleteArry() {
      if (handleChange.value) {
        deleteRow(handleChange.value);
      }
    }
    function editRow(row) {
      let obj = {};
      editFormData.formItem = [];
      timestamp.value = row.t0;
      measurementList.value = [];
      column1.list.forEach((item) => {
        if (item.prop !== 't0' && item.prop !== 'action') {
          obj[item.prop] = row[item.prop];
          editFormData.formItem.push({
            label: item.label,
            type: 'INPUT',
            size: 'small',
            width: '100%',
            placeholder: '',
            itemID: item.prop,
          });
          measurementList.value.push(item.label);
        }
      });
      editFormData.formData = obj;
      dialogFormVisible.editflag = true;
    }
    function saveData() {
      valueList.value = Object.values(editFormData.formData);
      editData(routeData.obj, { timestamp: new Date(timestamp.value), measurementList: measurementList.value, valueList: valueList.value }).then(() => {
        ElMessage({
          type: 'success',
          message: `保存成功!`,
        });
        getPview();
        dialogFormVisible.editflag = false;
      });
    }
    function getDate(timeS, timeE) {
      formdate.formData.time = [timeS, timeE];
    }
    function creatDevice() {
      funcs.addTab(`${routeData.obj.parentid}:newdevice`);
    }
    function editDevce() {
      router.push({ name: 'Device', params: { ...routeData.obj } });
    }
    function deleteData() {
      ElMessageBox.confirm(`${t('device.deletecontent1')}"${routeData.obj.name}"？${t('device.deletecontent2')}`, '提示', {
        confirmButtonText: t('device.ok'),
        cancelButtonText: t('device.cencel'),
        type: 'warning',
      })
        .then(() => {
          deleteDevice(routeData.obj).then(() => {
            ElMessage({
              type: 'success',
              message: `${t('device.deleteSuccess')}!`,
            });
            props.func.updateTree();
            props.func.removeTab(routeData.obj.id);
          });
        })
        .catch(() => {
          ElMessage({
            type: 'info',
            message: t('device.cencel'),
          });
        });
    }
    function deleteRow(dataArr) {
      let arr = column1.list.filter((item) => item.prop !== 't0' && item.prop !== 'action').map((item) => item.label);
      let timeArr = dataArr.map((item) => new Date(item.t0));
      ElMessageBox.confirm(`确定是否删除物理量`, '提示', {
        confirmButtonText: t('device.ok'),
        cancelButtonText: t('device.cencel'),
        type: 'warning',
      }).then(() => {
        deleteDeviceData(routeData.obj, { timestampList: timeArr, measurementList: arr }).then(() => {
          ElMessage({
            type: 'success',
            message: `${t('device.deleteSuccess')}!`,
          });
          handleChange.value = null;
          getPview();
        });
      });
    }
    function exportData() {
      let sTime = null;
      let eTime = null;
      if (form1.formData.time.length > 0) {
        sTime = form1.formData.time[0];
        eTime = form1.formData.time[1];
      }
      let arr = column1.list.filter((item) => item.prop !== 't0' && item.prop !== 'action').map((item) => item.label);
      exportDataCSV(routeData.obj, { measurementList: arr, startTime: sTime, endTime: eTime }).then((res) => {
        ElMessage({
          type: 'success',
          message: `导出成功!`,
        });
        let name = routeData.obj.name.split('.')[routeData.obj.name.split('.').length - 1];
        handleExport(res, `${name}.CSV`);
      });
    }
    function serchFormData() {
      pagination.pageSize = 10;
      pagination.pageNum = 1;
      getListData();
    }
    function serchFormData1() {
      pagination1.pageSize = 10;
      pagination1.pageNum = 1;
      getPview();
    }
    function getPagintions(val) {
      console.log(val);
      console.log(pagination);
    }
    function getListData() {
      getList(routeData.obj, { ...pagination, ...form.formData }).then((res) => {
        tableData.list = res.data.measurementVOList;
        totalCount.value = res.data.totalCount;
        getPview();
      });
    }
    function getPview() {
      tableflag.flag = false;
      let sTime = null;
      let eTime = null;
      if (form1.formData.time.length > 0) {
        sTime = form1.formData.time[0];
        eTime = form1.formData.time[1];
      }
      let data = tableData.list.map((item) => item.timeseries);
      column1.list = [
        {
          label: 'device.action',
          prop: 'action',
          width: 100,
        },
      ];
      tableData1.list = [];
      getDataDeviceList(routeData.obj, pagination1, { startTime: sTime, endTime: eTime, measurementList: data }).then((res) => {
        res.data.metaDataList.forEach((item, index) => {
          column1.list.push({ label: item, prop: `t${index}`, value: '——' });
        });
        res.data.valueList.forEach((item) => {
          let obj = {};
          item.forEach((etem, index) => {
            obj[`t${index}`] = etem;
          });
          tableData1.list.push(obj);
        });
        totalCount1.value = res.data.totalCount;
        tableflag.flag = true;
      });
    }
    function randomImData() {
      randomImport(routeData.obj, newData.formData).then((res) => {
        console.log(res);
      });
      dialogFormVisible.flag = false;
    }
    function getdData() {
      getDeviceDate(routeData.obj).then((res) => {
        deviceObj.deviceData = res.data;
      });
    }
    function checkDateValue() {
      console.log(formdate.formData.time);
      drawerRef.value.setEchartsTime(formdate.formData.time);
    }
    onActivated(() => {
      routeData.obj = Object.assign(routeData.obj, route.params);
      form.formData.keyword = '';
      if (route.params.forceupdate === 'true') {
        formdate.formData.time = [];
      }
      setTimeout(() => {
        getdData();
        getListData();
      }, 500);
    });
    return {
      serchFormData1,
      downloadEx,
      tableData2,
      column2,
      exportData,
      deleteRow,
      totalCount1,
      saveData,
      editRow,
      editFormData,
      getPview,
      randomImData,
      tableflag,
      tableData1,
      pagination1,
      selectFile,
      filesd,
      dialogVisible1,
      getDataLook,
      formTable,
      newData,
      dialogFormVisible,
      deleteArry,
      column1,
      form1,
      activeName,
      form,
      widths,
      column,
      drawer,
      connection,
      getDate,
      drawerRef,
      deviceObj,
      routeData,
      deleteData,
      pagination,
      totalCount,
      getPagintions,
      getListData,
      serchFormData,
      drawerFlag,
      tableData,
      formdate,
      searchRow,
      selectData,
      editDevce,
      closeDrawer,
      creatDevice,
      loading,
    };
  },
  components: {
    StandTable,
    FormTable,
    ElButton,
    Echarts,
    action,
    ElTabs,
    ElTabPane,
    ElDropdown,
    ElDropdownMenu,
    ElDropdownItem,
    ElDialog,
    ElForm,
    ElFormItem,
    ElProgress,
  },
};
</script>

<style lang="scss" scoped>
$cursor: pointer;
.export_notice {
  margin-left: 20px;
  color: #abb1c7;
}
.div_children {
  height: 150px;
  display: flex;
  width: 100%;
  align-items: center;
  div {
    width: 100%;
    .info_div {
      padding-top: 10px;
      text-align: center;
    }
  }
}
.button_div {
  display: flex;
  width: 180px;
  justify-content: space-between;
}
.table2 {
  cursor: pointer;
  &.edit {
    color: #16c493;
  }
  &.delete {
    color: red;
    margin-left: 15px;
  }
}
.frist_div {
  background: #fff;
  height: calc(100vh - 383px);
  border-radius: 4px;
  border: 1px solid #eaecf0;
  padding: 20px;
}
.messIcon {
  color: rgba($color: #000000, $alpha: 0.25);
}
.content_padding {
  padding: 20px;
  // background: #f9fbfc;
}
.newButton {
  background-color: $theme-color;
  border-color: $theme-color;
}
.actionSpan {
  height: 70px;
  display: flex;
  align-items: center;
}
.formtable {
  padding: 10px 30px;
  text-align: initial;
}
.drawertitle {
  height: 35px;
  background: rgb(227, 227, 227);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30px;
  font-size: 11px;
}
.drawer {
  // width: 84%;
  bottom: 0px;
  background: #fff;
  position: absolute;
  z-index: 9;
  overflow: hidden;
  transition: all 0.3s ease-out 0.1s;
  //   box-shadow: 3px 3px 3px #a2a2a2;
}
.headerbox {
  text-align: left;
  padding: 0 30px;
  border-radius: 4px;
  border: 1px solid #eaecf0;
  margin-bottom: 20px;
  background: #fff;
}
.headerSpan {
  font-size: 20px;
  font-weight: bold;
}
.flexBox {
  display: flex;
  justify-content: space-between;
}
.headerIcon {
  width: 110px;
  padding: 0 20px;
}
.edit {
  color: #16c493;
  cursor: $cursor;
}
.delete {
  color: red;
  cursor: $cursor;
}
.spanmargin {
  margin-left: 60px;
}
.messageBox {
  padding: 10px 0 15px 0;
  color: #909399;
  font-size: 14px;
}
.creatButton {
  padding: 0px 40px;
  height: 35px;
  min-height: 0px !important;
  background: #409eff;
  color: #fff;
  margin-top: 3px;
}
</style>
<style lang="scss">
.tab_class {
  .el-tabs__nav-wrap::after {
    background: none;
  }
  .el-tabs__header {
    padding: 0 20px;
  }
  .el-tabs__content {
    background: #f9fbfc;
    padding: 20px;
    height: calc(100vh - 343px);
  }
}
.content_message {
  .el-form-item__label {
    text-align: left;
  }
  .el-form-item__content {
    margin-left: 0px !important;
  }
}
</style>
