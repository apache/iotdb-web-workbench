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
      <permit-dialog ref="permitDialogRef" :name="baseInfoForm.userName" dialog-origin="user" @submit="submitPermit"></permit-dialog>
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
          >{{ countInfo.storageGroupCount }}</span
        >
        <span
          ><span class="more-title">{{ $t('sourcePage.entityNum') + ':' }}</span
          >{{ countInfo.deviceCount }}</span
        >
        <span
          ><span class="more-title">{{ $t('sourcePage.physicalNum') + ':' }}</span
          >{{ countInfo.monitorCount }}</span
        >
        <span
          ><span class="more-title">{{ $t('sourcePage.dataNum') + ':' }}</span
          >{{ countInfo.dataCount }}</span
        >
      </p>
      <div class="buttons">
        <el-button @click="editSource()">
          <svg class="icon icon-edit" aria-hidden="true">
            <use xlink:href="#icon-se-icon-f-edit"></use>
          </svg>
          {{ $t('common.edit') }}</el-button
        >

        <el-popconfirm placement="top" :confirmButtonText="$t('common.submit')" :cancelButtonText="$t('common.cancel')" :title="$t('sourcePage.deleteSourceConfirm')" @confirm="deleteSource()">
          <template #reference>
            <el-button class="button-delete"
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
            <el-button class="title" @click="goToAllModal()">{{ $t('sourcePage.showMore') }}</el-button>
            <div class="tip">第一层最多展示多{{ showNum }}个模型，若需查看所有模型，点击查看更多</div>
            <DataModal @show-num="onShowNum"></DataModal>
          </div>
        </el-tab-pane>
        <el-tab-pane :label="$t('sourcePage.accountPermitLabel')" name="a">
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
                    <el-popconfirm
                      placement="top"
                      :confirmButtonText="$t('common.submit')"
                      :cancelButtonText="$t('common.cancel')"
                      :title="$t('sourcePage.deleteUserConfirm')"
                      @confirm="deleteUser(item)"
                    >
                      <template #reference>
                        <span class="icon-del del-user">
                          <svg v-if="activeIndex == item.username && item.username !== 'new'" class="icon" aria-hidden="true">
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
                              <!-- <el-tooltip class="item" effect="dark" :content="baseInfoForm.password" placement="top"> -->
                              <!-- <div>{{ baseInfoForm.password }}</div> -->
                              <div>***</div>
                              <!-- </el-tooltip> -->
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
                            <div v-else-if="scope.row.type == 2">
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
                              <!-- </div> -->
                            </div>
                            <div v-else-if="scope.row.type == 3">
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
                              <el-popconfirm
                                placement="top"
                                :confirmButtonText="$t('common.submit')"
                                :cancelButtonText="$t('common.cancel')"
                                :title="$t('sourcePage.deleteAuthConfirm')"
                                @confirm="deleteRowAuth(scope)"
                              >
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
                          <div class="box box1">
                            <el-checkbox v-model="userRelationAll" :disabled="baseInfoForm.userName == 'root'" :label="$t('sourcePage.userRelevance')" @change="changeUserRelation()"></el-checkbox>
                          </div>
                          <el-checkbox-group v-model="userRelationItems" class="wraper" :disabled="baseInfoForm.userName == 'root'">
                            <el-checkbox v-for="item in userRelationList[0]" :label="item.id" :key="item.id">{{ item.label }}</el-checkbox>
                          </el-checkbox-group>
                        </div>
                        <div class="permit-list-type">
                          <div class="box box2">
                            <el-checkbox v-model="roleRelationAll" :disabled="baseInfoForm.userName == 'root'" :label="$t('sourcePage.roleRelevance')" @change="changeRoleRelation()"></el-checkbox>
                          </div>
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
                            <el-checkbox v-model="triggerRelationAll" :disabled="baseInfoForm.userName == 'root'" :label="$t('sourcePage.trigger')" @change="changeTriggerRelation()"></el-checkbox>
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
                  <el-popconfirm
                    placement="top"
                    :confirmButtonText="$t('common.submit')"
                    :cancelButtonText="$t('common.cancel')"
                    :title="$t('storagePage.deleteGroupConfirm')"
                    @confirm="deleteGroup(scope)"
                  >
                    <template #reference>
                      <el-button type="text" size="small" class="el-button-delete" :disable="canGroupSet"> {{ $t('common.delete') }} </el-button>
                    </template>
                  </el-popconfirm>
                </template>
              </el-table-column>
            </el-table>
            <div class="pagination">
              <el-pagination
                @current-change="currentChange"
                v-model:currentPage="pagination.pageNum"
                v-model:page-size="pagination.pageSize"
                layout="total, prev, pager, next"
                :total="groupTotal"
                :hide-on-single-page="true"
              >
              </el-pagination>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
    <NewSource v-if="showDialog" :func="func" :serverId="serverId" :showDialog="showDialog" :types="types" @close="close()" @successFunc="successFunc(data)" />
  </div>
