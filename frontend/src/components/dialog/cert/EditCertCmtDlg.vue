<script setup lang="ts">
import type { CaInfoDTO, CertInfoDTO } from "@/api/types";
import { updateCaComment } from "@/api/admin/ca";
import { updateSslCertComment } from "@/api/user/cert/ssl";
import { useNotify } from "@/utils/composable";

// Models
const visible = defineModel<boolean>("visible");

// Emits
const emits = defineEmits<{ success: [] }>();

// Properties
const { variant, data } = defineProps<{
  variant: "ca" | "ssl";
  data?: CaInfoDTO | CertInfoDTO;
}>();

// Services
const { info, success, error } = useNotify();

// Reactive
const busy = ref(false);
const invalid = ref(false);

// Computed
const updCertCmtFn = computed(() =>
  variant === "ca" ? updateCaComment : updateSslCertComment
);

// Actions
const onSubmit = async (ev: Event) => {
  invalid.value = false;
  const formData = new FormData(ev.target as HTMLFormElement);

  // Validate data
  const comment = formData.get("comment")?.toString().trim() ?? "";
  if (comment === data!.comment) {
    invalid.value = true;
    error("Validation Error", "No changes found");
    return;
  }
  if (comment.length === 0) {
    invalid.value = true;
    error("Validation Error", "Comment is required");
    return;
  }

  // Try update
  try {
    busy.value = true;
    info("Info", "Updating");

    await updCertCmtFn.value(data!.uuid, comment);

    success("Success", "Successfully updated comment");
    emits("success");
    visible.value = false;
  } catch (err: unknown) {
    error("Fail to Update Comment", (err as Error).message);
  } finally {
    busy.value = false;
  }
};

// Watch
watch(visible, (v) => {
  if (v) {
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
