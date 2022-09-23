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
<!-- Authorized user Popup -->
<template>
  <el-dialog :title="$t('sourcePage.grantUser')" width="520px" :append-to-body="true" v-model="visible" custom-class="grant-dialog">
    <div class="dialog-content">
      <div class="left">
        <el-checkbox v-model="allCheck" :indeterminate="isIndeterminate" @change="handleCheckAllChange"
          ><span class="all-checkbox">{{ $t('sourcePage.userList') }} ({{ userList.length }})</span></el-checkbox
        >
        <el-checkbox-group v-model="checkedUser" @change="handleCheckedUser">
          <el-checkbox v-for="user in pagiUsers" :key="user" :label="user">
            {{ user }}
          </el-checkbox>
        </el-checkbox-group>
        <el-pagination
          layout="prev, pager, next"
          v-model:currentPage="pagination.currPage"
          :page-size="pagination.pageSize"
          :total="userList.length"
          @current-change="handleCurrentChange"
        ></el-pagination>
      </div>
      <div class="divider"></div>
      <div class="right">
        <div class="right-title">
          <span>{{ $t('sourcePage.selectedUser') }} ({{ checkedUser.length }})</span><span class="clear" @click="handleClear">{{ $t('common.clear') }}</span>
        </div>
        <div class="right-item" v-for="user in checkedPagiUsers" :key="user">
          {{ user }}
          <span class="delete" @click="deleteUser(user)">{{ $t('common.delete') }}</span>
        </div>
        <el-pagination
          layout="prev, pager, next"
          v-model:currentPage="checkedPagination.currPage"
          :page-size="checkedPagination.pageSize"
          :total="checkedUser.length"
          @current-change="handleCurrentSlectedChange"
        ></el-pagination>
      </div>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" @click="submit">{{ $t('common.submit') }}</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script>
import { ref, computed } from 'vue';
export default {
  name: 'DialogGrantUser',
  props: {
    userList: {
      type: Array,
      default: () => [],
    },
  },
  setup(props, { emit }) {
    let visible = ref(false);
    let allCheck = ref(false);
    let isIndeterminate = ref(false);
    let checkedUser = ref([]);
    let pagination = ref({
      currPage: 1,
      pageSize: 10,
    });
    let checkedPagination = ref({
      currPage: 1,
      pageSize: 10,
    });

    let handleCheckAllChange = (val) => {
      checkedUser.value = val ? props.userList : [];
      isIndeterminate.value = false;
    };
    let handleCheckedUser = () => {
      resetAllChecked();
    };

    let deleteUser = (user) => {
      checkedUser.value = checkedUser.value.filter((d) => d !== user);
      resetAllChecked();
    };

    let open = (users) => {
      visible.value = true;
      checkedUser.value = users ? [...users] : [];
      resetAllChecked();
    };
    let resetAllChecked = () => {
      const checkedCount = checkedUser.value.length;
      isIndeterminate.value = checkedUser.value.length > 0 && checkedUser.value.length < props.userList.length;
      allCheck.value = checkedCount === props.userList.length;
    };

    let handleCurrentChange = (currPage) => {
      pagination.value.currPage = currPage;
    };
    let handleCurrentSlectedChange = (currPage) => {
      checkedPagination.value.currPage = currPage;
    };

    let pagiUsers = computed(() => {
      let { pageSize, currPage } = pagination.value;
      return [...props.userList].splice((currPage - 1) * pageSize, pageSize);
    });
    let checkedPagiUsers = computed(() => {
      let { pageSize, currPage } = checkedPagination.value;
      return [...checkedUser.value].splice((currPage - 1) * pageSize, pageSize);
    });

    let handleClear = () => {
      checkedUser.value = [];
      resetAllChecked();
    };
    let submit = () => {
      emit('change', { userList: checkedUser });
      visible.value = false;
    };
    return {
      checkedPagination,
      checkedPagiUsers,
      handleCurrentSlectedChange,
      handleCurrentChange,
      pagination,
      pagiUsers,
      visible,
      open,
      allCheck,
      isIndeterminate,
      handleCheckAllChange,
      checkedUser,
      deleteUser,
      handleCheckedUser,
      submit,
      handleClear,
    };
  },
};
</script>
<style lang="scss">
.grant-dialog {
  .el-dialog__body {
    position: relative;

    .el-pagination {
      position: absolute;
      bottom: 10px;
    }
    .dialog-content {
      display: flex;

      .el-checkbox {
        display: block;
        margin: 15px 0;
        line-height: 20px;
        font-size: 12px;
        .el-checkbox__label {
          font-size: 12px;
        }

        &:first-child {
          margin-top: 0;
          margin-bottom: 18px;
        }
      }
      .left {
        width: calc(50% - 1px);
        font-size: 12px;
        .all-checkbox {
          color: #808ba3;
        }
      }
      .divider {
        width: 1px;
        height: 445px;
        margin: -10px 0;
        background-color: $border-color;
      }
      .right {
        width: 50%;
        box-sizing: border-box;
        padding-left: 20px;
        font-size: 12px;
        &-item {
          display: flex;
          justify-content: space-between;
          line-height: 20px;
          margin: 15px 0;
        }
        &-title {
          display: flex;
          justify-content: space-between;
          color: #808ba3;
          margin-bottom: 18px;
          line-height: 20px;
          .clear {
            color: $theme-color;
            cursor: pointer;
          }
        }
        .delete {
          color: $danger-color;
          cursor: pointer;
        }
      }
    }
  }
}
</style>
