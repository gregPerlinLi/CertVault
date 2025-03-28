<script setup lang="ts">
import type { CertInfoDTO } from "@/api/types";
import { deleteSslCert, getAllSslCertInfo } from "@/api/user/cert/ssl";
import { useUserStore } from "@/stores/user";
import { useNotify } from "@/utils/composable";
import { nanoid } from "nanoid";
import { useConfirm } from "primevue/useconfirm";

// Stores
const { role } = useUserStore();

// Services
const confirm = useConfirm();
const { info, success, error } = useNotify();

// Reactives
const loading = ref(false);
const searchKeyword = ref("");
const targetCertData = ref<CertInfoDTO>();

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
let nonce = nanoid();

// Actions
const refresh = async () => {
  nonce = nanoid();
  const tag = nonce;

  try {
    loading.value = true;

    const page = await getAllSslCertInfo(
      pagination.first / pagination.limit + 1,
      pagination.limit,
      searchKeyword.value.length === 0 ? undefined : searchKeyword.value
    );

    if (tag === nonce) {
      pagination.total = page.total;
      pagination.data = page.list ?? [];
    }
  } catch (err: unknown) {
    if (tag === nonce) {
      error("Fail to Fetch SSL Certificate Info", (err as Error).message);
    }
  } finally {
    if (tag === nonce) {
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
        info("Info", "Deleting SSL certificate");
        await deleteSslCert(data.uuid);
        success("Success", "Successfully deleted SSL certificate");
      } catch (err: unknown) {
        error("Fail to Delete SSL certificate", (err as Error).message);
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
    <Column header="Comment" class="w-0">
      <template #body="{ data }">
        <div class="flex gap-2">
          <p
            v-tooltip.bottom="{ value: data.comment, class: 'text-sm' }"
            class="max-w-md overflow-x-hidden text-ellipsis whitespace-nowrap">
            {{ data.comment }}
          </p>
          <Button
            v-tooltip.top="{ value: 'Edit', class: 'text-sm' }"
            aria-label="Edit certificate information"
            class="h-6 opacity-0 w-6 group-hover:opacity-100"
            icon="pi pi-pen-to-square"
            severity="help"
            size="small"
            variant="text"
            rounded
            @click="
              () => {
                targetCertData = data;
                dialog.editComment = true;
              }
            "></Button>
        </div>
      </template>
    </Column>
    <Column
      v-if="role === 'Admin' || role === 'Superadmin'"
      header="Owner"
      class="w-50">
      <template #body="{ data }">
        <p
          v-tooltip.bottom="{ value: data.owner, class: 'text-sm' }"
          class="max-w-50 overflow-x-hidden text-ellipsis whitespace-nowrap w-fit">
          {{ data.owner }}
        </p>
      </template>
    </Column>
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

  <!-- Dialogs -->
  <ReqNewSslCertDlg v-model:visible="dialog.reqNewCert" @success="refresh" />
  <SslCertInfoDlg v-model:visible="dialog.showInfo" :data="targetCertData" />
  <ExportCertDlg v-model:visible="dialog.exportCert" :data="targetCertData" />
  <RenewSslCertDlg
    v-model:visible="dialog.renewCert"
    :data="targetCertData"
    @success="refresh" />
  <EditSslCertDlg
    v-model:visible="dialog.editComment"
    :data="targetCertData"
    @success="refresh" />
</template>
