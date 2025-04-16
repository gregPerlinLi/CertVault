<script setup lang="ts">
// Async components
const AsyncCertMgr = defineAsyncComponent(
  () => import("@/components/panel/CertMgr.vue")
);
</script>

<template>
  <!-- Header -->
  <Breadcrumb
    class="mb-4"
    :home="{ icon: 'pi pi-home' }"
    :model="[
      {
        label: 'Certificates',
        icon: 'pi pi-verified'
      },
      {
        label: 'CA Certificates',
        icon: 'pi pi-building-columns'
      }
    ]">
    <template #item="{ item }">
      <div class="flex gap-2 items-center">
        <span :class="item.icon"></span>
        <span>{{ item.label }}</span>
      </div>
    </template>
  </Breadcrumb>

  <!-- Main -->
  <Suspense>
    <AsyncCertMgr variant="ca" />
    <template #fallback>
      <div class="flex items-center justify-center py-12">
        <ProgressSpinner aria-label="Loading" />
      </div>
    </template>
  </Suspense>
</template>
