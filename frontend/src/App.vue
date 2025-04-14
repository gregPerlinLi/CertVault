<script setup lang="ts">
import { useCommonStore } from "@/stores/common";

/* Stores */
const { initialized } = useCommonStore();
</script>

<template>
  <RouterView />
  <ConfirmDialog :pt="{ root: { class: 'z-[250]' } }" />
  <Toast
    position="bottom-right"
    :pt="{
      root: { class: 'z-[500]' },
      summary: { class: 'leading-none' },
      detail: { class: 'leading-none' }
    }" />
  <div
    v-if="!initialized"
    class="bg-white fixed flex inset-0 items-center justify-center z-[1000] dark:bg-black">
    <ProgressSpinner />
  </div>
</template>
