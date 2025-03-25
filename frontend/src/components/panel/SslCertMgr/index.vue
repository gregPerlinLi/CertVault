<script setup lang="ts">
import type { CertInfoDTO } from "@/api/types";
import { deleteSslCert, getAllSslCertInfo } from "@/api/user/cert/ssl";
import { useUserStore } from "@/stores/user";
import { nanoid } from "nanoid";
import { useConfirm, useToast } from "primevue";

// Stores
const { role } = useUserStore();

// Reactives
const confirm = useConfirm();
const toast = useToast();

const loading = ref(false);
const searchKeyword = ref("");
const selection = ref();

const pagination = reactive({
  total: 0,
  first: 0,
  limit: 10,
  data: [] as CertInfoDTO[]
});
const dialog = reactive({
  reqNewCert: false,
  showInfo: false,
  exportCert: false,
  renewCert: false,
  editComment: false
});

// Non-reactives
let currentRefreshTag = "";

// Actions
const refresh = async () => {
  currentRefreshTag = nanoid();
  const tag = currentRefreshTag;

  try {
    loading.value = true;
    const page = await getAllSslCertInfo(
      pagination.first / pagination.limit + 1,
      pagination.limit,
      searchKeyword.value.length === 0 ? undefined : searchKeyword.value
    );
    pagination.total = page.total;
    pagination.data = page.list ?? [];
  } catch (err: unknown) {
    if (tag === currentRefreshTag) {
      toast.add({
        severity: "error",
        summary: "Fail to Fetch Data",
        detail: (err as Error).message,
        life: 5000
      });
    }
  } finally {
    if (tag === currentRefreshTag) {
      loading.value = false;
    }
  }
};

// Watches
watch(
  () => [pagination.first, pagination.limit],
  () => refresh()
);
watchDebounced(searchKeyword, () => refresh(), { debounce: 500 });

// Actions
const tryDelCert = (data: CertInfoDTO) => {
  confirm.require({
    header: "Delete Certificate",
    message: "Are you sure to delete the certificate?",
    icon: "pi pi-exclamation-triangle",
    modal: true,
    blockScroll: true,
    acceptProps: { severity: "danger" },
    rejectProps: { severity: "secondary" },
    accept: async () => {
      try {
        toast.add({
          severity: "info",
          summary: "Info",
          detail: "Deleting SSL certificate",
          life: 3000
        });
        await deleteSslCert(data.uuid);
        toast.add({
          severity: "success",
          summary: "Success",
          detail: "Successfully deleted SSL certificate",
          life: 3000
        });
      } catch (err: unknown) {
        toast.add({
          severity: "error",
          summary: "Fail to Delete SSL certificate",
          detail: (err as Error).message,
          life: 5000
        });
      } finally {
        await refresh();
      }
    }
  });
};

// Hooks
onBeforeMount(() => refresh());
</script>

<template>
  <Toolbar class="border-none">
    <template #start>
      <div class="flex gap-4">
        <Button
          icon="pi pi-plus"
          label="Request New"
          size="small"
          @click="dialog.reqNewCert = true"></Button>
        <Button
          icon="pi pi-refresh"
          label="Refresh"
          severity="info"
          size="small"
          @click="refresh"></Button>
      </div>
    </template>
    <template #end>
      <IconField>
        <InputIcon class="pi pi-search" />
        <InputText
          v-model.trim="searchKeyword"
          name="search-keyword"
          placeholder="Search"
          size="small" />
      </IconField>
    </template>
  </Toolbar>
  <DataTable
    v-model:first="pagination.first"
    v-model:rows="pagination.limit"
    v-model:selection="selection"
    data-key="uuid"
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
      <p class="text-sm">
        Found {{ pagination.total }} SSL certificate(s) in total
      </p>
    </template>
    <Column selectionMode="multiple" class="w-4"></Column>
    <Column header="Comment" class="w-0">
      <template #body="{ data }">
        <p
          v-tooltip.right="{ value: data.comment, class: 'text-sm' }"
          class="overflow-x-hidden text-ellipsis whitespace-nowrap max-w-md">
          {{ data.comment }}
        </p>
      </template>
    </Column>
    <Column
      v-if="role === 'Admin' || role === 'Superadmin'"
      field="owner"
      header="Owner"
      class="w-50"></Column>
    <Column header="Status">
      <template #body="{ data }">
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
            @click="dialog.showInfo = true"></Button>
          <Button
            v-tooltip.top="{ value: 'Export', class: 'text-sm' }"
            aria-label="Export certificate"
            class="h-6 w-6"
            icon="pi pi-file-export"
            severity="success"
            size="small"
            variant="text"
            rounded
            @click="dialog.exportCert = true"></Button>
          <Button
            v-tooltip.top="{ value: 'Renew', class: 'text-sm' }"
            aria-label="Renew certificate"
            class="h-6 w-6"
            icon="pi pi-sync"
            severity="secondary"
            size="small"
            variant="text"
            rounded
            @click="dialog.renewCert = true"></Button>
          <Button
            v-tooltip.top="{ value: 'Edit', class: 'text-sm' }"
            aria-label="Edit certificate information"
            class="h-6 w-6"
            icon="pi pi-pen-to-square"
            severity="help"
            size="small"
            variant="text"
            rounded
            @click="dialog.editComment = true"></Button>
          <Button
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
    </Column>
  </DataTable>
  <ReqNewSslCertDlg v-model:visible="dialog.reqNewCert" @success="refresh" />
</template>
