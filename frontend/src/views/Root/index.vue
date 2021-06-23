<template>
  <div class="root">
    <el-container>
      <el-header>
        <el-menu
          :default-active="menuIndex"
          mode="horizontal"
          @select="handleMenuSelect"
        >
          <el-menu-item index="1">{{
            $t("rootPage.databaseManagement")
          }}</el-menu-item>
          <div class="lang-btn">
            <el-dropdown @command="handleLangCommand">
              <span class="el-dropdown-link">
                {{
                  [$t("rootPage.chinalang"), $t("rootPage.englishlang")][
                    langIndex
                  ]
                }}<i class="el-icon-arrow-down el-icon--right"></i>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item :disabled="langIndex === 0" command="0">{{
                    $t("rootPage.chinalang")
                  }}</el-dropdown-item>
                  <el-dropdown-item :disabled="langIndex === 1" command="1">{{
                    $t("rootPage.englishlang")
                  }}</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
          <div class="user-btn">
            <el-dropdown @command="handleLoginCommand">
              <span class="el-dropdown-link">
                {{ userName }} <i class="el-icon-arrow-down el-icon--right"></i>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="0">{{
                    $t("rootPage.loginoutText")
                  }}</el-dropdown-item>
                  <el-dropdown-item command="1">{{
                    $t("rootPage.about")
                  }}</el-dropdown-item>
                  <el-dropdown-item command="2">{{
                    $t("rootPage.help")
                  }}</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-menu>
      </el-header>
      <div class="content-page-block">
        <router-view></router-view>
      </div>
    </el-container>
  </div>
</template>

<script>
// @ is an alias to /src
import { onMounted, ref, watch } from "vue";
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import useLangSwitch from "./hooks/useLangSwitch.js";

import {
  ElContainer,
  ElHeader,
  ElMenu,
  ElMenuItem,
  ElDropdown,
  ElDropdownMenu,
  ElDropdownItem,
} from "element-plus";

export default {
  name: "Root",
  setup() {
    const router = useRouter();
    const store = useStore();
    const menuIndex = ref("1");
    const userName = ref("admin");

    const { langIndex, handleLangCommand } = useLangSwitch();
    const handleMenuSelect = (key) => {
      menuIndex.value = key;
    };
    const handleLoginCommand = (val) => {
      if (val === "0") {
        store.commit("setLogin", false);
        router.push({ name: "Login" });
      }
      if (val === "2") {
        window.open(
          "https://iotdb.apache.org/zh/UserGuide/Master/QuickStart/QuickStart.html"
        );
      }
    };

    onMounted(() => {
      // window.onbeforeunload = function () {
      //   router.push({ name: "Root" });
      //   return "";
      // };
      store.dispatch("fetchIsLogin");
    });

    watch(
      () => {
        return store.state.isLogin;
      },
      (newValue) => {
        if (!newValue) {
          router.push({ name: "Login" });
        }
      }
    );

    return {
      menuIndex,
      langIndex,
      userName,
      handleMenuSelect,
      handleLangCommand,
      handleLoginCommand,
    };
  },
  components: {
    ElMenuItem,
    ElContainer,
    ElHeader,
    ElMenu,
    ElDropdown,
    ElDropdownMenu,
    ElDropdownItem,
  },
};
</script>

<style scoped lang="scss">
.root {
  &::v-deep .el-header {
    padding: 0;
    .lang-btn {
      position: absolute;
      right: 100px;
      top: 50%;
      transform: translate(0, -50%);
    }
    .user-btn {
      position: absolute;
      right: 20px;
      top: 50%;
      transform: translate(0, -50%);
    }
  }
  .content-page-block {
    height: calc(100vh - 60px);
  }
}
</style>
