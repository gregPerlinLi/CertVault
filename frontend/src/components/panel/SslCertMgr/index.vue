<script setup lang="ts">
import type { CertInfoDTO } from "@/api/types";
import { getAllSslCertInfo } from "@/api/user/cert/ssl";
import { useUserStore } from "@/stores/user";
import { useToast } from "primevue";

// Stores
const { role } = useUserStore();

// Reactives
const toast = useToast();

const loading = ref(false);
const selection = ref();
const pageTotal = ref<number>();
const pageData = ref<CertInfoDTO[]>();
const pageFirst = ref(0);
const pageLimit = ref(10);

// Actions
const refresh = async () => {
  loading.value = true;

  try {
    const page = await getAllSslCertInfo(
      pageFirst.value / pageLimit.value + 1,
      pageLimit.value
    );
    pageTotal.value = page.total;
    pageData.value = page.list ?? undefined;
  } catch (err: unknown) {
    toast.add({
      severity: "error",
      summary: "Fail to Fetch Data",
      detail: (err as Error).message,
      life: 5000
    });
  }

  loading.value = false;
};

// Watches
watch([pageFirst, pageLimit], () => refresh());

// Hooks
onBeforeMount(async () => {
  await refresh();
});
</script>

<template>
  <Toolbar class="border-none">
    <template #start>
      <div class="flex gap-4">
        <Button icon="pi pi-plus" label="Request New" size="small"></Button>
        <Button
          icon="pi pi-refresh"
          label="Refresh"
          severity="info"
          size="small"
          @click="refresh"></Button>
      </div>
    </template>
    <template #end>
      <form @submit.prevent="() => {}">
        <IconField>
          <InputIcon class="pi pi-search" />
          <InputText name="search-keyword" placeholder="Search" size="small" />
        </IconField>
      </form>
    </template>
  </Toolbar>
  <DataTable
    v-model:first="pageFirst"
    v-model:rows="pageLimit"
    v-model:selection="selection"
    data-key="uuid"
    size="small"
    :loading="loading"
    :row-class="() => 'group'"
    :rows-per-page-options="[10, 20, 50]"
    :total-records="pageTotal"
    :value="pageData"
    lazy
    paginator
    row-hover>
    <template #header>
      <p class="text-sm">
        Found {{ pageTotal ?? 0 }} SSL certificate(s) in total
      </p>
    </template>
    <Column selectionMode="multiple" class="w-4"></Column>
    <Column header="Comment" class="w-0">
      <template #body="{ data }">
        <p
          v-tooltip.right="{ value: data.comment, class: 'text-sm' }"
          class="overflow-x-hidden text-ellipsis whitespace-nowrap max-w-md">
          {{ data.comment }}
        </p>
      </template>
    </Column>
    <Column
      v-if="role === 'Admin' || role === 'Superadmin'"
      field="owner"
      header="Owner"
      class="w-50"></Column>
    <Column header="Status">
      <template #body="{ data }">
        <Badge
          v-if="new Date(data.notBefore).getTime() > Date.now()"
          severity="warn"
          size="small"
          value="Not In Effect"></Badge>
        <Badge
          v-else-if="new Date(data.notAfter).getTime() < Date.now()"
          severity="warn"
          size="small"
          value="Expired"></Badge>
        <Badge v-else severity="success" size="small" value="In Effect"></Badge>
      </template>
    </Column>
    <Column>
      <template #body>
        <div class="gap-2 hidden justify-end group-hover:flex">
          <Button
            v-tooltip.top="{ value: 'Info', class: 'text-sm' }"
            class="h-6 w-6"
            icon="pi pi-info-circle"
            severity="info"
            size="small"
            variant="text"
            rounded></Button>
          <Button
            v-tooltip.top="{ value: 'Renew', class: 'text-sm' }"
            class="h-6 w-6"
            icon="pi pi-sync"
            severity="secondary"
            size="small"
            variant="text"
            rounded></Button>
          <Button
            v-tooltip.top="{ value: 'Edit', class: 'text-sm' }"
            class="h-6 w-6"
            icon="pi pi-pen-to-square"
            severity="help"
            size="small"
            variant="text"
            rounded></Button>
          <Button
            v-tooltip.top="{ value: 'Delete', class: 'text-sm' }"
            class="h-6 w-6"
            icon="pi pi-trash"
            severity="danger"
            size="small"
            variant="text"
            rounded></Button>
        </div>
      </template>
    </Column>
  </DataTable>
</template>
