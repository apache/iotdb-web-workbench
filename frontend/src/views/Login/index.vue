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
  <div class="login">
    <div class="header">
      <div class="header-logo"></div>
      <div class="lang-btn">
        <el-dropdown @command="handleLangCommand">
          <svg preserveAspectRatio="xMidYMid meet" viewBox="0 0 24 24" width="1.2em" height="1.2em" data-v-dd9c9540="">
            <path
              fill="currentColor"
              d="m18.5 10l4.4 11h-2.155l-1.201-3h-4.09l-1.199 3h-2.154L16.5 10h2zM10 2v2h6v2h-1.968a18.222 18.222 0 0 1-3.62 6.301a14.864 14.864 0 0 0 2.336 1.707l-.751 1.878A17.015 17.015 0 0 1 9 13.725a16.676 16.676 0 0 1-6.201 3.548l-.536-1.929a14.7 14.7 0 0 0 5.327-3.042A18.078 18.078 0 0 1 4.767 8h2.24A16.032 16.032 0 0 0 9 10.877a16.165 16.165 0 0 0 2.91-4.876L2 6V4h6V2h2zm7.5 10.885L16.253 16h2.492L17.5 12.885z"
            ></path>
          </svg>

          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item :disabled="langIndex === 0" command="0">中文</el-dropdown-item>
              <el-dropdown-item :disabled="langIndex === 1" command="1">English</el-dropdown-item>
              <el-dropdown-item :disabled="langIndex === 2" command="2">Deutsch</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
    <div class="body">
      <div class="left"></div>
      <div class="right">
        <div class="login-block">
          <div class="login-img"></div>
          <div class="login-title">
            {{ $t('loginPage.welcomeLogin') }}
          </div>
          <el-form :hide-required-asterisk="true" label-position="top" :model="ruleForm" :rules="rules" ref="formNameRef">
            <el-form-item :label="$t('loginPage.account')" prop="account">
              <el-input class="form-item-input" v-model="ruleForm.account" autocomplete="off" :placeholder="$t('loginPage.placeholderAccount')"></el-input>
            </el-form-item>
            <el-form-item class="form-item" :label="$t('loginPage.password')" prop="passport">
              <el-input class="form-item-input" type="password" v-model="ruleForm.passport" autocomplete="off" :placeholder="$t('loginPage.placeholderPassword')"></el-input>
              <span class="forget-btn" @click="showDialog">{{ $t('loginPage.forgetPassWord') }}?</span>
            </el-form-item>
            <el-form-item>
              <el-button class="submit-btn" type="primary" @click="submitForm('ruleForm')">{{ $t('loginPage.signIn') }}</el-button>
            </el-form-item>
          </el-form>
          <el-form-item v-if="casdoorSwitch === 'true'">
            <el-button class="submit-btn" type="primary" @click="getLoginUrl()">{{ $t('loginPage.signInWithCasdoor') }}</el-button>
          </el-form-item>
        </div>
      </div>
    </div>

    <el-dialog append-to-body :title="$t(`loginPage.forgetPassword`)" v-model="dialogVisible" width="32%">
      <div class="forget-tip">
        {{ $t(`loginPage.forgetPasswordTip`) }}
      </div>
    </el-dialog>
  </div>
</template>

<script>
// @ is an alias to /src
import { onMounted, reactive, ref } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';
import { ElForm, ElFormItem, ElInput, ElButton, ElDialog, ElMessage, ElDropdown, ElDropdownMenu, ElDropdownItem } from 'element-plus';
import { useI18n } from 'vue-i18n';
import axios from '@/util/axios.js';
import useLangSwitch from '@/views/Root/hooks/useLangSwitch.js';

