<!-- 授权用户弹窗 -->
<template>
  <el-dialog title="授权用户" width="520px" :append-to-body="true" v-model="visible" custom-class="grant-dialog">
    <div class="dialog-content">
      <div class="left">
        <el-checkbox v-model="allCheck" :indeterminate="isIndeterminate" @change="handleCheckAllChange">用户列表({{ userList.length }})</el-checkbox>
        <el-checkbox-group v-model="checkedUser" @change="handleCheckedUser">
          <el-checkbox v-for="user in userList" :key="user" :label="user">
            {{ user }}
          </el-checkbox>
        </el-checkbox-group>
      </div>
      <div class="divider"></div>
      <div class="right">
        <div class="right-title">
          <span>已选用户({{ checkedUser.length }})</span><span class="clear">清空</span>
        </div>
        <div class="right-item" v-for="user in checkedUser" :key="user">
          {{ user }}
          <span class="delete" @click="deleteUser(user)">删除</span>
        </div>
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
import { ref } from 'vue';
export default {
  name: 'DialogGrantUser',
  props: {
    userList: {
      type: Array,
      default: () => [],
    },
  },
  setup(props, {emit}) {
    let visible = ref(false);
    let allCheck = ref(false);
    let isIndeterminate = ref(false);
    let checkedUser = ref([]);

    let handleCheckAllChange = (val) => {
      checkedUser.value = val ? props.userList : [];
      isIndeterminate.value = false;
    };
    let handleCheckedUser = (value) => {
      const checkedCount = value.length;
      allCheck.value = checkedCount === props.userList.length;
      isIndeterminate.value = checkedCount > 0 && checkedCount < props.userList.length;
    };

    let deleteUser = (user) => {
      checkedUser.value = checkedUser.value.filter((d) => d !== user);
      isIndeterminate.value = checkedUser.value.length > 0 && checkedUser.value.length < props.userList.length;
    };

    let open = ({ type, data } = {}) => {
      visible.value = true;
      console.log(type, data);
    };
    let submit = () => {
      emit('change', {userList:checkedUser});
      visible.value = false;
    }
    return { visible, open, allCheck, isIndeterminate, handleCheckAllChange, checkedUser, deleteUser, handleCheckedUser ,submit};
  },
};
</script>
<style scoped lang="scss"></style>
<style lang="scss">
.grant-dialog {
  .el-dialog__body {
    .dialog-content {
      display: flex;

      .el-checkbox {
        display: block;
      }
      .left {
        width: calc(50% - 1px);
      }
      .divider {
        width: 1px;
        height: 405px;
        margin: -10px 0;
        background-color: $border-color;
      }
      .right {
        width: 50%;
        box-sizing: border-box;
        padding-left: 20px;
        &-item {
          display: flex;
          justify-content: space-between;
        }
        &-title {
          display: flex;
          justify-content: space-between;
          color: #808ba3;
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
