<script setup lang="ts">
import type { CaInfoDTO, CertInfoDTO } from "@/api/types";
import { getCaPrivKey } from "@/api/admin/ca";
import { getCaCert } from "@/api/user/cert/ca";
import { getSslCert, getSslPrivKey } from "@/api/user/cert/ssl";
import { b64ToU8Arr, saveFile } from "@/utils";
import { useNotify, useRole } from "@/utils/composable";

// Models
const visible = defineModel<boolean>("visible");

// Properties
const { variant, data } = defineProps<{
  variant: "ca" | "ssl";
  data?: CaInfoDTO | CertInfoDTO;
}>();

// Services
const { info, success, error } = useNotify();
const { aboveUser } = useRole();

// Reactive
const busy = reactive({
  exportCert: false,
  exportChain: false,
  exportPrivKey: false
});

// Computed
const canClose = computed(
  () => !busy.exportCert && !busy.exportChain && !busy.exportPrivKey
);
const getCertFn = computed(() => (variant === "ca" ? getCaCert : getSslCert));
const getPrivKeyFn = computed(() =>
  variant === "ca" ? getCaPrivKey : getSslPrivKey
);

// Actions
const exportCert = async (fullchain: boolean = false) => {
  try {
    if (fullchain) {
      busy.exportChain = true;
      info("Info", "Exporting fullchain");
    } else {
      busy.exportCert = true;
      info("Info", "Exporting");
    }

    const cert = await getCertFn.value(data!.uuid, fullchain);
    const pem = new TextDecoder().decode(b64ToU8Arr(cert));
    const file = new Blob([pem], { type: "application/x-pem-file" });
    saveFile(`${data!.uuid}.pem`, file);

    success(
      "Success",
      fullchain ? "Successfully exported fullchain" : "Successfully exported"
    );
  } catch (err: unknown) {
    error("Fail to Export SSL Certificate", (err as Error).message);
  } finally {
    if (fullchain) {
      busy.exportChain = false;
    } else {
      busy.exportCert = false;
    }
  }
};
const onSumbit = async (ev: Event) => {
  try {
    busy.exportPrivKey = true;
    info("Info", "Exporting private key");

    const formData = new FormData(ev.target as HTMLFormElement);
    const password = formData.get("password")!.toString();

    const cert = await getPrivKeyFn.value(data!.uuid, password);
    const pem = new TextDecoder().decode(b64ToU8Arr(cert));
    const file = new Blob([pem], { type: "application/x-pem-file" });
    saveFile(`${data!.uuid}.pem`, file);

    (ev.target as HTMLFormElement).querySelector("input")!.value = "";
    success("Success", "Successfully exported private key");
  } catch (err: unknown) {
    error("Fail to Export SSL Private Key", (err as Error).message);
  } finally {
    busy.exportPrivKey = false;
  }
};

// Watch
watch(visible, (v) => {
  if (v) {
    busy.exportCert = false;
    busy.exportChain = false;
    busy.exportPrivKey = false;
  }
});
</script>

<template>
  <Dialog
    v-model:visible="visible"
    header="Export SSL Certificate"
    :closable="canClose"
    modal>
    <section>
      <h1 class="font-bold my-2 text-lg">Certificate</h1>
      <div class="flex gap-4">
        <Button
          label="Export"
          size="small"
          :disabled="busy.exportCert"
          :loading="busy.exportCert"
          @click="exportCert()"></Button>
        <Button
          label="Export Fullchain"
          size="small"
          :disabled="busy.exportChain"
          :loading="busy.exportChain"
          @click="exportCert(true)"></Button>
      </div>
    </section>
    <section v-if="variant === 'ssl' || aboveUser" class="mt-8">
      <h1 class="font-bold my-2 text-lg">Private Key</h1>
      <form class="flex flex-col gap-2" @submit.prevent="onSumbit">
        <Password
          input-class="w-full"
          name="password"
          placeholder="Account Password"
          size="small"
          :feedback="false"
          required
          toggle-mask />
        <div class="flex justify-start">
          <Button
            label="Export"
            size="small"
            type="submit"
            :disabled="busy.exportPrivKey"
            :loading="busy.exportPrivKey"></Button>
        </div>
      </form>
    </section>
  </Dialog>
</template>
