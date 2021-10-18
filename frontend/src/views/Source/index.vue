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
    <div>
      <permit-dialog ref="permitDialogRef" @submit="submitPermit"></permit-dialog>
    </div>
    <div v-if="showRoleDialog">
      <role-dialog :showRoleDialog="showRoleDialog" :type="roleType" :editList="roleCheckeList" :serverId="serverId" @cancelRoleDialog="cancelRoleDialog" @submitRoleDialog="submitRoleDialog" />
    </div>
    <div class="info-box">
      <p class="title">{{ baseInfo.alias }}</p>
      <p class="more">
        <span
          ><span class="more-title">{{ $t('sourcePage.host') + ':' }}</span
          >{{ baseInfo.host }}</span
        >
        <span
          ><span class="more-title">{{ $t('sourcePage.port') + ':' }}</span
          >{{ baseInfo.port }}</span
        >
      </p>
      <p class="more last">
        <span
          ><span class="more-title">{{ $t('sourcePage.storageNum') + ':' }}</span
          >{{ baseInfo.host }}</span
        >
        <span
          ><span class="more-title">{{ $t('sourcePage.entityNum') + ':' }}</span
          >{{ baseInfo.port }}</span
        >
        <span
          ><span class="more-title">{{ $t('sourcePage.physicalNum') + ':' }}</span
          >{{ baseInfo.port }}</span
        >
        <span
          ><span class="more-title">{{ $t('sourcePage.dataNum') + ':' }}</span
          >{{ baseInfo.port }}</span
        >
      </p>
      <div class="buttons">
        <el-button @click="editSource()">
          <svg class="icon icon-edit" aria-hidden="true">
            <use xlink:href="#icon-se-icon-f-edit"></use>
          </svg>
          {{ $t('common.edit') }}</el-button
        >

        <el-popconfirm placement="top" :title="$t('sourcePage.deleteSourceConfirm')">
          <template #reference>
            <el-button @confirm="deleteSource()" class="button-delete"
              ><svg aria-hidden="true" class="icon icon-delete">
                <use xlink:href="#icon-se-icon-delete"></use></svg
              >{{ $t('common.delete') }}</el-button
            >
          </template>
        </el-popconfirm>
      </div>
    </div>
    <div class="tabs-wraper">
      <el-tabs v-model="sourceTabs" @tab-click="handleClickSource" class="tabs">
        <el-tab-pane :label="$t('sourcePage.dataModel')" name="d">
          <div class="tab-content">
            <el-button class="button-special title" @click="goToAllModal()">查看更多</el-button>
            <DataModal></DataModal>
          </div>
        </el-tab-pane>
        <el-tab-pane :label="$t('sourcePage.accountPermit')" name="a">
          <div class="tab-content">
            <div class="permit-content">
              <div class="left-part">
                <!-- <p class="title clearfix">
                  <el-button type="text" @click="newUser()">{{ $t('sourcePage.newAccount') }}</el-button>
                </p> -->
                <el-button class="button-special title clearfix" @click="newUser()">
                  <span>{{ $t('sourcePage.userAccount') }}</span>
                  <svg class="icon" aria-hidden="true">
                    <use xlink:href="#icon-add1"></use></svg
                ></el-button>
                <ul class="user-list">
                  <li v-for="(item, index) in userList" :class="activeIndex == item.username ? 'active' : ''" :key="index" @click="handleUser(index, item)">
                    <!-- <el-tooltip class="item" width="200" effect="dark" :content="item.username" placement="top"> -->
                    <div class="active-circle">
                      <div class="small-circle"></div>
                    </div>
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
                <el-button class="auth-add-btn" type="text" v-if="activeName == '2' && baseInfoForm.userName !== 'root'" @click="authAdd(0)">{{ $t('sourcePage.addAuthBtn') }}</el-button>
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

                          <el-form-item :label="$t('sourcePage.roleTitle')" class="role-form-item">
                            <svg v-if="!editRole && baseInfoForm.userName != 'root'" class="icons icon" aria-hidden="true" @click="editRoleInfo()">
                              <use xlink:href="#icon-se-icon-f-edit"></use>
                            </svg>
                            <ul>
                              <li v-for="item in roleCheckeList" :key="item">
                                <span>{{ item }}</span>
                                <svg class="icon" aria-hidden="true" v-if="editRole" @click="deleteRole(item)">
                                  <use xlink:href="#icon-close"></use>
                                </svg>
                              </li>
                            </ul>
                            <svg class="icon" aria-hidden="true" v-if="editRole" @click="addRole(1)">
                              <use xlink:href="#icon-add1"></use>
                            </svg>
                            <div v-if="editRole">
                              <el-button @click="cancelEditRole()">{{ $t('common.cancel') }}</el-button>
                              <el-button type="primary" @click="doEditRole()">{{ $t('common.submit') }}</el-button>
                            </div>
                          </el-form-item>
                        </el-form>
                      </div>
                      <div v-else class="tab-content left-base-content">
                        <el-form ref="baseInfoFormRef" :model="baseInfoForm" :rules="baseRules" label-position="top" class="source-form">
                          <el-form-item prop="userName" :label="$t('sourcePage.userNameTitle')" class="userName-form-item">
                            <el-input v-model="baseInfoForm.userName" prefix-icon="el-icon-user"></el-input>
                          </el-form-item>
                          <el-form-item :label="$t('sourcePage.passwordTitle')" prop="password" class="password-form-item">
                            <el-input show-password v-model="baseInfoForm.password" prefix-icon="el-icon-lock"></el-input>
                          </el-form-item>
                          <el-form-item :label="$t('sourcePage.roleTitle')" class="role-form-item">
                            <ul>
                              <li v-for="item in roleCheckeList" :key="item">
                                <span>{{ item }}</span>
                                <svg class="icon" aria-hidden="true" @click="deleteRole(item)">
                                  <use xlink:href="#icon-close"></use>
                                </svg>
                              </li>
                            </ul>
                            <svg class="icon" aria-hidden="true" @click="addRole(0)">
                              <use xlink:href="#icon-add1"></use>
                            </svg>
                          </el-form-item>
                          <div>
                            <el-button @click="cancelNew1()">{{ $t('common.cancel') }}</el-button>
                            <el-button type="primary" @click="doCreate()">{{ $t('common.submit') }}</el-button>
                          </div>
                        </el-form>
                      </div>
                    </template>
                  </el-tab-pane>
                  <el-tab-pane v-if="!isNew" :label="$t('sourcePage.accountPermit')" name="2">
                    <template v-if="activeIndex !== null">
                      <p class="auth-tips">{{ $t('sourcePage.authTips') }}</p>
                      <el-table :data="authTableData" style="width: 100%">
                        <el-table-column show-overflow-tooltip :label="$t('sourcePage.path')" width="80">
                          <template #default="scope">
                            <span v-if="!scope.row.edit">{{ pathMap[scope.row.type] }}</span>
                            <el-select v-if="scope.row.edit" v-model="scope.row.type" :disabled="!scope.row.new" @change="changeType(scope.row.type, scope)">
                              <el-option v-for="item in pathList" :key="item.value" :label="item.label" :value="item.value"></el-option>
                            </el-select>
                          </template>
                        </el-table-column>
                        <el-table-column :label="$t('sourcePage.range')" width="280">
                          <template #default="scope">
                            <div v-if="scope.row.type == 0">-</div>
                            <div v-else-if="scope.row.type == 1">
                              <div v-if="scope.row.edit">
                                <el-select v-model="scope.row.groupPaths" multiple>
                                  <el-option v-for="item in scope.row.allGroupPaths" :key="item" :value="item" :label="item"></el-option>
                                </el-select>
                              </div>
                              <div v-else>
                                <span>{{ $t('sourcePage.groupNameLabel') }} {{ scope.row.groupPaths.length || 0 }} 个</span>
                                <el-popover placement="top" width="180" trigger="hover">
                                  <div style="margin: 0">
                                    {{ $t('sourcePage.groupNameLabel') }}
                                    <div v-for="item in scope.row.groupPaths" :key="item" class="device-path">{{ item }}</div>
                                  </div>
                                  <template v-slot:reference>
                                    <div class="popover-btns">
                                      <svg class="icon" aria-hidden="true">
                                        <use xlink:href="#icon-se-icon-caret-bottom"></use>
                                      </svg>
                                    </div>
                                  </template>
                                </el-popover>
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
                                <span>{{ $t('sourcePage.groupNameLabel') }} 1 个</span> &nbsp;&nbsp;| &nbsp;&nbsp;
                                <span>{{ $t('sourcePage.deviceNameLabel') }} {{ scope.row.devicePaths.length || 0 }} 个</span>
                                <el-popover placement="top" width="180" trigger="hover">
                                  <div style="margin: 0">
                                    <p>{{ $t('sourcePage.groupNameLabel') }}</p>
                                    <div class="device-path">{{ scope.row.groupPaths[0] }}</div>
                                    <p>{{ $t('sourcePage.deviceNameLabel') }}</p>
                                    <div v-for="item in scope.row.devicePaths" :key="item" class="device-path">{{ item }}</div>
                                  </div>
                                  <template v-slot:reference>
                                    <div class="popover-btns">
                                      <svg class="icon" aria-hidden="true">
                                        <use xlink:href="#icon-se-icon-caret-bottom"></use>
                                      </svg>
                                    </div>
                                  </template>
                                </el-popover>
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
                                <span>{{ $t('sourcePage.groupNameLabel') }} 1 个</span> &nbsp;&nbsp;| &nbsp;&nbsp; <span>{{ $t('sourcePage.deviceNameLabel') }} 1 个</span> &nbsp;&nbsp;| &nbsp;&nbsp;
                                <span>{{ $t('sourcePage.timeNameLabel') }} {{ scope.row.timeseriesPaths.length || 0 }} 个</span>
                                <el-popover placement="top" width="180" trigger="hover">
                                  <div style="margin: 0">
                                    <p>{{ $t('sourcePage.groupNameLabel') }}</p>
                                    <div class="device-path">{{ scope.row.groupPaths[0] }}</div>
                                    <p>{{ $t('sourcePage.deviceNameLabel') }}</p>
                                    <div class="device-path">{{ scope.row.devicePaths[0] }}</div>
                                    <p>{{ $t('sourcePage.timeNameLabel') }}</p>
                                    <div v-for="item in scope.row.timeseriesPaths" :key="item" class="device-path">{{ item }}</div>
                                  </div>
                                  <template v-slot:reference>
                                    <div class="popover-btns">
                                      <svg class="icon" aria-hidden="true">
                                        <use xlink:href="#icon-se-icon-caret-bottom"></use>
                                      </svg>
                                    </div>
                                  </template>
                                </el-popover>
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
                  <el-tab-pane v-if="!isNew" :label="$t('sourcePage.permitPermission')" name="3">
                    <div class="permitpermission-content">
                      <p class="tips">{{ $t('sourcePage.permitTips') }}</p>
                      <div class="permit-list">
                        <div class="permit-list-type">
                          <div class="box box1"><el-checkbox v-model="userRelationAll" :disabled="baseInfoForm.userName == 'root'" label="用户相关" @change="changeUserRelation()"></el-checkbox></div>
                          <el-checkbox-group v-model="userRelationItems" class="wraper" :disabled="baseInfoForm.userName == 'root'">
                            <el-checkbox v-for="item in userRelationList[0]" :label="item.id" :key="item.id">{{ item.label }}</el-checkbox>
                          </el-checkbox-group>
                        </div>
                        <div class="permit-list-type">
                          <div class="box box2"><el-checkbox v-model="roleRelationAll" :disabled="baseInfoForm.userName == 'root'" label="角色相关" @change="changeRoleRelation()"></el-checkbox></div>
                          <el-checkbox-group v-model="roleRelationItems" :disabled="baseInfoForm.userName == 'root'">
                            <el-checkbox v-for="item in userRelationList[1]" :label="item.id" :key="item.id">{{ item.label }}</el-checkbox>
                          </el-checkbox-group>
                        </div>
                        <div class="permit-list-type">
                          <div class="box box3"><el-checkbox v-model="udfRelationAll" :disabled="baseInfoForm.userName == 'root'" label="UDF" @change="changeUdfRelation()"></el-checkbox></div>
                          <el-checkbox-group v-model="udfRelationItems">
                            <el-checkbox v-for="item in userRelationList[2]" :disabled="baseInfoForm.userName == 'root'" :label="item.id" :key="item.id">{{ item.label }}</el-checkbox>
                          </el-checkbox-group>
                        </div>
                        <div class="permit-list-type">
                          <div class="box box4">
                            <el-checkbox v-model="triggerRelationAll" :disabled="baseInfoForm.userName == 'root'" label="触发器" @change="changeTriggerRelation()"></el-checkbox>
                          </div>
                          <el-checkbox-group v-model="triggerRelationItems" :disabled="baseInfoForm.userName == 'root'">
                            <el-checkbox v-for="item in userRelationList[3]" :label="item.id" :key="item.id">{{ item.label }}</el-checkbox>
                          </el-checkbox-group>
                        </div>
                      </div>

                      <div class="permit-btn" v-if="baseInfoForm.userName !== 'root'">
                        <el-button type="primary" size="small" @click="savepermitAuth()">{{ $t('common.save') }}</el-button>
                        <el-button type="primary" size="small" @click="getPermitPermissionList({})">{{ $t('common.cancel') }}</el-button>
                      </div>
                    </div>
                  </el-tab-pane>
                </el-tabs>
              </div>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane :label="$t('sourcePage.accountRole')" name="r">
          <div class="tab-content">
            <user-role :base-info="baseInfoForm"></user-role>
          </div>
        </el-tab-pane>
        <el-tab-pane :label="$t('sourcePage.groupInfo') + '(' + groupTotal + ')'" name="g">
          <div class="tab-content">
            <el-table :data="tableData" class="group-table" height="100%">
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
        </el-tab-pane>
      </el-tabs>
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
// import { Close } from '@element-plus/icons';
import NewSource from './components/newSource.vue';
import UserRole from './components/role/Index.vue';
import { useI18n } from 'vue-i18n';
import DataModal from './components/dataModal.vue';
import axios from '@/util/axios.js';
import setOperation from '@/util/setOperation.js';

