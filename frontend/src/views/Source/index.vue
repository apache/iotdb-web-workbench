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
  <div class="source-detail-container">
    <div class="info-box">
      <p class="title">{{ baseInfo.alias }}</p>
      <p class="more">
        <span>{{ $t('sourcePage.host') + ':' }}{{ baseInfo.host }}</span>
        <span>{{ $t('sourcePage.port') + ':' }}{{ baseInfo.port }}</span>
      </p>
      <svg class="icon icon-edit" aria-hidden="true" @click="editSource()">
        <use xlink:href="#icon-se-icon-f-edit"></use>
      </svg>
      <el-popconfirm placement="top" :title="$t('sourcePage.deleteSourceConfirm')" @confirm="deleteSource()">
        <template #reference>
          <span class="icon-del">
            <svg aria-hidden="true" class="icon">
              <use xlink:href="#icon-se-icon-delete"></use>
            </svg>
          </span>
        </template>
      </el-popconfirm>
    </div>
    <div class="permission-box">
      <div class="info-head">
        <svg class="icon" aria-hidden="true">
          <use xlink:href="#icon-yonghuquanxian-color"></use>
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
            <li v-for="(item, index) in userList" :class="activeIndex == item.username ? 'active' : ''" :key="index" @click="handleUser(index, item)">
              <!-- <el-tooltip class="item" width="200" effect="dark" :content="item.username" placement="top"> -->
              <span class="content">{{ item.username }}</span>
              <!-- </el-tooltip> -->
              <el-popconfirm placement="top" :title="$t('sourcePage.deleteUserConfirm')" @confirm="deleteUser(item)">
                <template #reference>
                  <span class="icon-del del-user">
                    <svg v-if="activeIndex == item.username" class="icon" aria-hidden="true">
                      <use xlink:href="#icon-se-icon-delete"></use>
                    </svg>
                  </span>
                </template>
              </el-popconfirm>
            </li>
          </ul>
        </div>
        <div class="right-part">
          <el-button class="auth-add-btn" type="text" v-if="activeName == '2' && baseInfoForm.userName !== 'root'" @click="authAdd()">{{ $t('sourcePage.addAuthBtn') }}</el-button>
          <el-tabs v-model="activeName" @tab-click="handleClick" class="tabs">
            <el-tab-pane :label="$t('sourcePage.baseConfig')" name="1">
              <template v-if="activeIndex !== null">
                <div v-if="!isNew" class="tab-content left-base-content">
                  <el-form ref="baseInfoFormRef" :model="baseInfoForm" :rules="baseRules" label-position="top" class="source-form">
                    <el-form-item :label="$t('sourcePage.userNameTitle')">
                      <div v-if="baseInfoForm.userName && baseInfoForm.userName.length > 80">
                        <el-tooltip class="item" width="200" effect="dark" :content="baseInfoForm.userName" placement="top">
                          <div class="user-name">{{ baseInfoForm.userName }}</div>
                        </el-tooltip>
                      </div>
                      <div v-else class="user-name">{{ baseInfoForm.userName }}</div>
                    </el-form-item>
                    <el-form-item :label="$t('sourcePage.passwordTitle')" prop="password" class="password-form-item">
                      <el-input show-password v-if="edit" v-model="baseInfoForm.password"></el-input>

                      <svg v-if="!edit && baseInfoForm.userName != 'root'" class="icon" aria-hidden="true" @click="editBaseInfo()">
                        <use xlink:href="#icon-se-icon-f-edit"></use>
                      </svg>
                      <div v-if="!edit" class="password">
                        <el-tooltip class="item" effect="dark" :content="baseInfoForm.password" placement="top">
                          <div>{{ baseInfoForm.password }}</div>
                        </el-tooltip>
                      </div>
                      <div v-if="edit">
                        <el-button @click="cancelEdit()">{{ $t('common.cancel') }}</el-button>
                        <el-button type="primary" @click="doEdit()">{{ $t('common.submit') }}</el-button>
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
                        <el-button @click="cancelNew1()">{{ $t('common.cancel') }}</el-button>
                        <el-button type="primary" @click="doCreate()">{{ $t('common.submit') }}</el-button>
                      </div>
                    </el-form-item>
                  </el-form>
                </div>
              </template>
            </el-tab-pane>
            <el-tab-pane v-if="!isNew" :label="$t('sourcePage.accountPermit')" name="2">
              <template v-if="activeIndex !== null">
                <p class="auth-tips">{{ $t('sourcePage.authTips') }}</p>
                <el-table :data="authTableData" style="width: 100%">
                  <el-table-column show-overflow-tooltip :label="$t('sourcePage.path')" width="180">
                    <template #default="scope">
                      <span v-if="!scope.row.edit">{{ pathMap[scope.row.type] }}</span>
                      <el-select v-if="scope.row.edit" v-model="scope.row.type" :disabled="!scope.row.new" @change="changeType(scope.row.type, scope)">
                        <el-option v-for="item in pathList" :key="item.value" :label="item.label" :value="item.value"></el-option>
                      </el-select>
                    </template>
                  </el-table-column>
                  <el-table-column :label="$t('sourcePage.range')">
                    <template #default="scope">
                      <div v-if="scope.row.type == 0">-</div>
                      <div v-else-if="scope.row.type == 1">
                        <div v-if="scope.row.edit">
                          <el-select v-model="scope.row.groupPaths" multiple>
                            <el-option v-for="item in scope.row.allGroupPaths" :key="item" :value="item" :label="item"></el-option>
                          </el-select>
                        </div>
                        <div v-else>
                          <p>{{ $t('sourcePage.groupNameLabel') }}</p>
                          <span v-for="item in scope.row.groupPaths" :key="item" class="device-path">{{ item }}</span>
                        </div>
                      </div>
                      <div v-else-if="scope.row.type == 2">
                        <div v-if="scope.row.edit">
                          <el-select class="row-select-range" v-model="scope.row.groupPaths[0]" @change="getDeviceByGroupName(scope.row.groupPaths[0], scope)">
                            <el-option v-for="item in scope.row.allGroupPaths" :key="item" :value="item" :label="item"></el-option>
                          </el-select>
                          <el-select class="row-select-range" v-model="scope.row.devicePaths" multiple>
                            <el-option v-for="item in scope.row.allDevicePaths" :key="item" :value="item" :label="item"></el-option>
                          </el-select>
                        </div>
                        <div v-else>
                          <p>{{ $t('sourcePage.groupNameLabel') }}</p>
                          <span class="device-path">{{ scope.row.groupPaths[0] }}</span>
                          <p>{{ $t('sourcePage.deviceNameLabel') }}</p>
                          <span v-for="item in scope.row.devicePaths" :key="item" class="device-path">{{ item }}</span>
                        </div>
                      </div>
                      <div v-else-if="scope.row.type == 3">
                        <div v-if="scope.row.edit">
                          <el-select class="row-select-range" v-model="scope.row.groupPaths[0]" @change="getDeviceByGroupName(scope.row.groupPaths[0], scope)">
                            <el-option v-for="item in scope.row.allGroupPaths" :key="item" :value="item" :label="item"></el-option>
                          </el-select>
                          <el-select class="row-select-range" v-model="scope.row.devicePaths[0]" @change="getTimeSeriesByDeviceName(scope.row.devicePaths[0], scope)">
                            <el-option v-for="item in scope.row.allDevicePaths" :key="item" :value="item" :label="item"></el-option>
                          </el-select>
                          <el-select class="row-select-range" v-model="scope.row.timeseriesPaths" multiple>
                            <el-option v-for="item in scope.row.allTimeseriesPaths" :key="item" :value="item" :label="item"></el-option>
                          </el-select>
                        </div>
                        <div v-else>
                          <p>{{ $t('sourcePage.groupNameLabel') }}</p>
                          <span class="device-path">{{ scope.row.groupPaths[0] }}</span>
                          <p>{{ $t('sourcePage.deviceNameLabel') }}</p>
                          <span class="device-path">{{ scope.row.devicePaths[0] }}</span>
                          <p>{{ $t('sourcePage.timeNameLabel') }}</p>
                          <span v-for="item in scope.row.timeseriesPaths" :key="item" class="device-path">{{ item }}</span>
                        </div>
                      </div>
                    </template>
                  </el-table-column>
                  <el-table-column :label="$t('sourcePage.func')">
                    <template #default="scope">
                      <el-checkbox-group v-model="scope.row.privileges" :class="scope.row.edit ? '' : 'show-only'">
                        <el-checkbox :disabled="!scope.row.edit" v-for="item in funcList[scope.row.type]" :label="item.id" :key="item.id" @change="changeCheckItem(scope)">{{
                          item.label
                        }}</el-checkbox>
                      </el-checkbox-group>
                    </template>
                  </el-table-column>

                  <el-table-column :label="$t('common.operation')">
                    <template #default="scope">
                      <div v-if="scope.row.edit">
                        <el-button type="text" size="small" @click="saveRowAuth(scope)">{{ $t('common.save') }}</el-button>
                        <el-button type="text" size="small" class="el-button-delete" @click="cancelRowAuth()">{{ $t('common.cancel') }}</el-button>
                      </div>
                      <div v-else>
                        <el-button type="text" size="small" :disabled="baseInfoForm.userName == 'root'" @click="changeEditState(scope)">{{ $t('common.edit') }}</el-button>
                        <el-popconfirm placement="top" :title="$t('sourcePage.deleteAuthConfirm')" @confirm="deleteRowAuth(scope)">
                          <template #reference>
                            <el-button type="text" size="small" :disabled="baseInfoForm.userName == 'root'" class="el-button-delete">{{ $t('common.delete') }}</el-button>
                          </template>
                        </el-popconfirm>
                      </div>
                    </template>
                  </el-table-column>
                </el-table>
              </template>
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
        {{ $t('sourcePage.groupInfo') }}({{ groupTotal }})
      </div>
      <el-table :data="tableData" class="group-table" max-height="280" height="280">
        <el-table-column prop="groupName" :label="$t('sourcePage.groupName')"> </el-table-column>
        <el-table-column prop="description" :label="$t('sourcePage.description')"> </el-table-column>
        <el-table-column prop="deviceCount" :label="$t('sourcePage.line')"> </el-table-column>
        <el-table-column :label="$t('common.operation')">
          <template #default="scope">
            <!-- @click="handleClick(scope.row)" -->
            <el-button type="text" size="small" @click="goGroupDetail(scope)">{{ $t('common.detail') }}</el-button>
            <el-button type="text" size="small" @click="goEditGroup(scope)">
              {{ $t('common.edit') }}
            </el-button>
            <el-popconfirm placement="top" :title="$t('storagePage.deleteGroupConfirm')" @confirm="deleteGroup(scope)">
              <template #reference>
                <el-button type="text" size="small" class="el-button-delete" :disable="canGroupSet"> {{ $t('common.delete') }} </el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <NewSource v-if="showDialog" :func="func" :serverId="serverId" :showDialog="showDialog" :types="types" @close="close()" @successFunc="successFunc(data)" />
  </div>
