<script setup lang="ts">
import type { UserProfileDTO } from "@api/types";
import type { DataTableCellEditCompleteEvent } from "primevue/datatable";
import { getAllUsrInfo } from "@api/admin/user";
import { deleteMultiUsrs, updateUsrInfo } from "@api/superadmin/user";
import { useConfirm } from "primevue/useconfirm";

/* Services */
const confirm = useConfirm();
const { success, info, warn, error, remove } = useNotify();

/* Stores */
const { isSuperadmin } = useUserStore();

/* Reactive */
const refUsrTable = useTemplateRef("user-table");
const usrTbl = reactive({
  selection: [] as UserProfileDTO[],
  loading: false
});
const busy = reactive({
  delSelUsrs: false
});
const dialog = reactive({
  target: null as UserProfileDTO | null,
  createNewUsrs: false,
  updRols: false,
  updPasswd: false
});

/* Actions */
const onEdit = async ({
  data,
  field,
  newValue,
  value
}: DataTableCellEditCompleteEvent) => {
  newValue = newValue.trim();
  value = value.trim();

  if (newValue === value) {
    return;
  }

  if (field === "displayName") {
    if (newValue.length === 0) {
      warn("Display name cannot be empty");
      return;
    }

    const msg = info("Updating display name");
    try {
      await updateUsrInfo({
        username: data.username,
        displayName: newValue
      });
      success("Successfully updated display name");
      refUsrTable.value?.refresh();
    } catch (err: unknown) {
      error((err as Error).message, "Fail to Update Display Name");
    }
    remove(msg);
  }

  if (field === "email") {
    if (newValue.length === 0) {
      warn("Email cannot be empty");
      return;
    }

    const msg = info("Updating email");
    try {
      await updateUsrInfo({
        username: data.username,
        email: newValue
      });
      success("Successfully updated email");
      refUsrTable.value?.refresh();
    } catch (err: unknown) {
      error((err as Error).message, "Fail to Update Email");
    }
    remove(msg);
  }
};

const tryDelSelUsrs = () =>
  confirm.require({
    header: "Delete Selected Users",
    message: "Are you sure to delete selected users?",
    icon: "pi pi-exclamation-triangle",
    modal: true,
    acceptProps: { severity: "danger" },
    rejectProps: { severity: "secondary", variant: "outlined" },
    accept: async () => {
      busy.delSelUsrs = true;
      const msg = info("Deleting");

      try {
        await deleteMultiUsrs({
          usernames: usrTbl.selection.map(({ username }) => username)
        });

        success("Deleted");
        usrTbl.selection = [];
        refUsrTable.value?.refresh();
      } catch (err: unknown) {
        error((err as Error).message, "Fail to Delete");
      }

      remove(msg);
      busy.delSelUsrs = false;
    }
  });

/* Hooks */
onMounted(() => refUsrTable.value?.refresh());
</script>

<template>
  <!-- Headers -->
  <Breadcrumb
    class="mb-4"
    :home="{ icon: 'pi pi-home' }"
    :model="[
      {
        label: 'Users',
        icon: 'pi pi-users'
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
  <Toolbar class="border-none">
    <template #start>
      <div class="flex gap-4">
        <Button
          icon="pi pi-plus"
          label="Create New"
          severity="success"
          size="small"
          :disabled="usrTbl.loading"
          @click="dialog.createNewUsrs = true"></Button>
        <Button
          icon="pi pi-trash"
          label="Delete Selected"
          severity="danger"
          size="small"
          :disabled="usrTbl.selection.length === 0"
          :loading="busy.delSelUsrs"
          @click="tryDelSelUsrs()"></Button>
        <Button
          icon="pi pi-user"
          label="Update Selected Roles"
          severity="help"
          size="small"
          :disabled="usrTbl.selection.length === 0"
          @click="dialog.updRols = true"></Button>
        <Button
          icon="pi pi-refresh"
          label="Refresh"
          severity="info"
          size="small"
          :loading="usrTbl.loading"
          @click="refUsrTable?.refresh()"></Button>
      </div>
    </template>
  </Toolbar>

  <!-- User table -->
  <UsrTbl
    v-model:selection="usrTbl.selection"
    v-model:loading="usrTbl.loading"
    ref="user-table"
    :editable="isSuperadmin"
    :refresh-fn="
      (page, limit, keyword, abort) =>
        getAllUsrInfo({
          page,
          limit,
          keyword,
          orderBy: 'role',
          isAsc: false,
          abort
        })
    "
    @cell-edit-complete="onEdit"
    selectable>
    <template #operations>
      <Column v-if="isSuperadmin">
        <template #body="{ data }">
          <div class="gap-2 hidden justify-end group-hover:flex">
            <OperationButton
              icon="pi-history"
              label="Manage Sessions"
              severity="help" />
            <OperationButton
              icon="pi-key"
              label="Update Password"
              severity="success"
              @click="
                () => {
                  dialog.target = data;
                  dialog.updPasswd = true;
                }
              " />
          </div>
        </template>
      </Column>
    </template>
  </UsrTbl>

  <!-- Dialogs -->
  <CreateNewUsrsDlg
    v-model:visible="dialog.createNewUsrs"
    @success="refUsrTable?.refresh()" />
  <UpdUsrRolesDlg
    v-model:visible="dialog.updRols"
    :users="usrTbl.selection"
    @success="refUsrTable?.refresh()" />
  <UpdUsrPasswdDlg v-model:visible="dialog.updPasswd" :user="dialog.target" />
</template>
