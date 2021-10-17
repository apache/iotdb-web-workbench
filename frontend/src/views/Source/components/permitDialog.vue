<template>
  <div id="mains" class="mains-contain">
    <el-dialog v-model="visible" :title="dialogType === 'add' ? '新增权限' : '编辑权限'" width="520px" :before-close="handleClose">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="permit-form">
        <el-form-item :props="type" :label="$t('sourcePage.path')">
          <el-radio-group v-model="form.type" @change="changeRadio">
            <el-radio v-for="item in pathList" :key="item.value" :label="item.value">{{ item.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :props="path" :label="$t('sourcePage.range')">
          <!-- 数据连接 -->
          <template v-if="form.type === 0"> -- </template>
          <!-- 存储组 -->
          <template v-else-if="form.type === 1">
            <tree-select :checkedKeys="storage" :data="storageGroupTreeOption" :type="DataGranularityMap.group" @change="changeTreeValue($event, DataGranularityMap.group)"></tree-select>
          </template>
          <!-- 实体 -->
          <template v-else-if="form.type === 2">
            <el-select v-model="device.storage" placeholder="请选择存储组" @change="changeStorageInDevice">
              <el-option v-for="item in storageGroupOption" :key="item.groupName" :label="item.groupName" :value="item.groupName"> </el-option>
            </el-select>
            <tree-select :checkedKeys="device.device" :data="deviceTreeOption" :type="DataGranularityMap.device" @change="changeTreeValue($event, DataGranularityMap.device)"></tree-select>
          </template>
          <!-- 物理量 -->
          <template v-else>
            <el-select v-model="time.storage" placeholder="请选择存储组" @change="changeStorageInTime">
              <el-option v-for="item in storageGroupOption" :key="item.groupName" :label="item.groupName" :value="item.groupName"> </el-option>
            </el-select>
            <el-select v-model="time.device" placeholder="请选择实体" @change="changeDeviceInTime">
              <el-option v-for="item in deviceOption" :key="item" :label="item" :value="item"> </el-option>
            </el-select>
            <el-select v-model="time.time" placeholder="请选择物理量" multiple @change="changeTime">
              <el-option v-for="item in timeSeriesOption" :key="item.id" :label="item.name" :value="item.id"> </el-option>
            </el-select>
          </template>
        </el-form-item>
        <el-form-item :props="path" :label="$t('sourcePage.selectPermissions')">
          <el-checkbox-group v-model="form.privileges">
            <el-checkbox v-for="item in dataPrivileges[form.type]" :key="item.id" :label="item.id" :value="item.id">{{ item.label }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCancel">{{ $t('common.cancel') }}</el-button>
          <el-button type="primary" @click="handleSubmit">{{ $t('common.submit') }}</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { onMounted, reactive, ref, watch, computed, toRefs } from 'vue';
import { useI18n } from 'vue-i18n';
import TreeSelect from '@/components/TreeSelect';
import api from '../api/index';
// import axios from '@/util/axios.js';
// import { useStore } from 'vuex';
import { useRoute } from 'vue-router';
import { DataGranularityMap as dataMap } from '@/util/constant';

export default {
  name: 'PermitDialog',
  setup(props, { emit }) {
    const { t, locale } = useI18n();
    let checkList = ref([]);
    let visible = ref(false);
    let dialogType = ref({});
    let originType = ref({});
    let oldForm = ref({});
    let formRef = ref(null);
    // 数据粒度 0数据连接 1存储组 2实体 3物理量
    let granularityValue = ref(null);
    // 表单数据
    let form = reactive({
      type: 0, //数据粒度
      path: [], //树形
      privileges: [], //权限
      groupPaths: [],
      devicePaths: [],
      timeseriesPaths: [],
      dataGroupValue: [],
      deviceValue: [],
    });

    let storage = ref([]);
    let device = reactive({
      storage: '',
      device: [],
    });
    let time = reactive({
      storage: '',
      device: '',
      time: '',
    });

    let groups = ref();

    let options = reactive({
      // 存储组树形
      storageGroupTreeOption: [],
      // 存储组平铺
      storageGroupOption: [],
      // 实体树形
      deviceTreeOption: [],
      // 实体平铺
      deviceOption: [],
      // 物理量平铺
      timeSeriesOption: [],
    });

    let serverId = useRoute().params.serverid;

    // let DataGranularityMap = computed(() => {
    //   return dataMap;
    // });
    let DataGranularityMap = reactive(dataMap);

    let dataPrivileges = ref({
      0: [
        { id: 'SET_STORAGE_GROUP', label: t('sourcePage.createGroup') },
        {
          id: 'CREATE_TIMESERIES',
          label: t('sourcePage.createTimeSeries'),
        },
        {
          id: 'INSERT_TIMESERIES',
          label: t('sourcePage.insertTimeSeries'),
        },
        { id: 'READ_TIMESERIES', label: t('sourcePage.readTimeSeries') },
        {
          id: 'DELETE_TIMESERIES',
          label: t('sourcePage.deleteTimeSeries'),
        },
      ],
      1: [
        { id: 'SET_STORAGE_GROUP', label: t('sourcePage.createGroup') },
        {
          id: 'CREATE_TIMESERIES',
          label: t('sourcePage.createTimeSeries'),
        },
        {
          id: 'INSERT_TIMESERIES',
          label: t('sourcePage.insertTimeSeries'),
        },
        { id: 'READ_TIMESERIES', label: t('sourcePage.readTimeSeries') },
        {
          id: 'DELETE_TIMESERIES',
          label: t('sourcePage.deleteTimeSeries'),
        },
      ],
      2: [
        {
          id: 'CREATE_TIMESERIES',
          label: t('sourcePage.createTimeSeries'),
        },
        {
          id: 'INSERT_TIMESERIES',
          label: t('sourcePage.insertTimeSeries'),
        },
        { id: 'READ_TIMESERIES', label: t('sourcePage.readTimeSeries') },
        {
          id: 'DELETE_TIMESERIES',
          label: t('sourcePage.deleteTimeSeries'),
        },
      ],
      3: [
        {
          id: 'INSERT_TIMESERIES',
          label: t('sourcePage.insertTimeSeries'),
        },
        { id: 'READ_TIMESERIES', label: t('sourcePage.readTimeSeries') },
        {
          id: 'DELETE_TIMESERIES',
          label: t('sourcePage.deleteTimeSeries'),
        },
      ],
    });

    let pathList = ref([
      { label: t('sourcePage.selectAlias'), value: 0 },
      { label: t('sourcePage.selectGroup'), value: 1 },
      { label: t('sourcePage.selectDevice'), value: 2 },
      { label: t('sourcePage.selectTime'), value: 3 },
    ]);
    let pathMap = ref({
      0: t('sourcePage.selectAlias'),
      1: t('sourcePage.selectGroup'),
      2: t('sourcePage.selectDevice'),
      3: t('sourcePage.selectTime'),
    });
    let rules = computed(() => {
      return {};
    });
    watch(locale, () => {
      dataPrivileges.value = {
        0: [
          { id: 'SET_STORAGE_GROUP', label: t('sourcePage.createGroup') },
          {
            id: 'CREATE_TIMESERIES',
            label: t('sourcePage.createTimeSeries'),
          },
          {
            id: 'INSERT_TIMESERIES',
            label: t('sourcePage.insertTimeSeries'),
          },
          { id: 'READ_TIMESERIES', label: t('sourcePage.readTimeSeries') },
          {
            id: 'DELETE_TIMESERIES',
            label: t('sourcePage.deleteTimeSeries'),
          },
        ],
        1: [
          { id: 'SET_STORAGE_GROUP', label: t('sourcePage.createGroup') },
          {
            id: 'CREATE_TIMESERIES',
            label: t('sourcePage.createTimeSeries'),
          },
          {
            id: 'INSERT_TIMESERIES',
            label: t('sourcePage.insertTimeSeries'),
          },
          { id: 'READ_TIMESERIES', label: t('sourcePage.readTimeSeries') },
          {
            id: 'DELETE_TIMESERIES',
            label: t('sourcePage.deleteTimeSeries'),
          },
        ],
        2: [
          {
            id: 'CREATE_TIMESERIES',
            label: t('sourcePage.createTimeSeries'),
          },
          {
            id: 'INSERT_TIMESERIES',
            label: t('sourcePage.insertTimeSeries'),
          },
          { id: 'READ_TIMESERIES', label: t('sourcePage.readTimeSeries') },
          {
            id: 'DELETE_TIMESERIES',
            label: t('sourcePage.deleteTimeSeries'),
          },
        ],
        3: [
          {
            id: 'INSERT_TIMESERIES',
            label: t('sourcePage.insertTimeSeries'),
          },
          { id: 'READ_TIMESERIES', label: t('sourcePage.readTimeSeries') },
          {
            id: 'DELETE_TIMESERIES',
            label: t('sourcePage.deleteTimeSeries'),
          },
        ],
      };
      pathList.value = [
        { label: t('sourcePage.selectAlias'), value: 0 },
        { label: t('sourcePage.selectGroup'), value: 1 },
        { label: t('sourcePage.selectDevice'), value: 2 },
        { label: t('sourcePage.selectTime'), value: 3 },
      ];
      pathMap.value = { 0: t('sourcePage.selectAlias'), 1: t('sourcePage.selectGroup'), 2: t('sourcePage.selectDevice'), 3: t('sourcePage.selectTime') };
    });
    let showPermitDialogs = ref(false);
    // type 弹出框类型
    // data 编辑回显数据
    // origin user or role
    const open = ({ type, origin, data } = {}) => {
      dialogType.value = type;
      originType.value = origin;
      oldForm.value = data;
      visible.value = true;
      form.type = 0;
      form.privileges = [];
    };
    // 改变存储组/实体的树形
    const changeTreeValue = (data, type) => {
      if (type === DataGranularityMap.group) {
        storage.value = data;
      } else {
        device.device = data;
      }
    };
    // 数据粒度为实体的时候, 改变存储组
    const changeStorageInDevice = (groupName) => {
      device.storage = groupName;
      getDeviceTree({ serverId, groupName });
    };

    // 数据粒度为物理量的时候, 改变存储组
    const changeStorageInTime = (groupName) => {
      time.storage = groupName;
      getDevice({ serverId, groupName });
    };

    // 数据粒度为物理量的时候, 改变实体
    const changeDeviceInTime = (deviceName) => {
      time.device = deviceName;
      getTimeseries({ serverId, deviceName, groupName: time.storage });
    };

    // 数据粒度为物理量的时候, 改变物理量, 处理全选
    const changeTime = (val) => {
      if (val.includes(null) || (val.length === options.timeSeriesOption.length - 1 && !val.includes(null))) {
        time.time = [null];
      }
    };

    const changeRadio = async (radio) => {
      let value = Number(radio);
      form.privileges = [];
      // 存储组
      if (value === 1) {
        getStorageGroupTree();
      } else if (value === 2) {
        getStorageGroup();
      } else if (value === 3) {
        getStorageGroup();
      }
    };
    // 获取存储组树形
    const getStorageGroupTree = async () => {
      options.storageGroupTreeOption = (await api.getStorageGroupTree({ serverId })).data;
    };
    // 获取存储组平铺
    const getStorageGroup = async () => {
      options.storageGroupOption = (await api.getStorageGroup({ serverId })).data;
    };
    // 获取实体树形
    const getDeviceTree = async ({ serverId, groupName }) => {
      options.deviceTreeOption = (await api.getDeviceTreeByGroup({ serverId, groupName })).data;
    };
    // 获取实体平铺
    const getDevice = async ({ serverId, groupName }) => {
      options.deviceOption = (await api.getDeviceByGroup({ serverId, groupName })).data;
    };
    // 获取物理量平铺
    const getTimeseries = async ({ serverId, groupName, deviceName }) => {
      options.timeSeriesOption = (await api.getTimeseries({ serverId, groupName, deviceName })).data.map((timeSeries) => ({ id: timeSeries, name: timeSeries }));
      options.timeSeriesOption.unshift({ id: null, name: '全部物理量' });
      console.log(options.timeSeriesOption);
    };
    const handleClose = () => {
      visible.value = false;
    };
    const handleCancel = () => {
      visible.value = false;
    };
    const handleSubmit = () => {
      let { type, privileges } = form;
      let range = [];
      if (type === 0) {
        range = [];
      } else if (type === 1) {
        range = storage.value;
      } else if (type === 2) {
        range = { ...device };
        if (range.device.includes(null)) {
          range.device = options.deviceOption.filter((d) => d.id !== null).map((i) => i.name);
        }
      } else if (type === 3) {
        range = { ...time };
        if (range.time.includes(null)) {
          range.time = options.timeSeriesOption.filter((d) => d.id !== null).map((i) => i.name);
        }
      }
      emit('submit', { type, range, privileges, dialogType: dialogType.value });
    };
    onMounted(() => {
      console.log(dataPrivileges.value);
      showPermitDialogs.value = props.showPermitDialog;
    });
    return {
      t,
      checkList,
      locale,
      visible,
      dialogType,
      handleClose,
      handleSubmit,
      handleCancel,
      dataPrivileges,
      pathList,
      pathMap,
      formRef,
      form,
      granularityValue,
      rules,
      groups,
      ...toRefs(options),
      open,
      origin,
      serverId,
      changeRadio,
      DataGranularityMap,
      changeTreeValue,
      storage,
      time,
      device,
      changeStorageInDevice,
      changeStorageInTime,
      changeDeviceInTime,
      changeTime,
    };
  },
  components: { TreeSelect },
};
</script>

<style scoped lang="scss">
.mains-contain {
  width: 100%;
  height: 100%;
  overflow: auto;
  &:deep(.el-select) {
    width: 100%;
  }
}
</style>
