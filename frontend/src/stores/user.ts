import type { UpdateProfileRequestPayload } from "@/api/user/user";
import type { ToastServiceMethods } from "primevue";
import { setNoTimeout } from "@/api";
import { login, logout } from "@/api/authentication";
import { getOidcProvider } from "@/api/authentication/oauth";
import { getProfile, updateProfile } from "@/api/user/user";

// Type
export type Role = "User" | "Admin" | "Superadmin";

// Export store
export const useUserStore = createGlobalState(() => {
  // States
  const initialized = ref(false);
  const oidcProvider = ref<string | null>(null);
  const oidcLogo = ref<string>();
  const signedIn = ref(false);
  const username = ref<string | null>(null);
  const displayName = ref<string | null>(null);
  const email = ref<string | null>(null);
  const innerRole = ref<1 | 2 | 3 | null>(null);

  // Getters
  const role = computed((): Role | "" => {
    switch (innerRole.value) {
      case 1:
        return "User";
      case 2:
        return "Admin";
      case 3:
        return "Superadmin";
      default:
        return "";
    }
  });

  // Actions
  const clearSignedInfo = () => {
    signedIn.value = false;
    username.value = null;
    displayName.value = null;
    email.value = null;
    innerRole.value = null;
  };
  const init = async (toast?: ToastServiceMethods) => {
    if (initialized.value) {
      return;
    }

    setNoTimeout(true);
    try {
      const provider = await getOidcProvider();
      if (provider !== null) {
        oidcProvider.value = provider.provider;
        oidcLogo.value = provider.logo;
      }
    } catch (err: unknown) {
      toast?.add({
        severity: "error",
        summary: "Failed to Get OIDC Provider",
        detail: (err as Error).message,
        life: 5000
      });
    }

    const err = await syncFromRemote();
    initialized.value = true;
    setNoTimeout(false);
    if (err !== null) {
      toast?.add({
        severity: "error",
        summary: "Authentication Failed",
        detail: err.message,
        life: 5000
      });
    } else {
      signedIn.value = true;
    }
  };
  const signIn = async (
    usrname: string,
    passwd: string,
    toast: ToastServiceMethods
  ) => {
    try {
      const r = await login(usrname, passwd);

      signedIn.value = true;
      username.value = r.username;
      displayName.value = r.displayName;
      email.value = r.email;
      innerRole.value = r.role;

      toast.add({
        severity: "success",
        summary: "Success",
        detail: "Successfully signed in",
        life: 3000
      });

      return null;
    } catch (err: unknown) {
      toast.add({
        severity: "error",
        summary: "Failed to Sign in",
        detail: (err as Error).message,
        life: 5000
      });

      return err as Error;
    }
  };
  const signOut = async (toast: ToastServiceMethods) => {
    try {
      await logout();

      toast.add({
        severity: "success",
        summary: "Success",
        detail: "Successfully signed out",
        life: 3000
      });

      return null;
    } catch (err: unknown) {
      toast.add({
        severity: "error",
        summary: "Failed to Sign out",
        detail: (err as Error).message,
        life: 5000
      });

      return err as Error;
    } finally {
      clearSignedInfo();
    }
  };
  const syncFromRemote = async (toast?: ToastServiceMethods) => {
    try {
      const p = await getProfile();

      username.value = p.username;
      displayName.value = p.displayName;
      email.value = p.email;
      innerRole.value = p.role;

      return null;
    } catch (err: unknown) {
      toast?.add({
        severity: "error",
        summary: "Failed to Get User Profile",
        detail: (err as Error).message,
        life: 5000
      });

      return err as Error;
    }
  };
  const syncToRemote = async (
    payload: UpdateProfileRequestPayload,
    toast: ToastServiceMethods
  ) => {
    try {
      await updateProfile(payload);

      const err = await syncFromRemote();
      if (err !== null) {
        throw err;
      }

      toast.add({
        severity: "success",
        summary: "Success",
        detail: "Successfully updated the user profile",
        life: 3000
      });

      return null;
    } catch (err: unknown) {
      toast.add({
        severity: "error",
        summary: "Fail to Update User Profile",
        detail: (err as Error).message,
        life: 5000
      });

      return err as Error;
    }
  };

  // Returns
  return {
    initialized,
    oidcProvider,
    oidcLogo,
    signedIn,
    username,
    displayName,
    email,
    role,
    clearSignedInfo,
    init,
    signIn,
    signOut,
    syncFromRemote,
    syncToRemote
  };
});
