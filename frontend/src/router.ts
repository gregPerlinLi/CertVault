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
      component: () => import("@views/Dashboard.vue")
    }
  ]
});
