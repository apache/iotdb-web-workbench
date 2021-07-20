<template>
  <div class="login">
    <div class="header">
      <div class="header-logo"></div>
      <div class="lang-btn">
        <el-dropdown @command="handleLangCommand">
          <span class="el-dropdown-link"> {{ [$t('rootPage.chinalang'), $t('rootPage.englishlang')][langIndex] }}<i class="el-icon-arrow-down el-icon--right"></i> </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item :disabled="langIndex === 0" command="0">{{ $t('rootPage.chinalang') }}</el-dropdown-item>
              <el-dropdown-item :disabled="langIndex === 1" command="1">{{ $t('rootPage.englishlang') }}</el-dropdown-item>
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
          <el-form label-position="left" :model="ruleForm" :rules="rules" ref="formNameRef">
            <el-form-item :label="$t('loginPage.account')" prop="account">
              <el-input v-model="ruleForm.account" autocomplete="off" :placeholder="$t('loginPage.placeholderAccount')"></el-input>
            </el-form-item>
            <el-form-item class="form-item" :label="$t('loginPage.password')" prop="passport">
              <el-input type="password" v-model="ruleForm.passport" autocomplete="off" :placeholder="$t('loginPage.placeholderPassword')"></el-input>
              <span class="forget-btn" @click="showDialog">{{ $t('loginPage.forgetPassWord') }}?</span>
            </el-form-item>
            <el-form-item>
              <el-button class="submit-btn" type="primary" @click="submitForm('ruleForm')">{{ $t('loginPage.signIn') }}</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>

    <el-dialog append-to-body :title="$t(`loginPage.forgetPassword`)" v-model="dialogVisible" width="30%">
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
      right: 20px;
      top: 50%;
      transform: translate(0, -50%);
    }
    .header-logo {
      background-image: url(~@/assets/logo.png);
      background-size: 100% 100%;
      width: 200px;
      height: 36px;
      position: absolute;
      left: 20px;
      top: 14px;
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
        width: 35vw;
        position: absolute;
        left: 50%;
        top: 45%;
        transform: translate(-50%, -50%);
        .login-img {
          background-image: url(~@/assets/logo.png);
          background-size: 100% 100%;
          width: 156px;
          height: 28px;
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
          margin-top: 10px;
        }
        .form-item {
          position: relative;
          .forget-btn {
            cursor: pointer;
            position: absolute;
            right: 0;
            top: 0;
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
