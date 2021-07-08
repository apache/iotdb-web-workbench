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
      <stand-table ref="standtable" :column="column" :tableData="tableData" :selectData="selectData" :lineHeight="5" :encoding="encoding" :maxHeight="460">
        <template #default="{ scope }">
          <el-button @click="deleteRow(scope.row, scope.$index)" type="text" size="small" style="color: red">
            {{ $t('device.delete') }}
          </el-button>
        </template>
      </stand-table>
    </div>
    <div class="footer">
      <el-button type="info">{{ $t('device.cencel') }}</el-button>
      <el-button type="primary" @click="sumbitData">{{ $t('device.ok') }}</el-button>
    </div>
  </div>
</template>

<script>
import FormTable from '@/components/FormTable';
import StandTable from '@/components/StandTable';
import { ElButton, ElMessageBox, ElMessage } from 'element-plus';
import { onMounted, reactive, ref } from 'vue';
import { getDeviceDate, getList, deviceAddEdite, deleteData } from './api';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
export default {
  name: 'DeviceAddEidt',
  props: {
    func: Object,
  },
  setup(props) {
    const router = useRouter();
    const route = useRoute();
    const standtable = ref(null);
    const { t } = useI18n();
    const deviceData = reactive({
      obj: {},
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
          width: 350,
          required: true, //必填标志
          size: 'small',
          event: checkVal,
        },
        {
          label: 'device.datatype',
          prop: 'dataType',
          type: 'SELECT',
          width: 200,
          options: [
            { label: 'BOOLEAN', value: 'BOOLEAN' },
            { label: 'INT32', value: 'INT32' },
            { label: 'INT64', value: 'INT64' },
            { label: 'FLOAT', value: 'FLOAT' },
            { label: 'DOUBLE', value: 'DOUBLE' },
            { label: 'TEXT', value: 'TEXT' },
          ],
          required: true,
          size: 'small',
        },
        {
          label: 'device.codingmode',
          prop: 'encoding',
          type: 'SELECTCH',
          width: 200,
          required: true,
          size: 'small',
          icon: 'el-icon-question',
        },
        {
          label: 'device.physicaldescr',
          prop: 'description',
          type: 'TEXT',
          width: 700,
          maxlength: 255,
          size: 'small',
        },
        {
          label: 'device.action',
          prop: 'action',
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
          placeholder: '请输入设备名称', //灰色提示文字
          required: true, //是否必填
          message: '请输入设备名称', //报错提示信息
        },
        {
          label: 'device.description', //名称
          type: 'INPUT', //控件类型
          size: 'small', //尺寸
          labelWidth: '40%', //块宽度 px 或 %
          itemID: 'description', //数据字段名
          placeholder: '请输入设备描述', //灰色提示文字
          required: false, //是否必填
          message: '请输入设备描述', //报错提示信息
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
    function checkVal(val, ev) {
      if (!/^\w+$/.test(val)) {
        ElMessage.error(`"${val}"物理量必须由字⺟、数字、下划线组成`);
        ev.target.focus();
      } else if (val === null || val.length > 255) {
        ElMessage.error(`"${val}"物理量必须⼤于0⼤字符，⼩于255个字符`);
        ev.target.focus();
      }
    }
    function deleteRow(row, index) {
      console.log(row.timeseries);
      console.log(index);
      ElMessageBox.confirm(`${t('device.deletecontent1')}"${row.timeseries}"？${t('device.deletecontent2')}`, '提示', {
        confirmButtonText: t('device.ok'),
        cancelButtonText: t('device.cencel'),
        type: 'warning',
      })
        .then(() => {
          deleteData(deviceData.obj, row.timeseries).then(() => {
            tableData.list.splice(index, 1);
            ElMessage({
              type: 'success',
              message: '删除成功!',
            });
          });
        })
        .catch(() => {
          ElMessage({
            type: 'info',
            message: '已取消删除',
          });
        });
      // deleteData(9, 'mytest', 'test1',row.timeseries)
    }
    function addItem() {
      tableData.list.unshift({
        timeseries: null,
        dataType: null,
        encoding: null,
        description: null,
        display: true,
      });
    }
    function sumbitData() {
      let checkfalg = true;
      try {
        tableData.list.forEach((item) => {
          if (item.timeseries === null || item.dataType === null) {
            item.timeseries === null ? ElMessage.error(`请填写物理量名称`) : ElMessage.error(`${item.timeseries}物理量必须选择数据类型`);
            checkfalg = false;
            throw Error();
          }
        });
      } catch (e) {
        console.log(e);
      }
      if (checkfalg) {
        deviceAddEdite(deviceData.obj.connectionid, deviceData.obj.storagegroupid, { ...form.formData, deviceDTOList: tableData.list }).then(() => {
          ElMessage({
            type: 'success',
            message: '保存成功!',
          });
          deviceData.obj.name = form.formData.deviceName;
          props.func.updateTree();
          if (route.params.name === '新建实体') {
            props.func.removeTab(route.params.id);
          } else {
            router.go(-1);
          }
        });
      }
    }
    function getListData() {
      getList(deviceData.obj, { pageSize: 10, pageNum: 1 }).then((res) => {
        tableData.list = res.data.measurementVOList;
      });
    }
    function getdData() {
      getDeviceDate(deviceData.obj).then((res) => {
        form.formData = reactive({
          description: res.data.description,
          deviceName: deviceData.obj.name,
          groupName: deviceData.obj.storagegroupid,
          deviceId: res.data.deviceId,
        });
      });
    }
    onMounted(() => {
      deviceData.obj = route.params;
      if (route.params.name !== '新建实体') {
        getdData();
        getListData();
      } else {
        form.formData = reactive({
          description: null,
          deviceName: null,
          groupName: deviceData.obj.storagegroupid,
          deviceId: null,
        });
      }
    });
    return {
      sumbitData,
      deleteRow,
      addItem,
      encoding,
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
  background-color: #0080ff;
  border-color: #0080ff;
  line-height: 0px;
}
.addbutton:hover {
  border-color: #0882fc;
}
.tableBox {
  margin-top: 20px;
  border: 1px solid #ebeef5;
}
.footer {
  position: absolute;
  bottom: 10px;
  left: 50%;
  // text-align: center;
}
</style>
