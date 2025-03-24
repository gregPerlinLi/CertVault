<script setup lang="ts">
import type { CaInfoDTO } from "@/api/types";
import { getAllCaInfo } from "@/api/admin/ca";
import { getAllBindedCaInfo } from "@/api/user/cert/ca";
import { useUserStore } from "@/stores/user";

// Stores
const { role } = useUserStore();

// Reactive
const data = ref<CaInfoDTO[]>([]);

const showReqNewCaDia = ref(false);

// Hooks
onBeforeMount(async () => {
  const page = await (role.value === "User"
    ? getAllBindedCaInfo(1, 10)
    : getAllCaInfo(1, 10));

  data.value = page.list ?? [];
});
</script>

<template>
  <Toolbar class="rounded-4xl">
    <template #start>
      <Button
        v-if="['Superadmin', 'Admin'].includes(role)"
        v-tooltip.top="{ value: 'Request New', class: 'text-xs' }"
        icon="pi pi-plus"
        severity="secondary"
        size="small"
        variant="text"
        rounded
        @click="showReqNewCaDia = true"></Button>
    </template>
  </Toolbar>
  <DataTable
    class="mt-4"
    :value="data"
    paginator
    :rows="5"
    :rowsPerPageOptions="[5, 10, 20, 50]"
    size="small"
    tableStyle="min-width: 50rem">
    <Column selectionMode="multiple" class="w-4"></Column>
    <Column field="uuid" header="UUID" class="w-90"></Column>
    <Column field="owner" header="Owner"></Column>
    <Column field="comment" header="Comment" class="w-1/4"></Column>
    <Column header="Allow Sub CA" class="w-36">
      <template #body="{ data }">
        <i v-if="data.allowSubCa" class="pi pi-check text-green-500"></i>
        <i v-else class="pi pi-times text-red-500"></i>
      </template>
    </Column>
    <Column header="Status">
      <template #body="{ data }">
        <Badge
          v-if="!data.available"
          class="mr-2"
          severity="danger"
          size="small"
          value="Disabled"></Badge>
        <Badge
          v-if="new Date(data.notBefore).getTime() > Date.now()"
          severity="warn"
          size="small"
          value="Not In Effect"></Badge>
        <Badge
          v-else-if="new Date(data.notAfter).getTime() < Date.now()"
          severity="warn"
          size="small"
          value="Expired"></Badge>
        <Badge v-else severity="success" size="small" value="In Effect"></Badge>
      </template>
    </Column>
    <Column>
      <template #body> </template>
    </Column>
  </DataTable>

  <!-- Dialog -->
  <RequestNewCa v-model:visible="showReqNewCaDia" />
</template>
