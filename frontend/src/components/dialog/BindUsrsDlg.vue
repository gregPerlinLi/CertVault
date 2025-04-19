<script setup lang="ts">
import type { CaInfoDTO, UserProfileDTO } from "@/api/types";
import { getAllUsrInfo } from "@/api/admin/user";
import { useNotify, useReloadableAsyncGuard } from "@/utils/composable";
import { bindCaToUsrs } from "@/api/admin/cert/binding";

/* Async components */
const AsyncDataTable = defineAsyncComponent(() => import("primevue/datatable"));

/* Models */
const visible = defineModel<boolean>("visible");

// Properties
const props = defineProps<{ ca: CaInfoDTO | null }>();

// Emits
const emits = defineEmits<{ success: [] }>();

/* Services */
const { toast, info, success, error } = useNotify();
const { isActivate, getSignal, reload, cancel } = useReloadableAsyncGuard();

/* Reactives */
const busyBind = ref(false);

const userList = reactive({
  first: 0,
  total: 0,
  limit: 10,
  search: "",
  data: [] as UserProfileDTO[],
  selections: [] as UserProfileDTO[],
  loading: false
});

/* Actions */
const refresh = async () => {
  userList.loading = true;
  userList.selections = [];
  try {
    const page = await getAllUsrInfo(
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
const tryBind = async () => {
  busyBind.value = true;
  const msg = info("Info", "Binding");

  try {
    await bindCaToUsrs(
      props.ca!.uuid,
      userList.selections.map(({ username }) => username)
    );

    success("Success", "Successfully binded");
    emits("success");
    visible.value = false;
  } catch (err: unknown) {
    error("Fail to Bind", (err as Error).message);
  }

  toast.remove(msg);
  busyBind.value = false;
};

/* Watches */
watch(visible, (newValue) => {
  if (newValue) {
    reload();
    refresh();
  } else {
    cancel();
    busyBind.value = false;
    userList.first = 0;
    userList.total = 0;
    userList.limit = 10;
    userList.search = "";
    userList.data = [];
    userList.selections = [];
    userList.loading = false;
  }
});
watch(
  () => [userList.first, userList.limit],
  () => refresh()
);
watchDebounced(
  () => userList.search,
  () => refresh(),
  { debounce: 500, maxWait: 1000 }
);
</script>

<template>
  <Dialog v-model:visible="visible" header="Bind Users" :closable="false" modal>
    <!-- User table -->
    <AsyncDataTable
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
              size="small"
              :disabled="busyBind" />
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

    <!-- Buttons -->
    <div class="flex justify-between mt-4">
      <Button
        label="Refresh"
        severity="info"
        size="small"
        :disabled="busyBind"
        :loading="userList.loading"
        @click="refresh"></Button>
      <div class="flex gap-2">
        <Button
          label="Cancel"
          severity="secondary"
          size="small"
          :disabled="busyBind"
          @click="visible = false"></Button>
        <Button
          label="Bind"
          size="small"
          :disabled="userList.selections.length === 0"
          :loading="busyBind"
          @click="tryBind"></Button>
      </div>
    </div>
  </Dialog>
</template>
