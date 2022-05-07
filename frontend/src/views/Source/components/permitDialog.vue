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
  <div id="mains" class="mains-contain">
    <el-dialog v-model="visible" :title="dialogType === 'add' ? $t('sourcePage.addPermission') : $t('sourcePage.editPermission')" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="permit-form">
        <el-form-item prop="type" :label="language.path">
          <el-radio-group v-model="form.type" @change="changeRadio">
            <el-radio v-for="item in pathList" :disabled="dialogType === 'edit'" :key="item.value" :label="item.value">{{ item.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item prop="path" :label="language.range">
          <!-- data connection -->
          <template v-if="form.type === 0"> -- </template>
          <!-- Storage group -->
          <template v-else-if="form.type === 1">
            <tree-select :checked-keys="storage" :data="storageGroupTreeOption" :type="DataGranularityMap.group" @change="changeTreeValue($event, DataGranularityMap.group)"></tree-select>
          </template>
          <!-- Entity -->
          <template v-else-if="form.type === 2">
            <el-select v-model="device.storage" :placeholder="$t('sourcePage.selectdataconnection')" @change="changeStorageInDevice">
              <el-option v-for="item in storageGroupOption" :key="item.groupName" :label="item.groupName" :value="item.groupName"> </el-option>
            </el-select>
            <tree-select :checked-keys="device.device" :data="deviceTreeOption" :type="DataGranularityMap.device" @change="changeTreeValue($event, DataGranularityMap.device)"></tree-select>
          </template>
          <!-- Measurement  -->
          <template v-else>
            <el-select v-model="time.storage" :placeholder="$t('sourcePage.selectdataconnection')" @change="changeStorageInTime">
              <el-option v-for="item in storageGroupOption" :key="item.groupName" :label="item.groupName" :value="item.groupName"> </el-option>
            </el-select>
            <el-select v-model="time.device" :placeholder="$t('sourcePage.selectEntity')" @change="changeDeviceInTime">
              <el-option v-for="item in deviceOption" :key="item" :label="item" :value="item"> </el-option>
            </el-select>
            <el-select v-model="time.time" :placeholder="$t('sourcePage.selectTimeseries')" multiple @change="changeTime">
              <el-option v-for="item in timeSeriesOption" :key="item.id" :label="item.name" :value="item.id"> </el-option>
            </el-select>
          </template>
        </el-form-item>
        <el-form-item prop="privileges" :label="language.selectPermissions">
          <el-checkbox-group v-model="form.privileges">
            <el-checkbox v-for="item in dataPrivileges[form.type]" :key="item.id" :label="item.id" :value="item.id">{{ item.label }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCancel">{{ language.cancel }}</el-button>
          <el-button type="primary" @click="handleSubmit">{{ language.submit }}</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { reactive, ref, watch, toRefs, nextTick, computed } from 'vue';
import { useI18n } from 'vue-i18n';
import TreeSelect from '@/components/TreeSelect';
import api from '../api/index';
// import axios from '@/util/axios.js';
// import { useStore } from 'vuex';
import { useRoute } from 'vue-router';
import { DataGranularityMap as dataMap } from '@/util/constant';
import { ElMessage } from 'element-plus';

export default {
  name: 'PermitDialog',
  props: {
    dialogOrigin: {
      type: String,
      default: 'user',
    },
    name: {
      type: String,
      default: 'user',
    },
  },
  setup(props, { emit }) {
    const { t, locale } = useI18n();
    let checkList = ref([]);
    let visible = ref(false);
    let dialogType = ref({});
    let formRef = ref(null);
    // Data granularity 0: data connection 1: storage group 2: entity 3: Measurement
    let granularityValue = ref(null);
    let form = reactive({
      type: 0, //Data granularity
      path: [], //tree
      privileges: [],
    });
    let language = computed(() => {
      return {
        cancel: t('common.cancel'),
        submit: t('common.submit'),
        path: t('sourcePage.path'),
        range: t('sourcePage.range'),
        selectPermissions: t('sourcePage.selectPermissions'),
      };
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
    let oldValue = ref({});

    let options = reactive({
      // Storage group tree
      storageGroupTreeOption: [],
      // Storage group list
      storageGroupOption: [],
      // Entity tree
      deviceTreeOption: [],
      // Entity list
      deviceOption: [],
      // Measurement list
      timeSeriesOption: [],
    });

    let serverId = useRoute().params.serverid;

    let DataGranularityMap = reactive(dataMap);

    let methodMap = computed(() => {
      return {
        user: api.editUserDataPrivilege,
        role: api.editDataPrivilege,
      }[props.dialogOrigin];
    });
    let paramMap = computed(() => {
      return {
        user: 'userName',
        role: 'roleName',
      }[props.dialogOrigin];
    });

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

    const validatePath = (rule, value, callback) => {
      if (form.type === 1 && !storage.value.length) {
        callback(new Error(t('sourcePage.selectRange')));
      } else if (form.type === 2 && (!device.storage || !device.device.length)) {
        callback(new Error(t('sourcePage.selectRange')));
      } else if (form.type === 3 && (!time.storage || !time.device || !time.time.length)) {
        callback(new Error(t('sourcePage.selectRange')));
      } else {
        callback();
      }
    };
    const validatePrivileges = (rule, value, callback) => {
      if (!value.length) {
        callback(new Error(t('sourcePage.selectPermission')));
      }
      callback();
    };
    let rules = ref({
      type: [
        {
          required: true,
        },
      ],
      path: [
        {
          required: true,
          validator: validatePath,
          trigger: 'change',
        },
      ],
      privileges: [
        {
          required: true,
          validator: validatePrivileges,
          trigger: 'change',
        },
      ],
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
    // type : edit or add
    const open = async ({ type, data } = {}) => {
      dialogType.value = type;
      oldValue.value = { ...data };
      visible.value = true;
      nextTick(() => {
        formRef.value.clearValidate();
      });
      let dataType = data?.type;
      if (type === 'add') {
        form.type = 0;
        form.privileges = [];
      } else {
        form.type = dataType;
        form.privileges = data.privileges;
        // Storage group
        if (dataType === 1) {
          await getStorageGroupTree();
          storage.value = [...data.groupPaths];
        } else if (dataType === 2) {
          await getStorageGroup();
          device.storage = data.groupPaths[0];
          await getDeviceTree({ serverId, groupName: device.storage });
          device.device = [...data.devicePaths];
        } else {
          if (dataType === 0) return;
          await getStorageGroup();
          time.storage = data.groupPaths[0];
          await getDevice({ serverId, groupName: time.storage });
          time.device = data.devicePaths[0];
          await getTimeseries({ serverId, groupName: time.storage, deviceName: time.device });
          time.time = [...data.timeseriesPaths];
        }
      }
    };
    // Change the tree of storage group / entity
    const changeTreeValue = (data, type) => {
      if (type === DataGranularityMap.group) {
        storage.value = data;
      } else {
        device.device = data;
      }
    };
    // When the data granularity is entity, change the storage group
    const changeStorageInDevice = (groupName) => {
      device.storage = groupName;
      getDeviceTree({ serverId, groupName });
    };

    // When the data granularity is a physical quantity, change the storage group
    const changeStorageInTime = (groupName) => {
      time.storage = groupName;
      getDevice({ serverId, groupName });
    };

    // When the data granularity is a physical quantity, the entity is changed
    const changeDeviceInTime = (deviceName) => {
      time.device = deviceName;
      getTimeseries({ serverId, deviceName, groupName: time.storage });
    };

    // When the data granularity is physical quantity, change the physical quantity and process select all
    const changeTime = (val) => {
      if (val.includes(null) || (val.length === options.timeSeriesOption.length - 1 && !val.includes(null))) {
        time.time = [null];
      }
    };

    const changeRadio = async (radio) => {
      let value = Number(radio);
      form.privileges = [];
      nextTick(() => {
        formRef.value.clearValidate();
      });
      // Storage group
      if (value === 1) {
        getStorageGroupTree();
      } else if (value === 2) {
        getStorageGroup();
      } else if (value === 3) {
        getStorageGroup();
      }
    };
    // Get storage group tree
    const getStorageGroupTree = async () => {
      options.storageGroupTreeOption = (await api.getStorageGroupTree({ serverId })).data;
    };
    // Get storage group list
    const getStorageGroup = async () => {
      options.storageGroupOption = (await api.getStorageGroup({ serverId })).data;
    };
    // Get entity tree
    const getDeviceTree = async ({ serverId, groupName }) => {
      options.deviceTreeOption = (await api.getDeviceTreeByGroup({ serverId, groupName })).data;
    };
    // Get entity list
    const getDevice = async ({ serverId, groupName }) => {
      options.deviceOption = (await api.getDeviceByGroup({ serverId, groupName })).data;
    };
    // Get Measurement  list
    const getTimeseries = async ({ serverId, groupName, deviceName }) => {
      options.timeSeriesOption = (await api.getTimeseries({ serverId, groupName, deviceName })).data.map((timeSeries) => ({ id: timeSeries, name: timeSeries }));
      options.timeSeriesOption.length && options.timeSeriesOption.unshift({ id: null, name: t('sourcePage.allMeasurement') });
    };
    const handleCancel = () => {
      visible.value = false;
    };
    const handleSubmit = async () => {
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
      await formRef.value.validate();
      let payload = { type };
      let params = {
        serverId,
      };
      params[paramMap.value] = props.name;
      // Processing authority
      let dealPivilege = handlePath('privileges', [...privileges]);
      payload.privileges = privileges;
      payload.cancelPrivileges = dealPivilege.deleteList;
      // Processing storage groups
      if (type === 1) {
        payload.groupPaths = [...range];
      }
      // Processing entity
      if (type === 2) {
        payload.groupPaths = [range.storage];
        payload.devicePaths = range.device;
      }
      // Processing physical quantity
      if (type === 3) {
        payload.groupPaths = [range.storage];
        payload.devicePaths = [range.device];
        payload.timeseriesPaths = range.time;
      }
      await methodMap.value(params, payload);
      visible.value = false;
      ElMessage.success(`${dialogType.value === 'add' ? t('sourcePage.addPrivilegeSuccess') : t('sourcePage.editPrivilegeSuccess')}`);
      emit('submit');
    };
    const handlePath = (props, List) => {
      if (!oldValue.value) return {};
      let deleteList = oldValue?.value[props]?.filter((d) => !List.includes(d));
      let addList = List.filter((d) => !oldValue?.value[props]?.includes(d));
      return {
        addList,
        deleteList,
      };
    };
    return {
      t,
      checkList,
      locale,
      visible,
      dialogType,
      handleSubmit,
      handleCancel,
      dataPrivileges,
      pathList,
      pathMap,
      formRef,
      form,
      granularityValue,
      rules,
      language,

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
    margin-bottom: 10px;
  }
}
</style>
