<script setup lang="ts">
import type { LoginRecordDTO } from "@/api/types";
import {
  forceLogoutSession,
  forceLogoutUsr,
  getUsrLoginRecs
} from "@/api/user/user";
import { useUserStore } from "@/stores/user";
import { useAsyncGuard, useNotify } from "@/utils/composable";
import { useConfirm } from "primevue/useconfirm";

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

/* Services */
const router = useRouter();
const confirm = useConfirm();
const { isActivate, signal } = useAsyncGuard();
const { toast, info, success, error } = useNotify();

/* Stores */
const user = useUserStore();

/* Reactives */
const online = ref<LoginRecordDTO[] | null>(null);
const offline = ref<LoginRecordDTO[] | null>(null);
const selectedOnline = ref<boolean[]>([]);

const busy = reactive({
  signOutSelected: false,
  signOutAll: false
});
const loading = reactive({
  online: false,
  offline: false
});

/* Actions */
const parseIpRegion = (dto: LoginRecordDTO) => {
  const arr = [dto.region, dto.province, dto.city].filter(
    (s) => s !== "Unknown"
  );
  return arr.length === 0 ? "Unknown" : arr.join(", ");
};
const refreshOnline = async () => {
  loading.online = true;
  online.value = [];
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
  offline.value = [];
  try {
    const page = await getUsrLoginRecs(0, 20, 0, { signal });

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
  const cnt = selectedOnline.value
    .slice(0, -1)
    .reduce((prev, cur) => (cur ? prev + 1 : prev), 0);
  if (cnt === 0) {
    error("Fail to Sign out Selected Session", "No session selected");
    return;
  }

  confirm.require({
    header: "Sign out Selected Sessions",
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
      busy.signOutSelected = true;
      const msg = info("Info", "Signing out");

      try {
        await Promise.allSettled(
          selectedOnline.value
            .slice(0, -1)
            .map((v, i) => {
              return v ? forceLogoutSession(online.value![i].uuid) : null;
            })
            .filter((v) => v !== null)
        );
        success("Success", "Successfully signed out");
      } catch (err: unknown) {
        error("Fail to Sign out", (err as Error).message);
      }

      refreshOnline();
      toast.remove(msg);
      busy.signOutSelected = false;
    }
  });
};
const trySignOutAll = () =>
  confirm.require({
    header: "Sign out all Sessions",
    message: "Are you sure to sign out all the sessions?",
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
      busy.signOutAll = true;
      const msg = info("Info", "Signing out");

      try {
        await forceLogoutUsr();
        success("Success", "Successfully signed out, please re-sign in");
        user.clear();
        router.push("/");
      } catch (err: unknown) {
        error("Fail to Sign out", (err as Error).message);
      }

      toast.remove(msg);
      busy.signOutAll = false;
    }
  });

/* Hooks */
onBeforeMount(() => {
  refreshOnline();
  refreshOffline();
});
</script>

<template>
  <!-- Header -->
  <Breadcrumb
    :home="{ icon: 'pi pi-home' }"
    :model="[
      {
        label: 'My Account',
        icon: 'pi pi-user'
      },
      {
        label: 'Security',
        icon: 'pi pi-shield'
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
  <h2 class="font-bold my-4 text-lg">Online Sessions</h2>
  <AsyncDataTable
    data-key="uuid"
    size="small"
    :loading="loading.online"
    :pt="{ header: { class: 'flex gap-4' } }"
    :value="online"
    row-hover>
    <template #header>
      <Button
        icon="pi pi-refresh"
        label="Refresh"
        severity="info"
        size="small"
        @click="refreshOnline"></Button>
      <Button
        icon="pi pi-sign-out"
        label="Sign out Selected"
        severity="danger"
        size="small"
        :loading="busy.signOutSelected"
        @click="trySignOutSelected"></Button>
      <Button
        icon="pi pi-times"
        label="Sign out all"
        severity="danger"
        size="small"
        :loading="busy.signOutAll"
        @click="trySignOutAll"></Button>
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

  <h2 class="font-bold my-4 text-lg">Last 20 Offline Sessions</h2>
  <AsyncDataTable
    data-key="uuid"
    size="small"
    :loading="loading.offline"
    :value="offline"
    row-hover>
    <template #header>
      <Button
        icon="pi pi-refresh"
        label="Refresh"
        severity="info"
        size="small"
        @click="refreshOffline"></Button>
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
