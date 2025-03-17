<script setup lang="ts">
import { useUserStore } from "@/stores/user";

useTitle("Dashboard - CertVault");

// Reactive
const { displayName, role } = useUserStore();
</script>

<template>
  <h1 class="font-bold text-2xl">
    <i class="mr-2 pi pi-gauge text-xl"></i>Dashboard
  </h1>
  <hr class="border-2 border-neutral-200 dark:border-neutral-500 my-2" />
  <h1 class="font-bold text-xl">Welcome to use CertVault, {{ displayName }}</h1>
  <h2>
    You have signed in as a
    <span v-if="role === 'super'" class="font-bold text-red-500"
      >superadmin</span
    ><span v-else-if="role === 'admin'" class="text-red-500">admin</span
    ><span v-else>user</span>
  </h2>
</template>
