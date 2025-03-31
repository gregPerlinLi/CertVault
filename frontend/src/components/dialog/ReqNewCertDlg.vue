<script setup lang="ts">
import type { CaInfoDTO } from "@/api/types";
import type { VirtualScrollerLazyEvent } from "primevue";
import { getAllCaInfo, requestCaCert } from "@/api/admin/ca";
import { getAllBindedCaInfo } from "@/api/user/cert/ca";
import { requestSslCert } from "@/api/user/cert/ssl";
import { useUserStore } from "@/stores/user";
import { useNotify } from "@/utils/composable";
import { reqNewCertSchema } from "@/utils/schema";
import { validateForm } from "@/utils/validate";
import { nanoid } from "nanoid";

// Models
const visible = defineModel<boolean>("visible");

// Properties
const { variant } = defineProps<{ variant: "ca" | "ssl" }>();

// Emits
const emits = defineEmits<{ success: [] }>();

// Stores
const { role } = useUserStore();

// Services
const { info, success, error } = useNotify();

// Reactives
const busy = ref(false);
const errField = ref<string>();

const caUuid = ref<string | null>(null);
const allowSubCa = ref(true);

const caList = ref<CaInfoDTO[]>([]);
const loadingCaList = ref(false);

// Computed
const reqNewCertFn = computed(() =>
  variant === "ca" ? requestCaCert : requestSslCert
);

// Non-reactive
let nonce = nanoid();

// Actions
const clearErr = (field: string) => {
  if (errField.value === field) {
    errField.value = undefined;
  }
};

const onLazyLoadCaList = async (ev: VirtualScrollerLazyEvent) => {
  nonce = nanoid();
  const tag = nonce;

  try {
    loadingCaList.value = true;

    const page =
      role.value === "User"
        ? await getAllBindedCaInfo(Math.trunc(ev.first / 30) + 1, 30)
        : await getAllCaInfo(Math.trunc(ev.first / 30) + 1, 30);
    if (tag === nonce && page.list !== null) {
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
    if (tag === nonce && visible) {
      error("Fail to Get Binded CA List", (err as Error).message);
    }
  } finally {
    if (tag === nonce) {
      loadingCaList.value = false;
    }
  }
};

const onSubmit = async (ev: Event) => {
  errField.value = undefined;
  const {
    success: validateOk,
    data,
    error: validateErr
  } = validateForm(ev.target as HTMLFormElement, reqNewCertSchema);
  if (!validateOk) {
    error("Validation Error", validateErr.issues[0].message);
    errField.value = validateErr.issues[0].path[0] as string;
    return;
  }

  try {
    busy.value = true;
    info("Info", "Requesting");

    await reqNewCertFn.value({
      ...data,
      caUuid: caUuid.value,
      allowSubCa: allowSubCa.value
    });

    success("Success", "Successfully requested");
    emits("success");
    visible.value = false;
  } catch (err: unknown) {
    error("Fail to Request", (err as Error).message);
  } finally {
    busy.value = false;
  }
};

// Watches
watch(visible, (v) => {
  if (v) {
    busy.value = false;
    errField.value = undefined;
    caUuid.value = null;
    allowSubCa.value = true;
    caList.value = [];
    loadingCaList.value = false;
    nonce = nanoid();
  }
});
watch(caUuid, (v) => {
  allowSubCa.value = v === null;
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
            :invalid="errField === 'ca-uuid'"
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
            :invalid="errField === 'country'"
            @focus="clearErr('country')"
            required />
        </section>
        <section class="w-1/3">
          <label for="province" required>Province</label>
          <InputText
            id="province"
            name="province"
            placeholder="e.g. Guangdong"
            size="small"
            :invalid="errField === 'province'"
            @focus="clearErr('province')"
            required />
        </section>
        <section class="w-1/3">
          <label for="city" required>City</label>
          <InputText
            id="city"
            name="city"
            placeholder="e.g. Guangzhou"
            size="small"
            :invalid="errField === 'city'"
            @focus="clearErr('city')"
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
            :invalid="errField === 'organization'"
            @focus="clearErr('organization')"
            required />
        </section>
        <section class="w-1/2">
          <label for="organizational-unit" required>Organizational Unit</label>
          <InputText
            id="organizational-unit"
            name="organizational-unit"
            size="small"
            :invalid="errField === 'organizational-unit'"
            @focus="clearErr('organizational-unit')"
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
            :invalid="errField === 'common-name'"
            @focus="clearErr('common-name')"
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
            :invalid="errField === 'expiry'"
            :max="365"
            :min="1"
            @focus="clearErr('expiry')"
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
          :invalid="errField === 'subject-alt-names'"
          @focus="clearErr('subject-alt-names')"
          size="small"></Textarea>
      </section>

      <section>
        <label for="comment" required>Certificate Comment</label>
        <InputText
          id="comment"
          name="comment"
          size="small"
          :invalid="errField === 'comment'"
          @focus="clearErr('comment')"
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
