<script setup lang="ts">
import { useMainStore } from "@/stores";
import { useToast } from "primevue/usetoast";
import { useConfirm } from "primevue/useconfirm";

// Reactive
const toast = useToast();
const confirm = useConfirm();
const router = useRouter();
const { username } = useMainStore();

// Actions
const signOut = (): void => {
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
    accept: (): void => {
      toast.add({
        severity: "info",
        summary: "Info",
        detail: "Signed out",
        life: 3000
      });

      // TODO: remove JWT

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
      <p class="italic text-neutral-500 text-sm">Welcome, {{ username }}</p>
      <Button
        v-tooltip.bottom="{ value: 'Sign Out', class: 'text-xs' }"
        aria-label="Sign out"
        icon="pi pi-sign-out"
        severity="danger"
        size="small"
        @click="signOut"></Button>
    </div>
  </header>
</template>
