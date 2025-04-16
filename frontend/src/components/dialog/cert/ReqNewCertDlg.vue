<script setup lang="ts">
import type { CaInfoDTO } from "@/api/types";
import type { VirtualScrollerLazyEvent } from "primevue/virtualscroller";
import { getAllCaInfo, requestCaCert } from "@/api/admin/cert/ca";
import { getAllBindedCaInfo } from "@/api/user/cert/ca";
import { requestSslCert } from "@/api/user/cert/ssl";
import { useUserStore } from "@/stores/user";
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

/* Stores */
const { isUser } = useUserStore();

/* Reactives */
const busy = ref(false);

const caUuid = ref<string | null>(null);
const allowSubCa = ref(true);

const caList = ref<CaInfoDTO[]>([]);
const loadingCaList = ref(false);

/* Computed */
const reqNewCertFn = computed(() =>
  variant === "ca" ? requestCaCert : requestSslCert
);

/* Actions */
const onLazyLoadCaList = async (ev: VirtualScrollerLazyEvent) => {
  loadingCaList.value = true;

  try {
    const page = isUser.value
      ? await getAllBindedCaInfo(Math.trunc(ev.first / 30) + 1, 30, undefined, {
          signal: getSignal()
        })
      : await getAllCaInfo(Math.trunc(ev.first / 30) + 1, 30, undefined, {
          signal: getSignal()
        });

    if (isActivate.value && page.list !== null) {
      const tmp = caList.value.slice();
      for (let i = ev.first; i < ev.first + page.list.length; i++) {
        tmp[i] = page.list[i - ev.first];
      }

      caList.value = tmp
        .filter(({ available }) => available)
        .filter(({ allowSubCa }) =>
          variant === "ca" ? allowSubCa === true : true
        );
    }
  } catch (err: unknown) {
    if (isActivate.value) {
      error("Fail to Get Binded CA List", (err as Error).message);
    }
  }

  loadingCaList.value = false;
};

const onSubmit = async (ev: Event) => {
  // Validate form
  const result = validate(ev.target as HTMLFormElement);
  if (!result.success) {
    error("Validation Error", result.issues![0].message);
    return;
  }

  if (variant === "ssl" && caUuid.value === null) {
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
      caUuid: caUuid.value,
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
  } else {
    cancel();
    busy.value = false;
    caUuid.value = null;
    allowSubCa.value = true;
    caList.value = [];
    loadingCaList.value = false;
  }
});
watch(caUuid, () => {
  allowSubCa.value = caUuid.value === null;
});
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
            v-model="caUuid"
            option-label="uuid"
            option-value="uuid"
            placeholder="Select a CA"
            size="small"
            :invalid="isInvalid('ca-uuid')"
            :options="caList"
            :show-clear="variant === 'ca'"
            :virtual-scroller-options="{
              delay: 250,
              itemSize: 30,
              lazy: true,
              loading: loadingCaList,
              onLazyLoad: onLazyLoadCaList,
              showLoader: true
            }"
            @focus="clearInvalid('ca-uuid')"
            checkmark>
            <template #option="slotProps">
              <p
                v-tooltip.left="{
                  value: slotProps.option.uuid,
                  class: 'text-sm -translate-x-3'
                }"
                class="w-full">
                {{ slotProps.option.comment }}
              </p>
            </template>
          </Select>
        </section>
        <section v-if="variant === 'ca'" class="w-1/4">
          <label>Allow Sub CA</label>
          <ToggleButton
            v-model="allowSubCa"
            size="small"
            :disabled="caUuid === null" />
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
