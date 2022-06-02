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
              >&nbsp;{{ $t('common.edit') }}
            </el-button>
            <el-button @click="deleteData">
              <svg class="icon delete" aria-hidden="true">
                <use xlink:href="#icon-se-icon-delete"></use></svg
              >&nbsp;{{ $t('common.delete') }}
            </el-button>
          </div>
        </div>
        <div class="messageBox">
          <span>
            <svg class="icon" aria-hidden="true">
              <use xlink:href="#icon-shujulianjie1"></use>
            </svg>
          </span>
          <span style="margin-left: 5px">{{ $t('device.dataconnection') }}：</span>
          <span>{{ routeData.obj.parentids }}</span>
          <!-- <span class="spanmargin">{{ $t('device.group') }}：</span>
        <span>{{ routeData.obj.storagegroupid }}</span> -->
          <span class="spanmargin">
            <svg class="icon" aria-hidden="true">
              <use xlink:href="#icon-describe"></use>
            </svg>
          </span>
          <span style="margin-left: 5px">{{ $t('device.description') }}：</span>
          <span class="ellipsis" :title="deviceObj.deviceData.description">{{ deviceObj.deviceData.description }}</span>
          <span class="spanmargin">
            <svg class="icon" aria-hidden="true">
              <use xlink:href="#icon-user"></use>
            </svg>
          </span>
          <span style="margin-left: 5px"> {{ $t('device.creator') }}： </span>
          <span>{{ deviceObj.deviceData.creator }}</span>
          <span class="spanmargin">
            <svg class="icon" aria-hidden="true">
              <use xlink:href="#icon-shizhong"></use>
            </svg>
          </span>
          <span style="margin-left: 5px">{{ $t('device.createTime') }}：</span>
          <span>{{ deviceObj.deviceData.time }}</span>
        </div>
      </div>
    </div>
    <el-tabs v-model="activeName" @tab-click="handleClick" class="tab_class">
      <el-tab-pane :label="$t('device.entityStructure')" name="first">
        <div class="frist_div">
          <div style="padding: 0 0 10px 0" class="flexBox edit-box">
            <el-button type="primary" class="newButton" @click="editDevce">{{ $t('device.editTimeseries') }}</el-button>
            <form-table :form="form" @serchFormData="serchFormData"></form-table>
          </div>
          <stand-table
            v-loading="loading"
            :column="column"
            :tableData="tableData"
            :getList="getListData"
            :total="totalCount"
            :lineHeight="10"
            :lineWidth="21"
            :celineWidth="23"
            :celineHeight="10"
            :maxHeight="500"
            :pagination="pagination"
            @getPagintions="getPagintions"
          >
            <template #default="{ scope }">
              <div @click="searchRow(scope.row)" v-if="scope.row.newValue * 1">
                {{ scope.row }}
                <action :echartsData="routeData.obj" :row="scope.row" :scope="scope"></action>
              </div>
              <div v-else class="actionSpan">
                <span>——</span>
              </div>
            </template>
          </stand-table>
        </div>
      </el-tab-pane>
      <el-tab-pane :label="$t('device.dataPreview')" name="second">
        <div class="frist_div">
          <div style="padding: 0 0 10px 0" class="flexBox edit-box">
            <div class="button_div">
              <el-dropdown trigger="click">
                <el-button type="primary">{{ $t('device.addData') }}</el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="openRandomDataDialog">{{ $t('device.randomGeneration') }}</el-dropdown-item>
                    <el-dropdown-item @click="dialogVisible1.flag = true">{{ $t('device.batchImport') }}</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
              <el-button @click="exportData">{{ $t('device.exportData') }}</el-button>
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
            :maxHeight="500"
            :pagination="pagination1"
            :selectData="selectData"
            :deleteArry="deleteArry"
            @getPagintions="getPagintions"
          >
            <template #default="{ scope }">
              <span class="table2 edit" @click="editRow(scope.row)">{{ $t('common.edit') }}</span>
              <span class="table2 delete" @click="deleteRow([scope.row], scope.$index)">{{ $t('common.delete') }}</span>
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
      <div style="border: 1px solid #e7eaf2">
        <action :echartsData="routeData.obj" :row="echart.row" :idCopyStr="1"></action>
      </div>
    </div>
    <el-dialog :title="$t('device.randomData')" v-model="dialogFormVisible.flag" width="500px">
      <div>
        <span style="color: red">{{ $t('device.randomTip') }}</span>
        <div style="padding-top: 20px">
          <form-table ref="formTable" :form="newData"></form-table>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogFormVisible.flag = false">{{ $t('common.cancel') }}</el-button>
          <el-button type="primary" @click="randomImData"> {{ $t('common.submit') }}</el-button>
        </span>
      </template>
    </el-dialog>
    <el-dialog :title="$t('device.batchImport')" v-model="dialogVisible1.flag" width="700px" @close="cencelexport" class="export_form">
      <div v-if="display.flag" class="div_children">
        <div>
          <input ref="filesd" type="file" multiple="multiple" style="display: none" @change="getfile" />
          <el-form ref="ruleForm" label-position="top">
            <el-form-item :label="$t('device.templateDownload')">
              <el-button size="small" @click="downloadEx"
                >{{ $t('device.downloadTemplate') }}
                <i class="iconfont se-icon-download"></i>
              </el-button>
            </el-form-item>
            <el-form-item :label="$t('device.fileUpload')" prop="categoryName">
              <el-button type="primary" size="small" @click="selectFile"
                >{{ $t('device.fileUpload') }}
                <i class="iconfont se-icon-upload2"></i>
              </el-button>
              <span class="export_notice">{{ $t('device.uploadTip') }}</span>
            </el-form-item>
          </el-form>
        </div>
      </div>
      <div v-else-if="display.flag1" class="div_children">
        <div>
          <el-progress :text-inside="true" :stroke-width="12" :percentage="percentage.count" color="#16C493">
            <span></span>
          </el-progress>
          <div class="info_div">{{ $t('device.uploading') }}：{{ percentage.count }}%</div>
        </div>
      </div>
      <div v-else class="div_children">
        <div>
          <div class="import-result">{{ $t('device.importResult') }}</div>
          <stand-table :column="column2" :tableData="tableData2" :lineHeight="10" :celineHeight="10">
            <template #default="{ scope }">
              <span :class="['table2 edit', !scope.row.failCount ? 'no-edit' : '']" @click="downfile(scope.row.downloadUrl, '')">{{ $t('standTable.download') }}</span>
            </template>
          </stand-table>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cencelexport">{{ $t('common.cancel') }}</el-button>
          <el-button type="primary" @click="cencelexport">{{ $t('common.submit') }}</el-button>
        </span>
      </template>
    </el-dialog>
    <el-dialog :title="$t('device.editData')" v-model="dialogFormVisible.editflag" width="500px">
      <div>
        <form-table v-if="dialogFormVisible.editflag" ref="formTable" :form="editFormData" :labelIcon="true"></form-table>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogFormVisible.editflag = false">{{ $t('common.cancel') }}</el-button>
          <el-button type="primary" @click="saveData">{{ $t('common.submit') }}</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ElMessageBox, ElMessage, ElButton, ElTabs, ElTabPane, ElDropdown, ElDropdownMenu, ElDropdownItem, ElDialog, ElForm, ElFormItem, ElProgress } from 'element-plus';
