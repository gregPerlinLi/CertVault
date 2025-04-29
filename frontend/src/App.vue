<script setup lang="ts">
import { useCommonStore } from "@stores/common";

/* Stores */
const { initialized } = useCommonStore();
</script>

<template>
  <!-- Router view -->
  <RouterView />

  <!-- Service components -->
  <ConfirmDialog :pt="{ root: { class: 'z-[250]' } }" />
  <Toast
    position="bottom-right"
    :pt="{
      root: { class: 'z-[500]' },
      summary: { class: 'leading-none' },
      detail: { class: 'leading-none' }
    }" />

  <!-- Initialization loader -->
  <div
    v-if="!initialized"
    class="bg-white fixed flex inset-0 items-center justify-center z-[1000] dark:bg-black">
    <ProgressSpinner />
  </div>
</template>