</template>

<script>
// @ is an alias to /src
import { onMounted, reactive, ref, watch, onActivated } from 'vue';
import {
  ElButton,
  ElTable,
  ElTableColumn,
  ElTabs,
  ElTabPane,
  ElForm,
  ElFormItem,
  ElInput,
  ElSelect,
  ElOption,
  ElCheckbox,
  ElCheckboxGroup,
  ElMessage,
  ElPopconfirm,
  ElPopover,
  ElPopper,
  ElTooltip,
} from 'element-plus';
import NewSource from './components/newSource.vue';
import { useI18n } from 'vue-i18n';

import axios from '@/util/axios.js';
// import { useStore } from 'vuex';

import { useRouter } from 'vue-router';
export default {
  name: 'Source',
  props: ['func', 'data'],
  setup(props) {
    const { t, locale } = useI18n();
    // const store = useStore();

    let showDialog = ref(false);
    let types = ref(0);
    let activeName = ref('1');
    // 是否可以创建用户
    let canCreateUser = ref(false);
    //是否可以创建存储组，用于判断是否可以删除存储组
    let canGroupSet = ref(false);
    // 是否可以删除用户
    let canDeleteUser = ref(false);
    // 是否可以修改密码
    let canModifyPassword = ref(false);
    // 是否可以查看用户
    let canShowUser = ref(false);
    // 是否可以用户赋权
    let canAuth = ref(false);
    const router = useRouter();

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
          message: () => {
            return t(`sourcePage.newPasswordTip`);
          },
          trigger: 'change',
        },
        {
          pattern: /^\S+$/,
          message: () => {
            return t(`sourcePage.newUserErrorTip`);
          },
          trigger: 'change',
        },
        {
          min: 4,
          max: 255,
          message: () => {
            return t(`sourcePage.newpasswordErrorTip1`);
          },
          trigger: 'change',
        },
      ],
      userName: [
        {
          required: true,
          message: () => {
            return t(`sourcePage.newUserEmptyTip`);
          },
          trigger: 'change',
        },
        {
          pattern: /^[a-zA-Z0-9_\u4e00-\u9fa5]+$/,
          message: () => {
            return t(`sourcePage.newUserErrorTip`);
          },
          trigger: 'change',
        },
        {
          pattern: /^\S+$/,
          message: () => {
            return t(`sourcePage.newUserErrorTip`);
          },
          trigger: 'change',
        },
        {
          min: 4,
          max: 255,
          message: () => {
            return t(`sourcePage.newUserErrorTip1`);
          },
          trigger: 'change',
        },
      ],
    });
    let authTableData = ref([]);
    /**
     * 当前连接下所有存储组信息
     */
    let allGroupPaths = ref([]);
    let activeIndex = ref(null);
    let edit = ref(false);
    let isNew = ref(false);
    let groupTotal = ref(0);
    const serverId = ref(null);
    let funcTypeOne = () => {
      return [
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
      ];
    };
    let funcTypeTwo = () => {
      return [
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
      ];
    };
    const funcList = ref({
      0: funcTypeOne(),
      1: funcTypeTwo(),
      2: funcTypeTwo(),
      3: funcTypeTwo(),
    });

    watch(locale, () => {
      funcList.value = {
        0: funcTypeOne(),
        1: funcTypeTwo(),
        2: funcTypeTwo(),
        3: funcTypeTwo(),
      };
      pathList.value = [
        { label: t('sourcePage.selectAlias'), value: 0 },
        { label: t('sourcePage.selectGroup'), value: 1 },
        { label: t('sourcePage.selectDevice'), value: 2 },
        { label: t('sourcePage.selectTime'), value: 3 },
      ];
      pathMap.value = { 0: t('sourcePage.selectAlias'), 1: t('sourcePage.selectGroup'), 2: t('sourcePage.selectDevice'), 3: t('sourcePage.selectTime') };
    });
    /**
     * 用户基本信息及所有权限列表
     */
    let userAuthInfo = ref({});
    let userAuthInfoTemp = ref({});
    /**
     * 新增或编辑数据连接
     */
    const editSource = () => {
      showDialog.value = true;
      types.value = 1;
    };
    /**
     * 删除数据源
     */
    const deleteSource = () => {
      axios.delete(`/servers/${serverId.value}`).then((rs) => {
        if (rs && rs.code == 0) {
          ElMessage.success(t('sourcePage.successDeleteLabel'));
          props.func.updateTree();
          props.func.removeTab(props.data.id);
        }
      });
    };
    /**
     * 关闭或者取消新增/编辑数据连接操作
     */
    const close = () => {
      showDialog.value = false;
      types.value = 0;
    };
    /**
     * 新增或编辑数据源成功回调
     */
    const successFunc = () => {
      showDialog.value = false;
      types.value = 0;
      getBaseInfo();
      getUserList(1);
      getGroupList();
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
      for (let i = 0; i < userList.value.length; i++) {
        if (userList.value[i].username == 'new') {
          ElMessage.error(t(`sourcePage.addFirstLabel`));
          return false;
        }
      }
      edit.value = false;
      activeIndex.value = item.username;
      getUserAuth(item);
    };
    /**
     * 编辑用户基本信息(密码)
     */
    const editBaseInfo = () => {
      if (!canModifyPassword.value) {
        ElMessage.error(t(`sourcePage.noAuthTip`));
        return false;
      }
      edit.value = true;
    };
    /**
     * 取消编辑用户密码操作
     */
    const cancelEdit = () => {
      edit.value = false;
    };
    /**
     * 修改用户密码
     */
    const doEdit = () => {
      if (!baseInfoForm.password) {
        ElMessage.error(t(`sourcePage.passwordEmptyTip`));
        return false;
      }
      let reqObj = {
        userName: baseInfoForm.userName,
        password: baseInfoForm.password,
      };
      axios.post(`/servers/${serverId.value}/users/pwd`, { ...reqObj }).then((rs) => {
        if (rs && rs.code == 0) {
          ElMessage.success(t('sourcePage.modifySuccessLabel'));
          edit.value = false;
        }
      });
    };
    /**
     * 创建用户
     */
    const doCreate = () => {
      baseInfoFormRef.value.validate((valid) => {
        let reqObj = {
          userName: baseInfoForm.userName,
          password: baseInfoForm.password,
        };
        if (valid) {
          axios.post(`/servers/${serverId.value}/users/`, { ...reqObj }).then((rs) => {
            if (rs && rs.code == 0) {
              ElMessage.success(t('sourcePage.addSuccessLabel'));
              cancelNew(reqObj.userName);
            }
          });
        }
      });
    };
    /**
     * 删除用户操作
     * item: 当前被删除的数据
     */
    const deleteUser = (item) => {
      if (!canDeleteUser.value) {
        ElMessage.error(t(`sourcePage.noAuthTip`));
        return false;
      }
      axios.delete(`/servers/${serverId.value}/users/${item.username}`).then((rs) => {
        if (rs && rs.code == 0) {
          ElMessage.success(t('sourcePage.deleteUserSuccessLabel'));
          activeIndex.value = null;
          getUserList(1);
        }
      });
    };
    /**
     * 新建用户操作
     */
    const newUser = () => {
      if (!canCreateUser.value || !canShowUser.value) {
        ElMessage.error(t(`sourcePage.noAuthTip`));
        return false;
      }
      for (let i = 0; i < userList.value.length; i++) {
        if (userList.value[i].username == 'new') {
          ElMessage.error(t(`sourcePage.addFirstLabel`));
          return false;
        }
      }

      isNew.value = true;
      activeName.value = '1';
      baseInfoForm.password = null;
      baseInfoForm.userName = null;
      userList.value.unshift({ username: 'new' });
      activeIndex.value = 'new';
    };
    /**
     * 取消新建用户
     * userName
     */
    const cancelNew = (username) => {
      isNew.value = false;
      // activeIndex.value == 'new' && (activeIndex.value = null);
      getUserList();
      if (username) {
        activeIndex.value = username;
        getUserAuth({ username });
      }
    };
    const cancelNew1 = () => {
      isNew.value = false;
      getUserList(1);
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
      authTableData.value[scope.$index].edit = true;
    };
    /**
     * 获取头部数据连接基本信息
     */
    const getBaseInfo = (func) => {
      axios.get(`/servers/${serverId.value}`, {}).then((res) => {
        if (res && res.code == 0) {
          baseInfo.value = res.data;
          // if (!func) {
          //   baseInfoForm.userName = null;
          //   baseInfoForm.password = null;
          // } else {
          func && func(res.data);
          // }
        } else {
          baseInfo.value = {};
        }
      });
    };
    /**
     * 添加权限按钮
     */
    const authAdd = () => {
      if (!canAuth.value) {
        ElMessage.error(t(`sourcePage.noAuthTip`));
        return false;
      }
      for (let i = 0; i < authTableData.value.length; i++) {
        if (authTableData.value[i].new) {
          ElMessage.error(t(`sourcePage.addAuthFirstLabel`));
          return false;
        }
      }
      authTableData.value.push({
        edit: true,
        new: true,
        privileges: [],
        groupPaths: [],
        allGroupPaths: allGroupPaths.value,
        allDevicePaths: [],
        devicePaths: [],
        allTimeseriesPaths: [],
        timeseriesPaths: [],
        type: null,
      });
    };
    /**
     * 获取某一个用户权限
     * userinfo:用户信息
     * type: 1用户本身信息
     */
    const getUserAuth = (userinfo, type) => {
      axios.get(`/servers/${serverId.value}/users/${userinfo.username}`, {}).then((res) => {
        if (res && res.code == 0) {
          userAuthInfo.value = res.data;
          userAuthInfoTemp.value = JSON.parse(JSON.stringify(res.data));
          baseInfoForm.userName = res.data.userName;
          baseInfoForm.password = res.data.password;
          authTableData.value = res.data.privilegesInfo;
          baseInfo.value.privilegesInfo = res.data.privilegesInfo;
          if (type == 1) {
            checkAuth(res.data.privilegesInfo);
          }
        } else {
          userAuthInfo.value = {};
          baseInfoForm.userName = null;
          baseInfoForm.password = null;
          authTableData.value = [];
          baseInfo.value.privilegesInfo = [];
        }
      });
    };
    /**
     * 检查登入用户是否有各项操作权限
     * data:权限数组
     */
    const checkAuth = (data) => {
      for (let i = 0; i < data.length; i++) {
        if (data[i].type == 0) {
          canCreateUser.value = data[i].privileges.indexOf('CREATE_USER') >= 0 ? true : false;
          canDeleteUser.value = data[i].privileges.indexOf('DELETE_USER') >= 0 ? true : false;
          canModifyPassword.value = data[i].privileges.indexOf('MODIFY_PASSWORD') >= 0 ? true : false;
          canShowUser.value = data[i].privileges.indexOf('LIST_USER') >= 0 ? true : false;
          canAuth.value = data[i].privileges.indexOf('GRANT_USER_PRIVILEGE') >= 0 ? true : false;
          canGroupSet.value = data[i].privileges.indexOf('SET_STORAGE_GROUP') >= 0 ? true : false;
        }
      }
    };
    /**
     * 获取当前数据连接的所有存储组
     */
    const getGroupList = () => {
      axios.get(`/servers/${serverId.value}/storageGroups/info`, {}).then((res) => {
        if (res && res.code == 0) {
          tableData.value = res.data;
          groupTotal.value = (res.data && res.data.length) || 0;
          let temp = [];
          for (let i = 0; i < res.data.length; i++) {
            temp.push(res.data[i].groupName);
          }
          allGroupPaths.value = temp;
        } else {
          tableData.value = [];
          allGroupPaths.value = [];
          groupTotal.value = 0;
        }
      });
    };
    /**
     * 获取用户列表
     * type：1初始化列表时默认选中第一个并且请求相关用户的权限列表
     */
    const getUserList = (type) => {
      axios.get(`/servers/${serverId.value}/users`, {}).then((res) => {
        if (res && res.code == 0) {
          let temp = [];
          for (let i = 0; i < res.data.length; i++) {
            temp.push({ username: res.data[i] });
          }
          userList.value = temp;
          if (type == 1 && userList.value.length) {
            activeIndex.value = userList.value[0].username;
            getUserAuth(userList.value[0]);
          }
        } else {
          userList.value = [];
          baseInfoForm.password = null;
          baseInfoForm.userName = null;
          authTableData.value = [];
          baseInfo.value.privilegesInfo = [];
        }
      });
    };
    /**
     * 删除某一行权限
     * scope当前行数据
     */
    const deleteRowAuth = (scope) => {
      if (!canAuth.value) {
        ElMessage.error(t(`sourcePage.noAuthTip`));
        return false;
      }
      let reqObj = scope.row;
      reqObj.cancelPrivileges = scope.row.privileges;
      reqObj.privileges = [];
      axios.post(`/servers/${serverId.value}/users/${activeIndex.value}`, { ...reqObj }).then((rs) => {
        if (rs && rs.code == 0) {
          ElMessage.success(t('sourcePage.deleteAuthLabel'));
          getUserAuth({ username: activeIndex.value });
        }
      });
    };
    /**
     * 编辑某一行权限
     * scope当前行数据
     */
    const saveRowAuth = (scope) => {
      let reqObj = JSON.parse(JSON.stringify(scope.row));
      if (reqObj.type != 0 && reqObj.type != 1 && reqObj.type != 2 && reqObj.type != 3) {
        ElMessage.error(t('sourcePage.submitTypeTips'));
        return false;
      }
      if (reqObj.type == 1 && (!reqObj.groupPaths || !reqObj.groupPaths.length)) {
        ElMessage.error(t('sourcePage.submitRangeTips'));
        return false;
      }
      if (reqObj.type == 2 && (!reqObj.groupPaths || !reqObj.groupPaths.length || !reqObj.devicePaths || !reqObj.devicePaths.length)) {
        ElMessage.error(t('sourcePage.submitRangeTips'));
        return false;
      }
      if (reqObj.type == 3 && (!reqObj.groupPaths || !reqObj.groupPaths.length || !reqObj.devicePaths || !reqObj.devicePaths.length || !reqObj.timeseriesPaths || !reqObj.timeseriesPaths.length)) {
        ElMessage.error(t('sourcePage.submitRangeTips'));
        return false;
      }
      if (!reqObj.privileges || !reqObj.privileges.length) {
        ElMessage.error(t('sourcePage.submitPrivilegesTips'));
        return false;
      }
      if (scope.row.new) {
        //用户新增权限
        reqObj.cancelPrivileges = [];
      } else {
        // 缓存权限信息
        //根据后端要求设置功能数组组装开始
        let tempRow = userAuthInfoTemp.value.privilegesInfo[scope.$index].privileges;
        // 用户删除的权限
        let deleteArr = tempRow.filter(function (val) {
          return scope.row.privileges.indexOf(val) === -1;
        });
        // 用户新增的权限
        let newArr = scope.row.privileges.filter(function (val) {
          return tempRow.indexOf(val) === -1;
        });
        //交集
        let intersection = tempRow.filter(function (val) {
          return scope.row.privileges.indexOf(val) > -1;
        });
        newArr = newArr.concat(intersection);
        reqObj.privileges = newArr;
        reqObj.cancelPrivileges = deleteArr;
        //根据后端要求设置功能数组组装结束

        //根据后端要求设置范围数组组装开始
        if (scope.row.type == 1) {
          let tempGroupArr = userAuthInfoTemp.value.privilegesInfo[scope.$index].groupPaths;
          // 用户删除的权限
          let deleteGroupArr = tempGroupArr.filter(function (val) {
            return scope.row.groupPaths.indexOf(val) === -1;
          });
          // 用户新增的权限
          let newGroupArr = scope.row.groupPaths.filter(function (val) {
            return tempGroupArr.indexOf(val) === -1;
          });
          //交集
          let intersection = tempGroupArr.filter(function (val) {
            return scope.row.groupPaths.indexOf(val) > -1;
          });
          newGroupArr = newGroupArr.concat(intersection);
          reqObj.delGroupPaths = deleteGroupArr;
          reqObj.groupPaths = newGroupArr;
        } else if (scope.row.type == 2) {
          let tempGroupArr = userAuthInfoTemp.value.privilegesInfo[scope.$index].groupPaths;
          let tempDeviceArr = userAuthInfoTemp.value.privilegesInfo[scope.$index].devicePaths;
          if (tempGroupArr.toString() == scope.row.groupPaths.toString()) {
            //未修改存储组
            reqObj.delGroupPaths = [];
            reqObj.groupPaths = scope.row.groupPaths;
            let deleteDeviceArr = tempDeviceArr.filter(function (val) {
              return scope.row.devicePaths.indexOf(val) === -1;
            });
            // 用户新增的权限
            let newDeviceArr = scope.row.devicePaths.filter(function (val) {
              return tempDeviceArr.indexOf(val) === -1;
            });
            //交集
            let intersection = tempDeviceArr.filter(function (val) {
              return scope.row.devicePaths.indexOf(val) > -1;
            });
            newDeviceArr = newDeviceArr.concat(intersection);
            if (deleteDeviceArr.length) {
              reqObj.delGroupPaths = tempGroupArr;
            }
            reqObj.delDevicePaths = deleteDeviceArr;
            reqObj.devicePaths = newDeviceArr;
            // reqObj.privileges = scope.row.privileges;
          } else {
            //修改了存储组
            reqObj.delGroupPaths = tempGroupArr;
            reqObj.groupPaths = scope.row.groupPaths;
            reqObj.delDevicePaths = tempDeviceArr;
            reqObj.devicePaths = scope.row.devicePaths;
            // reqObj.privileges = scope.row.privileges;
          }
        } else if (scope.row.type == 3) {
          let tempGroupArr = userAuthInfoTemp.value.privilegesInfo[scope.$index].groupPaths;
          let tempDeviceArr = userAuthInfoTemp.value.privilegesInfo[scope.$index].devicePaths;
          let tempTimeArr = userAuthInfoTemp.value.privilegesInfo[scope.$index].timeseriesPaths;
          if (tempGroupArr.toString() == scope.row.groupPaths.toString()) {
            //未修改存储组
            reqObj.delGroupPaths = [];
            reqObj.groupPaths = scope.row.groupPaths;
            if (tempDeviceArr.toString() == scope.row.devicePaths.toString()) {
              // 未修改设备
              reqObj.delDevicePaths = [];
              reqObj.devicePaths = scope.row.devicePaths;
              if (tempDeviceArr.toString() == scope.row.timeseriesPaths.toString()) {
                // 未修改物理量
                reqObj.delTimeseriesPaths = [];
                reqObj.timeseriesPaths = scope.row.timeseriesPaths;
              } else {
                //修改了物理量
                let deleteTimeArr = tempTimeArr.filter(function (val) {
                  return scope.row.timeseriesPaths.indexOf(val) === -1;
                });
                // 用户新增的权限
                let newTimeArr = scope.row.timeseriesPaths.filter(function (val) {
                  return tempTimeArr.indexOf(val) === -1;
                });
                //交集
                let intersection = tempTimeArr.filter(function (val) {
                  return scope.row.timeseriesPaths.indexOf(val) > -1;
                });
                newTimeArr = newTimeArr.concat(intersection);
                if (deleteTimeArr.length) {
                  reqObj.delTimeseriesPaths = deleteTimeArr;
                  reqObj.delGroupPaths = scope.row.groupPaths;
                  reqObj.delDevicePaths = scope.row.devicePaths;
                }
                reqObj.timeseriesPaths = newTimeArr;

                // reqObj.privileges = scope.row.privileges;
              }
            } else {
              //修改了设备
              reqObj.delDevicePaths = tempDeviceArr;
              reqObj.devicePaths = scope.row.devicePaths;
              reqObj.delTimeseriesPaths = tempTimeArr;
              reqObj.timeseriesPaths = scope.row.timeseriesPaths;
              reqObj.delGroupPaths = scope.row.groupPaths;
              // reqObj.privileges = scope.row.privileges;
            }
          } else {
            //修改了存储组
            reqObj.delGroupPaths = tempGroupArr;
            reqObj.groupPaths = scope.row.groupPaths;
            reqObj.delDevicePaths = tempDeviceArr;
            reqObj.devicePaths = scope.row.devicePaths;
            reqObj.delTimeseriesPaths = tempTimeArr;
            reqObj.timeseriesPaths = scope.row.timeseriesPaths;
          }
        }
      }
      axios.post(`/servers/${serverId.value}/users/${activeIndex.value}`, { ...reqObj }).then((rs) => {
        if (rs && rs.code == 0) {
          ElMessage.success(t('sourcePage.operateAuthLabel'));
          getUserAuth({ username: activeIndex.value });
        }
      });
    };
    /**
     * 取消当前行新增或编辑
     * scope:当前行数据
     */
    const cancelRowAuth = () => {
      getUserAuth({ username: activeIndex.value });
    };
    /**
     * 修改当前行type时触发
     * val:当前type值
     * scope:当前行数据
     *
     */
    const changeType = (val, scope) => {
      scope.row.groupPaths = [];
      scope.row.devicePaths = [];
      scope.row.timeseriesPaths = [];
    };
    /**
     * 根据groupName获取该存储组下的实体
     * val存储组名称
     * scope当前行数据
     */
    const getDeviceByGroupName = (val, scope) => {
      scope.row.devicePaths = [];
      scope.row.timeseriesPaths = [];
      axios.get(`/servers/${serverId.value}/storageGroups/${val}/devices`).then((rs) => {
        if (rs && rs.code == 0) {
          scope.row.allDevicePaths = rs.data || [];
        }
      });
    };
    /**
     * 根据deviceName获取该设备下的测点
     * val设备名称
     * scope当前行数据
     */
    const getTimeSeriesByDeviceName = (val, scope) => {
      scope.row.timeseriesPaths = [];
      axios.get(`/servers/${serverId.value}/storageGroups/${scope.row.groupPaths[0]}/devices/${val}/timeseries`).then((rs) => {
        if (rs && rs.code == 0) {
          scope.row.allTimeseriesPaths = rs.data || [];
        }
      });
    };
    /**
     * 删除某一行存储组信息
     */
    const deleteGroup = (scope) => {
      axios.delete(`/servers/${serverId.value}/storageGroups/${scope.row.groupName}`).then((rs) => {
        if (rs && rs.code == 0) {
          ElMessage.success(t('sourcePage.deleteGroupLabel'));
          props.func.updateTreeByIds([serverId.value + 'connection']);

          getGroupList();
        }
      });
    };
    /**
     * 跳转编辑存储组 type为了区分是去往存储组编辑页
     */
    const goEditGroup = (scope) => {
      //先获取数据(防止是收起状态没有加载数据)
      props.func.updateTreeByIds([serverId.value + 'connection']);
      //展开数据
      props.func.expandByIds([serverId.value + 'connection']);
      props.func.addTab(serverId.value + 'connection' + scope.row.groupName + 'storageGroup', { type: 'edit' }, true);
    };
    /**
     * 查看存储组详情
     */
    const goGroupDetail = (scope) => {
      //先获取数据(防止是收起状态没有加载数据)
      props.func.updateTreeByIds([serverId.value + 'connection']);
      //展开数据
      props.func.expandByIds([serverId.value + 'connection']);
      props.func.addTab(serverId.value + 'connection' + scope.row.groupName + 'storageGroup', {}, true);
    };
    onMounted(() => {
      // serverId.value = router.currentRoute.value.params.serverid;
      // getBaseInfo((data) => {
      //   //此处调用用户权限接口是为了判断当前登入连接用户是否有各项权限
      //   getUserAuth(data, 1);
      // });
      // getGroupList();
      // getUserList(1);
    });
    onActivated(() => {
      serverId.value = router.currentRoute.value.params.serverid;
      getBaseInfo((data) => {
        //此处调用用户权限接口是为了判断当前登入连接用户是否有各项权限
        getUserAuth(data, 1);
      });
      getGroupList();
      getUserList(1);
    });
    return {
      editSource,
      close,
      deleteSource,
      successFunc,
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
      doCreate,
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
      saveRowAuth,
      cancelRowAuth,
      authAdd,
      doEdit,
      deleteRowAuth,
      changeType,
      getDeviceByGroupName,
      getTimeSeriesByDeviceName,
      deleteGroup,
      canCreateUser,
      canGroupSet,
      canDeleteUser,
      canModifyPassword,
      canShowUser,
      canAuth,
      goGroupDetail,
      goEditGroup,
      cancelNew1,
      groupTotal,
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
    ElPopconfirm,
    ElTooltip,
    /* eslint-disable */
    ElPopover,
    ElPopper,
    /* eslint-disable */
  },
};
</script>

