import { createRouter, createWebHashHistory } from 'vue-router';
import Root from '../views/Root';
import Login from '../views/Login';
import DataBaseM from '../views/DataBaseM';

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
            path: 'about',
            name: 'About',
            component: () => import(/* webpackChunkName: "about" */ '../views/About'),
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
        ],
      },
    ],
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

export default router;
