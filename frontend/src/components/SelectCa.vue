<script setup lang="ts">
import type { CaInfoDTO } from "@api/types";
import { getAllCaInfo } from "@api/admin/cert/ca";
import { getAllBindedCaInfo } from "@api/user/cert/ca";

/* Models */
const selection = defineModel<CaInfoDTO | null>("selection");

// Properties
const props = defineProps<{
  invalid?: boolean;
  optionLabel?: string;
  showClear?: boolean;
  variant?: "binding";
  visible: boolean;
}>();

// Emits
defineEmits<{ focus: [] }>();

/* Services */
const { error } = useNotify();
const { isActive, getSignal, reset, cancel } = useAsyncGuard();

/* Reactives */
const caList = reactive({
  first: 0,
  total: 0,
  search: "",
  data: [] as CaInfoDTO[],
  loading: false
});

/* Computed */
const fetchFn = computed(() =>
  props.variant === "binding" ? getAllCaInfo : getAllBindedCaInfo
);

/* Actions */
const refresh = async () => {
  caList.loading = true;
  try {
    selection.value = null;

    const data = await fetchFn.value({
      page: Math.floor(caList.first / 10) + 1,
      limit: 10,
      keyword: caList.search,
      orderBy: "status",
      isAsc: false,
      abort: { signal: getSignal() }
    });

    if (isActive.value) {
      caList.total = data.total;
      caList.data = data.list ?? [];
    }
  } catch (err: unknown) {
    if (isActive.value) {
      error((err as Error).message, "Fail to Fetch CA List");
    }
  }
  caList.loading = false;
};

/* Watches */
watch(
  () => props.visible,
  (newValue) => {
    if (newValue) {
      reset();
      refresh();
    } else {
      cancel();
      selection.value = null;
      caList.first = 0;
      caList.total = 0;
      caList.search = "";
      caList.data = [];
      caList.loading = false;
    }
  }
);
watch(
  () => caList.first,
  () => refresh()
);
watchDebounced(
  () => caList.search,
  () => refresh(),
  { debounce: 500, maxWait: 1000 }
);

/* Hooks */
onBeforeMount(() => refresh());
</script>

<template>
  <Select
    v-model="selection"
    placeholder="Select a CA"
    size="small"
    :invalid="invalid"
    :option-label="optionLabel"
    :options="caList.data"
    :pt="{
      virtualScroller: {
        root: { class: 'overflow-x-hidden! [&_.p-select-list]:w-full' }
      }
    }"
    :show-clear="showClear"
    :virtual-scroller-options="{
      itemSize: 40,
      lazy: true,
      loading: caList.loading,
      showLoader: true
    }"
    @focus="$emit('focus')"
    checkmark>
    <template #header>
      <div class="px-4 py-2">
        <IconField>
          <InputIcon class="pi pi-search" />
          <InputText
            v-model.trim="caList.search"
            class="w-full"
            placeholder="Search"
            size="small" />
        </IconField>
      </div>
    </template>
    <template #option="{ option }">
      <p
        v-tooltip.left="{
          value: option.comment,
          class: 'text-sm -translate-x-8'
        }"
        class="overflow-hidden text-ellipsis w-full">
        {{ option.comment }}
      </p>
    </template>
    <template #footer>
      <Paginator
        v-model:first="caList.first"
        current-page-report-template="{currentPage}/{totalPages}"
        template="FirstPageLink PrevPageLink CurrentPageReport NextPageLink LastPageLink"
        :rows="10"
        :total-records="caList.total" />
    </template>
  </Select>
</template>
