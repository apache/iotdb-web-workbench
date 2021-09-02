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

import { createRouter, createWebHashHistory } from 'vue-router';
import Root from '../views/Root';
import Login from '../views/Login';
import DataBaseM from '../views/DataBaseM';
import NProgress from 'nprogress';
import 'nprogress/nprogress.css';

NProgress.configure({
  showSpinner: false,
  easing: 'ease',
  speed: 500,
});

const routes = [
  {
    path: '/',
    name: 'Root',
    component: Root,
    redirect: { name: 'DataBaseM' },
    children: [
      {
        path: 'databasem',
        name: 'DataBaseM',
        component: DataBaseM,
        children: [
          {
            path: 'empty',
            name: 'Empty',
            component: () => import(/* webpackChunkName: "Empty" */ '../views/Source/components/empty.vue'),
          },
          {
            path: 'source/:serverid',
            name: 'Source',
            component: () => import(/* webpackChunkName: "source" */ '../views/Source'),
          },
          {
            path: 'storage/:serverid/:groupname',
            name: 'Storage',
            component: () => import(/* webpackChunkName: "storage" */ '../views/storage'),
          },
          {
            path: 'storage/new/:serverid',
            name: 'NewStorage',
            component: () => import(/* webpackChunkName: "NewStorage" */ '../views/storage/newStorage.vue'),
          },
          {
            path: 'storage/edit/:serverid/:groupname',
            name: 'EditStorage',
            component: () => import(/* webpackChunkName: "EditStorage" */ '../views/storage/newStorage.vue'),
          },
          {
            path: 'device/:connectionid/:storagegroupid/:name',
            name: 'Device',
            component: () => import('../views/Device'),
          },
          {
            path: 'devicemessage/:connectionid/:storagegroupid/:name',
            name: 'DeviceMessage',
            component: () => import('../views/DeviceMessage'),
          },
          {
            path: 'sqlserch/:connectionid/:name',
            name: 'SqlSerch',
            component: () => import('../views/SqlSerch'),
          },
        ],
      },
    ],
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
  },
  {
    path: '/about',
    name: 'About',
    component: () => import(/* webpackChunkName: "about" */ '../views/About'),
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

router.beforeEach(() => {
  NProgress.start();
});

router.afterEach(() => {
  NProgress.done();
});

export default router;
