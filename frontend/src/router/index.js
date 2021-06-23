import { createRouter, createWebHashHistory } from "vue-router";
import Root from "../views/Root";
import Login from "../views/Login";
import DataBaseM from "../views/DataBaseM";

const routes = [
  {
    path: "/",
    name: "Root",
    component: Root,
    redirect: { name: "DataBaseM" },
    children: [
      {
        path: "databasem",
        name: "DataBaseM",
        component: DataBaseM,
      },
    ],
  },
  {
    path: "/login",
    name: "Login",
    component: Login,
  },
  {
    path: "/about",
    name: "About",
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ "../views/About"),
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

export default router;
