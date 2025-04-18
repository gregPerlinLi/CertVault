<script setup lang="ts">
import type { CaInfoDTO } from "@/api/types";
import { requestCaCert } from "@/api/admin/cert/ca";
import { getAllBindedCaInfo } from "@/api/user/cert/ca";
import { requestSslCert } from "@/api/user/cert/ssl";
import {
  useFormValidator,
  useNotify,
  useReloadableAsyncGuard
} from "@/utils/composable";
import { reqNewCertSchema } from "@/utils/schema";

/* Models */
const visible = defineModel<boolean>("visible");

// Properties
const { variant } = defineProps<{ variant: "ca" | "ssl" }>();

// Emits
const emits = defineEmits<{ success: [] }>();

/* Services */
const { toast, info, success, error } = useNotify();
const { isActivate, reload, cancel, getSignal } = useReloadableAsyncGuard();
const { isInvalid, setInvalid, clearInvalid, validate } =
  useFormValidator(reqNewCertSchema);

/* Reactives */
const busy = ref(false);

const allowSubCa = ref(true);

const caList = reactive({
  first: 0,
  total: 0,
  search: "",
  data: [] as CaInfoDTO[],
  selection: null as string | null,
  loading: false
});

/* Computed */
const reqNewCertFn = computed(() =>
  variant === "ca" ? requestCaCert : requestSslCert
);

/* Actions */
const refreshCaList = async () => {
  caList.loading = true;
  caList.selection = null;
  try {
    const data = await getAllBindedCaInfo(
      Math.floor(caList.first / 10) + 1,
      10,
      caList.search,
      { signal: getSignal() }
    );

    if (isActivate.value) {
      caList.total = data.total;
      caList.data = data.list ?? [];
    }
  } catch (err: unknown) {
    if (isActivate.value) {
      error("Fail to Fetch CA List", (err as Error).message);
    }
  }
  caList.loading = false;
};
const onSubmit = async (ev: Event) => {
  // Validate form
  const result = validate(ev.target as HTMLFormElement);
  if (!result.success) {
    error("Validation Error", result.issues![0].message);
    return;
  }

  if (variant === "ssl" && caList.selection === null) {
    setInvalid("ca-uuid");
    error("Validation Error", "CA is required");
    return;
  }

  // Try to require
  busy.value = true;
  const msg = info("Info", "Requesting");

  try {
    await reqNewCertFn.value({
      ...result.output,
      caUuid: caList.selection,
      allowSubCa: allowSubCa.value
    });

    success("Success", "Successfully requested");
    emits("success");
    visible.value = false;
  } catch (err: unknown) {
    error("Fail to Request", (err as Error).message);
  }

  toast.remove(msg);
  busy.value = false;
};

/* Watches */
watch(visible, () => {
  if (visible.value) {
    reload();
    refreshCaList();
  } else {
    cancel();
    busy.value = false;
    allowSubCa.value = true;
    caList.first = 0;
    caList.total = 0;
    caList.search = "";
    caList.data = [];
    caList.selection = null;
    caList.loading = false;
  }
});
watch(
  () => caList.first,
  () => refreshCaList()
);
watchDebounced(
  () => caList.search,
  () => refreshCaList(),
  { debounce: 500, maxWait: 1000 }
);
watch(
  () => caList.selection,
  () => {
    allowSubCa.value = caList.selection === null;
  }
);
</script>

