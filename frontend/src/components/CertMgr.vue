<script setup lang="ts">
import type { CaInfoDTO, CertInfoDTO } from "@api/types";
import {
  deleteCaCert,
  getAllCaInfo,
  toggleCaAvailability
} from "@api/admin/cert/ca";
import { getAllBindedCaInfo } from "@api/user/cert/ca";
import { deleteSslCert, getAllSslCertInfo } from "@api/user/cert/ssl";
import { useUserStore } from "@stores/user";
import { useAsyncGuard } from "@utils/composable";
import { useConfirm } from "primevue/useconfirm";

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

// Properties
const { variant } = defineProps<{ variant: "ca" | "ssl" }>();

/* Services */
const confirm = useConfirm();
const { success, info, error, remove } = useNotify();
const { isActivate, signal } = useAsyncGuard();

/* Stores */
const { isUser, isAdmin, isSuperadmin } = useUserStore();

/* Reactives */
const loading = ref(false);
const searchKeyword = ref("");
const targetCertData = ref<CaInfoDTO | CertInfoDTO>();

const pagination = reactive({
  total: 0,
  first: 0,
  limit: 10,
  data: [] as CaInfoDTO[] | CertInfoDTO[]
});
const dialog = reactive({
  reqNewCert: false,
  uplNewCert: false,
  showInfo: false,
  exportCert: false,
  renewCert: false,
  editComment: false
});

/* Computed */
const getCertInfo = computed(() => {
  if (variant === "ca") {
    if (isUser.value) {
      return getAllBindedCaInfo;
    } else {
      return getAllCaInfo;
    }
  } else {
    return getAllSslCertInfo;
  }
});
const delCertFn = computed(() =>
  variant === "ca" ? deleteCaCert : deleteSslCert
);

/* Actions */
const refresh = async () => {
  loading.value = true;

  try {
    const page = await getCertInfo.value({
      page: pagination.first / pagination.limit + 1,
      limit: pagination.limit,
      keyword:
        searchKeyword.value.length === 0 ? undefined : searchKeyword.value,
      orderBy: "status",
      isAsc: false,
      abort: { signal }
    });

    if (isActivate.value) {
      pagination.total = page.total;
      pagination.data = page.list ?? [];
    }
  } catch (err: unknown) {
    if (isActivate.value) {
      error(
        (err as Error).message,
        variant === "ca"
          ? "Fail to Fetch CA Certificates List"
          : "Fail to Fetch SSL Certificates List"
      );
    }
  }

  loading.value = false;
};

/* Watches */
watch(
  () => [pagination.first, pagination.limit],
  () => refresh()
);
watchDebounced(searchKeyword, () => refresh(), { debounce: 500 });

/* Actions */
const tryToggleCertAvailable = (data: CaInfoDTO) =>
  confirm.require({
    header: "Toggle Certificate Availability",
    message: "Are you sure to toggle the availability?",
    icon: "pi pi-exclamation-triangle",
    modal: true,
    blockScroll: true,
    acceptProps: { severity: "danger" },
    rejectProps: { severity: "secondary" },
    accept: async () => {
      const msg = info("Toggling");

      try {
        await toggleCaAvailability({ uuid: data.uuid });
        success("Successfully toggled");
        refresh();
      } catch (err: unknown) {
        error((err as Error).message, "Fail to Delete");
      }

      remove(msg);
    }
  });

const tryDelCert = (data: CertInfoDTO) =>
  confirm.require({
    header: "Delete Certificate",
    message: "Are you sure to delete the certificate?",
    icon: "pi pi-exclamation-triangle",
    modal: true,
    blockScroll: true,
    acceptProps: { severity: "danger" },
    rejectProps: { severity: "secondary" },
    accept: async () => {
      const msg = info("Deleting");

      try {
        await delCertFn.value({ uuid: data.uuid });
        success("Successfully deleted");
        refresh();
      } catch (err: unknown) {
        error((err as Error).message, "Fail to Delete");
      }

      remove(msg);
    }
  });

/* Hooks */
onBeforeMount(() => refresh());
</script>

