<script setup lang="ts">
import { useUserStore } from "@/stores/user";

// Stores
const { initialized } = useUserStore();
</script>

<template>
  <Toast
    :pt="{
      root: { class: 'z-[500]' },
      summary: { class: 'leading-none' },
      detail: { class: 'leading-none' }
    }" />
  <ConfirmDialog />
  <div
    v-if="!initialized"
    class="bg-white fixed flex inset-0 items-center justify-center z-[1000]">
    <ProgressSpinner />
  </div>
  <RouterView />
</template>
