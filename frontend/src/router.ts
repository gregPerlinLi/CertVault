import { useToast } from "primevue/usetoast";
import { createRouter, createWebHistory } from "vue-router";

// Sync views
import NotFound from "@views/NotFound.vue";

// Create router
const router = createRouter({
  history: createWebHistory(),
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
          path: "account/profile",
          component: () => import("@/views/dashboard/account/Profile.vue"),
          meta: { title: "Profile - My Account - CertVault" }
        },
        {
          path: "account/security",
          component: () => import("@/views/dashboard/account/Security.vue"),
          meta: { title: "Security - My Account - CertVault" }
        },
        {
          path: "users",
          component: () => import("@views/dashboard/Users.vue"),
          meta: { title: "Users - CertVault" }
        },
        {
          path: "certificates/ca",
          component: () => import("@views/dashboard/certificates/CA.vue"),
          meta: { title: "CA Certificates - Certificates - CertVault" }
        },
        {
          path: "certificates/ssl",
          component: () => import("@views/dashboard/certificates/SSL.vue"),
          meta: { title: "SSL Certificates - Certificates - CertVault" }
        },
        {
          path: "binding",
          component: () => import("@views/dashboard/Binding.vue"),
          meta: { title: "CA Binding - CertVault" }
        }
      ]
    },
    {
      path: "/:pathMatch(.*)*",
      component: NotFound
    }
  ]
});

// Set guards
router.beforeEach(async (to) => {
  /* Services */
  const toast = (() => {
    try {
      const toast = useToast();
      return toast;
    } catch {
      return undefined;
    }
  })();

  /* Stores */
  const common = useCommonStore();
  const user = useUserStore();

  // Set title
  useTitle(to.meta.title as string);

  // Initialize
  await common.initialize(to.path === "/" ? undefined : toast);

  // Redirect
  if (to.path === "/" && user.isSignedIn.value) {
    return "/dashboard";
  }
  if (to.path !== "/" && !user.isSignedIn.value) {
    return "/";
  }
});

// Export router
export default router;
