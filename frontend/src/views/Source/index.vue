<template>
  <div class="source-detail-container">
    <div class="info-box">
      <p class="title">{{ baseInfo.alias }}</p>
      <p class="more">
        <span>{{ $t('sourcePage.host') + ':' }}{{ baseInfo.host }}</span>
        <span>{{ $t('sourcePage.port') + ':' }}{{ baseInfo.port }}</span>
      </p>
    </div>
    <div class="permission-box">
      <div class="info-head">
        <svg class="icon" aria-hidden="true">
          <use xlink:href="#icon-xinzengshujulianjie"></use>
        </svg>
        {{ $t('sourcePage.accountPermit') }}
      </div>
      <div class="permit-content">
        <div class="left-part">
          <p class="title clearfix">
            {{ $t('sourcePage.userAccount') }}
            <el-button type="text" @click="newUser()">{{ $t('sourcePage.newAccount') }}</el-button>
          </p>
          <ul class="user-list">
            <li v-for="(item, index) in userList" :class="activeIndex == index ? 'active' : ''" :key="index" @click="handleUser(index, item)">
              <span class="content">{{ item.username }}</span>
              <svg v-if="activeIndex == index" class="icon" aria-hidden="true" @click="deleteUser()">
                <use xlink:href="#icon-se-icon-delete"></use>
              </svg>
            </li>
          </ul>
        </div>
        <div class="right-part">
          <el-tabs v-model="activeName" @tab-click="handleClick">
            <el-tab-pane :label="$t('sourcePage.baseConfig')" name="1">
              <div v-if="!isNew" class="tab-content left-base-content">
                <el-form ref="baseInfoFormRef" :model="baseInfoForm" :rules="baseRules" label-position="top" class="source-form">
                  <el-form-item :label="$t('sourcePage.userNameTitle')">
                    <div>{{ baseInfoForm.userName }}</div>
                  </el-form-item>
                  <el-form-item :label="$t('sourcePage.passwordTitle')" prop="password" class="password-form-item">
                    <el-input show-password v-if="edit" v-model="baseInfoForm.password"></el-input>

                    <svg v-if="!edit" class="icon" aria-hidden="true" @click="editBaseInfo()">
                      <use xlink:href="#icon-se-icon-f-edit"></use>
                    </svg>
                    <div v-if="!edit">
                      {{ baseInfoForm.password }}
                    </div>
                    <div v-if="edit">
                      <el-button @click="cancelEdit()">{{ $t('common.cancel') }}</el-button>
                      <el-button type="primary">{{ $t('common.submit') }}</el-button>
                    </div>
                  </el-form-item>
                </el-form>
              </div>
              <div v-else class="tab-content left-base-content">
                <el-form ref="baseInfoFormRef" :model="baseInfoForm" :rules="baseRules" label-position="top" class="source-form">
                  <el-form-item prop="userName" :label="$t('sourcePage.userNameTitle')" class="userName-form-item">
                    <el-input v-model="baseInfoForm.userName"></el-input>
                  </el-form-item>
                  <el-form-item :label="$t('sourcePage.passwordTitle')" prop="password" class="password-form-item">
                    <el-input show-password v-model="baseInfoForm.password"></el-input>

                    <div>
                      <el-button @click="cancelNew()">{{ $t('common.cancel') }}</el-button>
                      <el-button type="primary">{{ $t('common.submit') }}</el-button>
                    </div>
                  </el-form-item>
                </el-form>
              </div>
            </el-tab-pane>
            <el-tab-pane v-if="!isNew" :label="$t('sourcePage.accountPermit')" name="2">
              <el-table :data="authTableData" style="width: 100%">
                <el-table-column show-overflow-tooltip :label="$t('sourcePage.path')" width="180">
                  <template #default="scope">
                    <span v-if="!scope.row.edit">{{ pathMap[scope.row.type] }}</span>
                    <el-select v-if="scope.row.edit" v-model="scope.row.type" disabled>
                      <el-option v-for="item in pathList" :key="item.value" :label="item.label" :value="item.value"></el-option>
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column show-overflow-tooltip :label="$t('sourcePage.range')">
                  <template #default="scope">
                    <div v-if="scope.row.type == 0">-</div>
                    <div v-else-if="scope.row.type == 1">
                      <div v-if="scope.row.edit">
                        <el-select v-model="scope.row.groupPaths" multiple>
                          <el-option v-for="item in scope.row.allGroupPaths" :key="item" :value="item" :label="item"></el-option>
                        </el-select>
                      </div>
                      <div v-else>
                        <span v-for="item in scope.row.groupPaths" :key="item" class="device-path">{{ item }}</span>
                      </div>
                    </div>
                    <div v-else-if="scope.row.type == 2">
                      <div v-if="scope.row.edit">
                        <el-select class="row-select-range" v-model="scope.row.groupPaths[0]">
                          <el-option v-for="item in scope.row.allGroupPaths" :key="item" :value="item" :label="item"></el-option>
                        </el-select>
                        <el-select class="row-select-range" v-model="scope.row.devicePaths" multiple>
                          <el-option v-for="item in scope.row.allDevicePaths" :key="item" :value="item" :label="item"></el-option>
                        </el-select>
                      </div>
                      <div v-else>
                        <span v-for="item in scope.row.devicePaths" :key="item" class="device-path">{{ item }}</span>
                      </div>
                    </div>
                    <div v-else-if="scope.row.type == 3">
                      <div v-if="scope.row.edit">
                        <el-select class="row-select-range" v-model="scope.row.groupPaths[0]">
                          <el-option v-for="item in scope.row.allGroupPaths" :key="item" :value="item" :label="item"></el-option>
                        </el-select>
                        <el-select class="row-select-range" v-model="scope.row.devicePaths[0]">
                          <el-option v-for="item in scope.row.allDevicePaths" :key="item" :value="item" :label="item"></el-option>
                        </el-select>
                        <el-select class="row-select-range" v-model="scope.row.timeseriesPaths" multiple>
                          <el-option v-for="item in scope.row.allTimeseriesPaths" :key="item" :value="item" :label="item"></el-option>
                        </el-select>
                      </div>
                      <div v-else>
                        <span v-for="item in scope.row.timeseriesPaths" :key="item" class="device-path">{{ item }}</span>
                      </div>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column :label="$t('sourcePage.func')">
                  <template #default="scope">
                    <el-checkbox-group v-model="scope.row.privileges" :class="scope.row.edit ? '' : 'show-only'">
                      <el-checkbox :disabled="!scope.row.edit" v-for="item in funcList[scope.row.type]" :label="item.id" :key="item.id" @change="changeCheckItem(scope)">{{ item.label }}</el-checkbox>
                    </el-checkbox-group>
                  </template>
                </el-table-column>

                <el-table-column :label="$t('common.operation')">
                  <template #default="scope">
                    <!-- @click="handleClick(scope.row)" -->
                    <div v-if="scope.row.edit">
                      <el-button type="text" size="small">{{ $t('common.save') }}</el-button>
                      <el-button type="text" size="small" class="el-button-delete">{{ $t('common.cancel') }}</el-button>
                    </div>
                    <div v-else>
                      <el-button type="text" size="small" @click="changeEditState(scope)">{{ $t('common.edit') }}</el-button>
                      <el-button type="text" size="small" class="el-button-delete">{{ $t('common.delete') }}</el-button>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </div>
    <div class="storage-box">
      <div class="info-head">
        <svg class="icon" aria-hidden="true">
          <use xlink:href="#icon-cunchuzu-color"></use>
        </svg>
        {{ $t('sourcePage.groupInfo') }}
      </div>
      <el-table :data="tableData" class="group-table" max-height="250">
        <el-table-column prop="groupName" :label="$t('sourcePage.groupName')"> </el-table-column>
        <el-table-column prop="description" :label="$t('sourcePage.description')"> </el-table-column>
        <el-table-column prop="deviceCount" :label="$t('sourcePage.line')"> </el-table-column>
        <el-table-column :label="$t('common.operation')">
          <template #default="scope">
            <!-- @click="handleClick(scope.row)" -->
            <el-button type="text" size="small">{{ $t('common.detail') }}{{ scope.row.ttl }}</el-button>
            <el-button type="text" size="small">
              {{ $t('common.edit') }}
            </el-button>
            <el-button type="text" size="small" class="el-button-delete">
              {{ $t('common.delete') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <el-button @click="newSource()">新建数据源</el-button>
    <NewSource v-if="showDialog" :serverId="serverId" :showDialog="showDialog" :types="types" @close="close()" />
  </div>
</template>

<script>
// @ is an alias to /src
import { onMounted, reactive, ref } from 'vue';
import { ElButton, ElTable, ElTableColumn, ElTabs, ElTabPane, ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElCheckbox, ElCheckboxGroup } from 'element-plus';
import NewSource from './components/newSource.vue';
import { useI18n } from 'vue-i18n';
import axios from '@/util/axios.js';
import { useRouter } from 'vue-router';
export default {
  name: 'Source',
  setup() {
    const { t } = useI18n();
    let showDialog = ref(false);
    let types = ref(0);
    let activeName = ref('1');
    const router = useRouter();
    const pathList = reactive([
      { label: t('sourcePage.selectAlias'), value: 0 },
      { label: t('sourcePage.selectGroup'), value: 1 },
      { label: t('sourcePage.selectDevice'), value: 2 },
      { label: t('sourcePage.selectTime'), value: 3 },
    ]);
    const pathMap = reactive({
      0: t('sourcePage.selectAlias'),
      1: t('sourcePage.selectGroup'),
      2: t('sourcePage.selectDevice'),
      3: t('sourcePage.selectTime'),
    });
    let userList = ref([]);
    let baseInfo = ref({
      host: '',
      port: '',
      alias: '',
    });
    let tableData = ref([]);
    let baseInfoForm = reactive({});
    const baseInfoFormRef = ref(null);
    const baseRules = reactive({
      password: [
        {
          required: true,
          message: t(`sourcePage.passwordEmptyTip`),
          trigger: 'change',
        },
      ],
      userName: [
        {
          required: true,
          message: t(`sourcePage.usernameEmptyTip`),
          trigger: 'change',
        },
      ],
    });
    let authTableData = ref([]);
    let activeIndex = ref(0);
    let edit = ref(false);
    let isNew = ref(false);
    const serverId = ref(null);
    const funcTypeOne = reactive([
      { id: 'SET_STORAGE_GROUP', label: t('sourcePage.createGroup') },
      { id: 'CREATE_USER', label: t('sourcePage.createUser') },
      { id: 'DELETE_USER', label: t('sourcePage.deleteUser') },
      { id: 'MODIFY_PASSWORD', label: t('sourcePage.editPassword') },
      { id: 'LIST_USER', label: t('sourcePage.listUser') },
      {
        id: 'GRANT_USER_PRIVILEGE',
        label: t('sourcePage.grantPrivilege'),
      },
      {
        id: 'REVOKE_USER_PRIVILEGE',
        label: t('sourcePage.revertPrivilege'),
      },
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
      { id: 'CREATE_TRIGGER', label: t('sourcePage.createTrigger') },
      { id: 'DROP_TRIGGER', label: t('sourcePage.uninstallTrigger') },
      { id: 'START_TRIGGER', label: t('sourcePage.startTrigger') },
      { id: 'STOP_TRIGGER', label: t('sourcePage.stopTrigger') },
      { id: 'CREATE_FUNCTION', label: t('sourcePage.createFunction') },
      { id: 'DROP_FUNCTION', label: t('sourcePage.uninstallFunction') },
    ]);
    const funcTypeTwo = reactive([
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
    ]);
    const funcList = reactive({
      0: funcTypeOne,
      1: funcTypeTwo,
      2: funcTypeTwo,
      3: funcTypeTwo,
    });
    /**
     * 用户基本信息及所有权限列表
     */
    let userAuthInfo = ref({});
    /**
     * 新增或编辑数据连接
     */
    const newSource = () => {
      showDialog.value = true;
      types.value = 1;
    };
    /**
     * 关闭或者取消新增/编辑数据连接操作
     */
    const close = () => {
      showDialog.value = false;
      types.value = 0;
    };
    /**
     * 切换基本配置与账号权限的tab操作
     */
    const handleClick = (tab) => {
      activeName.value = tab.paneName;
    };
    /**
     * 选中用户列表某一个用户
     * index: 用户下标
     * item:当前选中用户信息
     */
    const handleUser = (index, item) => {
      activeIndex.value = index;
      getUserAuth(item);
    };
    /**
     * 编辑用户基本信息(密码)
     */
    const editBaseInfo = () => {
      edit.value = true;
    };
    /**
     * 取消编辑用户密码操作
     */
    const cancelEdit = () => {
      edit.value = false;
    };
    /**
     * 删除用户操作
     */
    const deleteUser = () => {
      console.log(1);
    };
    /**
     * 新建用户操作
     */
    const newUser = () => {
      isNew.value = true;

      userList.value.unshift({ username: 'new' });
    };
    /**
     * 取消新建用户
     */
    const cancelNew = () => {
      isNew.value = false;
    };
    /**
     * 监听多选框change事件
     * scope:行数据
     */
    const changeCheckItem = (scope) => {
      if (!scope.row.edit) {
        return false;
      }
    };
    /**
     * 切换表格行编辑状态
     * scope:行数据
     * index: 行顺序
     */
    const changeEditState = (scope) => {
      console.log(scope.$index);
      authTableData.value[scope.$index].edit = true;
    };
    const getBaseInfo = (func) => {
      axios.get(`/servers/${router.currentRoute.value.params.serverid}`, {}).then((res) => {
        if (res && res.code == 0) {
          baseInfo.value = res.data;
          func && func(res.data);
        }
      });
    };
    /**
     * 获取某一个用户权限
     * userinfo:用户信息
     */
    const getUserAuth = (userinfo) => {
      axios.get(`/servers/${router.currentRoute.value.params.serverid}/users/${userinfo.username}`, {}).then((res) => {
        // axios.get(`/servers/${router.currentRoute.value.params.serverid}/users/test`, {}).then((res) => {
        if (res && res.code == 0) {
          console.log(res);
          userAuthInfo.value = res.data;
          baseInfoForm.userName = res.data.userName;
          baseInfoForm.password = res.data.password;
          authTableData.value = res.data.privilegesInfo;
        } else {
          userAuthInfo = {};
        }
      });
    };
    const getGroupList = () => {
      axios.get(`/servers/${router.currentRoute.value.params.serverid}/storageGroups/info`, {}).then((res) => {
        if (res && res.code == 0) {
          tableData.value = res.data;
        }
      });
    };
    const getUserList = () => {
      axios.get(`/servers/${router.currentRoute.value.params.serverid}/users`, {}).then((res) => {
        if (res && res.code == 0) {
          let temp = [];
          for (let i = 0; i < res.data.length; i++) {
            temp.push({ username: res.data[i] });
          }
          userList.value = temp;
        }
      });
    };
    onMounted(() => {
      serverId.value = router.currentRoute.value.params.serverid;

      getBaseInfo((data) => {
        getUserAuth(data);
      });
      getGroupList();
      getUserList();
    });

    return {
      newSource,
      close,
      showDialog,
      types,
      baseInfo,
      tableData,
      userList,
      activeIndex,
      handleUser,
      handleClick,
      activeName,
      baseInfoForm,
      baseInfoFormRef,
      baseRules,
      edit,
      serverId,
      editBaseInfo,
      cancelEdit,
      deleteUser,
      newUser,
      isNew,
      cancelNew,
      authTableData,
      pathList,
      pathMap,
      funcTypeOne,
      funcTypeTwo,
      funcList,
      getBaseInfo,
      changeCheckItem,
      changeEditState,
      userAuthInfo,
      getUserList,
    };
  },
  components: {
    ElButton,
    ElTable,
    ElTableColumn,
    NewSource,
    ElTabs,
    ElTabPane,
    ElForm,
    ElFormItem,
    ElInput,
    ElSelect,
    ElOption,
    ElCheckbox,
    ElCheckboxGroup,
  },
};
</script>

<style scoped lang="scss">
.source-detail-container {
  height: 100%;
  text-align: left;
  &::v-deep .el-tabs__nav-wrap::after {
    display: none !important;
  }

  &::v-deep .el-tabs__header .el-tabs__nav .el-tabs__item.is-active {
    background: #fff !important;
  }
  .info-box {
    padding: 10px 20px;
  }
  .info-box {
    font-size: 16px;
    font-weight: 500;
    color: #222222;
    .title {
      margin: 10px 0 20px;
    }
    .more {
      span {
        color: rgba(34, 34, 34, 0.65);
        font-size: 14px;
        margin-right: 40px;
      }
    }
  }
  .icon {
    cursor: pointer;
  }

  .permission-box {
    .permit-content {
      display: flex;
      flex-direction: row;
      .left-part {
        width: 240px;
        margin-top: 12px;
        border-right: 1px solid #f0f0f0ff;
        .title {
          height: 17px;
          font-size: 12px;
          font-weight: 400;
          line-height: 20px;
          color: rgba(34, 34, 34, 0.65);
          padding: 0 16px 10px;
          button {
            float: right;
            font-size: 12px;
            margin-top: -7px;
            padding-right: 0;
          }
        }
        .user-list {
          margin-bottom: 10px;
          max-height: 250px;
          overflow: auto;
          .active {
            background: rgba(69, 117, 246, 0.04);
            color: $theme-color;
          }
          li {
            height: 36px;
            line-height: 36px;
            position: relative;
            font-size: 12px;
            padding: 0 16px;
            cursor: pointer;
            .content {
              display: inline-block;
              max-width: 150px;
              overflow: hidden;
              text-overflow: ellipsis;
            }
            .icon {
              position: absolute;
              right: 16px;
              top: 10px;
            }
          }
        }
      }
      .right-part {
        flex: 1;
        &::v-deep .el-tabs__active-bar {
          // width: 23px !important;
          // margin-left: 14px;
        }
        .show-only {
          // &::v-deep .el-checkbox__input {
          //   display: none;
          // }
          &::v-deep .el-checkbox {
            display: none;
          }
          &::v-deep .is-checked {
            display: inline-block !important;
            .is-checked {
              display: none !important;
            }
          }
          &::v-deep .el-checkbox__input {
            display: none;
          }
        }
        &::v-deep .el-checkbox__input.is-disabled + span.el-checkbox__label {
          color: #606266;
          cursor: default;
        }
        .el-select {
          width: 100%;
        }
        .device-path {
          margin-right: 10px;
        }
        .row-select-range {
          display: block;
        }
        .tab-content {
          padding: 10px 30px;

          .password-form-item {
            position: relative;

            .icon {
              color: $theme-color;
              position: absolute;
              top: -31px;
              left: 100px;
              cursor: pointer;
            }
          }
        }
        .left-base-content {
          .el-input {
            width: 200px;
            // display: block;
            .el-input__suffix {
              top: auto;
            }
          }
        }
      }
    }
  }
  .info-head {
    height: 40px;
    line-height: 40px;
    background: #f6f6f8;
    font-size: 14px;
    color: #222222;
    padding-left: 20px;
    .icon {
      font-size: 16px;
      cursor: pointer;
      margin-right: 4px;
    }
  }
  .storage-box {
    .group-table {
      width: 100%;
      padding: 10px;
      overflow: auto;
      // &::v-deep .el-table__body-wrapper {
      //   max-height: 30vh;
      //   overflow: auto !important;
      // }
      .el-button {
        padding-left: 0;
      }
    }
  }
}
</style>
