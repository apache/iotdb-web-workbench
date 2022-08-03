/*
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
 */

import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import directive from './directive';
import plugins from './plugins';
import store from './store';
import { i18n } from 'element-plus/lib/locale';
import i18nFile from '@/i18n/index.js';
import '@/styles/reset.scss';
import '@/styles/element.scss';

const VUE_APP_VERSION = require('../package.json').version;
const vers = window.localStorage.getItem('appVersion');
if (VUE_APP_VERSION != vers) {
  localStorage.clear();
  window.localStorage.setItem('appVersion', VUE_APP_VERSION);
  location.reload();
}

i18n(i18nFile.global.t);
const app = createApp(App);
directive(app);
app.use(store).use(router).use(i18nFile).use(plugins).mount('#app');
