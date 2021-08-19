<template>
  <div class="root">
    <el-container>
      <el-header>
        <el-menu :default-active="menuIndex" mode="horizontal" @select="handleMenuSelect">
          <el-menu-item index="1">{{ $t('rootPage.databaseManagement') }}</el-menu-item>
        </el-menu>
        <div class="logo-img"></div>
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
        <div class="user-btn">
          <el-dropdown @command="handleLoginCommand">
            <span class="el-dropdown-link">
              {{ store.state.userInfo.name }}
              <i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="0">{{ $t('rootPage.loginoutText') }}</el-dropdown-item>
                <el-dropdown-item command="1">{{ $t('rootPage.about') }}</el-dropdown-item>
                <el-dropdown-item command="2">{{ $t('rootPage.help') }}</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <div class="content-page-block">
        <router-view></router-view>
      </div>
    </el-container>
  </div>
</template>

<script>
// @ is an alias to /src
import { onMounted, ref } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';
import useLangSwitch from './hooks/useLangSwitch.js';
import { ElContainer, ElHeader, ElMenu, ElMenuItem, ElDropdown, ElDropdownMenu, ElDropdownItem } from 'element-plus';

export default {
  name: 'Root',
  setup() {
    const router = useRouter();
    const store = useStore();
    const menuIndex = ref('1');
    const { langIndex, handleLangCommand } = useLangSwitch();
    const handleMenuSelect = (key) => {
      menuIndex.value = key;
    };

    const handleLoginCommand = (val) => {
      if (val === '0') {
        store.commit('setLogin', false);
        localStorage.setItem('authorization', '');
        router.push({ name: 'Login' });
      }
      if (val === '1') {
        router.push({ name: 'About' });
      }
      if (val === '2') {
        window.open('https://iotdb.apache.org/zh/UserGuide/Master/QuickStart/QuickStart.html');
      }
    };

    onMounted(() => {
      store.commit('setFirstPageLoad', true);
      store.dispatch('fetchIsLogin');
    });

    // watch(
    //   () => {
    //     return store.state.userInfo;
    //   },
    //   (newValue) => {
    //     console.log(newValue, 'kkk');
    //   }
    // );

    return {
      store,
      menuIndex,
      langIndex,
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
  &:deep(.el-header) {
    height: 64px !important;
    border-width: 0;
    border-bottom-width: 1px;
    border-style: solid;
    border-color: #e0e0e0;
    padding: 0;
    padding-left: 220px;
    position: relative;
    .el-menu {
      height: 100%;
      .el-menu-item {
        height: 50px;
        border-color: transparent;
        &.is-active {
          color: $theme-color;
          position: relative;
          &::after {
            bottom: 0;
            left: calc(50% - 6px);
            position: absolute;
            content: '';
            width: 12px;
            height: 2px;
            background-color: $theme-color;
          }
        }
      }
    }
  }
  .content-page-block {
    height: calc(100vh - 64px);
  }
  .logo-img {
    position: absolute;
    background-image: url(~@/assets/logo.png);
    background-size: 100% 100%;
    width: 200px;
    height: 36px;
    left: 20px;
    top: 14px;
  }
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
</style>
