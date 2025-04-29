<script setup lang="ts">
import type { AbortOption, LoginRecordDTO, PageVO } from "@/api/types";
import { parseIpRegion } from "@/utils";

/* Async components */
const AsyncDataTable = useAsyncDataTable();

/* Models */
const data = defineModel("data", { default: [] as LoginRecordDTO[] });
const loading = defineModel("loading", { default: false });

// Properties
const props = defineProps<{
  fetchFn: (
    abort?: AbortOption
  ) => PageVO<LoginRecordDTO> | Promise<PageVO<LoginRecordDTO>>;
}>();

/* Services */
const { error } = useNotify();
const { isActive, getSignal, reset, cancel } = useAsyncGuard();

/* Actions */
const resetStates = () => {
  data.value = [];
  loading.value = false;
};
const refresh = async () => {
  loading.value = true;
  data.value = [];

  try {
    const page = await props.fetchFn({ signal: getSignal() });

    if (isActive.value) {
      data.value = page.list ?? [];
    }
  } catch (err: unknown) {
    if (isActive.value) {
      error((err as Error).message, "Fail to Fetch Offline Sessions");
    }
  }

  loading.value = false;
};

/* Exposes */
defineExpose({
  refresh,
  resetStates,
  reset,
  cancel
});
</script>

<template>
  <AsyncDataTable
    data-key="uuid"
    size="small"
    :loading="loading"
    :value="data"
    row-hover>
    <template #header>
      <Button
        icon="pi pi-refresh"
        label="Refresh"
        severity="info"
        size="small"
        @click="refresh"></Button>
    </template>
    <Column class="w-65" field="loginTime" header="Login Timestamp" />
    <Column class="w-90" header="IP">
      <template #body="{ data }">
        <span
          v-tooltip.bottom="{
            value: parseIpRegion(data),
            pt: { text: 'text-sm w-fit whitespace-nowrap' }
          }"
          >{{ data.ipAddress }}</span
        >
      </template>
    </Column>
    <Column class="w-40" header="Platform">
      <template #body="{ data }">
        <span
          v-tooltip.bottom="{
            value: data.os,
            pt: { text: 'text-sm w-fit whitespace-nowrap' }
          }"
          >{{ data.platform }}</span
        >
      </template>
    </Column>
    <Column field="browser" header="Browser" />
  </AsyncDataTable>
</template>
