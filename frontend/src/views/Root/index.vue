<template>
  <div class="root">
    <el-container>
      <el-header>
        <el-menu
          :default-active="menuIndex"
          mode="horizontal"
          @select="handleMenuSelect"
        >
          <el-submenu index="1">
            <template #title>数据库管理</template>
            <el-menu-item index="1-1">选项1</el-menu-item>
          </el-submenu>
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
      <el-container>
        <el-aside width="200px">Aside</el-aside>
        <el-main
          >Main
          <el-date-picker type="date" placeholder="选择日期"> </el-date-picker>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
// @ is an alias to /src
import { onMounted, ref, watch } from "vue";
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import {
  ElContainer,
  ElHeader,
  ElAside,
  ElMain,
  ElMenu,
  ElMenuItem,
  ElSubmenu,
  ElDropdown,
  ElDropdownMenu,
  ElDropdownItem,
  ElDatePicker,
} from "element-plus";

export default {
  name: "Root",
  setup() {
    const router = useRouter();
    const store = useStore();
    const menuIndex = ref("2");
    let langMap = {
      cn: 0,
      en: 1,
    };
    const lang = langMap[localStorage.getItem("lang") || "cn"];
    const langIndex = ref(lang);
    const userName = ref("admin");
    const handleMenuSelect = (key) => {
      menuIndex.value = key;
    };
    // const internalInstance = getCurrentInstance();
    const handleLangCommand = (val) => {
      let langIndexMap = {
        0: "cn",
        1: "en",
      };
      langIndex.value = parseInt(val);
      localStorage.setItem("lang", langIndexMap[val]);
      location.reload();
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
    ElContainer,
    ElHeader,
    ElAside,
    ElMain,
    ElMenu,
    ElMenuItem,
    ElSubmenu,
    ElDropdown,
    ElDropdownMenu,
    ElDropdownItem,
    ElDatePicker,
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
}
</style>
