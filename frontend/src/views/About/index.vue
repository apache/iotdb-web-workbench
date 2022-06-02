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
  <div class="about">
    <div class="header">
      <div class="header-logo"></div>
      <div class="back-btn">
        <el-button @click="goBack" type="primary">{{ $t('about.back-btn') }}</el-button>
      </div>
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
      <div class="content">
        <div class="left">
          <div class="line-1" />
          <div class="line-2">{{ $t('about.line-2') }}</div>
          <div class="line-3">{{ $t('about.line-3') }}</div>
          <div class="line-4">{{ $t('about.line-4') }}</div>
          <div class="line-5">
            {{ $t('about.line-5') }}
          </div>
          <div class="line-6">
            <div class="line-6-img"></div>
            <div class="line-6-text">{{ $t('about.line-6-text') }}</div>
          </div>
          <div class="line-7">{{ $t('about.line-7') }}</div>
        </div>
        <div class="right"></div>
      </div>
    </div>
  </div>
</template>

<script>
// @ is an alias to /src
import { onMounted } from 'vue';
import { ElDropdown, ElDropdownMenu, ElDropdownItem, ElButton } from 'element-plus';
import useLangSwitch from '@/views/Root/hooks/useLangSwitch.js';
import { useRouter } from 'vue-router';

export default {
  name: 'About',
  setup() {
    const { langIndex, handleLangCommand } = useLangSwitch();
    const router = useRouter();

    onMounted(() => {});

    const goBack = () => {
      router.push({ name: 'DataBaseM' });
    };

    return {
      langIndex,
      handleLangCommand,
      goBack,
    };
  },
  components: { ElDropdown, ElDropdownMenu, ElDropdownItem, ElButton },
};
</script>

<style scoped lang="scss">
.about {
  min-width: 1150px;
  min-height: 620px;
  height: 100%;
  position: relative;
  overflow: hidden;
  .header {
    height: 63px;
    border-width: 0;
    border-bottom-width: 1px;
    border-style: solid;
    border-color: #e0e0e0;
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    .lang-btn {
      position: absolute;
      right: 40px;
      top: 50%;
      transform: translate(0, -50%);
    }
    .back-btn {
      position: absolute;
      right: 90px;
      top: 50%;
      transform: translate(0, -50%);
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
    top: 64px;
    position: absolute;
    left: 0;
    right: 0;
    bottom: 0;
    .content {
      display: flex;
      position: absolute;
      left: 50%;
      top: 50%;
      transform: translate(-50%, -50%);
      .left {
        width: 442px;
        text-align: left;
        .line-1 {
          width: 36px;
          height: 4px;
          background-color: #4575f6;
          border-radius: 4px;
          margin-bottom: 30px;
        }
        .line-2 {
          font-size: 16px;
          font-weight: 400;
          line-height: 24px;
          color: rgba(34, 34, 34, 0.55);
          margin-bottom: 30px;
        }
        .line-3 {
          font-size: 30px;
          font-weight: 500;
          line-height: 38px;
          color: #222;
          margin-bottom: 30px;
        }
        .line-4,
        .line-5 {
          font-size: 14px;
          font-weight: 400;
          line-height: 22px;
          color: #222;
          margin-bottom: 20px;
        }
        .line-6 {
          margin-bottom: 20px;
          .line-6-img {
            width: 84px;
            height: 84px;
            background-image: url(~@/assets/2d-code.png);
            background-size: 100% 100%;
          }
          .line-6-text {
            width: 84px;
            text-align: center;
            margin-top: 12px;
            font-size: 12px;
            line-height: 20px;
            color: rgba(34, 34, 34, 0.55);
          }
        }
        .line-7 {
          font-size: 14px;
          line-height: 22px;
          color: #222;
        }
      }
      .right {
        margin-left: 40px;
        width: 616px;
        height: 510px;
        background-image: url(~@/assets/about-img.png);
        background-size: 100% 100%;
      }
    }
  }
}
</style>
