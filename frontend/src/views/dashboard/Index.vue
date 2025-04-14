<script setup lang="ts">
import { useUserStore } from "@/stores/user";

/* Stores */
const user = useUserStore();
</script>

<template>
  <h1 class="font-bold text-2xl">
    <i class="mr-2 pi pi-gauge text-xl"></i>Dashboard
  </h1>
  <hr class="border-2 border-neutral-200 dark:border-neutral-500 my-2" />
  <h1 class="font-bold text-xl">
    Welcome to use CertVault,
    <span :class="user.getRoleClass.value()">{{ user.displayName }}</span>
  </h1>
</template>
