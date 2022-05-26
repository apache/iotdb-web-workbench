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
  <div style="padding: 20px">
    <form-table :form="form"></form-table>
    <div class="addbox">
      {{ $t('device.physical') }}
      <el-button type="primary" class="addbutton" size="small" @click="addItem"> {{ $t('device.addphysical') }} </el-button>
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
        :celineHeight="5"
        :encoding="encoding"
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
      <el-button @click="closeTab">{{ $t('device.cencel') }}</el-button>
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
import _cloneDeep from 'lodash/cloneDeep';
import { useStore } from 'vuex';
export default {
  name: 'DeviceAddEidt',
  props: {
    func: Object,
    data: Object,
    dividerWidth: Object,
  },
  setup(props) {
    const store = useStore();
    const route = useRoute();
    const router = useRouter();
    const standtable = ref(null);
    const { t } = useI18n();
    let totalCount = ref(0);
    let erroflag = ref(true);
    let widths = ref(window.screen.width);
    let currRouteParams = reactive({});
    const deviceData = reactive({
      obj: {},
    });
    const pagination = reactive({
      pageSize: 10,
      pageNum: 1,
    });
    const encoding = {
      BOOLEAN: [
        { label: 'RLE', value: 'RLE' },
        { label: 'PLAIN', value: 'PLAIN' },
      ],
      TEXT: [{ label: 'PLAIN', value: 'PLAIN' }],
      DEFAULT: [
        { label: 'RLE', value: 'RLE' },
        { label: 'PLAIN', value: 'PLAIN' },
        { label: 'TS_2DIFF', value: 'TS_2DIFF' },
        { label: 'GORILLA', value: 'GORILLA' },
      ],
    };
    const column = reactive({
      list: [
        {
          label: 'device.physicalname',
          prop: 'timeseries',
          type: 'INPUT',
          required: true,
          size: 'small',
          border: true,
          event: checkVal,
        },
        {
          label: 'device.alias',
          prop: 'alias',
          type: 'INPUT',
          size: 'small',
          canEdit: true,
          border: true,
          required: true,
          event: checkValue,
        },
        {
          label: 'device.datatype',
          prop: 'dataType',
          type: 'SELECT',
          options: [
            { label: 'BOOLEAN', value: 'BOOLEAN' },
            { label: 'INT32', value: 'INT32' },
            { label: 'INT64', value: 'INT64' },
            { label: 'FLOAT', value: 'FLOAT' },
            { label: 'DOUBLE', value: 'DOUBLE' },
            { label: 'TEXT', value: 'TEXT' },
          ],
          event: changeBorder,
          eventf: true,
          required: true,
          size: 'small',
        },
        {
          label: 'device.codingmode',
          prop: 'encoding',
          type: 'SELECTCH',
          required: true,
          size: 'small',
          icon: 'el-icon-question',
        },
        {
          label: 'device.compressionMode',
          prop: 'compression',
          type: 'SELECT',
          size: 'small',
          icon: 'el-icon-question',
          options: [
            { label: 'UNCOMPRESSED', value: 'UNCOMPRESSED' },
            { label: 'SNAPPY', value: 'SNAPPY' },
            { label: 'LZ4', value: 'LZ4' },
            { label: 'GZIP', value: 'GZIP' },
          ],
        },
        {
          label: 'device.physicaldescr',
          prop: 'description',
          type: 'TEXT',
          size: 'small',
        },
        {
          label: 'device.tag',
          prop: 'tags',
          type: 'TAG',
          size: 'small',
        },
        {
          label: 'device.attributes',
          prop: 'attributes',
          type: 'ATTRIBUTES',
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
          tag: [],
          display: true,
          border: false,
          namecopy: false,
        },
      ],
    });
    const form = reactive({
      labelPosition: 'left',
      formData: {
        deviceId: null,
      },
      formItem: [
        {
          label: 'device.devicename',
          type: 'INPUT',
          size: 'small',
          labelWidth: '40%',
          itemID: 'deviceName',
          placeholder: 'device.inputdevice',
          required: true,
          disabled: false,
          inputHeader: true,
          inputHeaderText: (data) => `${data.groupName}.`,
          message: 'device.inputdevice',
        },
        {
          label: 'device.description',
          type: 'INPUT',
          size: 'small',
          labelWidth: '40%',
          itemID: 'description',
          placeholder: 'device.inputdecr',
          required: false,
          message: 'device.inputdecr',
        },
      ],
    });
    function changeBorder(scope) {
      tableData.list[scope.$index].seBorder = false;
    }
    function checkValue(scope, object, value, event, item) {
      if (value == null || value === '') {
        ElMessage.error(`${t('common.placeHolder')}${t(item.label)}`);
      } else {
        tableData.list[scope.$index].border = false;
      }
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
          alias: null,
          compression: 'SNAPPY',
          tags: [],
          attributes: [],
          display: true,
          border: false,
          namecopy: false,
          seBorder: false,
        });
      }
    }
    function closeTab() {
      router.go(-1);
      // router.push({ name: 'DeviceMessage', params: { ...deviceData.obj } });
    }
    function sumbitData() {
      let checkfalg = true;
      tableData.list.forEach((item) => {
        if (item.timeseries === null || item.dataType === null || item.border || item.seBorder || item.alias == null || item.alias === '') {
          if (checkfalg) {
            if (item.timeseries === null) {
              item.border = true;
              ElMessage.error(`${t('device.pynamel')}`);
            } else if (item.alias == null || item.alias === '') {
              item.border = true;
              ElMessage.error(`${t('common.placeHolder')}${t('device.alias')}`);
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
      // Verification passed
      if (checkfalg) {
        let copyForm = _cloneDeep(form);
        let { deviceName, groupName } = copyForm.formData;
        if (/\./.test(deviceName)) {
          return ElMessage.error(`"${t('device.devicename')}"${t('device.must')}`);
        }
        let copyTableData = _cloneDeep(tableData);
        copyForm.formData.deviceName = deviceName ? groupName + '.' + deviceName : groupName;

        if (copyTableData.list.length > 0) {
          copyTableData.list.forEach((item) => {
            if (item.timeseries.indexOf(groupName) === -1) {
              item.timeseries = copyForm.formData.deviceName + '.' + item.timeseries;
            }
          });
          deviceAddEdite(deviceData.obj.connectionid, deviceData.obj.storagegroupid, { ...copyForm.formData, deviceDTOList: copyTableData.list }).then((res) => {
            if (res.code === '0') {
              ElMessage({
                type: 'success',
                message: `${t('device.savesuccess')}!`,
              });
              deviceData.obj.name = copyForm.formData.deviceName;
              router.go(-1);
              // if (deviceData.obj.dflag) {
              let parentId = currRouteParams.parentid;
              let isAdd = route.query.id.endsWith(':newdevice');
              let id = '';
              if (isAdd) {
                // If an entity is added under a storage group, the ID will be processed separately
                if (currRouteParams.parent.type === 'storageGroup') {
                  id = parentId + currRouteParams.storagegroupid + 'device' + copyForm.formData.deviceName + 'device';
                } else {
                  id = parentId + copyForm.formData.deviceName + 'device';
                }
              } else {
                id = route.query.id;
              }
              props.func.updateTree();
              props.func.addTab(id, {}, true);
              // }
            }
          });
        } else {
          if (copyTableData.list.length <= 0) {
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
          deviceName: deviceData.obj.name.slice(deviceData.obj.storagegroupid.length + 1),
          groupName: `${deviceData.obj.storagegroupid}`,
          deviceId: res.data.deviceId,
        });
        form.formItem[0].disabled = true;
      });
    }
    function openWin() {
      window.open('https://iotdb.apache.org/zh/UserGuide/Master/Data-Concept/Encoding.html', '_blank');
    }
    // const addDevice = () => {};
    onActivated(() => {
      deviceData.obj = route.params;
      currRouteParams = store.state.dataBaseM.currRouteParams;

      console.log(currRouteParams);
      let keys = Object.keys(deviceData.obj);
      if (keys.length > 3) {
        if (route.params.type !== 'newdevice') {
          getdData();
          getListData();
        } else {
          form.formData = reactive({
            description: null,
            deviceName: null,
            groupName: `${currRouteParams.parent.deviceid ? currRouteParams.parent.deviceid : currRouteParams.storagegroupid}`,
            deviceId: null,
          });
          tableData.list = [
            {
              timeseries: null,
              dataType: null,
              encoding: null,
              description: null,
              alias: null,
              compression: 'SNAPPY',
              tags: [],
              attributes: [],
              display: true,
              border: false,
              namecopy: false,
              seBorder: false,
            },
          ];
          totalCount.value = 0;
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
  color: #fff;
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
  bottom: 0;
  left: 50%;
  background: #fff;
  height: 52px;
  line-height: 52px;
  z-index: 9;
  // text-align: center;
}
</style>
