import router from "@/router";
import { getUserProfile, login, logout, type ResultVO } from "@/utils/api";
import { useCookies } from "@vueuse/integrations/useCookies";
import { type ToastServiceMethods } from "primevue";

// Export store
export const useUserStore = createGlobalState(() => {
  // States
  const initialized = ref(false);
  const username = ref<string | null>(null);
  const displayName = ref<string | null>(null);
  const email = ref<string | null>(null);
  const innerRole = ref<1 | 2 | 3 | null>(null);

  // Getters
  const role = computed((): string => {
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
  const resetAuthInfo = (): void => {
    const cookies = useCookies();

    username.value = null;
    displayName.value = null;
    email.value = null;
    innerRole.value = null;

    cookies.remove("JSESSIONID");
  };
  const init = async (toast: ToastServiceMethods) => {
    if (initialized.value) {
      return;
    }

    try {
      const r = await getUserProfile();

      username.value = r.username;
      displayName.value = r.displayName;
      email.value = r.email;
      innerRole.value = r.role;

      initialized.value = true;
    } catch (err: unknown) {
      useCookies().remove("JSESSIONID");

      toast.add({
        severity: "error",
        summary: "Authentication Failed",
        detail: "Please re-sign in",
        life: 3000
      });

      router.push("/");
    }
  };
  const signIn = async (
    toast: ToastServiceMethods,
    usrname: string,
    passwd: string
  ) => {
    resetAuthInfo();
    toast.add({
      severity: "info",
      summary: "Info",
      detail: "Signing in",
      life: 3000
    });

    try {
      const r = await login(usrname, passwd);

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

      return true;
    } catch (err: unknown) {
      toast.add({
        severity: "error",
        summary: "Failed to Sign in",
        detail:
          err instanceof Error ? err.message : (err as ResultVO<null>).msg,
        life: 5000
      });

      return false;
    }
  };
  const signOut = async (toast: ToastServiceMethods) => {
    toast.add({
      severity: "info",
      summary: "Info",
      detail: "Signing out",
      life: 3000
    });

    try {
      await logout();
      resetAuthInfo();

      toast.add({
        severity: "success",
        summary: "Success",
        detail: "Successfully signed out",
        life: 3000
      });

      return true;
    } catch (err: unknown) {
      toast.add({
        severity: "error",
        summary: "Failed to Sign in",
        detail:
          err instanceof Error ? err.message : (err as ResultVO<null>).msg,
        life: 5000
      });

      return false;
    }
  };

  // Returns
  return {
    initialized,
    username,
    displayName,
    email,
    role,
    init,
    signIn,
    signOut
  };
});
