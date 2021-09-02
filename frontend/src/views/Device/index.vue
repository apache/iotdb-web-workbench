/* * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements. See the NOTICE file * distributed with this work for additional information * regarding
copyright ownership. The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License. You may obtain a
copy of the License at * * http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, * software distributed under the License is distributed on an * "AS
IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY * KIND, either express or implied. See the License for the * specific language governing permissions and limitations * under the License. */

<template>
  <div style="padding: 20px">
    <form-table :form="form"></form-table>
    <div class="addbox">
      {{ $t('device.physical') }}
      <el-button type="primary" class="addbutton" size="small" @click="addItem">
        {{ $t('device.addphysical') }}
      </el-button>
    </div>
    <div class="tableBox">
      <stand-table
        ref="standtable"
        :column="column"
        :total="totalCount"
        :getList="getListData"
        :tableData="tableData"
        :selectData="selectData"
        :pagination="pagination"
        :lineHeight="5"
        :encoding="encoding"
        :maxHeight="430"
        @iconEvent="openWin"
      >
        <template #default="{ scope }">
          <el-button @click="deleteRow(scope.row, scope.$index)" type="text" size="small" style="color: red">
            {{ $t('device.delete') }}
          </el-button>
        </template>
      </stand-table>
    </div>
    <div class="footer" :style="{ left: dividerWidth + 'px', width: widths - dividerWidth + 'px' }">
      <el-button type="info" @click="closeTab">{{ $t('device.cencel') }}</el-button>
      <el-button type="primary" class="sumbitButton" @click="sumbitData">{{ $t('device.ok') }}</el-button>
    </div>
  </div>
</template>

