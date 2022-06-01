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
  <el-config-provider :locale="language">
    <router-view />
  </el-config-provider>
</template>

<script>
import { ref, watch, nextTick } from 'vue';
import { ElConfigProvider } from 'element-plus';
import { useI18n } from 'vue-i18n';
import enLocale from 'element-plus/lib/locale/lang/en';
import deLocale from 'element-plus/lib/locale/lang/de';
import zhLocale from 'element-plus/lib/locale/lang/zh-cn';

export default {
  name: 'App',
  components: {
    ElConfigProvider,
  },
  setup() {
    const map = {
      [enLocale.name]: enLocale,
      [deLocale.name]: deLocale,
      [zhLocale.name]: zhLocale,
    };
    let { locale } = useI18n();
    let language = ref(map[locale.value]);
    function useLanguageWatch(refValue, callback) {
      let { locale } = useI18n();
      watch(locale, () => {
        refValue.value = map[locale.value];
        nextTick(() => {
          refValue.value = callback();
        });
      });
    }
    useLanguageWatch(language, () => {
      return map[locale.value];
    });
    return {
      language,
    };
  },
};
</script>
<style lang="scss">
#app {
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #222;
  font-family: PingFang SC, Arial, sans-serif;
  text-align: center;
  height: 100vh;
  position: relative;
  .icon {
    width: 1em;
    height: 1em;
    vertical-align: -0.15em;
    fill: currentColor;
    overflow: hidden;
  }
}
// 滚动条开始
::-webkit-scrollbar-track-piece {
  background-color: #fff;
}
::-webkit-scrollbar {
  width: 4px;
  height: 6px;
  background-color: #fff;
}
::-webkit-scrollbar-thumb {
  height: 20%;
  background-color: rgba(193, 193, 193, 0.5);
  border-radius: 6px;
  outline-offset: -2px;
  filter: alpha(opacity = 50);
  opacity: 0.5;
}
::-webkit-scrollbar-corner {
  background-color: #fff;
}
//滚动条完
.clearfix {
  zoom: 1;
}
.clearfix::after {
  content: '';
  display: block;
  height: 0;
  clear: both;
  visibility: hidden;
}
.move-resize {
  cursor: w-resize !important;
  user-select: none;
  .el-tree,
  .el-tree-node__content,
  li {
    cursor: w-resize !important;
  }
  .divider {
    background-color: $theme-color !important;
    width: 2px !important;
  }
}
</style>
