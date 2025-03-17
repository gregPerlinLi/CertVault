import { useUserStore } from "@stores/user";
import { createRouter, createWebHashHistory } from "vue-router";
import { useToast } from "primevue";

// Create router
const router = createRouter({
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

// Set guards
router.afterEach(async (to) => {
  const { init } = useUserStore();
  const toast = useToast();

  // Pass guard
  if (to.path === "/") {
    return;
  }

  // Initialize user
  init(toast);
});

// Export router
export default router;
