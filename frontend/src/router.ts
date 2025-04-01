import { useUserStore } from "@stores/user";
import { createRouter, createWebHashHistory } from "vue-router";
import { useToast } from "primevue/usetoast";

// Create router
const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: "/",
      component: () => import("@views/SignIn.vue"),
      meta: { title: "CertVault" }
    },
    {
      path: "/dashboard",
      component: () => import("@views/Dashboard.vue"),
      children: [
        {
          path: "",
          component: () => import("@views/dashboard/Index.vue"),
          meta: { title: "Dashboard - CertVault" }
        },
        {
          path: "profile",
          component: () => import("@views/dashboard/Profile.vue"),
          meta: { title: "Profile - CertVault" }
        },
        {
          path: "users",
          component: () => import("@views/dashboard/Users.vue"),
          meta: { title: "Users - CertVault" }
        },
        {
          path: "certificates",
          component: () => import("@views/dashboard/Certificates.vue"),
          meta: { title: "Certificates - CertVault" }
        }
      ]
    }
  ]
});

// Set guards
router.beforeEach(async (to) => {
  const { signedIn, init } = useUserStore();
  const toast = useToast();

  // Set title
  useTitle(to.meta.title as string);

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
