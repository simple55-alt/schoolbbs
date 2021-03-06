import Vue from "vue";
import VueRouter from "vue-router";
import Index from "../views/Index.vue";
import Home from "../components/topic/home.vue";
import Register from "../views/Register";
import Block from "../components/block/Block";
import Cplate from "../components/plate/cplate";
import Topic from "../components/topic/Topic";
import Message from "element-ui/packages/message/src/main";
import store from "../store/index";
import UserIndex from "../components/user/UserIndex";
import Usersetting from "../components/user/Usersetting";
import Manager from "../views/Manager";

Vue.use(VueRouter);

const routes = [
  {
    path: "/",
    redirect: "/home"
  },
  {
    path: "/index",
    name: "index",
    component: Index,
    children: [
      {
        path: "/home",
        name: "/home",
        component: Home
      },
      {
        path: "/block",
        name: "/block",
        component: Block
      },
      {
        path: "/plate/:plateid/:index",
        component: Cplate
      },
      {
        path: "/topic/:topicid/:index",
        component: Topic
      },
      {
        path: "/user/:userid",
        component: UserIndex
      },
      {
        path: "/userset/:userid",
        component: Usersetting
      }
    ]
  },
  {
    path: "/register",
    name: "register",
    component: Register
  },
  {
    path: "/manager",
    name: "/manager",
    component: Manager
  }
];

const router = new VueRouter({
  routes,
  mode: "history"
});
router.beforeEach((to, from, next) => {
  if (to.path == "/home" || to.path == "/register" || to.path == "/manager") {
    next();
  } else {
    if (!store.state.Authorization) {
      Message({
        showClose: true,
        message: "请先登录",
        type: "error"
      });
      next("/");
    } else {
      next();
    }
  }
});
export default router;
