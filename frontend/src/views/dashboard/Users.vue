<script setup lang="ts">
// Async components
const AsyncUsrMgr = defineAsyncComponent(
  () => import("@/components/panel/UsrMgr.vue")
);
</script>

<template>
  <!-- Headers -->
  <h1 class="font-bold mb-4 text-2xl">
    <i class="mr-2 pi pi-users text-xl"></i>Users
  </h1>

  <!-- Main -->
  <Suspense>
    <AsyncUsrMgr />
    <template #fallback>
      <div class="flex items-center justify-center py-12">
        <ProgressSpinner aria-label="Loading" />
      </div>
    </template>
  </Suspense>
</template>
