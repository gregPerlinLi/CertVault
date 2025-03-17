<script setup lang="ts">
import { useUserStore } from "@/stores/user";

// Stores
const { initialized } = useUserStore();
</script>

<template>
  <div
    v-if="!initialized"
    class="bg-white fixed flex inset-0 items-center justify-center z-[1000]">
    <ProgressSpinner />
  </div>
  <div class="flex flex-col">
    <AppHeader />
    <div class="flex">
      <AppSidebar />
      <main class="grow p-4">
        <RouterView />
      </main>
    </div>
  </div>
</template>
