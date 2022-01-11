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
  <div>
    <div class="serch_div">
      <el-select v-model="groupName" :placeholder="$t('device.selectdataconnection')" class="elinput selectIcon">
        <el-option v-for="item in data.list" :key="item.value" :label="item.label" :value="item.value" @click="getdevicel"> </el-option>
      </el-select>
      <el-select v-model="deviceName" :placeholder="$t('device.selectp')" class="elinput selectIcon">
        <el-option v-for="item in devicelist.list" :key="item.value" :label="item.label" :value="item.value" @click="getpylist(item)"> </el-option>
      </el-select>
      <el-input v-model="filterText" :placeholder="$t('device.pleaseinput')" class="elinput inputIcon" suffix-icon="el-icon-search" @input="serchpylist"></el-input>
    </div>
    <div class="serch_div maxheight">
      <span class="custom-tree-node chil" v-for="(item, index) in pyData.list" :key="item.value" @dblclick="getFunction(item)" :style="{ color: index === 0 ? '#c7c6c6' : 'black' }">
        <el-tooltip content="item.label" placement="top">
          <span>{{ item.label }}</span>
        </el-tooltip>
        <span>{{ item.decr || '——' }}</span>
        <span>{{ item.type }}</span>
      </span>
    </div>
  </div>
</template>

<script>
import { ElInput, ElSelect, ElOption } from 'element-plus';
import { ref, reactive } from 'vue';
import { useStore } from 'vuex';
import { getCList, getDevice } from '../api/index';
import { useI18n } from 'vue-i18n';
export default {
  props: {
    placeholder: String,
    treeList: Object,
    id: Number,
  },
  setup(props, { emit }) {
    const { t } = useI18n();
    const userInfo = useStore();
    const data = reactive(props.treeList);
    let filterText = ref(null);
    let groupName = ref('');
    let deviceName = ref('');
    const tree = ref(null);
    const devicelist = reactive({
      list: [],
    });
    const pyCopyData = reactive({
      list: [],
    });
    const pyData = reactive({
      list: [],
    });
    const defaultProps = {
      children: 'children',
      label: 'label',
    };
    function append(val) {
      console.log(val);
    }
    function getFunction(val) {
      console.log(userInfo.state.userInfo.name);
      emit('getFunction', val.label);
    }
    function getdevicel() {
      deviceName.value = '';
      getDevice(props.id, groupName.value).then((res) => {
        const datas = res.data.map((item) => {
          return {
            label: item,
            value: item,
            parentid: groupName.value,
          };
        });
        devicelist.list = datas;
      });
    }
    function getpylist(deviceData) {
      getCList(props.id, deviceData.parentid, deviceData.value, { pageSize: 99999, pageNum: 1 }).then((res) => {
        const data = res.data.measurementVOList.map((item) => {
          return {
            parents: deviceData.parentid,
            parent: deviceData.value,
            label: item.timeseries,
            type: item.dataType,
            decr: item.description,
          };
        });
        data.unshift({
          label: t('device.measurement'),
          type: t('sqlserch.type'),
          decr: t('sqlserch.description'),
        });
        pyData.list = data;
        pyCopyData.list = JSON.parse(JSON.stringify(data));
        console.log(data);
      });
    }
    function serchpylist() {
      if (!filterText.value) {
        pyData.list = JSON.parse(JSON.stringify(pyCopyData.list));
      } else {
        pyData.list = pyData.list.filter((item) => {
          return item.label.indexOf(filterText.value) !== -1;
        });
        pyData.list.unshift({
          label: t('device.measurement'),
          type: t('sqlserch.type'),
          decr: t('sqlserch.description'),
        });
      }
    }
    return { t, data, defaultProps, filterText, tree, append, serchpylist, getFunction, groupName, getdevicel, getpylist, devicelist, deviceName, pyData };
  },
  components: {
    ElInput,
    ElSelect,
    ElOption,
  },
};
</script>

<style lang="scss" scoped>
.serch_div {
  padding: 10px 20px 10px 20px;
  background: #fff;
  &.maxheight {
    height: 65vh;
    overflow: auto;
  }
}
.elinput {
  width: 100%;
  height: 30px;
  padding: 3px 0;
}
</style>
<style lang="scss">
.selectIcon {
  .el-input__suffix {
    top: -4px;
  }
}
.inputIcon {
  .el-input__suffix {
    top: 3px;
  }
}
.serch_div .el-tree-node__content {
  height: 35px !important;
}
.elinput {
  .el-input__inner {
    height: 30px;
    line-height: 30px;
    margin-top: 5px;
  }
  .el-input__icon {
    line-height: 30px;
  }
}
.custom-tree-node.chil {
  display: flex;
  font-size: 12px;
  justify-content: space-between;
  border-bottom: 1px solid #ebeef5;
  padding: 10px 0;
  cursor: pointer;
  span {
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>
