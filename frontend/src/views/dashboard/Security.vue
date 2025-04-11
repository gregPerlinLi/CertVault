<script setup lang="ts">
import type { LoginRecordDTO } from "@/api/types";
import { getUsrLoginRecs } from "@/api/user/user";

// Async components
const AsyncDataTable = defineAsyncComponent(() => import("primevue/datatable"));

// Reactive
const online = ref<LoginRecordDTO[] | null>(null);
const offline = ref<LoginRecordDTO[] | null>(null);
const selectedOnline = ref<LoginRecordDTO[]>([]);

// Watches
// watch(
//   selectedOnline,
//   () => {
//     selectedOnline.value = [];
//   },
//   { flush: "post" }
// );

// Hooks
onBeforeMount(async () => {
  getUsrLoginRecs(0, 20, 1).then(
    (p) =>
      (online.value = (p.list ?? []).sort((a, b) =>
        a.loginTime > b.loginTime ? -1 : 1
      ))
  );
  getUsrLoginRecs(0, 20, 0).then(
    (p) =>
      (offline.value = (p.list ?? []).sort((a, b) =>
        a.loginTime > b.loginTime ? -1 : 1
      ))
  );
});
</script>

<template>
  <h1 class="font-bold text-2xl">
    <i class="mr-2 pi pi-shield text-xl"></i>Security
  </h1>
  <hr class="border-2 border-neutral-200 dark:border-neutral-500 my-2" />
  <h2 class="font-bold my-4 text-lg">Online Sessions</h2>
  <AsyncDataTable
    v-model:selection="selectedOnline"
    data-key="uuid"
    size="small"
    :loading="online === null"
    :pt="{ header: { class: 'flex gap-4' } }"
    :value="online">
    <template #header>
      <Button
        icon="pi pi-refresh"
        label="Refresh"
        severity="info"
        size="small"></Button>
      <Button
        icon="pi pi-sign-out"
        label="Logout Selected"
        severity="danger"
        size="small"></Button>
      <Button
        icon="pi pi-times"
        label="Logout All"
        severity="danger"
        size="small"></Button>
    </template>
    <Column class="w-0" selection-mode="multiple"></Column>
    <Column class="w-65" field="loginTime" header="Login Timestamp" />
    <Column class="w-90" field="ipAddress" header="IP" />
    <Column field="browser" header="Browser" />
    <Column field="platform" header="Platform" />
    <Column field="os" header="OS" />
  </AsyncDataTable>
  <h2 class="font-bold my-4 text-lg">Last 20 Offline Sessions</h2>
  <AsyncDataTable
    data-key="uuid"
    size="small"
    :loading="offline === null"
    :value="offline">
    <Column class="w-65" field="loginTime" header="Login Timestamp" />
    <Column class="w-90" field="ipAddress" header="IP" />
    <Column field="browser" header="Browser" />
    <Column field="platform" header="Platform" />
    <Column field="os" header="OS" />
  </AsyncDataTable>
</template>
