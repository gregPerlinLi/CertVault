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
        }
      ]
    }
  ]
});

// Set guards
router.beforeEach(async (to) => {
  const { signedIn, init } = useUserStore();
  const toast = useToast();

  // Initialize user
  await init(to.path === "/" ? undefined : toast);

  // Redirect
  if (signedIn.value) {
    if (to.path === "/") {
      return "/dashboard";
    }
  } else if (to.path !== "/") {
    return "/";
  }
});

// Export router
export default router;
