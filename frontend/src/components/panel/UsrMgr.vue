<script setup lang="ts">
import type { UserProfileDTO } from "@/api/types";
import { getAllUsrInfo } from "@/api/admin/user";
import { useNotify, useRole } from "@/utils/composable";
import { nanoid } from "nanoid";
import { useConfirm } from "primevue/useconfirm";

// Async components
const AsyncDataTable = defineAsyncComponent(() => import("primevue/datatable"));

// Services
const confirm = useConfirm();
const { error } = useNotify();
const { isSuperadmin } = useRole();

// Reactives
const loading = ref(false);
const searchKeyword = ref("");
const selectedUsers = ref([]);

const pagination = reactive({
  total: 0,
  first: 0,
  limit: 10,
  data: [] as UserProfileDTO[]
});

// Non-reactives
let nonce = nanoid();

// Actions
const refresh = async () => {
  nonce = nanoid();
  const tag = nonce;

  try {
    loading.value = true;

    const page = await getAllUsrInfo(
      pagination.first / pagination.limit + 1,
      pagination.limit,
      searchKeyword.value.length === 0 ? undefined : searchKeyword.value
    );

    if (tag === nonce) {
      pagination.total = page.total;
      pagination.data = (page.list ?? []).sort((a, b) =>
        a.role !== b.role ? a.role - b.role : a.username < b.username ? -1 : 1
      );
    }
  } catch (err: unknown) {
    if (tag === nonce) {
      error("Fail to Fetch User List", (err as Error).message);
    }
  } finally {
    if (tag === nonce) {
      loading.value = false;
    }
  }
};

// Watches
watch(
  () => [pagination.first, pagination.limit],
  () => refresh()
);
watchDebounced(searchKeyword, () => refresh(), { debounce: 500 });

// Hooks
onBeforeMount(() => refresh());
</script>

<template>
  <Toolbar class="border-none">
    <template #start>
      <div class="flex gap-4">
        <Button
          v-if="isSuperadmin"
          icon="pi pi-plus"
          label="Batch Create"
          size="small"></Button>
        <Button
          v-if="isSuperadmin"
          icon="pi pi-trash"
          label="Delete Selected"
          severity="danger"
          size="small"></Button>
        <Button
          icon="pi pi-refresh"
          label="Refresh"
          severity="info"
          size="small"
          @click="refresh"></Button>
      </div>
    </template>
    <template #end>
      <IconField>
        <InputIcon class="pi pi-search" />
        <InputText
          v-model.trim="searchKeyword"
          name="search-keyword"
          placeholder="Search"
          size="small" />
      </IconField>
    </template>
  </Toolbar>

  <AsyncDataTable
    v-model:first="pagination.first"
    v-model:rows="pagination.limit"
    v-model:selection="selectedUsers"
    data-key="username"
    size="small"
    :loading="loading"
    :row-class="() => 'group'"
    :rows-per-page-options="[10, 20, 50]"
    :total-records="pagination.total"
    :value="pagination.data"
    lazy
    paginator
    row-hover>
    <template #header>
      <p class="text-sm">Found {{ pagination.total }} User(s) in total</p>
    </template>

    <Column v-if="isSuperadmin" class="w-4" selection-mode="multiple"> </Column>
    <Column header="Username">
      <template #body="{ data }">
        <span
          :class="
            data.role === 1
              ? 'font-bold text-red-500'
              : data.role === 2
                ? 'text-blue-500'
                : ''
          "
          >{{ data.username }}</span
        >
      </template>
    </Column>
    <Column field="displayName" header="Display Name"></Column>
    <Column field="email" header="Email"></Column>

    <!-- Comment column -->
    <!-- <Column header="Comment" class="w-0">
      <template #body="{ data }">
        <div class="flex gap-2 min-w-md">
          <p
            v-tooltip.bottom="{ value: data.comment, class: 'text-sm' }"
            class="overflow-x-hidden text-ellipsis whitespace-nowrap"
            :class="
              variant === 'ssl' || aboveUser
                ? 'max-w-[calc(var(--container-md)-32px)]'
                : 'max-w-md'
            ">
            {{ data.comment }}
          </p>
          <Button
            v-if="variant === 'ssl' || aboveUser"
            v-tooltip.top="{ value: 'Edit', class: 'text-sm' }"
            aria-label="Edit certificate information"
            class="h-6 opacity-0 w-6 group-hover:opacity-100"
            icon="pi pi-pen-to-square"
            severity="help"
            size="small"
            variant="text"
            rounded
            @click="
              () => {
                targetCertData = data;
                dialog.editComment = true;
              }
            "></Button>
        </div>
      </template>
    </Column> -->

    <!-- Operations column -->
    <!-- <Column>
      <template #body="{ data }">
        <div class="gap-2 hidden justify-end group-hover:flex">
          <Button
            v-tooltip.top="{ value: 'Info', class: 'text-sm' }"
            aria-label="Certificate information"
            class="h-6 w-6"
            icon="pi pi-info-circle"
            severity="info"
            size="small"
            variant="text"
            rounded
            @click="
              () => {
                targetCertData = data;
                dialog.showInfo = true;
              }
            "></Button>
          <Button
            v-tooltip.top="{ value: 'Export', class: 'text-sm' }"
            aria-label="Export certificate"
            class="h-6 w-6"
            icon="pi pi-file-export"
            severity="success"
            size="small"
            variant="text"
            rounded
            @click="
              () => {
                targetCertData = data;
                dialog.exportCert = true;
              }
            "></Button>
          <Button
            v-if="variant === 'ssl' || aboveUser"
            v-tooltip.top="{ value: 'Renew', class: 'text-sm' }"
            aria-label="Renew certificate"
            class="h-6 w-6"
            icon="pi pi-sync"
            severity="secondary"
            size="small"
            variant="text"
            rounded
            @click="
              () => {
                targetCertData = data;
                dialog.renewCert = true;
              }
            "></Button>
          <Button
            v-if="variant === 'ca' && aboveUser"
            v-tooltip.top="{
              value: data.available ? 'Disable' : 'Enable',
              class: 'text-sm'
            }"
            class="h-6 w-6"
            size="small"
            variant="text"
            :aria-label="data.available ? 'Disable' : 'Enable'"
            :icon="data.available ? 'pi pi-ban' : 'pi pi-check-circle'"
            :severity="data.available ? 'danger' : 'success'"
            rounded
            @click="tryToggleCertAvailable(data)"></Button>
          <Button
            v-if="variant === 'ssl' || aboveUser"
            v-tooltip.top="{ value: 'Delete', class: 'text-sm' }"
            aria-label="Delete certificate"
            class="h-6 w-6"
            icon="pi pi-trash"
            severity="danger"
            size="small"
            variant="text"
            rounded
            @click="tryDelCert(data)"></Button>
        </div>
      </template>
    </Column> -->
  </AsyncDataTable>
</template>
