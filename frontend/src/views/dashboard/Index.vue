<script setup lang="ts">
import { useMainStore } from "@/stores";

useTitle("Dashboard - CertVault");

// Reactive
const { username, role } = useMainStore();
</script>

<template>
  <h1 class="font-bold text-2xl">
    <i class="mr-2 pi pi-gauge text-xl"></i>Dashboard
  </h1>
  <hr class="border-2 border-neutral-500 my-2" />
  <h1 class="font-bold text-xl">Welcome to use CertVault, {{ username }}</h1>
  <h2>
    You have signed in as a
    <span v-if="role === 'super'" class="font-bold text-red-500"
      >superadmin</span
    ><span v-else-if="role === 'admin'" class="text-red-500">admin</span
    ><span v-else>user</span>
  </h2>
</template>
