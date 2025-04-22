<script setup lang="ts">
import type { FileUploadSelectEvent } from "primevue";
import { importCa } from "@api/admin/cert/ca";
import { useNotify } from "@utils/composable";

/* Models */
const visible = defineModel<boolean>("visible");

// Emits
const emits = defineEmits<{ success: [] }>();

/* Services */
const { toast, info, success, error } = useNotify();

/* Reactive */
const busy = ref(false);
const invalid = ref(false);
const certData = ref<string>();
const keyData = ref<string>();

/* Actions */
const onCertSelected = (ev: FileUploadSelectEvent) => {
  const file = ev.files[0];
  const reader = new FileReader();
  reader.onload = () => {
    certData.value = window.btoa(reader.result as string);
  };
  reader.readAsText(file);
};

const onPrivKeySelected = (ev: FileUploadSelectEvent) => {
  const file = ev.files[0];
  const reader = new FileReader();
  reader.onload = () => {
    keyData.value = window.btoa(reader.result as string);
  };
  reader.readAsText(file);
};

const onSubmit = async (ev: Event) => {
  invalid.value = false;
  const formData = new FormData(ev.target as HTMLFormElement);

  // Validate data
  if (certData.value === undefined) {
    error("Validation Error", "Certificate not selected");
    return;
  }

  if (keyData.value === undefined) {
    error("Validation Error", "Private key not selected");
    return;
  }

  const comment = formData.get("comment")?.toString().trim() ?? "";
  if (comment.length === 0) {
    invalid.value = true;
    error("Validation Error", "Comment is required");
    return;
  }

  // Try to import
  busy.value = true;
  const msg = info("Info", "Importing");

  try {
    await importCa({
      certificate: certData.value,
      privkey: keyData.value,
      comment
    });
    success("Success", "Successfully imported");
    emits("success");
    visible.value = false;
  } catch (err: unknown) {
    error("Fail to Import", (err as Error).message);
  }

  toast.remove(msg);
  busy.value = false;
};

/* Watch */
watch(visible, () => {
  if (!visible.value) {
    busy.value = false;
    invalid.value = false;
    certData.value = undefined;
    keyData.value = undefined;
  }
});
</script>

<template>
  <Dialog
    v-model:visible="visible"
    header="Import New CA Certificate"
    :closable="false"
    modal>
    <form @submit.prevent="onSubmit">
      <section class="flex flex-col gap-1 my-2">
        <label required>Certificate File</label>
        <div class="flex items-center justify-between">
          <FileUpload
            accept=".pem"
            class="text-sm"
            mode="basic"
            @select="onCertSelected"
            auto
            custom-upload />
          <span
            v-if="certData !== undefined"
            class="pi pi-check text-green-500"></span>
          <span v-else class="pi pi-times text-red-500"></span>
        </div>
      </section>
      <section class="flex flex-col gap-1 my-2">
        <label required>Private Key File</label>
        <div class="flex items-center justify-between">
          <FileUpload
            accept=".pem"
            class="text-sm"
            mode="basic"
            @select="onPrivKeySelected"
            auto
            custom-upload />
          <span
            v-if="keyData !== undefined"
            class="pi pi-check text-green-500"></span>
          <span v-else class="pi pi-times text-red-500"></span>
        </div>
      </section>
      <section class="flex flex-col gap-1 my-2">
        <label for="comment" required>Comment</label>
        <InputText
          id="comment"
          name="comment"
          size="small"
          :invalid="invalid" />
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
          label="Upload"
          size="small"
          type="submit"
          :disabled="busy"
          :loading="busy"></Button>
      </div>
    </form>
  </Dialog>
</template>