// import { useStore } from 'vuex';

import { useRouter } from 'vue-router';
import PermitDialog from './components/permitDialog.vue';
import api from './api/index';

import roleDialog from './components/roleDialog.vue';
export default {
  name: 'Source',
  props: ['func', 'data'],
  setup(props) {
    const { t, locale } = useI18n();
    // const store = useStore();

    let showDialog = ref(false);
    let permitDialogRef = ref(null);
    let types = ref(0);
    let activeName = ref('1');
    let sourceTabs = ref('d');
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
    // 是否可以用户赋权角色
    let canAuthRole = ref(false);
    const router = useRouter();
    let selectArray = ['一级 1'];
    let checkedKeys = [1];
    let treeData = [
      {
        id: 1,
        label: '一级 1',
        children: [
          {
            id: 4,
            label: '二级 1-1',
          },
        ],
      },
      {
        id: 2,
        label: '一级 2',
        children: [
          {
            id: 5,
            label: '二级 2-1',
          },
          {
            id: 6,
            label: '二级 2-2',
          },
        ],
      },
      {
        id: 3,
        label: '一级 3',
        children: [
          {
            id: 7,
            label: '二级 3-1',
          },
          {
            id: 8,
            label: '二级 3-2',
          },
        ],
      },
    ];
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
        // { id: 'CREATE_USER', label: t('sourcePage.createUser') },
        // { id: 'DELETE_USER', label: t('sourcePage.deleteUser') },
        // { id: 'MODIFY_PASSWORD', label: t('sourcePage.editPassword') },
        // { id: 'LIST_USER', label: t('sourcePage.listUser') },
        // {
        //   id: 'GRANT_USER_PRIVILEGE',
        //   label: t('sourcePage.grantPrivilege'),
        // },
        // {
        //   id: 'REVOKE_USER_PRIVILEGE',
        //   label: t('sourcePage.revertPrivilege'),
        // },
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
        // { id: 'CREATE_TRIGGER', label: t('sourcePage.createTrigger') },
        // { id: 'DROP_TRIGGER', label: t('sourcePage.uninstallTrigger') },
        // { id: 'START_TRIGGER', label: t('sourcePage.startTrigger') },
        // { id: 'STOP_TRIGGER', label: t('sourcePage.stopTrigger') },
        // { id: 'CREATE_FUNCTION', label: t('sourcePage.createFunction') },
        // { id: 'DROP_FUNCTION', label: t('sourcePage.uninstallFunction') },
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
    let funcTypeThree = () => {
      return [
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
    let userRelationListFunc = () => {
      return [
        {
          id: 'LIST_USER',
          label: t('sourcePage.listUser'),
        },
        {
          id: 'CREATE_USER',
          label: t('sourcePage.createUser'),
        },
        { id: 'DELETE_USER', label: t('sourcePage.deleteUser') },
        { id: 'MODIFY_PASSWORD', label: t('sourcePage.editPassword') },
        {
          id: 'GRANT_USER_PRIVILEGE',
          label: t('sourcePage.grantPrivilege'),
        },
        {
          id: 'REVOKE_USER_PRIVILEGE',
          label: t('sourcePage.revertPrivilege'),
        },
        {
          id: 'GRANT_USER_ROLE',
          label: t('sourcePage.grantUserRole'),
        },
        {
          id: 'REVOKE_USER_ROLE',
          label: t('sourcePage.revokeUserRole'),
        },
      ];
    };
    let roleRelationListFunc = () => {
      return [
        {
          id: 'LIST_ROLE',
          label: t('sourcePage.listRole'),
        },

        { id: 'CREATE_ROLE', label: t('sourcePage.createRole') },
        { id: 'DELETE_ROLE', label: t('sourcePage.deleteRole') },
        {
          id: 'GRANT_ROLE_PRIVILEGE',
          label: t('sourcePage.grantRolePrivilege'),
        },
        {
          id: 'REVOKE_ROLE_PRIVILEGE',
          label: t('sourcePage.revertRolePrivilege'),
        },
      ];
    };
    let udfRelationListFunc = () => {
      return [
        {
          id: 'CREATE_FUNCTION',
          label: t('sourcePage.createFunction'),
        },
        {
          id: 'DROP_FUNCTION',
          label: t('sourcePage.uninstallFunction'),
        },
      ];
    };
    let triggerRelationListFunc = () => {
      return [
        { id: 'CREATE_TRIGGER', label: t('sourcePage.createTrigger') },
        { id: 'DROP_TRIGGER', label: t('sourcePage.uninstallTrigger') },
        { id: 'START_TRIGGER', label: t('sourcePage.startTrigger') },
        { id: 'STOP_TRIGGER', label: t('sourcePage.stopTrigger') },
      ];
    };
    let userRelationList = ref({
      0: userRelationListFunc(),
      1: roleRelationListFunc(),
      2: udfRelationListFunc(),
      3: triggerRelationListFunc(),
    });
    const funcList = ref({
      0: funcTypeOne(),
      1: funcTypeOne(),
      2: funcTypeTwo(),
      3: funcTypeThree(),
    });
    const userRelationAll = ref(false);
    const userRelationItems = ref([]);
    const roleRelationAll = ref(false);
    const roleRelationItems = ref([]);
    const udfRelationAll = ref(false);
    const udfRelationItems = ref([]);
    const triggerRelationAll = ref(false);
    const triggerRelationItems = ref([]);
    /**
     * 数据管理权限弹框是否展示
     */
    let permitType = ref(0);
    watch(locale, () => {
      funcList.value = {
        0: funcTypeOne(),
        1: funcTypeOne(),
        2: funcTypeTwo(),
        3: funcTypeThree(),
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
     * 权限管理权限列表及备份
     */
    let permitPermissionListTemp = ref([]);
    /**
     * 查看完整数据模型树
     */
    const goToAllModal = () => {
      props.func.addTab(`${serverId.value}connection`, { type: 'modal' });
    };
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
      if (tab.paneName == '3') {
        getPermitPermissionList({});
      }
    };
    /**
     * 保存用户权限管理权限
     */
    const savepermitAuth = () => {
      let permitList = userRelationItems.value.concat(roleRelationItems.value).concat(udfRelationItems.value).concat(triggerRelationItems.value);
      let deletePermitList = permitPermissionListTemp.value.filter(function (val) {
        return permitList.indexOf(val) === -1;
      });
      let reqObj = {
        cancelPrivileges: deletePermitList,
        privileges: permitList,
      };
      axios.post(`/servers/${serverId.value}/users/${baseInfoForm.userName}/authorityPrivilege`, { ...reqObj }).then((rs) => {
        console.log(rs);
      });
    };
    /**
     * 获取用户权限管理权限
     */
    const getPermitPermissionList = (userinfo, type) => {
      axios.get(`/servers/${serverId.value}/users/${userinfo.username || baseInfoForm.userName}/authorityPrivilege`, {}).then((rs) => {
        console.log(rs);
        if (rs && rs.code == 0) {
          permitPermissionListTemp.value = rs.data;
          let userRelationItemsTemp = [];
          let roleRelationItemsTemp = [];
          let udfRelationItemsTemp = [];
          let triggerRelationItemsTemp = [];
          for (let i = 0; i < userRelationList.value[0].length; i++) {
            if (rs.data.indexOf(userRelationList.value[0][i].id) >= 0) {
              userRelationItemsTemp.push(userRelationList.value[0][i].id);
            }
          }
          for (let j = 0; j < userRelationList.value[1].length; j++) {
            if (rs.data.indexOf(userRelationList.value[1][j].id) >= 0) {
              roleRelationItemsTemp.push(userRelationList.value[1][j].id);
            }
          }
          for (let k = 0; k < userRelationList.value[2].length; k++) {
            if (rs.data.indexOf(userRelationList.value[2][k].id) >= 0) {
              udfRelationItemsTemp.push(userRelationList.value[2][k].id);
            }
          }
          for (let m = 0; m < userRelationList.value[3].length; m++) {
            if (rs.data.indexOf(userRelationList.value[3][m].id) >= 0) {
              triggerRelationItemsTemp.push(userRelationList.value[3][m].id);
            }
          }
          console.log(triggerRelationItemsTemp);
          userRelationItems.value = userRelationItemsTemp;
          roleRelationItems.value = roleRelationItemsTemp;
          udfRelationItems.value = udfRelationItemsTemp;
          triggerRelationItems.value = triggerRelationItemsTemp;
          userRelationAll.value = userRelationItemsTemp.length == 8 ? true : false;
          roleRelationAll.value = roleRelationItemsTemp.length == 5 ? true : false;
          udfRelationAll.value = udfRelationItemsTemp.length == 2 ? true : false;
          triggerRelationAll.value = triggerRelationItemsTemp.length == 4 ? true : false;
          if (type && type == 1) {
            checkPermitAuth();
          }
        }
      });
    };
    /**
     * 切换数据源展示类型tab操作
     */
    const handleClickSource = (tab) => {
      sourceTabs.value = tab.paneName;
      if (tab.paneName == 'a') {
        activeName.value = '1';
      }
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
      editRole.value = false;
      activeIndex.value = item.username;
      getUserAuth(item);
      if (activeName.value == '3') {
        getPermitPermissionList({ username: item.username });
      }
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
              let data = { roleList: roleCheckeList.value };
              grantRole(data, () => {
                ElMessage.success(t('sourcePage.addSuccessLabel'));
                cancelNew(reqObj.userName);
              });
            }
          });
        }
      });
    };
    /**
     * 给用户角色赋权
     */
    const grantRole = (data, func) => {
      axios.post(`/servers/${serverId.value}/users/${baseInfoForm.userName}/grant`, data).then((rs) => {
        if (rs && rs.code == 0) {
          func && func();
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
      // todo
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
      roleCheckeList.value = [];
      editRole.value = false;
      edit.value = false;
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
     * 监听全选用户相关操作
     */
    const changeUserRelation = () => {
      if (userRelationAll.value) {
        let temp = [];
        for (let i = 0; i < userRelationList.value[0].length; i++) {
          temp.push(userRelationList.value[0][i].id);
        }
        userRelationItems.value = temp;
      } else {
        userRelationItems.value = [];
      }
    };
    /**
     * 监听全选角色相关操作
     */
    const changeRoleRelation = () => {
      if (roleRelationAll.value) {
        let temp = [];
        for (let i = 0; i < userRelationList.value[1].length; i++) {
          temp.push(userRelationList.value[1][i].id);
        }
        roleRelationItems.value = temp;
      } else {
        roleRelationItems.value = [];
      }
    };
    /**
     * 监听全选udf相关操作
     */
    const changeUdfRelation = () => {
      if (udfRelationAll.value) {
        let temp = [];
        for (let i = 0; i < userRelationList.value[2].length; i++) {
          temp.push(userRelationList.value[2][i].id);
        }
        udfRelationItems.value = temp;
      } else {
        udfRelationItems.value = [];
      }
    };
    /**
     * 监听全选触发器相关操作
     */
    const changeTriggerRelation = () => {
      if (triggerRelationAll.value) {
        let temp = [];
        for (let i = 0; i < userRelationList.value[3].length; i++) {
          temp.push(userRelationList.value[3][i].id);
        }
        triggerRelationItems.value = temp;
      } else {
        triggerRelationItems.value = [];
      }
    };
    let oldValue = ref({});
    /**
     * 切换表格行编辑状态
     * scope:行数据
     * index: 行顺序
     */
    const changeEditState = (scope) => {
      oldValue.value = scope.row;
      // authTableData.value[scope.$index].edit = true;
      permitDialogRef.value.open({ type: 'edit', data: scope.row });
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
    let showRoleDialog = ref(false);
    let editRole = ref(false);
    let roleEditObj = ref([]);
    let roleType = ref(0);
    const editRoleInfo = () => {
      if (!canModifyPassword.value) {
        ElMessage.error(t(`sourcePage.noAuthTip`));
        return false;
      }
      editRole.value = true;
    };
    /**
     * 添加角色
     */
    const addRole = (type) => {
      if (!canAuthRole.value) {
        ElMessage.error(t(`sourcePage.noAuthTip`));
        return false;
      }
      roleType.value = type;
      showRoleDialog.value = true;
    };
    const deleteRole = (item) => {
      let temp = roleCheckeList.value;
      let index = roleCheckeList.value.indexOf(item);
      temp.splice(index, 1);
      roleCheckeList.value = temp;
    };
    const cancelEditRole = () => {
      editRole.value = false;
      getUserInfo({ username: baseInfoForm.userName });
    };
    const doEditRole = () => {
      let temp = roleCheckeListTemp.value;
      let deleteArr = setOperation(temp, roleCheckeList.value);
      let newArr = setOperation(roleCheckeList.value, temp);
      console.log(deleteArr);
      console.log(newArr);
      let data = { roleList: newArr, cancelRoleList: deleteArr };
      grantRole(data, () => {
        ElMessage.success(t('sourcePage.editSuccessLabel'));
        editRole.value = false;
      });
    };
    /**
     * 添加权限按钮
     */
    const authAdd = (type) => {
      if (!canAuth.value) {
        ElMessage.error(t(`sourcePage.noAuthTip`));
        return false;
      }
      permitType.value = type;
      permitDialogRef.value.open({ type: 'add' });
      // for (let i = 0; i < authTableData.value.length; i++) {
      //   if (authTableData.value[i].new) {
      //     ElMessage.error(t(`sourcePage.addAuthFirstLabel`));
      //     return false;
      //   }
      // }
      // authTableData.value.push({
      //   edit: true,
      //   new: true,
      //   privileges: [],
      //   groupPaths: [],
      //   allGroupPaths: allGroupPaths.value,
      //   allDevicePaths: [],
      //   devicePaths: [],
      //   allTimeseriesPaths: [],
      //   timeseriesPaths: [],
      //   type: null,
      // });
    };
    const submitPermit = ({ type, privileges, dialogType, range } = {}) => {
      console.log(type, privileges, dialogType, range);
      let payload = { type };
      let params = {
        serverId: serverId.value,
        userName: baseInfoForm.userName,
      };
      // 处理权限
      let dealPivilege = handlePath('privileges', privileges);
      payload.privileges = privileges;
      payload.cancelPrivileges = dealPivilege.deleteList;
      // 处理存储组
      if (type === 1) {
        payload.groupPaths = range;
      }
      // 处理实体
      if (type === 2) {
        payload.groupPaths = [range.storage];
        payload.devicePaths = range.device;
      }
      // 处理物理量
      if (type === 3) {
        payload.groupPaths = [range.storage];
        payload.devicePaths = [range.device];
        payload.timeseriesPaths = range.time;
      }
      api.editUserDataPrivilege(params, payload);

      permitDialogRef.value.visible = false;
      ElMessage.success(`${dialogType === 'add' ? '新增' : '编辑'}权限成功`);
      // getData();
    };
    const cancelDialog = () => {
      // showPermitDialog.value = false;
    };
    const cancelRoleDialog = () => {
      showRoleDialog.value = false;
    };
    const roleCheckeList = ref([]);
    const roleCheckeListTemp = ref([]);

    const submitRoleDialog = (data) => {
      roleCheckeList.value = data;
      showRoleDialog.value = false;
    };
    const handlePath = (props, List) => {
      console.log(oldValue.value)
      let deleteList = oldValue?.value[props]?.filter((d) => !List.includes(d));
      let addList = List.filter((d) => !oldValue?.value[props]?.includes(d));
      return {
        addList,
        deleteList,
      };
    };
    /**
     * 获取某一个用户权限
     * userinfo:用户信息
     * type: 1用户本身信息
     */
    const getUserAuth = (userinfo, type) => {
      getUserInfo(userinfo);
      axios.get(`/servers/${serverId.value}/users/${userinfo.username}/dataPrivilege`, {}).then((res) => {
        console.log(res);
        if (res && res.code == 0) {
          // userAuthInfo.value = res.data;
          userAuthInfoTemp.value.privileges = JSON.parse(JSON.stringify(res.data || []));
          // baseInfoForm.userName = res.data.userName || userinfo.username;
          // baseInfoForm.password = res.data.password;
          authTableData.value = res.data || [];
          baseInfo.value.privilegesInfo = res.data || [];
          if (type == 1) {
            checkAuth(res.data);
          }
        } else {
          // userAuthInfo.value = {};
          userAuthInfoTemp.value = {};

          authTableData.value = [];
          baseInfo.value.privilegesInfo = [];
        }
      });
    };
    /**
     * 获取用户账号基本信息
     */
    const getUserInfo = (userinfo) => {
      axios.get(`/servers/${serverId.value}/users/${userinfo.username}`).then((rs) => {
        console.log(rs);
        if (rs && rs.code == 0) {
          userAuthInfo.value = rs.data;
          userAuthInfoTemp.value.username = userinfo.username;
          userAuthInfoTemp.value.password = rs.data.password;
          userAuthInfoTemp.value.roleList = rs.data.roleList;
          roleCheckeList.value = rs.data.roleList || [];
          roleCheckeListTemp.value = JSON.parse(JSON.stringify(rs.data.roleList || []));
          baseInfoForm.userName = userinfo.username;
          baseInfoForm.password = rs.data.password;
          baseInfoForm.roleList = rs.data.roleList || [];
        } else {
          userAuthInfo.value = {};
          userAuthInfoTemp.value = {};
          roleCheckeList.value = [];
          roleCheckeListTemp.value = [];

          baseInfoForm.userName = '';
          baseInfoForm.password = '';
          baseInfoForm.roleList = [];
        }
      });
    };
    /**
     * 检查登入用户是否有各项操作权限
     * data:权限数组
     */
    const checkAuth = (data) => {
      for (let i = 0; i < data?.length; i++) {
        if (data[i].type == 0) {
          canGroupSet.value = data[i].privileges.indexOf('SET_STORAGE_GROUP') >= 0 ? true : false;
        }
      }
    };
    const checkPermitAuth = () => {
      canCreateUser.value = permitPermissionListTemp.value.indexOf('CREATE_USER') >= 0 ? true : false;
      canDeleteUser.value = permitPermissionListTemp.value.indexOf('DELETE_USER') >= 0 ? true : false;
      canModifyPassword.value = permitPermissionListTemp.value.indexOf('MODIFY_PASSWORD') >= 0 ? true : false;
      canShowUser.value = permitPermissionListTemp.value.indexOf('LIST_USER') >= 0 ? true : false;
      canAuth.value = permitPermissionListTemp.value.indexOf('GRANT_USER_PRIVILEGE') >= 0 ? true : false;
      canAuthRole.value = permitPermissionListTemp.value.indexOf('GRANT_ROLE_PRIVILEGE') >= 0 ? true : false;
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
        getPermitPermissionList(data, 1);
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
      sourceTabs,
      handleClickSource,
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
      grantRole,
      isNew,
      cancelNew,
      authTableData,
      pathList,
      pathMap,
      treeData,
      selectArray,
      checkedKeys,
      funcTypeOne,
      funcTypeTwo,
      funcTypeThree,
      funcList,
      getBaseInfo,
      changeCheckItem,
      changeEditState,
      userAuthInfo,
      getUserList,
      saveRowAuth,
      cancelRowAuth,
      addRole,
      deleteRole,
      editRoleInfo,
      cancelEditRole,
      doEditRole,
      authAdd,
      submitPermit,
      cancelDialog,
      cancelRoleDialog,
      submitRoleDialog,
      roleCheckeList,
      roleCheckeListTemp,
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
      userRelationList,
      userRelationListFunc,
      userRelationAll,
      userRelationItems,
      roleRelationAll,
      roleRelationItems,
      udfRelationAll,
      udfRelationItems,
      triggerRelationAll,
      triggerRelationItems,
      changeUserRelation,
      changeRoleRelation,
      changeUdfRelation,
      changeTriggerRelation,
      showRoleDialog,
      roleEditObj,
      roleType,
      editRole,
      goToAllModal,
      getPermitPermissionList,
      permitPermissionListTemp,
      savepermitAuth,
      permitDialogRef,
    };
  },
  components: {
    ElButton,
    ElTable,
    ElTableColumn,
    NewSource,
    PermitDialog,
    roleDialog,
    DataModal,
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
    // Close,
    /* eslint-disable */
    ElPopover,
    ElPopper,
    UserRole,
    /* eslint-disable */
  },
};
</script>

<style scoped lang="scss">
.source-detail-container {
  height: calc(100% - 16px);
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
    color: #fb5151ff;
  }
  .del-user {
    right: 0;
  }
  .info-box {
    padding: 10px 20px 14px;
    position: relative;
    // .icon {
    //   position: absolute;
    //   top: 19px;
    // }
  }
  .info-box {
    font-size: 16px;
    font-weight: 500;
    color: #222222;
    border: 1px solid #eaecf0;
    margin: 16px;
    .title {
      margin: 10px 0 20px;
    }
    .more {
      span {
        color: rgba(34, 34, 34, 0.65);
        font-size: 14px;
        margin-right: 40px;
      }
      .more-title {
        color: #828ca1ff;
        margin-right: 8px;
      }
    }
    .last {
      margin-top: 10px;
    }
  }
  .icon {
    cursor: pointer;
  }
  .buttons {
    position: absolute;
    top: 16px;
    right: 16px;

    .icon-edit {
      color: $theme-color;
      margin-right: 4px;
    }
    .icon-delete {
      color: #fb5151ff;
      margin-right: 4px;
    }
  }
  .tabs-wraper {
    height: calc(100% - 130px);
    &:deep(.el-tabs) {
      height: 100%;
    }
    &:deep(.el-tabs__content) {
      background: #f9fbfc;
      padding: 16px;
      height: calc(100% - 74px);
      overflow: auto;
    }
    &:deep(.el-tab-pane) {
      height: 100%;
    }
    .tab-content {
      background: #fff;
      height: calc(100% - 16px * 2);
      border: 1px solid #eaecf0;
      padding: 16px;
      .permit-content {
        display: flex;
        flex-direction: row;
        height: 100%;
        .left-part {
          width: 220px;
          // margin-top: 12px;
          margin-right: 20px;
          .title {
            height: 40px !important;
            width: 220px;
            font-size: 12px;
            background: #f9fbfc;
            line-height: 40px;
            padding: 0 20px;
            margin-bottom: 4px;
            border-color: #f9fbfc;
            text-align: left;
            .icon {
              margin-top: 7px;
              float: right;
            }
            button {
              float: right;
              font-size: 12px;
              padding-right: 0;
            }
          }
          .user-list {
            margin-bottom: 10px;
            overflow: auto;
            background: #f9fbfc;
            padding-top: 10px;
            height: calc(100% - 50px);
            padding-left: 10px;
            .active-circle {
              width: 20px;
              height: 20px;
              background: #edf8f5;
              border-radius: 50%;
              display: inline-block;
              align-items: center;
              justify-content: center;
              margin-right: 10px;
              visibility: hidden;
              position: relative;
              vertical-align: middle;
              .small-circle {
                width: 7px;
                height: 7px;
                background: #13c393;
                border-radius: 50%;
                position: absolute;
                left: 7px;
                top: 7px;
              }
            }
            .active {
              color: #7a859eff;
              background: #ffffff;
              border-radius: 30px 0px 0px 30px;
            }
            li {
              height: 36px;
              line-height: 36px;
              position: relative;
              font-size: 12px;
              padding: 0 10px;
              cursor: pointer;
              .content {
                display: inline-block;
                max-width: 150px;
                overflow: hidden;
                text-overflow: ellipsis;
                height: 36px;
                vertical-align: middle;
              }
              .icon {
                position: absolute;
                right: 16px;
                top: 12px;
              }
              &:hover {
                color: #7a859eff;
                background: #ffffff;
                border-radius: 30px 0px 0px 30px;
                .active-circle {
                  visibility: inherit;
                }
              }
            }
          }
        }
        .right-part {
          flex: 1;
          position: relative;
          &:deep(.el-input__suffix .el-input__icon) {
            // line-height: 42px !important;
          }
          &:deep(.el-input__icon) {
            line-height: 28px !important;
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
            display: block;
          }
          .row-select-range {
            display: block;
          }
          .popover-btns {
            display: inline-block;
          }
          .auth-tips {
            font-size: 12px;
            color: #fb5151ff;
            line-height: 16px;
            margin-left: 10px;
            background: rgba(211, 45, 47, 0.04);
            padding: 3px 8px;
            margin: 16px 18px 16px 14px;
          }
          .permitpermission-content {
            height: 100%;
            .tips {
              color: #fb5151ff;
              font-size: 12px;
              margin-bottom: 16px;
            }
            .wraper {
            }
            .permit-list {
              display: flex;
              height: calc(100% - 60px);

              .permit-list-type {
                flex: 1;
                background: #ffffff;
                border-radius: 4px;
                border: 1px solid #eaecf0;
                margin-right: 10px;
                padding: 16px;
                overflow: auto;
                &:last-child {
                  margin-right: 0;
                }
                .el-checkbox {
                  padding-left: 10px;
                  height: 32px;
                  line-height: 32px;
                }
                .box {
                  border-radius: 4px;
                  height: 40px;
                  line-height: 40px;
                  padding-left: 10px;
                  .el-checkbox {
                    padding-left: 0;
                    height: 40px;
                    line-height: 40px;
                  }
                }
                .box1 {
                  background: #fff9f3;
                }
                .box2 {
                  background: #f3fbff;
                }
                .box3 {
                  background: #fff3f3;
                }
                .box4 {
                  background: #f2fff6;
                }
              }
            }
            .permit-btn {
              text-align: center;
              margin-top: 10px;
            }
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
                top: -21px;
                left: 100px;
                cursor: pointer;
              }
            }
            .role-form-item {
              .icons {
                color: $theme-color;
                position: absolute;
                top: -21px;
                left: 100px;
                cursor: pointer;
              }
              ul {
                display: inline-block;
                li {
                  display: inline-block;
                  background: rgba(19, 195, 147, 0.1);
                  border-radius: 2px;
                  border: 1px solid rgba(19, 195, 147, 0.5);
                  height: 24px;
                  line-height: 24px;
                  padding: 0px 6px;
                  margin-right: 10px;
                  span {
                    // max-width: 20px;
                    // overflow: hidden;
                    // text-overflow: ellipsis;
                    // white-space: nowrap;
                    margin-right: 10px;
                  }
                }
              }
            }
          }
          .left-base-content {
            &:deep(.el-form-item__label) {
              font-size: 12px;
            }
            .el-input {
              width: 200px;
              // display: block;
              .el-input__suffix {
                top: auto;
              }
            }
            // .source-form {
            &:deep(.el-form--label-top .el-form-item__label) {
              //        .el-form--label-top .el-form-item__label {
              padding: 0 !important;
            }
            //       }
            // }
          }
        }
      }
    }
  }
  // .permission-box {
  //   .permit-content {
  //     display: flex;
  //     flex-direction: row;
  //     .left-part {
  //       width: 240px;
  //       margin-top: 12px;
  //       border-right: 1px solid #e0e0e0;
  //       .title {
  //         height: 17px;
  //         font-size: 12px;
  //         font-weight: 400;
  //         line-height: 20px;
  //         font-size: 12px;
  //         color: #fb5151ff;
  //         line-height: 16px;
  //         margin-left: 10px;
  //         padding: 3px 8px;
  //         color: rgba(34, 34, 34, 0.65);
  //         padding: 0 8px 10px 16px;

  //         button {
  //           float: right;
  //           font-size: 12px;
  //           margin-top: -7px;
  //           padding-right: 0;
  //         }
  //       }
  //       .user-list {
  //         margin-bottom: 10px;
  //         max-height: 250px;
  //         overflow: auto;
  //         .active {
  //           background: rgba(69, 117, 246, 0.04);
  //           color: $theme-color;
  //         }
  //         li {
  //           height: 36px;
  //           line-height: 36px;
  //           position: relative;
  //           font-size: 12px;
  //           padding: 0 16px;
  //           cursor: pointer;
  //           .content {
  //             display: inline-block;
  //             max-width: 150px;
  //             overflow: hidden;
  //             text-overflow: ellipsis;
  //             height: 36px;
  //           }
  //           .icon {
  //             position: absolute;
  //             right: 16px;
  //             top: 12px;
  //           }
  //         }
  //       }
  //     }
  //     .right-part {
  //       flex: 1;
  //       position: relative;
  //       &:deep(.el-input__suffix .el-input__icon) {
  //         line-height: 42px !important;
  //       }
  //       .auth-add-btn {
  //         position: absolute;
  //         right: 10px;
  //         top: 6px;
  //         z-index: 1000;
  //       }

  //       .tabs {
  //         position: relative;
  //       }
  //       .show-only {
  //         &:deep(.el-checkbox) {
  //           display: none;
  //         }
  //         &:deep(.is-checked) {
  //           display: inline-block !important;
  //           .is-checked {
  //             display: none !important;
  //           }
  //         }
  //         &:deep(.el-checkbox__input) {
  //           display: none;
  //         }
  //       }
  //       &:deep(.el-checkbox__input.is-disabled + span.el-checkbox__label) {
  //         color: #222222;
  //         cursor: default;
  //       }
  //       &:deep(.el-checkbox__label) {
  //         font-size: 12px !important;
  //         font-weight: 400;
  //       }
  //       &:deep(.el-input .el-input__inner) {
  //         font-size: 12px !important;
  //       }
  //       .el-select {
  //         width: 100%;
  //       }
  //       .device-path {
  //         margin-right: 10px;
  //       }
  //       .row-select-range {
  //         display: block;
  //       }
  //       .auth-tips {
  //         font-size: 12px;
  //         color: #fb5151ff;
  //         line-height: 16px;
  //         margin-left: 10px;
  //         background: rgba(211, 45, 47, 0.04);
  //         padding: 3px 8px;
  //         margin: 16px 18px 16px 14px;
  //       }
  //       .tab-content {
  //         padding: 10px 16px;
  //         .password {
  //           div {
  //             max-width: 200px;
  //             overflow: hidden;
  //             text-overflow: ellipsis;
  //             white-space: nowrap;
  //           }
  //         }
  //         .user-name {
  //           max-width: calc(100% - 50px);

  //           overflow: hidden;
  //           text-overflow: ellipsis;
  //           white-space: nowrap;
  //         }
  //         .password-form-item {
  //           position: relative;

  //           .icon {
  //             color: $theme-color;
  //             position: absolute;
  //             top: -31px;
  //             left: 100px;
  //             cursor: pointer;
  //           }
  //         }
  //       }
  //       .left-base-content {
  //         .el-input {
  //           width: 200px;
  //           // display: block;
  //           .el-input__suffix {
  //             top: auto;
  //           }
  //         }
  //       }
  //     }
  //   }
  // }
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
  .group-table {
    width: 100%;
    padding: 10px;
    height: 100% !important;
    max-height: initial !important;
    &:deep(.el-table__body-wrapper) {
      height: calc(100% - 32px) !important;
    }
    .el-button {
      padding-left: 0 !important;
    }
  }
}
</style>
