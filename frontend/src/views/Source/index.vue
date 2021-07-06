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
      <svg class="icon icon-del" aria-hidden="true" @click="deleteSource()">
        <use xlink:href="#icon-se-icon-delete"></use>
      </svg>
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
            <li v-for="(item, index) in userList" :class="activeIndex == item.username ? 'active' : ''" :key="index" @click="handleUser(index, item)">
              <span class="content">{{ item.username }}</span>
              <svg v-if="activeIndex == item.username" class="icon" aria-hidden="true" @click="deleteUser(item)">
                <use xlink:href="#icon-se-icon-delete"></use>
              </svg>
            </li>
          </ul>
        </div>
        <div class="right-part">
          <el-button class="auth-add-btn" type="text" v-if="activeName == '2'" @click="authAdd()">添加权限</el-button>
          <el-tabs v-model="activeName" @tab-click="handleClick" class="tabs">
            <el-tab-pane :label="$t('sourcePage.baseConfig')" name="1">
              <template v-if="activeIndex !== null">
                <div v-if="!isNew" class="tab-content left-base-content">
                  <el-form ref="baseInfoFormRef" :model="baseInfoForm" :rules="baseRules" label-position="top" class="source-form">
                    <el-form-item :label="$t('sourcePage.userNameTitle')">
                      <div class="user-name">{{ baseInfoForm.userName }}</div>
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
                        <el-button @click="cancelNew()">{{ $t('common.cancel') }}</el-button>
                        <el-button type="primary" @click="doCreate()">{{ $t('common.submit') }}</el-button>
                      </div>
                    </el-form-item>
                  </el-form>
                </div>
              </template>
            </el-tab-pane>
            <el-tab-pane v-if="!isNew" :label="$t('sourcePage.accountPermit')" name="2">
              <template v-if="activeIndex !== null">
                <el-table :data="authTableData" style="width: 100%">
                  <el-table-column show-overflow-tooltip :label="$t('sourcePage.path')" width="180">
                    <template #default="scope">
                      <span v-if="!scope.row.edit">{{ pathMap[scope.row.type] }}</span>
                      <el-select v-if="scope.row.edit" v-model="scope.row.type" :disabled="!scope.row.new" @change="changeType(scope.row.type, scope)">
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
                          <el-select class="row-select-range" v-model="scope.row.groupPaths[0]" @change="getDeviceByGroupName(scope.row.groupPaths[0], scope)">
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
                        <el-button type="text" size="small" @click="changeEditState(scope)">{{ $t('common.edit') }}</el-button>
                        <el-button type="text" size="small" class="el-button-delete" @click="deleteRowAuth(scope)">{{ $t('common.delete') }}</el-button>
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
        {{ $t('sourcePage.groupInfo') }}
      </div>
      <el-table :data="tableData" class="group-table" max-height="280" height="280">
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
            <el-button type="text" size="small" class="el-button-delete" :disable="canGroupSet" @click="deleteGroup(scope)"> {{ $t('common.delete') }} </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <NewSource v-if="showDialog" :serverId="serverId" :showDialog="showDialog" :types="types" @close="close()" @successFunc="successFunc(data)" />
  </div>
</template>

<script>
// @ is an alias to /src
import { onMounted, reactive, ref } from 'vue';
import { ElButton, ElTable, ElTableColumn, ElTabs, ElTabPane, ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElCheckbox, ElCheckboxGroup, ElMessage } from 'element-plus';
import NewSource from './components/newSource.vue';
import { useI18n } from 'vue-i18n';
import axios from '@/util/axios.js';
// import { useStore } from 'vuex';

