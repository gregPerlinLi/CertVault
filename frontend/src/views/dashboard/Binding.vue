<script setup lang="ts">
import type { CaInfoDTO, UserProfileDTO } from "@/api/types";
import type { VirtualScrollerLazyEvent } from "primevue/virtualscroller";
import { getAllCaInfo } from "@/api/admin/cert/ca";
import { useNotify, useReloadableAsyncGuard } from "@/utils/composable";
import { getAllCaBindedUsrs } from "@/api/admin/cert/binding";

/* Async components */
const AsyncDataTable = defineAsyncComponent(() => import("primevue/datatable"));

/* Services */
const { error } = useNotify();
const { isActivate, getSignal } = useReloadableAsyncGuard();

/* Reactives */
//const busy = ref(false);
const loading = ref(false);

const caUuid = ref<string | null>(null);

const caList = ref<CaInfoDTO[]>([]);
const loadingCaList = ref(false);

const selectedUsers = ref([]);
const pagination = reactive({
  total: 0,
  first: 0,
  limit: 10,
  data: [] as UserProfileDTO[]
});

/* Actions */
const onLazyLoadCaList = async (ev: VirtualScrollerLazyEvent) => {
  loadingCaList.value = true;

  try {
    const page = await getAllCaInfo(
      Math.trunc(ev.first / 30) + 1,
      30,
      undefined,
      {
        signal: getSignal()
      }
    );

    if (isActivate.value && page.list !== null) {
      const tmp = caList.value.slice();
      for (let i = ev.first; i < ev.first + page.list.length; i++) {
        tmp[i] = page.list[i - ev.first];
      }

      caList.value = tmp.filter(({ available }) => available);
    }
  } catch (err: unknown) {
    if (isActivate.value) {
      error("Fail to Get Binded CA List", (err as Error).message);
    }
  }

  loadingCaList.value = false;
};
const refresh = async () => {
  loading.value = true;

  try {
    const page = await getAllCaBindedUsrs(
      caUuid.value!,
      pagination.first / pagination.limit + 1,
      pagination.limit,
      //searchKeyword.value.length === 0 ? undefined : searchKeyword.value
      undefined,
      { signal: getSignal() }
    );

    if (isActivate.value) {
      pagination.total = page.total;
      pagination.data = (page.list ?? []).sort((a, b) =>
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
watch(caUuid, () => refresh());
</script>

<template>
  <!-- Headers -->
  <Breadcrumb
    class="mb-4"
    :home="{ icon: 'pi pi-home' }"
    :model="[
      {
        label: 'CA Binding',
        icon: 'pi pi-link'
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
  <Toolbar class="border-none">
    <template #start>
      <div class="flex gap-4">
        <Button
          icon="pi pi-link"
          label="Bind"
          size="small"
          :disabled="caUuid === null || loading"></Button>
        <Button
          icon="pi pi-trash"
          label="Unbind Selected"
          severity="danger"
          size="small"
          :disabled="caUuid === null || loading"></Button>
        <Button
          icon="pi pi-refresh"
          label="Refresh"
          severity="info"
          size="small"
          :disabled="caUuid === null || loading"
          @click="refresh"></Button>
      </div>
    </template>
    <template #end>
      <Select
        v-model="caUuid"
        class="w-120"
        option-label="comment"
        option-value="uuid"
        placeholder="Select a CA"
        size="small"
        :options="caList"
        :virtual-scroller-options="{
          delay: 250,
          itemSize: 30,
          lazy: true,
          loading: loadingCaList,
          onLazyLoad: onLazyLoadCaList,
          showLoader: true
        }"
        checkmark>
        <template #option="slotProps">
          <p
            v-tooltip.left="{
              value: slotProps.option.uuid,
              class: 'text-sm -translate-x-3'
            }"
            class="w-full">
            {{ slotProps.option.comment }}
          </p>
        </template>
      </Select>
    </template>
  </Toolbar>

  <div v-if="caUuid === null" class="font-bold py-24 text-2xl text-center">
    Please Select a CA
  </div>
  <AsyncDataTable
    v-else
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
    <Column class="w-4" selection-mode="multiple"></Column>

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
    <Column>
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
