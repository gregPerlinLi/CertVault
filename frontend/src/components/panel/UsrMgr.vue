<script setup lang="ts">
import type { UserProfileDTO } from "@/api/types";
import { getAllUsrInfo } from "@/api/admin/user";
import { useUserStore } from "@/stores/user";
import { useNotify } from "@/utils/composable";
import { nanoid } from "nanoid";
// import { useConfirm } from "primevue/useconfirm";

/* Async components */
const AsyncDataTable = defineAsyncComponent(() => import("primevue/datatable"));

/* Services */
// const confirm = useConfirm();
const { error } = useNotify();

/* Stores */
const { isSuperadmin } = useUserStore();

/* Reactives */
const loading = ref(false);
const searchKeyword = ref("");
const selectedUsers = ref([]);

const pagination = reactive({
  total: 0,
  first: 0,
  limit: 10,
  data: [] as UserProfileDTO[]
});

/* Non-reactives */
let nonce = nanoid();

/* Actions */
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
        a.role !== b.role ? b.role - a.role : a.username < b.username ? -1 : 1
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

/* Watches */
watch(
  () => [pagination.first, pagination.limit],
  () => refresh()
);
watchDebounced(searchKeyword, () => refresh(), { debounce: 500 });

/* Hooks */
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

    <!-- Selector column -->
    <Column v-if="isSuperadmin" class="w-4" selection-mode="multiple"></Column>

    <!-- Info column -->
    <Column class="w-0" header="Username">
      <template #body="{ data }">
        <div class="flex w-60">
          <p
            v-tooltip.bottom="{ value: data.username, class: 'text-sm' }"
            class="overflow-x-hidden text-ellipsis whitespace-nowrap"
            :class="
              data.role === 3
                ? 'font-bold text-red-500'
                : data.role === 2
                  ? 'text-blue-500'
                  : ''
            ">
            {{ data.username }}
          </p>
        </div>
      </template>
    </Column>
    <Column class="w-0" header="Display Name">
      <template #body="{ data }">
        <div class="flex w-60">
          <p
            v-tooltip.bottom="{ value: data.displayName, class: 'text-sm' }"
            class="overflow-x-hidden text-ellipsis whitespace-nowrap">
            {{ data.displayName }}
          </p>
        </div>
      </template>
    </Column>
    <Column header="Email">
      <template #body="{ data }">
        <div class="flex w-80">
          <p
            v-tooltip.bottom="{ value: data.email, class: 'text-sm' }"
            class="overflow-x-hidden text-ellipsis whitespace-nowrap">
            {{ data.email }}
          </p>
        </div>
      </template>
    </Column>

    <!-- Operations column -->
    <Column v-if="isSuperadmin">
      <template #body>
        <div class="gap-2 hidden justify-end group-hover:flex">
          <Button
            v-tooltip.top="{ value: 'Update Password', class: 'text-sm' }"
            aria-label="Update Password"
            class="h-6 w-6"
            icon="pi pi-key"
            severity="success"
            size="small"
            variant="text"
            rounded></Button>
          <Button
            v-tooltip.top="{ value: 'Update Role', class: 'text-sm' }"
            aria-label="Update Role"
            class="h-6 w-6"
            icon="pi pi-user"
            severity="help"
            size="small"
            variant="text"
            rounded></Button>
        </div>
      </template>
    </Column>
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
