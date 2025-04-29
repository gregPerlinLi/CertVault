<script setup lang="ts">
import type { AbortOption, LoginRecordDTO, PageVO } from "@/api/types";
import { forceLogoutSession } from "@/api/user/user";
import { parseIpRegion } from "@/utils";
import { useConfirm } from "primevue/useconfirm";

/* Async components */
const AsyncDataTable = useAsyncDataTable();

/* Models */
const data = defineModel("data", { default: [] as LoginRecordDTO[] });
const selection = defineModel("selection", { default: [] as LoginRecordDTO[] });
const loading = defineModel("loading", { default: false });

// Properties
const props = defineProps<{
  fetchFn: (
    abort?: AbortOption
  ) => PageVO<LoginRecordDTO> | Promise<PageVO<LoginRecordDTO>>;
  signOutAllFn: (abort?: AbortOption) => void | Promise<void>;
}>();

/* Services */
const confirm = useConfirm();
const { success, info, error, remove } = useNotify();
const { isActive, getSignal, reset, cancel } = useAsyncGuard();

/* Reactive */
const selectionFlags = ref<boolean[]>([]);
const busy = reactive({
  signOutSel: false,
  signOutAll: false
});

/* Computed */
const canSignOutSel = computed(
  () =>
    selectionFlags.value
      .slice(0, -1)
      .reduce((prev, cur) => (cur ? prev + 1 : prev), 0) > 0
);

/* Actions */
const resetStates = () => {
  data.value = [];
  selection.value = [];
  loading.value = false;
};
const refresh = async () => {
  loading.value = true;
  data.value = [];
  selection.value = [];

  try {
    const page = await props.fetchFn({ signal: getSignal() });

    if (isActive.value) {
      data.value = page.list ?? [];
      selectionFlags.value = Array(data.value.length + 1).fill(false);
    }
  } catch (err: unknown) {
    if (isActive.value) {
      error((err as Error).message, "Fail to Fetch Offline Sessions");
    }
  }

  loading.value = false;
};
const trySignOutSelected = () => {
  confirm.require({
    header: "Sign out Selected Sessions",
    message: "Are you sure to sign out the selected sessions?",
    icon: "pi pi-exclamation-triangle",
    modal: true,
    acceptProps: { severity: "danger" },
    rejectProps: {
      severity: "secondary",
      variant: "outlined"
    },
    accept: async () => {
      busy.signOutSel = true;
      const msg = info("Signing out");

      try {
        await Promise.allSettled(
          selection.value.map(({ uuid }) => forceLogoutSession({ uuid }))
        );
        success("Successfully signed out");
      } catch (err: unknown) {
        error((err as Error).message, "Fail to Sign out");
      }

      refresh();
      remove(msg);
      busy.signOutSel = false;
    }
  });
};
const trySignOutAll = () =>
  confirm.require({
    header: "Sign out all Sessions",
    message: "Are you sure to sign out all the sessions?",
    icon: "pi pi-exclamation-triangle",
    modal: true,
    acceptProps: { severity: "danger" },
    rejectProps: {
      severity: "secondary",
      variant: "outlined"
    },
    accept: async () => {
      busy.signOutAll = true;
      const msg = info("Signing out");

      try {
        await props.signOutAllFn({ signal: getSignal() });
        success("Successfully signed out, please re-sign in");
      } catch (err: unknown) {
        error((err as Error).message, "Fail to Sign out");
      }

      remove(msg);
      busy.signOutAll = false;
    }
  });

/* Watches */
watch(
  selectionFlags,
  (newValue) => {
    selection.value = data.value.filter((_, idx) => newValue[idx]);
  },
  { deep: true }
);

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
    :pt="{ header: { class: 'flex gap-4' } }"
    :value="data"
    row-hover>
    <template #header>
      <Button
        icon="pi pi-refresh"
        label="Refresh"
        severity="info"
        size="small"
        @click="refresh"></Button>
      <Button
        icon="pi pi-sign-out"
        label="Sign out Selected"
        severity="danger"
        size="small"
        :disabled="!canSignOutSel"
        :loading="busy.signOutSel"
        @click="trySignOutSelected()"></Button>
      <Button
        icon="pi pi-times"
        label="Sign out all"
        severity="danger"
        size="small"
        :loading="busy.signOutAll"
        @click="trySignOutAll()"></Button>
    </template>
    <Column class="w-0">
      <template #header>
        <Checkbox
          v-model="selectionFlags[selectionFlags.length - 1]"
          @change="
            () => {
              if (selectionFlags[selectionFlags.length - 1]) {
                selectionFlags = Array(selectionFlags.length)
                  .fill(true)
                  .map((_, idx) =>
                    idx < selectionFlags.length - 1 &&
                    data[idx].isCurrentSession
                      ? false
                      : true
                  );
              } else {
                selectionFlags = Array(selectionFlags.length).fill(false);
              }
            }
          "
          binary />
      </template>
      <template #body="{ data, index }">
        <Checkbox v-if="data.isCurrentSession" :disabled="true" binary />
        <Checkbox
          v-else
          v-model="selectionFlags[index]"
          @change="
            () => {
              const cnt = selectionFlags
                .slice(0, -1)
                .reduce((cnt, cur) => (cur ? cnt + 1 : cnt), 0);
              console.log(cnt, selectionFlags, selectionFlags.length);
              if (cnt === selectionFlags.length - 2) {
                selectionFlags[selectionFlags.length - 1] = true;
              } else {
                selectionFlags[selectionFlags.length - 1] = false;
              }
            }
          "
          binary />
      </template>
    </Column>
    <Column class="w-65" header="Login Timestamp">
      <template #body="{ data }">
        <span
          :class="data.isCurrentSession ? 'font-bold text-green-500' : ''"
          >{{ data.loginTime }}</span
        >
      </template>
    </Column>
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
