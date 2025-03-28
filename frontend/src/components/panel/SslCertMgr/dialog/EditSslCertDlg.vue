<script setup lang="ts">
import type { CertInfoDTO } from "@/api/types";
import { updateSslCertComment } from "@/api/user/cert/ssl";
import { useNotify } from "@/utils/composable";

// Models
const visible = defineModel<boolean>("visible");

// Emits
const emits = defineEmits<{ success: [] }>();

// Properties
const props = defineProps<{ data?: CertInfoDTO }>();

// Services
const { info, success, error } = useNotify();

// Reactive
const busy = ref(false);
const invalid = ref(false);

// Actions
const onSubmit = async (ev: Event) => {
  invalid.value = false;
  const formData = new FormData(ev.target as HTMLFormElement);

  // Validate data
  const comment = formData.get("comment")?.toString().trim() ?? "";
  if (comment === props.data!.comment) {
    invalid.value = true;
    error("Validation Error", "No changes found");
    return;
  }

  // Try update
  try {
    busy.value = true;
    info("Info", "Updating");

    await updateSslCertComment(props.data!.uuid, comment);

    success("Success", "Successfully updated comment");
    emits("success");
    visible.value = false;
  } catch (err: unknown) {
    error("Fail to Update Comment", (err as Error).message);
  } finally {
    busy.value = false;
  }
};
</script>

<template>
  <Dialog
    v-model:visible="visible"
    header="Edit SSL Certificate Comment"
    :closable="false"
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