<template>
  <Dialog
    v-model:visible="visible"
    :closable="false"
    :header="
      variant === 'ca'
        ? 'Request New CA Certificate'
        : 'Request New SSL Certificate'
    "
    modal>
    <form @submit.prevent="onSubmit">
      <div class="flex gap-8">
        <section :class="variant === 'ca' ? 'w-3/4' : 'w-full'">
          <label :required="variant === 'ssl'">CA for Issuance</label>
          <Select
            v-model="caList.selection"
            option-label="uuid"
            option-value="uuid"
            placeholder="Select a CA"
            size="small"
            :invalid="isInvalid('ca-uuid')"
            :options="caList.data"
            :pt="{
              virtualScroller: {
                root: { class: 'overflow-hidden! [&_.p-select-list]:w-full' }
              }
            }"
            :show-clear="variant === 'ca'"
            :virtual-scroller-options="{
              itemSize: 30,
              lazy: true,
              loading: caList.loading,
              showLoader: true
            }"
            @focus="clearInvalid('ca-uuid')"
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
        </section>
        <section v-if="variant === 'ca'" class="w-1/4">
          <label>Allow Sub CA</label>
          <ToggleButton
            v-model="allowSubCa"
            size="small"
            :disabled="caList.selection === null" />
        </section>
      </div>

      <div class="flex gap-8">
        <section class="w-1/3">
          <label for="country" required>Country</label>
          <InputText
            id="country"
            name="country"
            placeholder="e.g. CN"
            size="small"
            :invalid="isInvalid('country')"
            @focus="clearInvalid('country')"
            required />
        </section>
        <section class="w-1/3">
          <label for="province" required>Province</label>
          <InputText
            id="province"
            name="province"
            placeholder="e.g. Guangdong"
            size="small"
            :invalid="isInvalid('province')"
            @focus="clearInvalid('province')"
            required />
        </section>
        <section class="w-1/3">
          <label for="city" required>City</label>
          <InputText
            id="city"
            name="city"
            placeholder="e.g. Guangzhou"
            size="small"
            :invalid="isInvalid('city')"
            @focus="clearInvalid('city')"
            required />
        </section>
      </div>

      <div class="flex gap-8">
        <section class="w-1/2">
          <label for="organization" required>Organization</label>
          <InputText
            id="organization"
            name="organization"
            size="small"
            :invalid="isInvalid('organization')"
            @focus="clearInvalid('organization')"
            required />
        </section>
        <section class="w-1/2">
          <label for="organizational-unit" required>Organizational Unit</label>
          <InputText
            id="organizational-unit"
            name="organizational-unit"
            size="small"
            :invalid="isInvalid('organizational-unit')"
            @focus="clearInvalid('organizational-unit')"
            required />
        </section>
      </div>

      <div class="flex gap-8">
        <section class="w-2/3">
          <label for="common-name" required>Common Name</label>
          <InputText
            id="common-name"
            name="common-name"
            size="small"
            :invalid="isInvalid('common-name')"
            @focus="clearInvalid('common-name')"
            required />
        </section>
        <section class="w-1/3">
          <label for="expiry" required>Expiry</label>
          <InputNumber
            input-id="expiry"
            name="expiry"
            size="small"
            suffix=" day(s)"
            :default-value="30"
            :invalid="isInvalid('expiry')"
            :max="365"
            :min="1"
            @focus="clearInvalid('expiry')"
            required
            show-buttons />
        </section>
      </div>

      <section>
        <label for="subject-alt-names">Subject Alternative Names</label>
        <Textarea
          class="min-h-24"
          id="subject-alt-names"
          name="subject-alt-names"
          :placeholder="'Each line is a key-value pair seperated by a \'=\'\ne.g. \'IP_ADDRESS=1.1.1.1\''"
          :invalid="isInvalid('subject-alt-names')"
          @focus="clearInvalid('subject-alt-names')"
          size="small"></Textarea>
      </section>

      <section>
        <label for="comment" required>Certificate Comment</label>
        <InputText
          id="comment"
          name="comment"
          size="small"
          :invalid="isInvalid('comment')"
          @focus="clearInvalid('comment')"
          required />
      </section>

      <!-- Buttons -->
      <div class="flex justify-end gap-2 mt-4">
        <Button
          label="Cancel"
          severity="secondary"
          size="small"
          type="button"
          :disabled="busy"
          @click="visible = false"></Button>
        <Button
          label="Request"
          size="small"
          type="submit"
          :disabled="busy"
          :loading="busy"></Button>
      </div>
    </form>
  </Dialog>
</template>

<style scoped>
@import "tailwindcss";

section {
  @apply flex flex-col gap-1 my-2;
}
</style>