<script>
import FormTable from '@/components/FormTable';
import StandTable from '@/components/StandTable';
import { ElButton, ElMessageBox, ElMessage } from 'element-plus';
import { onActivated, reactive, ref } from 'vue';
import { getDeviceDate, getList, deviceAddEdite, deleteData } from './api';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
export default {
  name: 'DeviceAddEidt',
  props: {
    func: Object,
    data: Object,
    dividerWidth: Object,
  },
  setup(props) {
    const route = useRoute();
    const router = useRouter();
    const standtable = ref(null);
    const { t } = useI18n();
    let totalCount = ref(0);
    let erroflag = ref(true);
    let widths = ref(window.screen.width);
    const deviceData = reactive({
      obj: {},
    });
    const pagination = reactive({
      pageSize: 10,
      pageNum: 1,
    });
    const encoding = {
      BOOLEAN: [
        { label: 'PLAIN', value: 'PLAIN' },
        { label: 'RLE', value: 'RLE' },
      ],
      TEXT: [{ label: 'PLAIN', value: 'PLAIN' }],
      DEFAULT: [
        { label: 'PLAIN', value: 'PLAIN' },
        { label: 'RLE', value: 'RLE' },
        { label: 'TS_2DIFF', value: 'TS_2DIFF' },
        { label: 'GORILLA', value: 'GORILLA' },
      ],
    };
    const column = reactive({
      list: [
        {
          label: 'device.physicalname',
          prop: 'timeseries',
          type: 'INPUT', //控件类型
          // width: 350,
          required: true, //必填标志
          size: 'small',
          event: checkVal,
        },
        {
          label: 'device.datatype',
          prop: 'dataType',
          type: 'SELECT',
          // width: 200,
          options: [
            { label: 'BOOLEAN', value: 'BOOLEAN' },
            { label: 'INT32', value: 'INT32' },
            { label: 'INT64', value: 'INT64' },
            { label: 'FLOAT', value: 'FLOAT' },
            { label: 'DOUBLE', value: 'DOUBLE' },
            { label: 'TEXT', value: 'TEXT' },
          ],
          event: changeBorder,
          required: true,
          size: 'small',
        },
        {
          label: 'device.codingmode',
          prop: 'encoding',
          type: 'SELECTCH',
          // width: 200,
          required: true,
          size: 'small',
          icon: 'el-icon-question',
        },
        {
          label: 'device.physicaldescr',
          prop: 'description',
          type: 'TEXT',
          width: 300,
          // maxlength: 255,
          size: 'small',
        },
        {
          label: 'device.action',
          prop: 'action',
          width: 100,
          align: 'center',
        },
      ],
    });
    let tableData = reactive({
      list: [
        {
          timeseries: null,
          dataType: null,
          encoding: null,
          description: null,
          display: true,
          border: false,
          namecopy: false,
        },
      ],
    });
    const form = reactive({
      labelPosition: 'left', //文本对齐方式
      formData: {
        deviceId: null,
      },
      formItem: [
        {
          label: 'device.devicename', //名称
          type: 'INPUT', //控件类型
          size: 'small', //element尺寸
          labelWidth: '40%', //块宽度 px 或 %
          itemID: 'deviceName', //数据字段名
          placeholder: 'device.inputdevice', //灰色提示文字
          required: true, //是否必填
          disabled: false,
          message: 'device.inputdevice', //报错提示信息
        },
        {
          label: 'device.description', //名称
          type: 'INPUT', //控件类型
          size: 'small', //尺寸
          labelWidth: '40%', //块宽度 px 或 %
          itemID: 'description', //数据字段名
          placeholder: 'device.inputdecr', //灰色提示文字
          required: false, //是否必填
          message: 'device.inputdecr', //报错提示信息
        },
        {
          label: 'device.group', //名称
          type: 'TEXT', //控件类型
          size: 'small', //尺寸
          labelWidth: '40%', //块宽度 px 或 %
          itemID: 'groupName', //数据字段名
          placeholder: '', //灰色提示文字
          required: false, //是否必填
          message: '', //报错提示信息
        },
      ],
    });
    function changeBorder(scope) {
      tableData.list[scope.$index].seBorder = false;
    }
    function checkVal(scope, obj, val) {
      console.log(obj);
      if (!/^\w+$/.test(val)) {
        if (erroflag.value) {
          ElMessage.error(`"${val}"${t('device.pyname')}`);
          erroflag.value = false;
        }
        tableData.list[scope.$index].border = true;
      } else if (val === null || val.length > 255) {
        if (erroflag.value) {
          ElMessage.error(`"${val}"${t('device.pynamel')}`);
          erroflag.value = false;
        }
        tableData.list[scope.$index].border = true;
      } else {
        const arr = JSON.parse(JSON.stringify(tableData.list));
        arr.splice(scope.$index, 1);
        if (arr.length) {
          try {
            arr.forEach((item) => {
              if (item.timeseries === val) {
                if (erroflag.value) {
                  ElMessage.error(`"${val}"${t('device.pynamecopy')}`);
                  erroflag.value = false;
                }
                tableData.list[scope.$index].border = true;
                obj.namecopy = true;
                throw Error();
              } else {
                tableData.list[scope.$index].border = false;
                obj.namecopy = false;
              }
            });
          } catch (e) {
            console.log('erro');
          }
        } else {
          tableData.list[scope.$index].border = false;
          obj.namecopy = false;
        }
      }
      setTimeout(() => {
        erroflag.value = true;
      }, 500);
    }
    function deleteRow(row, index) {
      console.log(row.timeseries);
      console.log(index);
      if (!row.display) {
        ElMessageBox.confirm(`${t('device.deletecontent1')}"${row.timeseries}"？${t('device.deletecontent2')}`, `${t('device.tips')}`, {
          confirmButtonText: t('device.ok'),
          cancelButtonText: t('device.cencel'),
          type: 'warning',
        })
          .then(() => {
            deleteData(deviceData.obj, row.timeseries).then((res) => {
              if (res.code === '0') {
                tableData.list.splice(index, 1);
                ElMessage({
                  type: 'success',
                  message: `${t('device.deletetitle')}!`,
                });
              }
            });
          })
          .catch(() => {
            ElMessage({
              type: 'info',
              message: `${t('device.canceldelete')}!`,
            });
          });
      } else {
        tableData.list.splice(index, 1);
      }
    }
    function addItem() {
      if (tableData.list.length > 2000) {
        ElMessage.error(`${t('device.addpydata')}!`);
      } else {
        tableData.list.unshift({
          timeseries: null,
          dataType: null,
          encoding: null,
          description: null,
          display: true,
          border: false,
          namecopy: false,
          seBorder: false,
        });
      }
    }
    function closeTab() {
      ElMessage({
        type: 'info',
        message: `${t('device.cencel')}!`,
      });
      router.go(-1);
    }
    function sumbitData() {
      let checkfalg = true;
      console.log(tableData.list);
      tableData.list.forEach((item) => {
        if (item.timeseries === null || item.dataType === null || item.border || item.seBorder) {
          if (checkfalg) {
            if (item.timeseries === null) {
              item.border = true;
              ElMessage.error(`${t('device.pynamel')}`);
            } else if (item.dataType === null) {
              item.seBorder = true;
              ElMessage.error(`"${item.timeseries}"${t('device.selectdatatype')}`);
            } else if (item.namecopy) {
              item.border = true;
              ElMessage.error(`"${item.timeseries}"${t('device.pynamecopy')}`);
            }
          }
          checkfalg = false;
        }
      });
      if (checkfalg && form.formData.deviceName) {
        if (tableData.list.length > 0) {
          deviceAddEdite(deviceData.obj.connectionid, deviceData.obj.storagegroupid, { ...form.formData, deviceDTOList: tableData.list }).then((res) => {
            if (res.code === '0') {
              ElMessage({
                type: 'success',
                message: `${t('device.savesuccess')}!`,
              });
              deviceData.obj.name = form.formData.deviceName;
              router.go(-1);
              if (deviceData.obj.dflag) {
                props.func.updateTree();
              }
            }
          });
        } else {
          if (tableData.list.length <= 0) {
            ElMessage.error(`${t('device.minphysical')}`);
          }
        }
      }
    }
    function getListData() {
      getList(deviceData.obj, pagination).then((res) => {
        tableData.list = res.data.measurementVOList;
        totalCount.value = res.data.totalCount;
      });
    }
    function getdData() {
      getDeviceDate(deviceData.obj).then((res) => {
        form.formData = reactive({
          description: res.data.description,
          deviceName: deviceData.obj.name,
          groupName: `root.${deviceData.obj.storagegroupid}`,
          deviceId: res.data.deviceId,
        });
        form.formItem[0].disabled = true;
      });
    }
    function openWin() {
      window.open('https://iotdb.apache.org/zh/UserGuide/Master/Data-Concept/Encoding.html', '_blank');
    }
    onActivated(() => {
      deviceData.obj = route.params;
      console.log(291751);
      console.log(deviceData.obj);
      let keys = Object.keys(deviceData.obj);
      if (keys.length > 3) {
        if (route.params.type !== 'newdevice') {
          getdData();
          getListData();
        } else {
          form.formData = reactive({
            description: null,
            deviceName: null,
            groupName: `root.${deviceData.obj.storagegroupid}`,
            deviceId: null,
          });
          tableData.list = [
            {
              timeseries: null,
              dataType: null,
              encoding: null,
              description: null,
              display: true,
              border: false,
              namecopy: false,
              seBorder: false,
            },
          ];
        }
      }
    });
    return {
      sumbitData,
      deleteRow,
      addItem,
      getListData,
      closeTab,
      openWin,
      widths,
      pagination,
      encoding,
      totalCount,
      form,
      tableData,
      column,
      standtable,
    };
  },
  components: {
    FormTable,
    ElButton,
    StandTable,
  },
};
</script>

<style lang="scss" scoped>
.addbox {
  font-size: 14px;
  text-align: left;
  color: #606266;
}
.addbutton {
  color: #ffffff;
  margin-left: 20px;
  padding: 10px 30px;
  background-color: $theme-color;
  border-color: $theme-color;
  line-height: 0px;
}
.addbutton:hover {
  border-color: $theme-color;
}
.sumbitButton {
  background-color: $theme-color;
  border-color: $theme-color;
}
.tableBox {
  margin-top: 20px;
  border: 1px solid #ebeef5;
}
.footer {
  position: absolute;
  bottom: 0px;
  left: 50%;
  background: #fff;
  height: 52px;
  line-height: 52px;
  z-index: 9;
  // text-align: center;
}
</style>
