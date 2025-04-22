<script setup lang="ts">
import type { CaInfoDTO, CertInfoDTO } from "@api/types";
import { updateCaComment } from "@api/admin/cert/ca";
import { updateSslCertComment } from "@api/user/cert/ssl";

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
const invalid = ref(false);

/* Computed */
const updCertCmtFn = computed(() =>
  variant === "ca" ? updateCaComment : updateSslCertComment
);

/* Actions */
const onSubmit = async (ev: Event) => {
  invalid.value = false;
  const formData = new FormData(ev.target as HTMLFormElement);

  // Validate data
  const comment = formData.get("comment")?.toString().trim() ?? "";
  if (comment === data!.comment) {
    invalid.value = true;
    error("No changes found", "Validation Error");
    return;
  }
  if (comment.length === 0) {
    invalid.value = true;
    error("Comment is required", "Validation Error");
    return;
  }

  // Try to update
  busy.value = true;
  const msg = info("Updating");

  try {
    await updCertCmtFn.value({ uuid: data!.uuid, comment });

    success("Successfully updated comment");
    emits("success");
    visible.value = false;
  } catch (err: unknown) {
    error((err as Error).message, "Fail to Update Comment");
  }

  remove(msg);
  busy.value = false;
};

/* Watch */
watch(visible, () => {
  if (!visible.value) {
    busy.value = false;
    invalid.value = false;
  }
});
</script>

<template>
  <Dialog
    v-model:visible="visible"
    :closable="false"
    :header="
      variant === 'ca'
        ? 'Edit CA Certificate Comment'
        : 'Edit SSL Certificate Comment'
    "
    modal>
    <form @submit.prevent="onSubmit">
      <section class="flex flex-col gap-1 my-2">
        <label for="comment" required>New Comment</label>
        <InputText
          id="comment"
          name="comment"
          size="small"
          :default-value="data?.comment"
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
          label="Update"
          size="small"
          type="submit"
          :disabled="busy"
          :loading="busy"></Button>
      </div>
    </form>
  </Dialog>
</template>
