<template>
  <div class="login">
    <el-form
      label-position="left"
      :model="ruleForm"
      status-icon
      :rules="rules"
      ref="formNameRef"
    >
      <el-form-item :label="$t('loginPage.account')" prop="account">
        <el-input v-model="ruleForm.account" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item :label="$t('loginPage.password')" prop="passport">
        <el-input
          type="password"
          v-model="ruleForm.passport"
          autocomplete="off"
        ></el-input>
        <span class="forget-btn" @click="showDialog"
          >{{ $t("loginPage.forgetPassWord") }}?</span
        >
      </el-form-item>
      <el-form-item>
        <el-button
          class="submit-btn"
          type="primary"
          @click="submitForm('ruleForm')"
          >{{ $t("loginPage.signIn") }}</el-button
        >
      </el-form-item>
    </el-form>
    <el-dialog append-to-body title="提示" v-model="dialogVisible" width="30%">
      <span>这是一段信息</span>
    </el-dialog>
  </div>
</template>

<script>
// @ is an alias to /src
import { onMounted, reactive, ref } from "vue";
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import { ElForm, ElFormItem, ElInput, ElButton, ElDialog } from "element-plus";

export default {
  name: "Root",
  setup() {
    const store = useStore();
    const router = useRouter();
    const formNameRef = ref(null);
    const dialogVisible = ref(false);
    const ruleForm = reactive({
      account: "",
      passport: "",
    });
    const rules = reactive({
      account: [{ required: true, message: "账号不能为空", trigger: "blur" }],
      passport: [{ required: true, message: "密码不能为空", trigger: "blur" }],
    });

    onMounted(() => {
      //   if (store.state.isLogin) {
      //     router.push({ name: "Root" });
      //   }
    });

    const submitForm = () => {
      formNameRef.value.validate((valid) => {
        if (valid) {
          store.commit("setLogin", true);
          router.push({ name: "Root" });
        }
      });
    };

    const showDialog = () => {
      dialogVisible.value = true;
    };

    return {
      dialogVisible,
      formNameRef,
      ruleForm,
      rules,
      submitForm,
      showDialog,
    };
  },
  components: { ElForm, ElFormItem, ElInput, ElButton, ElDialog },
};
</script>

<style scoped lang="scss">
.login {
  width: 300px;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  .submit-btn {
    width: 100%;
  }
  .forget-btn {
    float: left;
    // width: 100%;
    // text-align: left;
  }
}
</style>
