<script setup lang="ts">
import type { DataTableCellEditCompleteEvent } from "primevue/datatable";
import { getAllUsrInfo } from "@api/admin/user";
import { useUserStore } from "@stores/user";
import { useNotify } from "@utils/composable";

/* Services */
const { success } = useNotify();

const refUsrTable = useTemplateRef("user-table");

/* Stores */
const { isSuperadmin } = useUserStore();

/* Actions */
const onEdit = ({ newValue, value }: DataTableCellEditCompleteEvent) => {
  console.log(newValue, value);
  if (newValue === value) {
    return;
  }
  success("Edited", `"${value}" -> "${newValue}"`);
};

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

  <!-- Main -->
  <UsrTable
    ref="user-table"
    :editable="isSuperadmin"
    :refresh-fn="
      (page, limit, keyword, abort) =>
        getAllUsrInfo({ page, limit, keyword, abort })
    "
    @cell-edit-complete="onEdit">
    <template #operations>
      <Column v-if="isSuperadmin">
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
    </template>
  </UsrTable>
</template>
