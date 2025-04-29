import type { OidcProviderDTO } from "@api/types";
import type { ToastServiceMethods } from "primevue/toastservice";
import { getOidcProviders } from "@api/authentication/oauth";
import { getProfile } from "@api/user/user";

// Export store
export const useCommonStore = createGlobalState(() => {
  /* States */
  const initialized = ref(false);
  const oidcProviders = ref<OidcProviderDTO[] | null>(null);

  /* Getters */

  /* Actions */
  const initialize = async (toast?: ToastServiceMethods) => {
    // Check flag
    if (initialized.value) {
      return;
    }

    // Try to get OIDC providers
    try {
      const provider = await getOidcProviders({ abort: { timeout: -1 } });
      if (provider !== null) {
        oidcProviders.value = provider;
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
      const profile = await getProfile({ abort: { timeout: -1 } });
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