</template>

<script>
// @ is an alias to /src
import { onMounted, reactive, ref, watch, onActivated, getCurrentInstance } from 'vue';
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
  ElPagination,
} from 'element-plus';
// import { Close } from '@element-plus/icons';
import NewSource from './components/newSource.vue';
import UserRole from './components/role/Index.vue';
import { useI18n } from 'vue-i18n';
import DataModal from './components/dataModal.vue';
import axios from '@/util/axios.js';
import setOperation from '@/util/setOperation.js';
import { useStore } from 'vuex';

import { useRouter } from 'vue-router';
import PermitDialog from './components/permitDialog.vue';

import roleDialog from './components/roleDialog.vue';
export default {
  name: 'Source',
  props: ['func', 'data'],
  setup(props) {
    const { t, locale } = useI18n();
    const store = useStore();
    const emitter = getCurrentInstance().appContext.config.globalProperties.emitter;

    let showDialog = ref(false);
    let permitDialogRef = ref(null);
    let types = ref(0);
    let activeName = ref('1');
    let sourceTabs = ref('d');

    let canCreateUser = ref(false);

    let canGroupSet = ref(false);

    let canDeleteUser = ref(false);

    let canModifyPassword = ref(false);

    let canShowUser = ref(false);

    let canAuth = ref(false);

    let canAuthRole = ref(false);
    let showNum = ref(0);

    function onShowNum(value) {
      showNum.value = value;
    }

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
     * all storage info
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
     * can show permit dialog
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
      userRelationList.value = {
        0: userRelationListFunc(),
        1: roleRelationListFunc(),
        2: udfRelationListFunc(),
        3: triggerRelationListFunc(),
      };
    });
    /**
     * user info && all permit list
     */
    let userAuthInfo = ref({});
    let userAuthInfoTemp = ref({});

    let permitPermissionListTemp = ref([]);

    const goToAllModal = () => {
      props.func.addTab(`${serverId.value}connection`, { twinTab: true, title: baseInfo.value.alias, id: new Date().getTime() + '', type: 'modal' });
    };
    /**
     * new or eidt source
     */
    const editSource = () => {
      showDialog.value = true;
      types.value = 1;
    };
    /**
     * delete source
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

    const close = () => {
      showDialog.value = false;
      types.value = 0;
    };

    const successFunc = () => {
      showDialog.value = false;
      types.value = 0;
      getBaseInfo();
      getUserList(1);
      getGroupList();
    };

    const handleClick = (tab) => {
      activeName.value = tab.paneName;
      if (tab.paneName == '3') {
        getPermitPermissionList({});
      }
    };

    const pagination = reactive({
      pageSize: 10,
      pageNum: 1,
    });
    function currentChange() {
      getGroupList();
    }

    let paginationAll = {
      pagination,
      currentChange,
    };

    const savepermitAuth = () => {
      if (store.state.dataBaseM.privilegeListAll.indexOf('GRANT_USER_PRIVILEGE') < 0) {
        ElMessage.error(t(`sourcePage.noAuthTip`));
        return false;
      }
      let permitList = userRelationItems.value.concat(roleRelationItems.value).concat(udfRelationItems.value).concat(triggerRelationItems.value);
      let deletePermitList = permitPermissionListTemp.value.filter(function (val) {
        return permitList.indexOf(val) === -1;
      });
      let reqObj = {
        cancelPrivileges: deletePermitList,
        privileges: permitList,
      };
      if (deletePermitList.length && store.state.dataBaseM.privilegeListAll.indexOf('REVOKE_USER_PRIVILEGE') < 0) {
        ElMessage.error(t(`sourcePage.noAuthTip`));
        return false;
      }
      axios.post(`/servers/${serverId.value}/users/${baseInfoForm.userName}/authorityPrivilege`, { ...reqObj }).then((rs) => {
        if (rs && rs.code == 0) {
          ElMessage.success(t('sourcePage.successEditPermit'));
        }
      });
    };

    const getPermitPermissionList = (userinfo, type) => {
      axios.get(`/servers/${serverId.value}/users/${userinfo.username || baseInfoForm.userName}/authorityPrivilege`, {}).then((rs) => {
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

    const handleClickSource = (tab) => {
      sourceTabs.value = tab.paneName;
      if (tab.paneName == 'a') {
        activeName.value = '1';
      }
      if (tab.paneName === 'r') {
        emitter.emit('change-tab');
      }
    };
    /**
     *  selete one user
     * index: user index
     * item: current selete user info
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
     * edit user info(password)
     */
    const editBaseInfo = () => {
      if (store.state.dataBaseM.privilegeListAll.indexOf('MODIFY_PASSWORD') < 0) {
        ElMessage.error(t(`sourcePage.noAuthTip`));
        return false;
      }
      edit.value = true;
    };
    /**
     *  cancel edit user password
     */
    const cancelEdit = () => {
      edit.value = false;
    };
    /**
     * modify user password
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
     * create user
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
     * grant user privilege
     */
    const grantRole = (data, func) => {
      axios.post(`/servers/${serverId.value}/users/${baseInfoForm.userName}/grant`, data).then((rs) => {
        if (rs && rs.code == 0) {
          func && func();
        }
      });
    };
    /**
     * delete user
     * item: current user
     */
    const deleteUser = (item) => {
      if (store.state.dataBaseM.privilegeListAll.indexOf('DELETE_USER') < 0) {
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
     * new user
     */
    const newUser = () => {
      if (store.state.dataBaseM.privilegeListAll.indexOf('CREATE_USER') < 0 || store.state.dataBaseM.privilegeListAll.indexOf('LIST_USER') < 0) {
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
     * cancel create
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

    const changeCheckItem = (scope) => {
      if (!scope.row.edit) {
        return false;
      }
    };

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

    const changeEditState = (scope) => {
      oldValue.value = scope.row;
      // authTableData.value[scope.$index].edit = true;
      permitDialogRef.value.open({ type: 'edit', data: scope.row });
    };

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
      if (store.state.dataBaseM.privilegeListAll.indexOf('GRANT_USER_ROLE') < 0) {
        ElMessage.error(t(`sourcePage.noAuthTip`));
        return false;
      }
      editRole.value = true;
    };
    /**
     * add role
     */
    const addRole = (type) => {
      if (store.state.dataBaseM.privilegeListAll.indexOf('GRANT_ROLE_PRIVILEGE') < 0) {
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
      if (deleteArr.length && store.state.dataBaseM.privilegeListAll.indexOf('REVOKE_USER_ROLE') < 0) {
        ElMessage.error(t(`sourcePage.noAuthTip`));
        return false;
      }
      let data = { roleList: newArr, cancelRoleList: deleteArr };
      grantRole(data, () => {
        ElMessage.success(t('sourcePage.editSuccessLabel'));
        store.dispatch('fetchAllPrivileges', {
          serverId: serverId.value,
          userName: baseInfoForm.userName,
        });
        editRole.value = false;
      });
    };

    const authAdd = (type) => {
      if (store.state.dataBaseM.privilegeListAll.indexOf('GRANT_USER_PRIVILEGE') < 0) {
        ElMessage.error(t(`sourcePage.noAuthTip`));
        return false;
      }
      permitType.value = type;
      permitDialogRef.value.open({ type: 'add' });
    };
    const submitPermit = () => {
      getUserAuth({ username: baseInfoForm.userName });
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

    /**
     * get privilege by one user
     * userinfo: user info
     * type: 1  if user self
     */
    const getUserAuth = (userinfo, type) => {
      getUserInfo(userinfo);
      axios.get(`/servers/${serverId.value}/users/${userinfo.username}/dataPrivilege`, {}).then((res) => {
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
     * get user basic info
     */
    const getUserInfo = (userinfo) => {
      axios.get(`/servers/${serverId.value}/users/${userinfo.username}`).then((rs) => {
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

    const getGroupList = () => {
      axios.get(`/servers/${serverId.value}/storageGroups/info`, { params: pagination }).then((res) => {
        if (res && res.code == 0) {
          tableData.value = res.data?.groupInfoList;
          groupTotal.value = (res.data && res.data.groupCount) || 0;
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
     * get user list
     * type：1 check the first one
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

    const deleteRowAuth = (scope) => {
      if (store.state.dataBaseM.privilegeListAll.indexOf('GRANT_USER_PRIVILEGE') < 0) {
        ElMessage.error(t(`sourcePage.noAuthTip`));
        return false;
      }
      let reqObj = scope.row;
      reqObj.cancelPrivileges = scope.row.privileges;
      reqObj.privileges = [];
      axios.post(`/servers/${serverId.value}/users/${activeIndex.value}/dataPrivilege`, { ...reqObj }).then((rs) => {
        if (rs && rs.code == 0) {
          ElMessage.success(t('sourcePage.deleteAuthLabel'));
          getUserAuth({ username: activeIndex.value });
        }
      });
    };

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
        reqObj.cancelPrivileges = [];
      } else {
        let tempRow = userAuthInfoTemp.value.privilegesInfo[scope.$index].privileges;
        let deleteArr = tempRow.filter(function (val) {
          return scope.row.privileges.indexOf(val) === -1;
        });
        let newArr = scope.row.privileges.filter(function (val) {
          return tempRow.indexOf(val) === -1;
        });
        let intersection = tempRow.filter(function (val) {
          return scope.row.privileges.indexOf(val) > -1;
        });
        newArr = newArr.concat(intersection);
        reqObj.privileges = newArr;
        reqObj.cancelPrivileges = deleteArr;
        if (scope.row.type == 1) {
          let tempGroupArr = userAuthInfoTemp.value.privilegesInfo[scope.$index].groupPaths;
          let deleteGroupArr = tempGroupArr.filter(function (val) {
            return scope.row.groupPaths.indexOf(val) === -1;
          });
          let newGroupArr = scope.row.groupPaths.filter(function (val) {
            return tempGroupArr.indexOf(val) === -1;
          });
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
            reqObj.delGroupPaths = [];
            reqObj.groupPaths = scope.row.groupPaths;
            let deleteDeviceArr = tempDeviceArr.filter(function (val) {
              return scope.row.devicePaths.indexOf(val) === -1;
            });
            let newDeviceArr = scope.row.devicePaths.filter(function (val) {
              return tempDeviceArr.indexOf(val) === -1;
            });
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
            reqObj.delGroupPaths = [];
            reqObj.groupPaths = scope.row.groupPaths;
            if (tempDeviceArr.toString() == scope.row.devicePaths.toString()) {
              reqObj.delDevicePaths = [];
              reqObj.devicePaths = scope.row.devicePaths;
              if (tempDeviceArr.toString() == scope.row.timeseriesPaths.toString()) {
                reqObj.delTimeseriesPaths = [];
                reqObj.timeseriesPaths = scope.row.timeseriesPaths;
              } else {
                let deleteTimeArr = tempTimeArr.filter(function (val) {
                  return scope.row.timeseriesPaths.indexOf(val) === -1;
                });
                let newTimeArr = scope.row.timeseriesPaths.filter(function (val) {
                  return tempTimeArr.indexOf(val) === -1;
                });
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
              reqObj.delDevicePaths = tempDeviceArr;
              reqObj.devicePaths = scope.row.devicePaths;
              reqObj.delTimeseriesPaths = tempTimeArr;
              reqObj.timeseriesPaths = scope.row.timeseriesPaths;
              reqObj.delGroupPaths = scope.row.groupPaths;
              // reqObj.privileges = scope.row.privileges;
            }
          } else {
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
    const cancelRowAuth = () => {
      getUserAuth({ username: activeIndex.value });
    };
    const changeType = (val, scope) => {
      scope.row.groupPaths = [];
      scope.row.devicePaths = [];
      scope.row.timeseriesPaths = [];
    };
    const getDeviceByGroupName = (val, scope) => {
      scope.row.devicePaths = [];
      scope.row.timeseriesPaths = [];
      axios.get(`/servers/${serverId.value}/storageGroups/${val}/devices`).then((rs) => {
        if (rs && rs.code == 0) {
          scope.row.allDevicePaths = rs.data || [];
        }
      });
    };
    const getTimeSeriesByDeviceName = (val, scope) => {
      scope.row.timeseriesPaths = [];
      axios.get(`/servers/${serverId.value}/storageGroups/${scope.row.groupPaths[0]}/devices/${val}/timeseries`).then((rs) => {
        if (rs && rs.code == 0) {
          scope.row.allTimeseriesPaths = rs.data || [];
        }
      });
    };
    const deleteGroup = (scope) => {
      axios.delete(`/servers/${serverId.value}/storageGroups/${scope.row.groupName}`).then((rs) => {
        if (rs && rs.code == 0) {
          ElMessage.success(t('sourcePage.deleteGroupLabel'));
          props.func.updateTreeByIds([serverId.value + 'connection']);

          getGroupList();
        }
      });
    };
    const goEditGroup = (scope) => {
      props.func.updateTreeByIds([serverId.value + 'connection']);
      props.func.expandByIds([serverId.value + 'connection']);
      props.func.addTab(serverId.value + 'connection' + scope.row.groupName + 'storageGroup', { type: 'edit' }, true);
    };
    /**
     */
    const goGroupDetail = (scope) => {
      props.func.updateTreeByIds([serverId.value + 'connection']);
      props.func.expandByIds([serverId.value + 'connection']);
      props.func.addTab(serverId.value + 'connection' + scope.row.groupName + 'storageGroup', {}, true);
    };
    let countInfo = ref({
      groupCount: null,
      deviceCount: null,
      dataCount: null,
      measurementCount: null,
    });

    const getDataCount = () => {
      axios.get(`/servers/${serverId.value}/dataCount`, {}).then((res) => {
        if (res && res.code == 0) {
          countInfo.value = res.data;
        } else {
          countInfo.value = {};
        }
      });
    };
    onMounted(() => {
      // serverId.value = router.currentRoute.value.params.serverid;
      // getBaseInfo((data) => {
      //   getUserAuth(data, 1);
      // });
      // getGroupList();
      // getUserList(1);
    });
    onActivated(() => {
      serverId.value = router.currentRoute.value.params.serverid;
      getBaseInfo((data) => {
        // getUserAuth(data, 1);
        // getPermitPermissionList(data, 1);
        store.dispatch('fetchAllPrivileges', {
          serverId: serverId.value,
          userName: data.username,
        });
      });
      getGroupList();
      getUserList(1);
      getDataCount();
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
      getDataCount,
      countInfo,
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
      showNum,
      onShowNum,
      ...paginationAll,
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
    ElPagination,
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
    top: 0;
    right: 40px;
    color: #fb5151ff;
  }
  .del-user {
    right: 0;
  }
  .info-box {
    padding: 10px 20px 14px;
    position: relative;
    font-size: 16px;
    font-weight: 500;
    color: #222;
    border: 1px solid #eaecf0;
    margin: 16px 16px 0;
    .title {
      margin: 10px 0 20px;
    }
    .more {
      span {
        color: rgba(34, 34, 34, 0.65);
        font-size: 12px;
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
  .info-head {
    height: 40px;
    line-height: 40px;
    background: #f6f6f8;
    font-size: 14px;
    color: #222;
    padding-left: 20px;
    .icon {
      font-size: 16px;
      cursor: pointer;
      margin-right: 4px;
    }
  }
  .tabs-wraper {
    height: calc(100% - 130px);
    &:deep(.el-tabs) {
      height: 100%;
      .el-tabs__header {
        margin-left: 20px;
      }
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
              background: #fff;
              border-radius: 30px 0 0 30px;
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
                background: #fff;
                border-radius: 30px 0 0 30px;
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
          &:deep(.el-checkbox__label) {
            font-size: 12px !important;
            font-weight: 400;
          }
          &:deep(.el-input .el-input__inner) {
            font-size: 12px !important;
          }
          &:deep(.el-checkbox__input.is-disabled + span.el-checkbox__label) {
            color: #222;
            cursor: default;
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
            .permit-list {
              display: flex;
              height: calc(100% - 60px);
              .permit-list-type {
                flex: 1;
                background: #fff;
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
                  padding: 0 6px;
                  margin-right: 10px;
                  span {
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
              .el-input__suffix {
                top: auto;
              }
            }
            &:deep(.el-form--label-top .el-form-item__label) {
              padding: 0 !important;
            }
          }
        }
      }
    }
  }
  .group-table {
    width: 100%;
    padding: 10px;
    height: calc(100% - 34px) !important;
    max-height: initial !important;
    &:deep(.el-table__body-wrapper) {
      height: calc(100% - 32px) !important;
    }
    .el-button {
      padding-left: 0 !important;
    }
  }
  .pagination {
    display: flex;
    justify-content: flex-end;
    margin-top: 10px;

    // padding: 10px 0px;
    .el-pagination {
      padding: 4px 5px 0 5px;
    }
  }
  .tip {
    float: right;
    font-size: 12px;
    color: $danger-color;
  }
}
</style>
