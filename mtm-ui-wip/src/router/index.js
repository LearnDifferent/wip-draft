import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from "@/views/Login";
import Register from "@/views/Register";
import Admin from "@/views/Admin";
import HP from "@/views/HP";
import MyPage from "@/views/MyPage";
import Find from "@/views/Find";
import DeletingPage from "@/views/DeletingPage";
import Comment from "../component/Comment";

Vue.use(VueRouter)

const routes = [
    {path: '/', redirect: '/home'},
    {path: "/login", component: Login},
    {path: "/register", component: Register},
    {path: "/admin", component: Admin},
    {path: "/home", component: HP},
    {path: "/mypage", component: MyPage},
    {path: "/find", component: Find},
    {path: "/delete/:username", component: DeletingPage},
    {path: "/comment", component: Comment}
]

const router = new VueRouter({
    routes
})

export default router
