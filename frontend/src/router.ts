import { createRouter, createWebHashHistory } from "vue-router";

// Export router
export default createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: "/",
      component: () => import("@views/SignIn.vue")
    },
    {
      path: "/dashboard",
      component: () => import("@views/Dashboard.vue"),
      children: [
        {
          path: "",
          component: () => import("@views/dashboard/Index.vue")
        },
        {
          path: "profile",
          component: () => import("@views/dashboard/Profile.vue")
        },
        {
          path: "users",
          component: () => import("@views/dashboard/Users.vue")
        },
        {
          path: "certificates",
          component: () => import("@views/dashboard/Certificates.vue")
        },
        {
          path: "settings",
          component: () => import("@views/dashboard/Settings.vue")
        }
      ]
    }
  ]
});
