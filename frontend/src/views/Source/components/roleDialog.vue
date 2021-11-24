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
  <div class="role-dialog-contain">
    <el-dialog v-model="showRoleDialogs" :title="$t('sourcePage.roleTitle')" width="520px" :before-close="handleClose">
      <div class="dialog-content">
        <div class="left-part">
          <div class="check-title">
            <el-checkbox v-model="allChecked" @change="checkAllRole()" :label="$t('sourcePage.roleList') + '(' + (roleList.length || 0) + ')'"></el-checkbox>
          </div>
          <div>
            <el-checkbox-group v-model="checkList">
              <el-checkbox v-for="item in roleList" :label="item" :key="item" />
            </el-checkbox-group>
          </div>
        </div>
        <div class="right-part">
          <div class="check-title clearfix">
            <span>{{ $t('sourcePage.checkedList') }}</span>
            <el-button class="btn" type="text" size="small" @click="clearAll()">{{ $t('common.clear') }}</el-button>
          </div>
          <div>
            <ul>
              <li v-for="item in checkList" :key="item" class="check-item clearfix">
                <span>{{ item }}</span>
                <el-button class="btn el-button-delete" type="text" size="small" @click="deleteOneChecked(item)">{{ $t('common.delete') }}</el-button>
              </li>
            </ul>
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCancel()">{{ $t('common.cancel') }}</el-button>
          <el-button type="primary" @click="handleSubmit()">{{ $t('common.save') }}</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
// @ is an alias to /src
import { onMounted, ref, watch, onActivated } from 'vue';
import { ElDialog } from 'element-plus';
import { useI18n } from 'vue-i18n';
// import axios from '@/util/axios.js';
// import { useStore } from 'vuex';
// import { useRouter, useRoute } from 'vue-router';
import api from '../api/index';

export default {
  name: 'RoleDialog',
  props: {
    showRoleDialog: {
      type: Boolean,
      default: () => {},
    },
    serverId: {
      type: String,
      default: () => {},
    },
    type: {
      type: Number,
      default: () => {},
    },
    editList: {
      type: Object,
      default: () => {},
    },
  },
  setup(props, { emit }) {
    const { t } = useI18n();
    // const router = useRouter();
    // const route = useRoute();
    let showRoleDialogs = ref(false);
    let checkList = ref([]);
    let roleList = ref([]);
    let allChecked = ref(false);

    watch(
      () => props.showRoleDialog,
      (val) => {
        showRoleDialogs.value = val;
        if (props.type == 0) {
          checkList.value = JSON.parse(JSON.stringify(props.editList || []));
        }
      },
      { immediate: true }
    );
    const handleClose = () => {
      showRoleDialogs.value = false;
      emit('cancelRoleDialog');
    };
    const handleCancel = () => {
      showRoleDialogs.value = false;
      emit('cancelRoleDialog');
    };
    const handleSubmit = () => {
      emit('submitRoleDialog', checkList.value);
      showRoleDialogs.value = false;
    };

    const getRoleList = async () => {
      let result = await api.getRoles(props.serverId);
      roleList.value = result.data;
      if (checkList.value.length == roleList.value.length && checkList.value.length > 0) {
        allChecked.value = true;
      } else {
        allChecked.value = false;
      }
    };
    const checkAllRole = () => {
      if (allChecked.value) {
        let temp = [];
        for (let i = 0; i < roleList.value.length; i++) {
          temp.push(roleList.value[i]);
        }
        checkList.value = temp;
      } else {
        checkList.value = [];
      }
    };
    const clearAll = () => {
      checkList.value = [];
      allChecked.value = false;
    };
    const deleteOneChecked = (item) => {
      let temp = checkList.value;
      let index = checkList.value.indexOf(item);
      temp.splice(index, 1);
      checkList.value = temp;
      if (checkList.value.length == 0) {
        allChecked.value = false;
      }
    };
    onMounted(() => {
      showRoleDialogs.value = props.showRoleDialog;
      getRoleList();
      if (props.type == 1) {
        checkList.value = props.editList || [];
      }
    });
    onActivated(() => {});
    return {
      t,
      showRoleDialogs,
      handleClose,
      handleSubmit,
      handleCancel,
      checkAllRole,
      deleteOneChecked,
      clearAll,
      allChecked,
      checkList,
      roleList,
    };
  },
  components: {
    ElDialog,
  },
};
</script>

<style scoped lang="scss">
.role-dialog-contain {
  width: 100%;
  height: 100%;
  overflow: auto;
  .dialog-content {
    display: flex;
    height: 40vh;

    div {
      flex: 1;
    }
    .check-title {
      color: #808ba3ff;
      height: 32px;
      line-height: 32px;
      &:deep(.el-checkbox__label) {
        color: #808ba3ff;
      }
    }
    .left-part {
      border-right: 1px solid #eef0f5;
      overflow: auto;
      .el-checkbox {
        display: block;
        height: 32px;
        line-height: 32px;
      }
    }
    .right-part {
      padding: 0 0 0 20px;
      overflow: auto;
      .btn {
        float: right;
        margin-top: 1px;
        padding-right: 0 !important;
      }
      ul {
        li {
          height: 32px;
          line-height: 32px;
        }
      }
    }
  }
}
</style>
