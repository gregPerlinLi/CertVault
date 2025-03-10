<script setup lang="ts">
import { useMainStore } from "@/stores";

// Reactive
const { username, role } = useMainStore();
</script>

<template>
  <h1 class="font-bold text-xl">Welcome to use CertVault, {{ username }}</h1>
  <h2>
    You have signed in as a
    <span v-if="role === 'super'" class="font-bold text-red-500"
      >superadmin</span
    ><span v-else-if="role === 'admin'" class="text-red-500">admin</span
    ><span v-else>user</span>
  </h2>
  <hr class="border-t-2 border-gray-200 my-4" />
</template>
