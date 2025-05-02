<script setup lang="ts">
import type { CaInfoDTO, CertInfoDTO } from "@api/types";
import { renewCaCert } from "@api/admin/cert/ca";
import { renewSslCert } from "@api/user/cert/ssl";

/* Models */
const visible = defineModel<boolean>("visible");

// Emits
const emits = defineEmits<{ success: [] }>();

// Properties
const { variant, data } = defineProps<{
  variant: "ca" | "ssl";
  data?: CaInfoDTO | CertInfoDTO;
}>();

/* Services */
const { success, info, error, remove } = useNotify();

/* Reactive */
const busy = ref(false);
const expiry = ref(30);

/* Computed */
const renewCertFn = computed(() =>
  variant === "ca" ? renewCaCert : renewSslCert
);

/* Actions */
const onSubmit = async () => {
  // Try to renew
  busy.value = true;
  const msg = info("Requesting");
  try {
    await renewCertFn.value({ uuid: data!.uuid, expiry: expiry.value });

    emits("success");
    visible.value = false;
    success("Successfully renewed");
  } catch (err: unknown) {
    error((err as Error).message, "Fail to Renew");
  }

  remove(msg);
  busy.value = false;
};

/* Watch */
watch(visible, () => {
  if (!visible.value) {
    busy.value = false;
    expiry.value = 30;
  }
});
</script>

<template>
  <Dialog
    v-model:visible="visible"
    :closable="false"
    :header="
      variant === 'ca' ? 'Renew CA Certificate' : 'Renew SSL Certificate'
    "
    modal>
    <form @submit.prevent="onSubmit">
      <section class="flex flex-col gap-1 my-2">
        <label for="expiry" required>New Expiry</label>
        <InputNumber
          v-model="expiry"
          input-id="expiry"
          name="expiry"
          size="small"
          suffix=" day(s)"
          :max="variant === 'ca' ? 7300 : 365"
          :min="1"
          required
          show-buttons />
      </section>
      <div class="flex justify-end gap-2 mt-4">
        <Button
          label="Cancel"
          severity="secondary"
          size="small"
          type="button"
          :disabled="busy"
          @click="visible = false"></Button>
        <Button
          label="Renew"
          size="small"
          type="submit"
          :disabled="busy"
          :loading="busy"></Button>
      </div>
    </form>
  </Dialog>
</template>