export default {
  name: 'Root',
  setup() {
    const store = useStore();
    const router = useRouter();
    const formNameRef = ref(null);
    const dialogVisible = ref(false);
    const { t } = useI18n();
    const casdoorSwitch = process.env.VUE_APP_CASDOOR;
    const ruleForm = reactive({
      account: '',
      passport: '',
    });
    const { langIndex, handleLangCommand } = useLangSwitch();

    const validateAccount = (rule, value, callback) => {
      if (value === '') {
        callback(new Error(t(`loginPage.accountEmptyTip`)));
        return;
      }
      if (!/^[^_\d]\w+?/.test(value)) {
        callback(new Error(t(`loginPage.accountContentTip`)));
        return;
      }
      if (value.length < 3 || value.length > 32) {
        callback(new Error(t(`loginPage.accountLengthTip`)));
        return;
      }
      callback();
    };

    const validatePassport = (rule, value, callback) => {
      if (value === '') {
        callback(new Error(t(`loginPage.passwordEmptyTip`)));
        return;
      }
      if (value.length < 6) {
        callback(new Error(t(`loginPage.passwordLenghtTip`)));
        return;
      }
      callback();
    };

    const rules = reactive({
      account: [
        {
          required: true,
          validator: validateAccount,
          trigger: 'blur',
        },
      ],
      passport: [
        {
          required: true,
          validator: validatePassport,
          trigger: 'blur',
        },
      ],
    });

    onMounted(() => {
      //   if (store.state.isLogin) {
      //     router.push({ name: "Root" });
      //   }
      LoginWithCasdoor();
    });

    const submitForm = () => {
      formNameRef.value.validate((valid) => {
        if (valid) {
          axios.post('/login', {}, { params: { name: ruleForm.account, password: ruleForm.passport } }).then((res) => {
            if (res?.data?.code === '0') {
              localStorage.setItem('authorization', res?.headers?.authorization);
              store.commit('setLogin', true);
              store.commit('setUserInfo', res.data || {});
              router.push({ name: 'Root' });
            } else {
              ElMessage.error(t(`loginPage.loginErrorTip`));
            }
          });
        }
      });
    };

    const getLoginUrl = () => {
      axios.post('/getCasdoorUrl', {}, { params: { origin: window.location.origin } }).then((res) => {
        window.location.href = res.data;
      });
    };

    const LoginWithCasdoor = () => {
      const url = window.document.location.href;
      const u = new URL(url);
      let codes = u.searchParams.get('code');
      let state = u.searchParams.get('state');
      if (codes != null && state != null) {
        axios.post('/loginWithCasdoor', {}, { params: { code: codes, state: state } }).then((res) => {
          if (res?.data?.code === '0') {
            localStorage.setItem('authorization', res?.headers?.authorization);
            store.commit('setLogin', true);
            store.commit('setUserInfo', res.data || {});
            router.push({ name: 'Root' });
          } else {
            ElMessage.error(t(`loginPage.loginErrorTip`));
          }
        });
      }
    };

    const showDialog = () => {
      dialogVisible.value = true;
    };

    return {
      langIndex,
      handleLangCommand,
      dialogVisible,
      formNameRef,
      ruleForm,
      rules,
      submitForm,
      showDialog,
      getLoginUrl,
      LoginWithCasdoor,
      casdoorSwitch,
    };
  },
  components: { ElForm, ElFormItem, ElInput, ElButton, ElDialog, ElDropdown, ElDropdownMenu, ElDropdownItem },
};
</script>

<style scoped lang="scss">
.login {
  .header {
    height: 63px;
    border-width: 0;
    border-bottom-width: 1px;
    border-style: solid;
    border-color: #e0e0e0;
    position: relative;
    .lang-btn {
      position: absolute;
      right: 40px;
      top: 50%;
      transform: translate(0, -50%);
      cursor: pointer;
    }
    .header-logo {
      background-image: url(~@/assets/logo.png);
      background-size: 100% 100%;
      width: 150px;
      height: 22px;
      position: absolute;
      left: 20px;
      top: 20px;
    }
  }
  .body {
    display: flex;
    height: calc(100vh - 64px);
    .left {
      // flex-basis: calc((100vh - 64px) * 0.52);
      flex-basis: 30vw;
      flex-shrink: 0;
      overflow: hidden;
      background-image: url(~@/assets/login.png);
      background-size: 100% 100%;
    }
    .right {
      flex-grow: 1;
      position: relative;
      .login-block {
        width: 520px;
        position: absolute;
        left: 50%;
        top: 45%;
        transform: translate(-50%, -50%);
        .login-img {
          background-image: url(~@/assets/logo.png);
          background-size: 100% 100%;
          width: 150px;
          height: 22px;
          margin-bottom: 12px;
        }
        .login-title {
          font-weight: 500;
          line-height: 32px;
          font-size: 24px;
          text-align: left;
          margin-bottom: 25px;
        }
        .submit-btn {
          width: 100%;
          margin-top: 16px;
        }
        &::v-deep .form-item-input {
          width: 520px;
          .el-input__inner {
            height: 40px;
            line-height: 40px;
          }
          &::v-deep {
            .el-input__inner {
              padding: 0 12px;
            }
          }
        }
        .form-item {
          position: relative;
          .forget-btn {
            cursor: pointer;
            position: absolute;
            right: 0;
            top: -30px;
            font-size: 12px;
            color: $theme-color;
          }
        }
      }
    }
  }
}
.forget-tip {
  text-align: center;
  font-size: 14px;
  margin: 20px;
}
</style>