import { useRouter } from 'vue-router';
export default {
  name: 'Source',
  setup() {
    const { t } = useI18n();
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
    /**
     * 当前连接下所有存储组信息
     */
    let allGroupPaths = ref([]);
    let activeIndex = ref(null);
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
          ElMessage.success('删除连接成功');
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
    const successFunc = (data) => {
      console.log(data);
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
      activeIndex.value = item.username;
      getUserAuth(item);
    };
    /**
     * 编辑用户基本信息(密码)
     */
    const editBaseInfo = () => {
      if (!canModifyPassword.value) {
        ElMessage.error('您当前没有权限操作');
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
        ElMessage.error('请填写密码');
        return false;
      }
      let reqObj = {
        userName: baseInfoForm.userName,
        password: baseInfoForm.password,
      };
      axios.post(`/servers/${serverId.value}/users/pwd`, { ...reqObj }).then((rs) => {
        if (rs && rs.code == 0) {
          ElMessage.success('修改用户密码成功');
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
              ElMessage.success('新建用户成功');
              cancelNew();
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
        ElMessage.error('您当前没有权限操作');
        return false;
      }
      axios.delete(`/servers/${serverId.value}/users/${item.username}`).then((rs) => {
        if (rs && rs.code == 0) {
          ElMessage.success('删除用户成功');
          activeIndex.value = null;
          getUserList();
        }
      });
    };
    /**
     * 新建用户操作
     */
    const newUser = () => {
      if (!canCreateUser.value) {
        ElMessage.error('您当前没有权限操作');
        return false;
      }
      for (let i = 0; i < userList.value.length; i++) {
        if (userList.value[i].username == 'new') {
          ElMessage.error('请先完成当前新增账号的操作');
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
     */
    const cancelNew = () => {
      isNew.value = false;
      activeIndex.value == 'new' && (activeIndex.value = null);
      userList.value.shift();
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
      console.log(scope.$index);
      authTableData.value[scope.$index].edit = true;
    };
    /**
     * 获取头部数据连接基本信息
     */
    const getBaseInfo = (func) => {
      axios.get(`/servers/${serverId.value}`, {}).then((res) => {
        if (res && res.code == 0) {
          baseInfo.value = res.data;
          func && func(res.data);
        }
      });
    };
    /**
     * 添加权限按钮
     */
    const authAdd = () => {
      if (!canAuth.value) {
        ElMessage.error('您当前没有权限操作');
        return false;
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
          userAuthInfo = {};
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
          let temp = [];
          for (let i = 0; i < res.data.length; i++) {
            temp.push(res.data[i].groupName);
          }
          allGroupPaths.value = temp;
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
          if (type == 1) {
            activeIndex.value = userList.value.length && userList.value[0].username;
            getUserAuth(userList.value[0]);
          }
        }
      });
    };
    /**
     * 删除某一行权限
     * scope当前行数据
     */
    const deleteRowAuth = (scope) => {
      let reqObj = scope.row;
      reqObj.cancelPrivileges = scope.row.privileges;
      reqObj.privileges = [];
      axios.post(`/servers/${serverId.value}/users/${activeIndex.value}`, { ...reqObj }).then((rs) => {
        if (rs && rs.code == 0) {
          ElMessage.success('删除权限成功');
          getUserAuth({ username: activeIndex.value });
        }
      });
    };
    /**
     * 编辑某一行权限
     * scope当前行数据
     */
    const saveRowAuth = (scope) => {
      let reqObj = scope.row;

      if (scope.row.new) {
        //用户新增权限
        reqObj.cancelPrivileges = [];
      } else {
        // 缓存权限信息
        let tempRow = userAuthInfoTemp.value.privilegesInfo[scope.$index].privileges;
        // 用户删除的权限
        let deleteArr = tempRow.filter(function (val) {
          return scope.row.privileges.indexOf(val) === -1;
        });
        // 用户新增的权限
        let newArr = scope.row.privileges.filter(function (val) {
          return tempRow.indexOf(val) === -1;
        });

        reqObj.privileges = newArr;
        reqObj.cancelPrivileges = deleteArr;
      }

      axios.post(`/servers/${serverId.value}/users/${activeIndex.value}`, { ...reqObj }).then((rs) => {
        if (rs && rs.code == 0) {
          ElMessage.success('操作权限成功');
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
      console.log(val);
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
          ElMessage.success('删除存储组成功');
          getGroupList();
        }
      });
    };
    onMounted(() => {
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
    position: relative;
    .icon {
      position: absolute;
      top: 19px;
    }
    .icon-del {
      right: 20px;
      color: #d32d2fff;
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
              top: 12px;
            }
          }
        }
      }
      .right-part {
        flex: 1;
        position: relative;
        .auth-add-btn {
          position: absolute;
          right: 10px;
          top: 6px;
          z-index: 10000;
        }
        &::v-deep .el-tabs__active-bar {
          // width: 23px !important;
          // margin-left: 14px;
        }
        .tabs {
          position: relative;
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
          .user-name {
            max-width: 200px;
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
      // overflow: auto;
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
