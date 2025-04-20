<script setup lang="ts">
import type { AbortOption } from "@/api";
import type { PaginationVO, UserProfileDTO } from "@/api/types";
import type { DataTableCellEditCompleteEvent } from "primevue/datatable";
import { useNotify, useReloadableAsyncGuard } from "@/utils/composable";

import ErrorPlaceholer from "@comps/placeholder/ErrorPlaceholer.vue";
import LoadingPlaceholder from "@comps/placeholder/LoadingPlaceholder.vue";

/* Async components */
const AsyncDataTable = defineAsyncComponent({
  loader: () => import("primevue/datatable"),
  loadingComponent: LoadingPlaceholder,
  errorComponent: ErrorPlaceholer,
  onError: (err, retry, fail, attampts) => {
    if (attampts < 5) {
      retry();
    } else {
      error("Fail to Load Data Table Component", err.message);
      fail();
    }
  }
});

/* Models */
const first = defineModel<number>("first", { default: 0 });
const totalRecords = defineModel<number>("total-records", { default: 0 });
const rows = defineModel<number>("rows", { default: 10 });
const search = defineModel<string>("search", { default: "" });
const data = defineModel<UserProfileDTO[]>("data", { default: [] });
const selection = defineModel<UserProfileDTO[]>("selection", { default: [] });
const loading = defineModel<boolean>("loading", { default: false });

// Properties
const props = defineProps<{
  editable?: boolean;
  refreshFn: (
    page: number,
    limit: number,
    keyword?: string,
    abort?: AbortOption
  ) => PaginationVO<UserProfileDTO> | Promise<PaginationVO<UserProfileDTO>>;
  selectable?: boolean;
}>();

// Emits
defineEmits<{
  cellEditComplete: [DataTableCellEditCompleteEvent];
}>();

/* Services */
const { error } = useNotify();
const { isActivate, getSignal, reload, cancel } = useReloadableAsyncGuard();

/* Actions */
const resetStates = () => {
  first.value = 0;
  totalRecords.value = 0;
  rows.value = 10;
  search.value = "";
  data.value = [];
  selection.value = [];
  loading.value = false;
};
const refresh = async () => {
  if (!isActivate.value) {
    return;
  }

  loading.value = true;
  selection.value = [];
  try {
    const page = await props.refreshFn(
      Math.floor(first.value / rows.value) + 1,
      rows.value,
      search.value,
      { signal: getSignal() }
    );

    if (isActivate.value) {
      totalRecords.value = page.total;
      data.value = (page.list ?? []).sort((a, b) =>
        a.role !== b.role ? b.role - a.role : a.username < b.username ? -1 : 1
      );
    }
  } catch (err: unknown) {
    if (isActivate.value) {
      error("Fail to Fetch User List", (err as Error).message);
    }
  }
  loading.value = false;
};

/* Watches */
watch([first, rows], () => refresh());
watchDebounced(search, () => refresh(), { debounce: 500 });

/* Exposes */
defineExpose({
  refresh,
  resetStates,
  reload,
  cancel
});
</script>

<template>
  <AsyncDataTable
    v-model:first="first"
    v-model:rows="rows"
    v-model:selection="selection"
    data-key="username"
    size="small"
    :edit-mode="editable ? 'cell' : undefined"
    :loading="loading"
    :pt="{ pcPaginator: { root: { class: 'rounded-none' } } }"
    :row-class="() => 'group'"
    :row-hover="data.length > 0"
    :rows-per-page-options="[10, 20, 50]"
    :total-records="totalRecords"
    :value="data"
    @cell-edit-complete="$emit('cellEditComplete', $event)"
    lazy
    paginator>
    <!-- Header -->
    <template #header>
      <div class="flex items-end justify-between">
        <p class="text-sm">Found {{ totalRecords }} User(s) in total</p>
        <IconField>
          <InputIcon class="pi pi-search" />
          <InputText v-model.trim="search" placeholder="Search" size="small" />
        </IconField>
      </div>
    </template>

    <!-- Selector -->
    <Column
      v-if="selectable ?? false"
      class="w-4"
      selection-mode="multiple"></Column>

    <!-- Info columns -->
    <Column class="w-60" header="Username">
      <template #body="{ data }: { data: UserProfileDTO }">
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
    <Column class="w-60" field="displayName" header="Display Name">
      <template #body="{ data }: { data: UserProfileDTO }">
        <div class="flex items-baseline w-60">
          <p
            v-tooltip.bottom="{ value: data.displayName, class: 'text-sm' }"
            class="overflow-x-hidden text-ellipsis whitespace-nowrap">
            {{ data.displayName }}
          </p>
          <i
            v-if="editable ?? false"
            class="pi pi-pencil hidden! ml-2 opacity-50 text-xs group-hover:inline-block!"></i>
        </div>
      </template>
      <template #editor="{ data }: { data: UserProfileDTO }">
        <InputText v-model="data.displayName" size="small" autofocus />
      </template>
    </Column>
    <Column field="email" header="Email">
      <template #body="{ data }: { data: UserProfileDTO }">
        <div class="flex items-baseline w-80">
          <p
            v-tooltip.bottom="{ value: data.email, class: 'text-sm' }"
            class="overflow-x-hidden text-ellipsis whitespace-nowrap">
            {{ data.email }}
          </p>
          <i
            v-if="editable ?? false"
            class="pi pi-pencil hidden! ml-2 opacity-50 text-xs group-hover:inline-block!"></i>
        </div>
      </template>
      <template #editor="{ data }: { data: UserProfileDTO }">
        <InputText v-model="data.email" size="small" autofocus />
      </template>
    </Column>

    <!-- Operations column -->
    <slot name="operations"></slot>
  </AsyncDataTable>
</template>
