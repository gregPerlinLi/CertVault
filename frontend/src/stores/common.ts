import type { ToastServiceMethods } from "primevue/toastservice";
import { getOidcProvider } from "@/api/authentication/oauth";
import { getProfile } from "@/api/user/user";
import { useUserStore } from "@/stores/user";

// Types
export interface OidcProvider {
  name: string;
  href: string;
  logo: string;
}

// Export store
export const useCommonStore = createGlobalState(() => {
  /* States */
  const initialized = ref(false);
  const oidcProviders = ref<OidcProvider[] | null>(null);

  /* Getters */

  /* Actions */
  const initialize = async (toast?: ToastServiceMethods) => {
    // Check flag
    if (initialized.value) {
      return;
    }

    // Try to get OIDC providers
    try {
      /* TODO: maybe support multiple OIDC, but now only single */

      const provider = await getOidcProvider({ timeout: -1 });
      if (provider !== null) {
        oidcProviders.value = [
          {
            name: provider.provider,
            href: "/api/v1/auth/oauth/login",
            logo: provider.logo
          }
        ];
      }
    } catch (err: unknown) {
      toast?.add({
        severity: "error",
        summary: "Failed to Get OIDC Provider",
        detail: (err as Error).message,
        life: 5000
      });
    }

    // Try to get user profile
    try {
      const user = useUserStore();
      const profile = await getProfile({ timeout: -1 });
      user.update(profile);
    } catch (err: unknown) {
      toast?.add({
        severity: "error",
        summary: "Authentication Failed",
        detail: (err as Error).message,
        life: 5000
      });
    }

    // Done
    initialized.value = true;
  };

  // Returns
  return {
    initialized,
    oidcProviders,

    initialize
  };
});
