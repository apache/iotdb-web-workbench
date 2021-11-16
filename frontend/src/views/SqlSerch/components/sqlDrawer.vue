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
  <div class="drawer">
    <el-dialog :title="$t('device.newquery')" v-model="centerDialogVisible" width="30%" @close="centerDialogV">
      <el-form ref="form" label-width="75px">
        <el-form-item :label="$t('device.dataconnection')" :rules="{ required: true, message: $t('device.selectdataconnections'), trigger: 'blur' }">
          <!-- <span>{{ $t('device.dataconnection') }}</span> -->
          <el-select v-model="linkData" :placeholder="$t('device.selectdataconnections')">
            <el-option v-for="item in linkList.list" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="centerDialogV">{{ $t('device.cencel') }}</el-button>
          <el-button type="primary" @click="centerDialog">{{ $t('device.ok') }}</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ElDialog, ElButton, ElForm, ElSelect, ElOption, ElFormItem, ElMessage } from 'element-plus';
import { onMounted, reactive, ref } from 'vue';
import { useStore } from 'vuex';
import { useI18n } from 'vue-i18n';
import { getlink } from '../api/index';
export default {
  name: 'sqldrawer',
  props: ['func'],
  setup(props, { emit }) {
    const { t } = useI18n();
    const uerInfo = useStore();
    const linkList = reactive({
      list: [],
    });
    const linkData = ref(null);
    function centerDialog() {
      if (linkData.value) {
        props.func.updateTree([`${linkData.value}connection`, `${linkData.value}connection:querylist`], true);
        props.func.addTab(`${linkData.value}connection:querylist:newquery`, {}, true);
        emit('coloseDrawer');
      } else {
        ElMessage.error(`${t('device.selectdataconnection')}!`);
      }
    }
    function centerDialogV() {
      emit('coloseDrawer');
    }
    onMounted(() => {
      getlink({ userId: uerInfo.state.userInfo.userId }).then((res) => {
        linkList.list = res.data.aliasList.map((item) => {
          return {
            label: item.alias,
            value: item.id,
          };
        });
      });
    });
    const centerDialogVisible = ref(true);
    return { centerDialogVisible, linkList, linkData, centerDialog, centerDialogV };
  },
  components: {
    ElDialog,
    ElButton,
    ElForm,
    ElSelect,
    ElOption,
    ElFormItem,
  },
};
</script>

<style lang="scss" scoped>
.drawer {
  .el-dialog__header {
    border-bottom: 1px solid #efefef;
  }
}
:deep(.el-form) {
  .el-form-item {
    display: flex;
    width: 100%;
    margin-bottom: 0;
  }
  .el-select {
    width: 100%;
  }
  .el-input__suffix {
    top: -2px;
  }
}
</style>
