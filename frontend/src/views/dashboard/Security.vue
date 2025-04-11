<script setup lang="ts">
import type { LoginRecordDTO } from "@/api/types";
import { getUsrLoginRecs } from "@/api/user/user";

// Async components
const AsyncDataTable = defineAsyncComponent(() => import("primevue/datatable"));

// Reactive
const online = ref();
const offline = ref<LoginRecordDTO[] | null>(null);

// Hooks
onBeforeMount(async () => {
  online.value = await getUsrLoginRecs(0, 20, 1);
  getUsrLoginRecs(0, 20, 0).then((p) => (offline.value = p.list));
});
</script>

<template>
  <h1 class="font-bold text-2xl">
    <i class="mr-2 pi pi-shield text-xl"></i>Security
  </h1>
  <hr class="border-2 border-neutral-200 dark:border-neutral-500 my-2" />
  <h2 class="font-bold my-4 text-lg">Online Sessions</h2>
  <h2 class="font-bold my-4 text-lg">Offline Sessions</h2>
  <AsyncDataTable data-key="uuid" :loading="offline === null" :value="offline">
    <Column field="loginTime" header="Login Timestamp" />
    <Column field="ipAddress" header="IP" />
  </AsyncDataTable>
</template>
