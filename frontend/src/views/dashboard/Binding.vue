<script setup lang="ts">
import type { CaInfoDTO, UserProfileDTO } from "@api/types";
import { getAllCaBindedUsrs, unbindCaFromUsrs } from "@api/admin/cert/binding";
import { useConfirm } from "primevue/useconfirm";

/* Services */
const confirm = useConfirm();
const { success, info, error, remove } = useNotify();

const refUsrTable = useTemplateRef("usr-table");

/* Reactives */
const busyUnbind = ref(false);
const caSelection = ref<CaInfoDTO | null>(null);

const usrTable = reactive({
  search: "",
  selection: [] as UserProfileDTO[],
  loading: false
});
const dialog = reactive({
  bind: false,
  info: false
});

/* Actions */
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
      const msg = info("Unbinding");

      try {
        await unbindCaFromUsrs({
          caUuid: caSelection.value!.uuid,
          usernames: usrTable.selection.map(({ username }) => username)
        });

        success("Successfully unbinded");
        usrTable.selection = [];
        refUsrTable.value?.refresh();
      } catch (err: unknown) {
        error((err as Error).message, "Fail to Unbind");
      }

      remove(msg);
      busyUnbind.value = false;
    }
  });

/* Watches */
watch(caSelection, async () => {
  usrTable.search = "";
  await nextTick();
  refUsrTable.value?.refresh();
});
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
          :disabled="caSelection === null || usrTable.loading"
          @click="dialog.bind = true"></Button>
        <Button
          icon="pi pi-trash"
          label="Unbind Selected"
          severity="danger"
          size="small"
          :disabled="
            caSelection === null ||
            usrTable.loading ||
            usrTable.selection.length === 0
          "
          @click="tryUnbind"></Button>
        <Button
          icon="pi pi-refresh"
          label="Refresh"
          severity="info"
          size="small"
          :disabled="caSelection === null || usrTable.loading"
          @click="refUsrTable?.refresh()"></Button>
        <Button
          icon="pi pi-info-circle"
          label="CA Info"
          severity="secondary"
          size="small"
          :disabled="caSelection === null"
          @click="dialog.info = true"></Button>
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
  <UsrTbl
    v-else
    v-model:search="usrTable.search"
    v-model:selection="usrTable.selection"
    v-model:loading="usrTable.loading"
    ref="usr-table"
    :refresh-fn="
      (page, limit, keyword, abort) =>
        getAllCaBindedUsrs({
          uuid: caSelection!.uuid,
          page,
          limit,
          keyword,
          abort
        })
    "
    selectable />

  <!-- Dialogs -->
  <BindUsrsDlg
    v-model:visible="dialog.bind"
    :ca="caSelection"
    @success="refUsrTable?.refresh()" />
  <DispCertInfoDlg
    v-model:visible="dialog.info"
    variant="ca"
    :data="caSelection ?? undefined" />
</template>
