<script setup lang="ts">
import { notify } from "@kyvg/vue3-notification";

// Reactive
const router = useRouter();
const username = ref("username");
const role = ref("");

// Async components
const SpecificDashboad = defineAsyncComponent(() => {
  switch (role.value) {
    case "superAdmin":
      return import("../components/SuperAdminDashboard.vue");
    case "admin":
      return import("../components/AdminDashboard.vue");
    default:
      return import("../components/UserDashboard.vue");
  }
});

// Actions
const signOut = (): void => {
  notify({ text: "Signed out" });

  // TODO: remove JWT

  router.push("/");
};
</script>

<template>
  <div class="fixed flex flex-col inset-0">
    <header
      class="border-b border-neutral-200 flex items-center justify-between px-8 py-4">
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
    <SpecificDashboad />
  </div>
</template>