<template>
  <Toolbar class="border-none">
    <template #start>
      <div class="flex gap-4">
        <Button
          v-if="variant === 'ssl' || isAdmin || isSuperadmin"
          icon="pi pi-plus"
          label="Request New"
          size="small"
          @click="dialog.reqNewCert = true"></Button>
        <Button
          v-if="variant === 'ca' && (isAdmin || isSuperadmin)"
          icon="pi pi-upload"
          label="Upload New"
          size="small"
          @click="dialog.uplNewCert = true"></Button>
        <Button
          icon="pi pi-refresh"
          label="Refresh"
          severity="info"
          size="small"
          :loading="loading"
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

  <AsyncDataTable
    v-model:first="pagination.first"
    v-model:rows="pagination.limit"
    data-key="uuid"
    size="small"
    :loading="loading"
    :pt="{ pcPaginator: { root: { class: 'rounded-none' } } }"
    :row-class="() => 'group'"
    :row-hover="pagination.data.length > 0"
    :rows-per-page-options="[10, 20, 50]"
    :total-records="pagination.total"
    :value="pagination.data"
    lazy
    paginator>
    <template #header>
      <p class="text-sm">
        Found {{ pagination.total }} {{ variant.toUpperCase() }} certificate(s)
        in total
      </p>
    </template>

    <!-- Comment column -->
    <Column header="Comment" class="w-0">
      <template #body="{ data }">
        <div class="flex gap-2 min-w-md">
          <p
            v-tooltip.bottom="{
              value: data.comment,
              pt: { text: 'text-sm w-fit whitespace-nowrap' }
            }"
            class="overflow-x-hidden text-ellipsis whitespace-nowrap"
            :class="
              variant === 'ssl' || isAdmin || isSuperadmin
                ? 'max-w-[calc(var(--container-md)-32px)]'
                : 'max-w-md'
            ">
            {{ data.comment }}
          </p>
          <Button
            v-if="variant === 'ssl' || isAdmin || isSuperadmin"
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

    <!-- Owner column -->
    <Column
      v-if="variant === 'ca' || isAdmin || isSuperadmin"
      header="Owner"
      class="w-50">
      <template #body="{ data }">
        <p
          v-tooltip.bottom="{
            value: data.owner,
            pt: { text: 'text-sm w-fit whitespace-nowrap' }
          }"
          class="max-w-50 overflow-x-hidden text-ellipsis whitespace-nowrap w-fit">
          {{ data.owner }}
        </p>
      </template>
    </Column>

    <!-- Status column -->
    <Column header="Status">
      <template #body="{ data }">
        <div class="flex gap-2">
          <Badge
            v-if="variant === 'ca' && !data.available"
            severity="danger"
            size="small"
            value="Disabled" />
          <Badge
            v-if="variant === 'ca' && data.parentCa === null"
            severity="info"
            size="small"
            value="Root" />
          <Badge
            v-if="variant === 'ca' && !data.allowSubCa"
            severity="warn"
            size="small"
            value="Leaf" />
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
          <Badge
            v-else
            severity="success"
            size="small"
            value="In Effect"></Badge>
        </div>
      </template>
    </Column>

    <!-- Operations column -->
    <Column>
      <template #body="{ data }">
        <div class="gap-2 hidden justify-end group-hover:flex">
          <OperationButton
            icon="pi-info-circle"
            label="Show Details"
            severity="info"
            @click="
              () => {
                targetCertData = data;
                dialog.showInfo = true;
              }
            " />
          <OperationButton
            icon="pi-file-export"
            label="Export"
            severity="success"
            @click="
              () => {
                targetCertData = data;
                dialog.exportCert = true;
              }
            " />
          <OperationButton
            v-if="variant === 'ssl' || isAdmin || isSuperadmin"
            icon="pi-sync"
            label="Renew"
            severity="help"
            @click="
              () => {
                targetCertData = data;
                dialog.renewCert = true;
              }
            " />
          <OperationButton
            v-if="variant === 'ca' && (isAdmin || isSuperadmin)"
            :icon="data.available ? 'pi-ban' : 'pi-check-circle'"
            :label="data.available ? 'Disable' : 'Enable'"
            :severity="data.available ? 'danger' : 'success'"
            @click="tryToggleCertAvailable(data)" />
          <OperationButton
            v-if="variant === 'ssl' || isAdmin || isSuperadmin"
            icon="pi-trash"
            label="Delete"
            severity="danger"
            @click="tryDelCert(data)" />
        </div>
      </template>
    </Column>
  </AsyncDataTable>

  <!-- Dialogs -->
  <ReqNewCertDlg
    v-model:visible="dialog.reqNewCert"
    :variant="variant"
    @success="refresh" />
  <ImCaDlg v-model:visible="dialog.uplNewCert" @success="refresh" />
  <EditCertCmtDlg
    v-model:visible="dialog.editComment"
    :data="targetCertData"
    :variant="variant"
    @success="refresh" />
  <DispCertInfoDlg
    v-model:visible="dialog.showInfo"
    :data="targetCertData"
    :variant="variant" />
  <ExCertDlg
    v-model:visible="dialog.exportCert"
    :data="targetCertData"
    :variant="variant" />
  <RenewCertDlg
    v-model:visible="dialog.renewCert"
    :data="targetCertData"
    :variant="variant"
    @success="refresh" />
</template>
