<script setup lang="ts">
import type { UserProfileDTO } from "@/api/types";
import { updateMultiUsrRoles } from "@/api/superadmin/user";

/* Models */
const visible = defineModel<boolean>("visible");

// Properties
const props = defineProps<{ users: UserProfileDTO[] }>();

// Emits
const emits = defineEmits<{ success: [] }>();

/* Services */
const { success, info, error, warn, remove } = useNotify();

/* Reactive */
const busy = ref(false);
const role = ref<1 | 2>(1);

/* Actions */
const submit = async () => {
  // Check superadmin
  if (props.users.filter(({ role }) => role === 3).length > 0) {
    warn("Not allow to change superadmins' roles");
    return;
  }

  // Try to update
  busy.value = true;
  const msg = info("Updating");

  try {
    await updateMultiUsrRoles({
      usernames: props.users.map(({ username }) => username),
      role: role.value
    });
    emits("success");
    visible.value = false;
    success("Successfully updated");
  } catch (err: unknown) {
    error((err as Error).message, "Fail to Update");
  }

  remove(msg);
  busy.value = false;
};

/* Watches */
watch(visible, () => {
  if (!visible.value) {
    busy.value = false;
    role.value = 1;
  }
});
</script>

<template>
  <Dialog
    v-model:visible="visible"
    :closable="false"
    :header="`Update Roles for ${users.length} User(s)`"
    modal>
    <form @submit.prevent="submit">
      <Select
        v-model="role"
        class="mb-4 w-full"
        option-label="name"
        option-value="role"
        :options="[
          { name: 'User', role: 1 },
          { name: 'Admin', role: 2 }
        ]"></Select>
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
          :loading="busy"></Button>
      </div>
    </form>
  </Dialog>
</template>
