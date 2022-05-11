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
  <div class="root">
    <el-container>
      <el-header>
        <el-menu :default-active="menuIndex" mode="horizontal" @select="handleMenuSelect">
          <el-menu-item index="1">{{ $t('rootPage.databaseManagement') }}</el-menu-item>
          <el-menu-item index="2">{{ $t('rootPage.monitorManagement') }}</el-menu-item>
        </el-menu>
        <div class="logo-img"></div>
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
                <el-dropdown-item command="3">{{ $t('rootPage.feedback') }}</el-dropdown-item>
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
import { useRouter, useRoute } from 'vue-router';
import useLangSwitch from './hooks/useLangSwitch.js';
import { ElContainer, ElHeader, ElMenu, ElMenuItem, ElDropdown, ElDropdownMenu, ElDropdownItem } from 'element-plus';

export default {
  name: 'Root',
  setup() {
    const TopMenuMap = {
      DataBaseM: '1',
      Control: '2',
    };
    const router = useRouter();
    const route = useRoute();
    const store = useStore();
    const menuIndex = ref(TopMenuMap[route.matched[1]?.name] || '1');
    const { langIndex, handleLangCommand } = useLangSwitch();
    const handleMenuSelect = (key) => {
      menuIndex.value = key;
      if (key == 1) {
        store.commit('setFirstPageLoad', true);
        router.push({ name: 'DataBaseM' });
      } else if (key == 2) {
        router.push({ name: 'Control' });
      }
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
      if (val === '3') {
        window.open('https://docs.qq.com/sheet/DWXlxU2pVVGFab1Vi?tab=BB08J2');
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
  &::v-deep(.el-header) {
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
    width: 150px;
    height: 22px;
    left: 20px;
    top: 20px;
  }
  .lang-btn {
    position: absolute;
    right: 100px;
    top: 52%;
    transform: translate(0, -50%);
    cursor: pointer;
  }
  .user-btn {
    position: absolute;
    right: 20px;
    top: 50%;
    transform: translate(0, -50%);
    cursor: pointer;
  }
}
</style>
