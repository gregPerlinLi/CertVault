<script setup lang="ts">
import type { CaInfoDTO, CertInfoDTO } from "@api/types";
import { getCaPrivKey } from "@api/admin/cert/ca";
import { getCaCert } from "@api/user/cert/ca";
import { getSslCert, getSslPrivKey } from "@api/user/cert/ssl";
import { useUserStore } from "@stores/user";
import { b64ToU8Arr, saveFile } from "@utils/index";

/* Models */
const visible = defineModel<boolean>("visible");

// Properties
const { variant, data } = defineProps<{
  variant: "ca" | "ssl";
  data?: CaInfoDTO | CertInfoDTO;
}>();

/* Services */
const { success, info, error, remove } = useNotify();

/* Stores */
const { isAdmin, isSuperadmin } = useUserStore();

/* Reactives */
const busy = reactive({
  exportCert: false,
  exportChain: false,
  exportChainRoot: false,
  exportPrivKey: false
});

/* Computed */
const canClose = computed(
  () =>
    !busy.exportCert &&
    !busy.exportChain &&
    !busy.exportChainRoot &&
    !busy.exportPrivKey
);
const getCertFn = computed(() => (variant === "ca" ? getCaCert : getSslCert));
const getPrivKeyFn = computed(() =>
  variant === "ca" ? getCaPrivKey : getSslPrivKey
);

/* Actions */
const exportCert = async (
  isChain: boolean = false,
  needRootCa: boolean = false
) => {
  const msg = (() => {
    if (needRootCa) {
      busy.exportChainRoot = true;
      return info("Exporting fullchain with root CA");
    } else if (isChain) {
      busy.exportChain = true;
      return info("Exporting fullchain");
    } else {
      busy.exportCert = true;
      return info("Exporting");
    }
  })();

  try {
    const cert = await getCertFn.value({
      uuid: data!.uuid,
      isChain,
      needRootCa
    });
    const pem = new TextDecoder().decode(b64ToU8Arr(cert));
    const file = new Blob([pem], { type: "application/x-pem-file" });
    saveFile(`${data!.uuid}.pem`, file);

    success(
      isChain ? "Successfully exported fullchain" : "Successfully exported"
    );
  } catch (err: unknown) {
    error((err as Error).message, "Fail to Export SSL Certificate");
  }

  remove(msg);
  if (needRootCa) {
    busy.exportChainRoot = false;
  } else if (isChain) {
    busy.exportChain = false;
  } else {
    busy.exportCert = false;
  }
};
const onSumbit = async (ev: Event) => {
  busy.exportPrivKey = true;
  const msg = info("Exporting private key");

  try {
    const formData = new FormData(ev.target as HTMLFormElement);
    const password = formData.get("password")!.toString();

    const cert = await getPrivKeyFn.value({ uuid: data!.uuid, password });
    const pem = new TextDecoder().decode(b64ToU8Arr(cert));
    const file = new Blob([pem], { type: "application/x-pem-file" });
    saveFile(`${data!.uuid}.pem`, file);

    (ev.target as HTMLFormElement).querySelector("input")!.value = "";
    success("Successfully exported private key");
  } catch (err: unknown) {
    error((err as Error).message, "Fail to Export SSL Private Key");
  }

  remove(msg);
  busy.exportPrivKey = false;
};

/* Watch */
watch(visible, () => {
  if (!visible.value) {
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
        <Button
          label="Export Fullchain with Root CA"
          size="small"
          :disabled="busy.exportChainRoot"
          :loading="busy.exportChainRoot"
          @click="exportCert(true, true)"></Button>
      </div>
    </section>
    <section v-if="variant === 'ssl' || isAdmin || isSuperadmin" class="mt-8">
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
