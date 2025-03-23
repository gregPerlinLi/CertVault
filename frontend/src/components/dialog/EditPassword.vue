<script setup lang="ts">
import { useUserStore } from "@/stores/user";
import { useToast } from "primevue/usetoast";

// Models
const visible = defineModel<boolean>("visible");
const newPassword = defineModel<string>("new-password");

// Stores
const { syncToRemote } = useUserStore();

// Reactive
const toast = useToast();

const invalid = ref(false);
const busy = ref(false);

const oldPassword = ref("");

// Actions
const submit = async () => {
  // Clear error flag
  invalid.value = false;

  // Validate
  if (oldPassword.value.length === 0) {
    invalid.value = true;
    toast.add({
      severity: "error",
      summary: "Validation Error",
      detail: "Old password is required",
      life: 5000
    });
    return;
  }

  // Try update
  busy.value = true;
  toast.add({
    severity: "info",
    summary: "Info",
    detail: "Updating",
    life: 3000
  });

  const err = await syncToRemote(
    { oldPassword: oldPassword.value, newPassword: newPassword.value },
    toast
  );
  if (err === null) {
    newPassword.value = "";
    visible.value = false;
  }
  busy.value = false;
};
</script>

<template>
  <Dialog
    v-model:visible="visible"
    header="Confirm Old Password"
    :closable="false"
    modal>
    <form @submit.prevent="submit">
      <InputText
        v-model="oldPassword"
        class="mb-4 w-full"
        type="password"
        :disabled="busy"
        :invalid="invalid" />
      <div class="flex justify-end gap-2">
        <Button
          label="Cancel"
          severity="secondary"
          size="small"
          type="button"
          :disabled="busy"
          @click="visible = false"></Button>
        <Button
          label="Confirm"
          size="small"
          type="submit"
          :disabled="busy"
          :loading="busy"></Button>
      </div>
    </form>
  </Dialog>
</template>
