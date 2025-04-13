<script setup lang="ts">
import type { LoginRecordDTO } from "@/api/types";
import { forceLogoutUsr, getUsrLoginRecs } from "@/api/user/user";
import { useAsyncGuard, useNotify } from "@/utils/composable";
import { useConfirm } from "primevue/useconfirm";

// Async components
const AsyncDataTable = defineAsyncComponent(() => import("primevue/datatable"));

// Services
const router = useRouter();
const confirm = useConfirm();
const { isActivate, signal } = useAsyncGuard();
const { info, error } = useNotify();

// Reactive
const online = ref<LoginRecordDTO[] | null>(null);
const offline = ref<LoginRecordDTO[] | null>(null);
const selectedOnline = ref<boolean[]>([]);

const loading = reactive({
  online: false,
  offline: false
});

// Actions
const refreshOnline = async () => {
  loading.online = true;
  try {
    const page = await getUsrLoginRecs(0, 5, 1, { signal });

    if (isActivate.value) {
      online.value = (page.list ?? []).sort((a, b) =>
        a.loginTime > b.loginTime ? -1 : 1
      );
      selectedOnline.value = Array(online.value.length + 1).fill(false);
    }
  } catch (err: unknown) {
    if (isActivate.value) {
      error("Fail to Fetch Online Sessions", (err as Error).message);
    }
  }
  loading.online = false;
};
const refreshOffline = async () => {
  loading.offline = true;
  try {
    const page = await getUsrLoginRecs(0, 50, 0, { signal });

    if (isActivate.value) {
      offline.value = (page.list ?? []).sort((a, b) =>
        a.loginTime > b.loginTime ? -1 : 1
      );
    }
  } catch (err: unknown) {
    if (isActivate.value) {
      error("Fail to Fetch Offline Sessions", (err as Error).message);
    }
  }
  loading.offline = false;
};
const trySignOutSelected = () => {
  if (selectedOnline.value.length === 0) {
    error("Fail to Sign out Selected Session", "No session selected");
    return;
  }

  confirm.require({
    header: "Sign Out Selected Sessions",
    message: "Are you sure to sign out the selected sessions?",
    icon: "pi pi-exclamation-triangle",
    modal: true,
    acceptProps: {
      severity: "danger"
    },
    rejectProps: {
      severity: "secondary",
      variant: "outlined"
    },
    accept: async () => {
      info("Info", "Signing out");
    }
  });
};
const trySignOutAll = () => {
  confirm.require({
    header: "Sign Out All Sessions",
    message: "Are you sure to sign out ALL the sessions?",
    icon: "pi pi-exclamation-triangle",
    modal: true,
    acceptProps: {
      severity: "danger"
    },
    rejectProps: {
      severity: "secondary",
      variant: "outlined"
    },
    accept: async () => {
      info("Info", "Signing out");
      loading.online = true;
      await forceLogoutUsr();
      router.push("/");
    }
  });
};

// Hooks
onBeforeMount(() => {
  refreshOnline();
  refreshOffline();
});
</script>

<template>
  <h1 class="font-bold text-2xl">
    <i class="mr-2 pi pi-shield text-xl"></i>Security
  </h1>
  <hr class="border-2 border-neutral-200 dark:border-neutral-500 my-2" />
  <h2 class="font-bold my-4 text-lg">Online Sessions</h2>
  <AsyncDataTable
    data-key="uuid"
    size="small"
    :loading="loading.online"
    :pt="{ header: { class: 'flex gap-4' } }"
    :value="online">
    <template #header>
      <Button
        icon="pi pi-refresh"
        label="Refresh"
        severity="info"
        size="small"
        @click="refreshOnline" />
      <Button
        icon="pi pi-sign-out"
        label="Sign out Selected"
        severity="danger"
        size="small"
        @click="trySignOutSelected" />
      <Button
        icon="pi pi-times"
        label="Sign out All"
        severity="danger"
        size="small"
        @click="trySignOutAll" />
    </template>
    <Column class="w-0">
      <template #header>
        <Checkbox
          v-model="selectedOnline[selectedOnline.length - 1]"
          @change="
            () => {
              if (selectedOnline[selectedOnline.length - 1]) {
                selectedOnline = Array(selectedOnline.length)
                  .fill(true)
                  .map((_, idx) =>
                    idx < selectedOnline.length - 1 &&
                    online![idx].isCurrentSession
                      ? false
                      : true
                  );
              } else {
                selectedOnline = Array(selectedOnline.length).fill(false);
              }
            }
          "
          binary />
      </template>
      <template #body="{ data, index }">
        <Checkbox v-if="data.isCurrentSession" :disabled="true" binary />
        <Checkbox
          v-else
          v-model="selectedOnline[index]"
          @change="
            () => {
              const cnt = selectedOnline.reduce(
                (cnt, cur) => (cur ? cnt + 1 : cnt),
                0
              );
              if (cnt === selectedOnline.length - 2) {
                selectedOnline[selectedOnline.length - 1] = true;
              } else {
                selectedOnline[selectedOnline.length - 1] = false;
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
    <Column class="w-90" field="ipAddress" header="IP" />
    <Column field="browser" header="Browser" />
    <Column field="platform" header="Platform" />
    <Column field="os" header="OS" />
  </AsyncDataTable>
  <h2 class="font-bold my-4 text-lg">Last 50 Offline Sessions</h2>
  <AsyncDataTable
    data-key="uuid"
    size="small"
    :loading="loading.offline"
    :value="offline">
    <template #header>
      <Button
        icon="pi pi-refresh"
        label="Refresh"
        severity="info"
        size="small"
        @click="refreshOffline"></Button>
    </template>
    <Column class="w-65" field="loginTime" header="Login Timestamp" />
    <Column class="w-90" field="ipAddress" header="IP" />
    <Column field="browser" header="Browser" />
    <Column field="platform" header="Platform" />
    <Column field="os" header="OS" />
  </AsyncDataTable>
</template>