import StandTable from '@/components/StandTable';
import FormTable from '@/components/FormTable';
import { reactive, ref, onActivated, computed } from 'vue';
import { getList, getDeviceDate, deleteDevice, getDataDeviceList, randomImport, editData, deleteDeviceData, exportDataCSV, downloadFile, importData } from './api';
import Echarts from '@/components/Echarts';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import action from './components/action.vue';
import { handleExport } from '@/util/export';
import axios from '@/util/axios';
import dayjs from 'dayjs';
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
    let timeseriesOptions = ref([]);
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
    const percentage = reactive({
      count: 0,
    });
    // const _dayjs = reactive(dayjs);
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
    const echart = reactive({
      row: {},
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
    const display = reactive({
      flag: true,
      flag1: false,
    });
    const form = reactive({
      inline: true,
      formData: {},
      formItem: [
        {
          label: '',
          type: 'INPUT',
          size: 'small',
          width: '400px',
          itemID: 'keyword',
          suffixIcon: 'el-icon-search',
          placeholder: 'device.serchPy',
        },
      ],
    });
    const form1 = reactive({
      inline: true, //横向
      formData: {
        time: [dayjs(dayjs().format('YYYY-MM-DD 00:00:00')).valueOf() - 7 * 24 * 60 * 60 * 1000, dayjs(dayjs().format('YYYY-MM-DD 23:59:59')).valueOf()],
        measurementList: [''],
      },
      formItem: [
        {
          label: 'device.timeRange',
          type: 'DATE',
          size: 'small',
          width: '400px',
          itemID: 'time',
          // suffixIcon: "el-icon-search",
          startPlaceholder: 'device.startTime',
          endPlaceholder: 'device.endTime',
          defauleTime: [new Date(2000, 1, 1, 0, 0, 0), new Date(2000, 2, 1, 23, 59, 59)],
          disabledDate: (time) => {
            return time > dayjs(dayjs().format('YYYY-MM-DD 23:59:59')).valueOf();
          },
        },
        {
          label: 'device.screenPhysical',
          type: 'SELECT',
          size: 'small',
          width: '150px',
          itemID: 'measurementList',
          placeholder: 'device.serchPy',
          options: computed(() => [{ label: t('device.all'), value: '' }, ...timeseriesOptions.value]),
          multiple: true,
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
          label: 'device.startTime',
          type: 'DATETIME',
          size: 'small',
          width: '100%',
          itemID: 'startTime',
          placeholder: t('device.chooseDate'),
          required: true,
          rules: [{ required: true, message: t('device.startDateTip'), trigger: 'blur' }],
        },
        {
          label: 'device.step',
          type: 'INPUTNUM',
          size: 'small',
          width: '100%',
          itemID: 'stepSize',
          placeholder: 'device.stepTip',
          unit: 'ms',
          required: true,
          rules: [
            { required: true, message: t('device.stepTip'), trigger: 'blur' },
            {
              trigger: 'change',
              validator: async (rule, value, callback) => {
                if (!/^[0-9]*$/.test(value) || value < 0) {
                  callback(new Error(t('device.stepErrorTip')));
                  return;
                }
              },
            },
          ],
        },
        {
          label: 'device.generatedQuantity',
          type: 'INPUTNUM',
          size: 'small',
          width: '100%',
          itemID: 'totalLine',
          placeholder: 'device.generateTip',
          required: true,
          rules: [
            { required: true, message: t('device.generateTip'), trigger: 'blur' },
            {
              trigger: ['change', 'blur'],
              validator: async (rule, value, callback) => {
                if (!/^[0-9]*$/.test(value)) {
                  callback(new Error(t('device.stepErrorTip')));
                  return;
                }
                if (value > 1000000) {
                  callback(new Error(t('device.generateErrorTip')));
                  return;
                }
              },
            },
          ],
        },
      ],
    });
    const formdate = reactive({
      inline: true,
      formData: {
        time: [],
      },
      formItem: [
        {
          label: 'device.time',
          type: 'DATE',
          size: 'small',
          width: '400px',
          itemID: 'time',
          // suffixIcon: "el-icon-search",
          startPlaceholder: 'device.startTime',
          endPlaceholder: 'device.endTime',
          Event: checkDateValue,
        },
      ],
    });
    const column = reactive({
      list: [
        {
          label: 'device.physicalname',
          prop: 'timeseries',
          value: '——',
        },
        {
          label: 'device.alias',
          prop: 'alias',
          value: '——',
        },
        {
          label: 'device.datatype',
          prop: 'dataType',
          value: '——',
        },
        {
          label: 'device.codingmode',
          prop: 'encoding',
          value: '——',
        },
        {
          label: 'device.compressionMode',
          prop: 'compression',
          value: '——',
        },
        {
          label: 'device.dataNum',
          prop: 'dataCount',
          value: '——',
        },
        {
          label: 'device.newValue',
          prop: 'newValue',
          value: '——',
        },
        // {
        //   label: 'device.datatrends',
        //   prop: 'action',
        // },
        {
          label: 'device.tag',
          prop: 'tags',
          type: 'TAG',
          onlyShow: true,
          value: '——',
        },
        {
          label: 'device.attributes',
          prop: 'attributes',
          onlyShow: true,
          type: 'ATTRIBUTES',
          value: '——',
        },
        {
          label: 'device.physicaldescr',
          prop: 'description',
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
          label: 'device.uploadedNum',
          prop: 'totalCount',
          value: 0,
        },
        {
          label: 'device.successNum',
          prop: 'SCount',
          value: 0,
        },
        {
          label: 'device.failedNum',
          prop: 'failCount',
          value: 0,
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
    //Select upload file
    function selectFile() {
      filesd.value.click();
    }
    //Upload file
    function getfile(obj) {
      console.log('obj', obj);
      let files = obj?.target?.files[0];
      if (!files) {
        return;
      }
      if (files.name.split('.')[1] !== 'csv' && files.name.split('.')[1] !== 'CSV') {
        ElMessage.error(t('device.uploadTip'));
        return;
      }
      display.flag = false;
      display.flag1 = true;
      let timeId = setInterval(() => {
        percentage.count += 1;
        if (percentage.count === 99) {
          clearInterval(timeId);
        }
      }, 100);
      let file = obj.target.files[0];
      let data = new FormData();
      data.append('file', file);
      importData(routeData.obj, data)
        .then((res) => {
          percentage.count = 100;
          let arr = [
            {
              totalCount: res.data.totalCount,
              SCount: res.data.totalCount - res.data.failCount,
              failCount: res.data.failCount,
              downloadUrl: res.data.fileDownloadUri,
              // res.data.fileDownloadUri.slice(-36),
            },
          ];
          tableData2.list = arr;
          clearInterval(timeId);
          setTimeout(() => {
            display.flag1 = false;
          }, 500);
        })
        .catch(() => {
          setTimeout(() => {
            display.flag1 = false;
          }, 500);
        });
    }
    // Download failed data
    function downfile(downUrl) {
      axios.get(downUrl).then((res) => {
        handleExport(res, `log.txt`);
      });
    }
    //The file import pop-up window closes and resets the data
    function cencelexport() {
      dialogVisible1.flag = false;
      display.flag = true;
      display.flag1 = false;
      filesd.value.value = null;
      percentage.count = 0;
      getPview();
    }
    function getDataLook() {
      formTable.value.checkData(dialogFormVisible);
    }
    //Download template
    function downloadEx() {
      downloadFile().then((res) => {
        if (res.data) {
          handleExport(res.data, `template.CSV`);
        }
      });
    }
    function searchRow(val) {
      routeData.obj.timeseries = val.timeseries;
      routeData.obj.dataType = val.dataType;
      echart.row = val;
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
        drawer.value = 450;
      }, 10);
    }
    function closeDrawer() {
      drawer.value = 0;
      setTimeout(() => {
        drawerFlag.value = false;
      }, 400);
    }
    //Select the data preview check box
    function selectData(val) {
      handleChange.value = val;
    }
    //Batch delete
    function deleteArry() {
      if (handleChange?.value?.length) {
        deleteRow(handleChange.value);
        return;
      }
      ElMessage.error(t('device.deleteTip'));
    }
    //Edit physical quantity data
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
            type: item.icon === 'BOOLEAN' ? 'RADIO' : 'INPUT',
            size: 'small',
            width: '100%',
            placeholder: '',
            icon: item.icon,
            itemID: item.prop,
          });
          measurementList.value.push(item.label);
        }
      });
      editFormData.formData = obj;
      dialogFormVisible.editflag = true;
    }
    //Edit and save physical quantity
    function saveData() {
      valueList.value = Object.values(editFormData.formData);
      editData(routeData.obj, { timestamp: new Date(timestamp.value), measurementList: measurementList.value, valueList: valueList.value })
        .then((res) => {
          if (res.code === '0') {
            ElMessage({
              type: 'success',
              message: t('device.savesuccess'),
            });
            getPview();
            dialogFormVisible.editflag = false;
          }
        })
        .catch((res) => {
          console.log(res);
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
    //Delete entity
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
    //Delete physical quantity
    function deleteRow(dataArr) {
      let arr = column1.list.filter((item) => item.prop !== 't0' && item.prop !== 'action').map((item) => item.label);
      let timeArr = dataArr.map((item) => new Date(item.t0));
      ElMessageBox.confirm(`${t('device.deleteSingleDataTip')}`, `${t('device.tips')}`, {
        confirmButtonText: t('device.ok'),
        cancelButtonText: t('device.cencel'),
        type: 'warning',
      }).then(() => {
        deleteDeviceData(routeData.obj, { timestampList: timeArr, measurementList: arr }).then((res) => {
          if (res?.code === '0') {
            ElMessage({
              type: 'success',
              message: `${t('device.deleteSuccess')}!`,
            });
            handleChange.value = null;
            getPview();
          }
        });
      });
    }
    //Export physical quantity
    function exportData() {
      let sTime = null;
      let eTime = null;
      if (form1.formData.time.length > 0) {
        sTime = form1.formData.time[0];
        eTime = form1.formData.time[1];
      }
      let arr = column1.list.filter((item) => item.prop !== 't0' && item.prop !== 'action').map((item) => item.label);
      exportDataCSV(routeData.obj, { measurementList: arr, startTime: sTime, endTime: eTime }).then((res) => {
        let name = routeData.obj.name.split('.')[routeData.obj.name.split('.').length - 1];
        handleExport(res, `${name}.CSV`);
        ElMessage({
          type: 'success',
          message: `${t('device.exportSucceeded')}!`,
        });
      });
    }
    //Physical quantity page turning
    function serchFormData() {
      pagination.pageSize = 10;
      pagination.pageNum = 1;
      getListData();
    }
    //Physical quantity data preview page turning
    function serchFormData1({ type, value } = {}) {
      if (type === 'select') {
        let lastValue = value[value.length - 1];
        if (!value.length) {
          form1.formData.measurementList = [''];
        } else if (!form1.formData.measurementList?.includes('') && form1.formData.measurementList?.length === timeseriesOptions.value.length - 1) {
          form1.formData.measurementList = [''];
        } else if (form1.formData.measurementList?.includes('') && form1.formData.measurementList?.length < timeseriesOptions.value.length) {
          if (lastValue !== '') {
            form1.formData.measurementList = form1.formData.measurementList.filter((d) => d !== '');
          } else {
            form1.formData.measurementList = [''];
          }
        }
      }
      pagination1.pageSize = 10;
      pagination1.pageNum = 1;
      getPview();
    }
    function getPagintions(val) {
      console.log(val);
      console.log(pagination);
    }
    //Get physical quantity list
    async function getListData() {
      try {
        loading.value = true;
        await getList(routeData.obj, { ...pagination, ...form.formData }).then((res) => {
          tableData.list = res.data.measurementVOList;
          totalCount.value = res.data.totalCount;
          getTimeseriesOption(res.data.measurementVOList.map((item) => item.timeseries));
        });
      } catch (error) {
        //   console.error(error)
      } finally {
        loading.value = false;
      }
    }
    async function getTimeseriesOption(array) {
      let before = timeseriesOptions.value.map((item) => item.value);
      before.unshift(...array);
      timeseriesOptions.value = [...new Set(before)].map((d) => ({ label: d, value: d })).slice(0, 50);
      form1.formData.measurementList[0] = '';
      getPview();
    }
    //Get physical quantity data preview list
    function getPview() {
      tableflag.flag = false;
      let sTime = null;
      let eTime = null;
      if (form1.formData.time?.length > 0) {
        sTime = form1.formData.time[0];
        eTime = form1.formData.time[1];
      }
      column1.list = [
        {
          label: 'device.action',
          prop: 'action',
          width: 100,
        },
      ];
      tableData1.list = [];
      let data = [...form1.formData.measurementList];
      if (form1.formData.measurementList[0] === '') {
        data = timeseriesOptions.value.filter((d) => d.value !== '').map((d) => d.value);
      }
      if (!Array.isArray(data) || data.length === 0) return;
      getDataDeviceList(routeData.obj, pagination1, { startTime: sTime, endTime: eTime, measurementList: data }).then((res) => {
        res.data.metaDataList.forEach((item, index) => {
          column1.list.push({
            label: item,
            prop: `t${index}`,
            value: '——',
            icon: index ? res.data.typeList[index] : 'TIME',
            closable:
              index > 0 && (form1.formData.measurementList.length === 0 || form1.formData.measurementList[0] !== item)
                ? (data) => {
                    const index = timeseriesOptions.value.findIndex((item) => item.value === data.label);
                    timeseriesOptions.value.splice(index, 1);
                    getPview();
                  }
                : void 0,
          });
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
    const openRandomDataDialog = () => {
      newData.formData = {
        startTime: new Date(),
        stepSize: null,
        totalLine: null,
      };
      dialogFormVisible.flag = true;
      formTable.value.clearValidator();
    };
    //Random import of physical quantity data
    async function randomImData() {
      await formTable.value.checkData(dialogFormVisible);
      randomImport(routeData.obj, newData.formData).then((res) => {
        if (res?.code === '0') {
          ElMessage({
            type: 'success',
            message: t('standTable.importTip'),
          });
          getPview();
        }
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
      timeseriesOptions.value = [];
      form.formData.keyword = '';
      if (route.params.forceupdate === 'true') {
        formdate.formData.time = [];
      }
      setTimeout(async () => {
        getdData();
        await getListData();
        await getPview();
      }, 500);
    });
    return {
      echart,
      downfile,
      percentage,
      cencelexport,
      getfile,
      display,
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
      timeseriesOptions,
      openRandomDataDialog,
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
  display: flex;
  width: 100%;
  align-items: center;
  .import-result {
    padding-bottom: 10px;
    color: gray;
  }
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

  &.no-edit {
    color: gray;
    pointer-events: none;
  }
}
.frist_div {
  background: #fff;
  height: calc(100vh - 383px);
  border-radius: 4px;
  border: 1px solid #eaecf0;
  padding: 20px;
  overflow: auto;
}
.messIcon {
  color: rgba($color: #000, $alpha: 0.25);
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
  height: 44px;
  display: flex;
  align-items: center;
}
.formtable {
  padding: 10px 30px;
  text-align: initial;
}
.drawertitle {
  height: 35px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30px;
  font-size: 11px;
  border: 1px solid #eef0f5;
}
.drawer {
  // width: 84%;
  bottom: 0;
  background: #fff;
  position: absolute;
  z-index: 9;
  overflow: hidden;
  transition: all 0.3s ease-out 0.1s;
  box-shadow: 14px -2px 12px 0 rgba(0, 0, 0, 0.16);
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
:deep(.flexBox) {
  display: flex;
  justify-content: space-between;
  &.edit-box {
    margin-right: -10px;
  }
  .el-form {
    .el-form-item {
      margin-bottom: 0;
    }
  }
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
  padding: 0 40px;
  height: 35px;
  min-height: 0 !important;
  background: #409eff;
  color: #fff;
  margin-top: 3px;
}

.ellipsis {
  display: inline-block;
  width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  vertical-align: top;
  text-align: left;
}
</style>
<style lang="scss">
.tab_class {
  margin-left: 20px;
  .el-tabs__header {
    .el-tabs__nav {
      .el-tabs__item.is-active {
        background-color: transparent !important;
        border: 0 !important;
      }
    }
    .el-tabs__nav-scroll {
      background-color: transparent !important;
    }

    margin-bottom: 4px;
    background-color: #fff;
  }
  .el-tabs__nav-wrap::after {
    background: none;
  }
  .el-tabs__content {
    background: #f9fbfc;
    padding: 20px;
    height: calc(100vh - 343px);
  }
}
</style>
