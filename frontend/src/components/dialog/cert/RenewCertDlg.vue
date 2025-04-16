<script setup lang="ts">
import type { CaInfoDTO, CertInfoDTO } from "@/api/types";
import { renewCaCert } from "@/api/admin/cert/ca";
import { renewSslCert } from "@/api/user/cert/ssl";
import { useNotify } from "@/utils/composable";

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
const { toast, info, success, error } = useNotify();

/* Reactive */
const busy = ref(false);

/* Computed */
const renewCertFn = computed(() =>
  variant === "ca" ? renewCaCert : renewSslCert
);

/* Actions */
const onSubmit = async (ev: Event) => {
  // Parse form data
  const formData = new FormData(ev.target as HTMLFormElement);
  const expiry = parseInt(formData.get("expiry")!.toString().trim());

  // Try to renew
  busy.value = true;
  const msg = info("Info", "Requesting");
  try {
    await renewCertFn.value(data!.uuid, expiry);

    success("Success", "Successfully renewed");
    emits("success");
    visible.value = false;
  } catch (err: unknown) {
    error("Fail to Renew", (err as Error).message);
  }

  toast.remove(msg);
  busy.value = false;
};

/* Watch */
watch(visible, () => {
  if (!visible.value) {
    busy.value = false;
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
          input-id="expiry"
          name="expiry"
          size="small"
          suffix=" day(s)"
          :default-value="30"
          :max="365"
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
