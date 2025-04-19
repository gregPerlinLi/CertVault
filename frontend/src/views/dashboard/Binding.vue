<script setup lang="ts">
import type { CaInfoDTO, UserProfileDTO } from "@/api/types";
import { useNotify, useReloadableAsyncGuard } from "@/utils/composable";
import { getAllCaBindedUsrs, unbindCaFromUsrs } from "@/api/admin/cert/binding";
import { useConfirm } from "primevue/useconfirm";

/* Async components */
const AsyncDataTable = defineAsyncComponent(() => import("primevue/datatable"));

/* Services */
const confirm = useConfirm();
const { toast, info, success, error } = useNotify();
const { isActivate, getSignal } = useReloadableAsyncGuard();

/* Reactives */
const busyUnbind = ref(false);
const caSelection = ref<CaInfoDTO | null>(null);

const userList = reactive({
  first: 0,
  total: 0,
  limit: 10,
  search: "",
  data: [] as UserProfileDTO[],
  selections: [] as UserProfileDTO[],
  loading: false
});

const dialogBind = ref(false);

/* Actions */
const refreshUser = async () => {
  if (caSelection.value === null) {
    return;
  }

  userList.loading = true;
  userList.selections = [];
  try {
    const page = await getAllCaBindedUsrs(
      caSelection.value.uuid,
      Math.floor(userList.first / userList.limit) + 1,
      userList.limit,
      userList.search,
      { signal: getSignal() }
    );

    if (isActivate.value) {
      userList.total = page.total;
      userList.data = (page.list ?? []).sort((a, b) =>
        a.role !== b.role ? b.role - a.role : a.username < b.username ? -1 : 1
      );
    }
  } catch (err: unknown) {
    if (isActivate.value) {
      error("Fail to Fetch User List", (err as Error).message);
    }
  }
  userList.loading = false;
};
const tryUnbind = () =>
  confirm.require({
    header: "Unbind Selected Users",
    message: "Are you sure to unbind selected users from selected CA?",
    icon: "pi pi-exclamation-triangle",
    modal: true,
    acceptProps: { severity: "danger" },
    rejectProps: { severity: "secondary", variant: "outlined" },
    accept: async () => {
      busyUnbind.value = true;
      const msg = info("Info", "Unbinding");

      try {
        await unbindCaFromUsrs(
          caSelection.value!.uuid,
          userList.selections.map(({ username }) => username)
        );

        success("Success", "Successfully unbinded");
        userList.selections = [];
        refreshUser();
      } catch (err: unknown) {
        error("Fail to Unbind", (err as Error).message);
      }

      toast.remove(msg);
      busyUnbind.value = false;
    }
  });

/* Watches */
watch(caSelection, () => {
  userList.search = "";
  refreshUser();
});
watch(
  () => [userList.first, userList.limit],
  () => refreshUser()
);
watchDebounced(
  () => userList.search,
  () => refreshUser(),
  { debounce: 500, maxWait: 1000 }
);
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

  <!-- Tools -->
  <Toolbar class="border-none mb-4">
    <template #start>
      <div class="flex gap-4">
        <Button
          icon="pi pi-link"
          label="Bind"
          size="small"
          :disabled="caSelection === null || userList.loading"
          @click="dialogBind = true"></Button>
        <Button
          icon="pi pi-trash"
          label="Unbind Selected"
          severity="danger"
          size="small"
          :disabled="
            caSelection === null ||
            userList.loading ||
            userList.selections.length === 0
          "
          @click="tryUnbind"></Button>
        <Button
          icon="pi pi-refresh"
          label="Refresh"
          severity="info"
          size="small"
          :disabled="caSelection === null || userList.loading"
          @click="refreshUser"></Button>
      </div>
    </template>
    <template #end>
      <SelectCa
        v-model:selection="caSelection"
        class="w-120"
        option-label="comment"
        variant="binding"
        :visible="true" />
    </template>
  </Toolbar>

  <!-- User table -->
  <div v-if="caSelection === null" class="font-bold py-24 text-2xl text-center">
    Please Select a CA
  </div>
  <AsyncDataTable
    v-else
    v-model:first="userList.first"
    v-model:rows="userList.limit"
    v-model:selection="userList.selections"
    data-key="username"
    size="small"
    :loading="userList.loading"
    :pt="{ pcPaginator: { root: { class: 'rounded-none' } } }"
    :row-class="() => 'group'"
    :row-hover="userList.data.length > 0"
    :rows-per-page-options="[10, 20, 50]"
    :total-records="userList.total"
    :value="userList.data"
    lazy
    paginator>
    <template #header>
      <div class="flex items-end justify-between">
        <p class="text-sm">Found {{ userList.total }} User(s) in total</p>
        <IconField>
          <InputIcon class="pi pi-search" />
          <InputText
            v-model.trim="userList.search"
            placeholder="Search"
            size="small" />
        </IconField>
      </div>
    </template>

    <!-- Selector column -->
    <Column class="w-4" selection-mode="multiple"></Column>

    <!-- Info columns -->
    <Column class="w-60" header="Username">
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
    <Column class="w-60" header="Display Name">
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
  </AsyncDataTable>

  <!-- Dialogs -->
  <BindUsrsDlg
    v-model:visible="dialogBind"
    :ca="caSelection"
    @success="refreshUser" />
</template>
