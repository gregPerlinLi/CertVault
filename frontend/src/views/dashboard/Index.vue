<script setup lang="ts">
import { countAllUsrs, countRequestedCaCerts } from "@/api/admin/stats";
import { countAllCaCerts, countAllSslCerts } from "@/api/superadmin/stats";
import { countBindedCa, countRequestedSslCerts } from "@/api/user/stats";
import ErrorPlaceholer from "@comps/placeholder/ErrorPlaceholer.vue";
import LoadingPlaceholder from "@comps/placeholder/LoadingPlaceholder.vue";

/* Async components */
const AsyncDataTable = defineAsyncComponent({
  suspensible: false,
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
const { error } = useNotify();

/* Stores */
const { displayName, isAdmin, isSuperadmin, getRoleClass } = useUserStore();

/* Reactive */
const busy = ref(false);
const count = reactive({
  bindedCa: null as number | null,
  reqSsl: null as number | null,
  allUsr: null as number | null,
  reqCa: null as number | null,
  allCa: null as number | null,
  allSsl: null as number | null
});

/* Computed */
const tableItems = computed(() => {
  const ret = [
    {
      name: "#Binded CA",
      value: count.bindedCa
    },
    {
      name: "#Requested SSL Certificates",
      value: count.reqSsl
    }
  ];

  if (isAdmin.value || isSuperadmin.value) {
    ret.push(
      {
        name: "#Total Users",
        value: count.allUsr
      },
      {
        name: "#Requested CA Certificates",
        value: count.reqCa
      }
    );
  }

  if (isSuperadmin.value) {
    ret.push(
      {
        name: "#Total CA Certificates",
        value: count.allCa
      },
      {
        name: "#Total SSL Certificates",
        value: count.allSsl
      }
    );
  }

  return ret;
});

/* Actions */
const refresh = async () => {
  busy.value = true;

  try {
    const results = await Promise.allSettled([
      countBindedCa({ abort: { timeout: 20000 } }).then(
        (v) => (count.bindedCa = v)
      ),
      countRequestedSslCerts({ abort: { timeout: 20000 } }).then(
        (v) => (count.reqSsl = v)
      ),
      ...(isAdmin.value || isSuperadmin.value
        ? [
            countAllUsrs({ abort: { timeout: 20000 } }).then(
              (v) => (count.allUsr = v)
            ),
            countRequestedCaCerts({ abort: { timeout: 20000 } }).then(
              (v) => (count.reqCa = v)
            )
          ]
        : []),
      ...(isSuperadmin.value
        ? [
            countAllCaCerts({ abort: { timeout: 20000 } }).then(
              (v) => (count.allCa = v)
            ),
            countAllSslCerts({ abort: { timeout: 20000 } }).then(
              (v) => (count.allSsl = v)
            )
          ]
        : [])
    ]);

    for (const i of results) {
      if (i.status === "rejected") {
        throw i.reason;
      }
    }
  } catch (err: unknown) {
    error((err as Error).message, "Fail to Get Statistics");
  }

  busy.value = false;
};

/* Hooks */
onBeforeMount(() => refresh());
</script>

<template>
  <!-- Headers -->
  <Breadcrumb
    class="mb-4"
    :home="{ icon: 'pi pi-home' }"
    :model="[
      {
        label: 'Dashboard',
        icon: 'pi pi-gauge'
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
  <h1 class="font-bold my-8 text-xl">
    Welcome to use CertVault,
    <span :class="getRoleClass()">{{ displayName }}</span>
  </h1>
  <Panel>
    <template #header>
      <div class="flex gap-1 items-center">
        <span class="font-bold">Statistics</span>
        <OperationButton
          icon="pi-refresh"
          label="Refresh"
          severity="secondary"
          :disabled="busy"
          @click="refresh()" />
      </div>
    </template>
    <div v-if="busy" class="text-center">
      <ProgressSpinner />
    </div>
    <AsyncDataTable v-else data-key="name" :value="tableItems">
      <Column body-class="font-bold" field="name" />
      <Column field="value" />
    </AsyncDataTable>
  </Panel>
</template>
