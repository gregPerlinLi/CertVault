<script setup lang="ts">
import { useUserStore } from "@/stores/user";
import { useNotify } from "@/utils/composable";
import { useConfirm } from "primevue/useconfirm";

// Services
const router = useRouter();
const confirm = useConfirm();
const { toast, info } = useNotify();

// Stores
const { displayName, signOut } = useUserStore();

// Actions
const signOutOnClick = (): void => {
  confirm.require({
    header: "Sign Out",
    message: "Are you sure to sign out?",
    icon: "pi pi-exclamation-triangle",
    modal: true,
    acceptProps: {
      severity: "danger"
    },
    rejectProps: {
      severity: "secondary",
      variant: "outlined"
    },
    accept: async () => {
      info("Info", "Signing out");
      await signOut(toast);
      router.push("/");
    }
  });
};
</script>

<template>
  <header
    class="bg-white border-b border-neutral-200 flex items-center justify-between px-8 py-4 sticky top-0 z-50 dark:bg-neutral-800 dark:border-neutral-500">
    <RouterLink class="flex gap-2 items-center" to="/dashboard">
      <img class="h-8 w-8" draggable="false" src="/favicon.svg" />
      <h1 class="font-bold select-none text-2xl">CertVault</h1>
    </RouterLink>
    <div class="flex gap-4 items-baseline">
      <p class="italic text-neutral-500 text-sm">Welcome, {{ displayName }}</p>
      <Button
        v-tooltip.bottom="{ value: 'Sign Out', class: 'text-xs' }"
        aria-label="Sign out"
        icon="pi pi-sign-out"
        severity="danger"
        size="small"
        @click="signOutOnClick"></Button>
    </div>
  </header>
</template>
