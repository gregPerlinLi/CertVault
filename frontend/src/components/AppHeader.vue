<script setup lang="ts">
import { logout } from "@api/authentication";
import { useConfirm } from "primevue/useconfirm";

/* Services */
const router = useRouter();
const confirm = useConfirm();
const { success, info, error, remove } = useNotify();

/* Stores */
const user = useUserStore();

/* Reactives */
const busy = ref(false);

/* Actions */
const trySignOut = () =>
  confirm.require({
    header: "Sign out",
    message: "Are you sure to sign out?",
    icon: "pi pi-exclamation-triangle",
    modal: true,
    acceptProps: { severity: "danger" },
    rejectProps: { severity: "secondary", variant: "outlined" },
    accept: async () => {
      busy.value = true;
      const msg = info("Signing out");

      try {
        await logout({ abort: { timeout: -1 } });

        user.clear();
        router.push("/");
        success("Successfully signed out");
      } catch (err: unknown) {
        error((err as Error).message, "Fail to Sign out");
      }

      remove(msg);
      busy.value = false;
    }
  });
</script>

<template>
  <header
    class="bg-white border-b border-neutral-200 flex items-center justify-between px-8 py-4 sticky top-0 z-50 dark:bg-neutral-800 dark:border-neutral-500">
    <RouterLink class="flex gap-2 items-center" to="/dashboard">
      <img class="h-10" draggable="false" src="@/assets/logo.svg" />
    </RouterLink>
    <div class="flex gap-4 items-baseline">
      <p class="italic text-neutral-500 text-sm">
        Welcome, {{ user.displayName }}
      </p>
      <Button
        v-tooltip.bottom="{ value: 'Sign Out', class: 'text-xs' }"
        aria-label="Sign out"
        icon="pi pi-sign-out"
        severity="danger"
        size="small"
        :loading="busy"
        @click="trySignOut"></Button>
    </div>
  </header>
</template>