<style scoped lang="scss">
.source-detail-container {
  height: 100%;
  text-align: left;
  &:deep(.el-tabs__nav-wrap::after) {
    display: none !important;
  }

  &:deep(.el-tabs__header .el-tabs__nav .el-tabs__item.is-active) {
    background: #fff !important;
  }
  .icon-del {
    position: absolute;
    top: 0px;
    right: 40px;
    color: #d32d2fff;
  }
  .del-user {
    right: 0;
  }
  .info-box {
    padding: 10px 20px 14px;
    position: relative;
    .icon {
      position: absolute;
      top: 19px;
    }

    .icon-edit {
      right: 60px;
      color: $theme-color;
    }
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
        border-right: 1px solid #e0e0e0;
        .title {
          height: 17px;
          font-size: 12px;
          font-weight: 400;
          line-height: 20px;
          font-size: 12px;
          color: #d32d2fff;
          line-height: 16px;
          margin-left: 10px;
          padding: 3px 8px;
          color: rgba(34, 34, 34, 0.65);
          padding: 0 8px 10px 16px;

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
              height: 36px;
            }
            .icon {
              position: absolute;
              right: 16px;
              top: 12px;
            }
          }
        }
      }
      .right-part {
        flex: 1;
        position: relative;
        &:deep(.el-input__suffix .el-input__icon) {
          line-height: 42px !important;
        }
        .auth-add-btn {
          position: absolute;
          right: 10px;
          top: 6px;
          z-index: 1000;
        }

        .tabs {
          position: relative;
        }
        .show-only {
          &:deep(.el-checkbox) {
            display: none;
          }
          &:deep(.is-checked) {
            display: inline-block !important;
            .is-checked {
              display: none !important;
            }
          }
          &:deep(.el-checkbox__input) {
            display: none;
          }
        }
        &:deep(.el-checkbox__input.is-disabled + span.el-checkbox__label) {
          color: #222222;
          cursor: default;
        }
        &:deep(.el-checkbox__label) {
          font-size: 12px !important;
          font-weight: 400;
        }
        &:deep(.el-input .el-input__inner) {
          font-size: 12px !important;
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
        .auth-tips {
          font-size: 12px;
          color: #d32d2fff;
          line-height: 16px;
          margin-left: 10px;
          background: rgba(211, 45, 47, 0.04);
          padding: 3px 8px;
          margin: 16px 18px 16px 14px;
        }
        .tab-content {
          padding: 10px 16px;
          .password {
            div {
              max-width: 200px;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }
          }
          .user-name {
            max-width: calc(100% - 50px);

            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }
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
      .el-button {
        padding-left: 0 !important;
      }
    }
  }
}
</style>
