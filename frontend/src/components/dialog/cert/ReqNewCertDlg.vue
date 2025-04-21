<script setup lang="ts">
import type { CaInfoDTO } from "@/api/types";
import { requestCaCert } from "@/api/admin/cert/ca";
import { requestSslCert } from "@/api/user/cert/ssl";
import { useFormValidator, useNotify } from "@/utils/composable";
import { reqNewCertSchema } from "@/utils/schema";

/* Models */
const visible = defineModel<boolean>("visible");

// Properties
const { variant } = defineProps<{ variant: "ca" | "ssl" }>();

// Emits
const emits = defineEmits<{ success: [] }>();

/* Services */
const { toast, info, success, warn, error } = useNotify();
const { isInvalid, setInvalid, clearInvalid, validate } =
  useFormValidator(reqNewCertSchema);

/* Reactives */
const busy = ref(false);

const caSelection = ref<CaInfoDTO | null>(null);
const allowSubCa = ref(true);
const algorithm = ref<string | null>(null);
const keySize = ref<number | null>(null);

/* Computed */
const reqNewCertFn = computed(() =>
  variant === "ca" ? requestCaCert : requestSslCert
);
const isKeySizeRequired = computed(() =>
  ["RSA", "EC"].includes(caSelection.value?.algorithm ?? "")
);
const keySizeOptions = computed(() => {
  switch (caSelection.value?.algorithm || algorithm.value) {
    case "RSA":
      return [2048, 3072, 4096, 5120];
    case "EC":
      return [256, 384, 521];
    case "Ed25519":
      return [256];
    default:
      return [];
  }
});

/* Actions */
const onSubmit = async (ev: Event) => {
  // Validate form
  const result = validate(ev.target as HTMLFormElement);
  if (!result.success) {
    error("Validation Error", result.issues![0].message);
    return;
  }
  if (variant === "ssl" && caSelection.value === null) {
    setInvalid("ca-uuid");
    error("Validation Error", "CA is required");
    return;
  }

  // Try to require
  busy.value = true;
  const msg = info("Info", "Requesting");

  try {
    await reqNewCertFn.value(
      {
        ...result.output,
        caUuid: caSelection.value?.uuid,
        allowSubCa: allowSubCa.value,
        algorithm: algorithm.value,
        keySize: keySize.value
      },
      { timeout: -1 }
    );

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
watch(visible, (newValue) => {
  if (!newValue) {
    busy.value = false;
    allowSubCa.value = true;
    algorithm.value = null;
    keySize.value = null;
  }
});
watch(caSelection, (newValue) => {
  if (newValue !== null && !newValue.allowSubCa) {
    queueMicrotask(() => {
      caSelection.value = null;
      warn("Warning", `"${newValue.comment}" is not allowed to have sub-CA`);
    });
    return;
  }

  allowSubCa.value = newValue === null;
  algorithm.value = null;
  switch (newValue?.algorithm) {
    case "RSA":
      keySize.value = 2048;
      break;
    case "EC":
    case "Ed25519":
      keySize.value = 256;
      break;
  }
});
watch(algorithm, (newValue) => {
  switch (newValue) {
    case "RSA":
      keySize.value = 2048;
      break;
    case "EC":
    case "Ed25519":
      keySize.value = 256;
      break;
  }
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
          <SelectCa
            v-model:selection="caSelection"
            option-label="uuid"
            :invalid="isInvalid('ca-uuid')"
            :show-clear="variant === 'ca'"
            :visible="visible ?? false"
            @focus="clearInvalid('ca-uuid')" />
        </section>
        <section v-if="variant === 'ca'" class="w-1/4">
          <label>Allow Sub CA</label>
          <ToggleButton
            v-model="allowSubCa"
            size="small"
            :disabled="caSelection === null" />
        </section>
      </div>

      <div class="flex gap-8">
        <section class="w-1/2">
          <label :required="variant === 'ca'">Algorithm</label>
          <InputText
            v-if="variant === 'ssl' || caSelection !== null"
            size="small"
            :model-value="caSelection?.algorithm"
            disabled />
          <Select
            v-else
            v-model:model-value="algorithm"
            size="small"
            :options="['RSA', 'EC', 'Ed25519']"></Select>
        </section>
        <section class="w-1/2">
          <label :required="isKeySizeRequired">Key Size</label>
          <Select
            v-model:model-value="keySize"
            size="small"
            :disabled="
              caSelection?.algorithm === undefined && algorithm === null
            "
            :options="keySizeOptions"></Select>
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
