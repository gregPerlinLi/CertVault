<script setup lang="ts">
/* Services */
const { warn } = useNotify();

/* Stores */
const { username, displayName, email, displayRole, getRoleClass } =
  useUserStore();

/* Reactive */
const newPassword = ref("");
const newPasswordInvalid = ref(false);

const dialog = reactive({
  editDispName: false,
  editEmail: false,
  editPasswd: false
});

/* Action */
const onSubmitNewPassword = () => {
  if (newPassword.value.length === 0) {
    newPasswordInvalid.value = true;
    warn("New password is required");
    return;
  }
  dialog.editPasswd = true;
};
</script>

<template>
  <!-- Header -->
  <Breadcrumb
    :home="{ icon: 'pi pi-home' }"
    :model="[
      {
        label: 'My Account',
        icon: 'pi pi-user'
      },
      {
        label: 'Profile',
        icon: 'pi pi-user-edit'
      }
    ]">
    <template #item="{ item }">
      <div class="flex gap-2 items-center">
        <span :class="item.icon"></span>
        <span>{{ item.label }}</span>
      </div>
    </template>
  </Breadcrumb>

  <!-- Main -->
  <section>
    <h2>Username</h2>
    <p>{{ username }}</p>
  </section>
  <section>
    <h2>Display Name</h2>
    <p>
      {{ displayName }}
      <Button
        icon="pi pi-pen-to-square"
        severity="help"
        size="small"
        type="button"
        variant="text"
        @click="dialog.editDispName = true"></Button>
    </p>
  </section>
  <section>
    <h2>Email</h2>
    <p>
      {{ email }}
      <Button
        icon="pi pi-pen-to-square"
        severity="help"
        size="small"
        type="button"
        variant="text"
        @click="dialog.editEmail = true"></Button>
    </p>
  </section>
  <section>
    <h2>Role</h2>
    <p :class="getRoleClass()">{{ displayRole }}</p>
  </section>
  <section>
    <h2>New Password</h2>
    <form
      class="flex gap-2 items-center mt-1"
      @submit.prevent="onSubmitNewPassword">
      <Password
        v-model:model-value="newPassword"
        size="small"
        :invalid="newPasswordInvalid"
        @focus="newPasswordInvalid = false" />
      <Button
        icon="pi pi-check"
        severity="help"
        size="small"
        type="submit"
        variant="text"></Button>
    </form>
  </section>

  <!-- Dialogs -->
  <EditDispNameDlg
    v-model:visible="dialog.editDispName"
    :value="displayName ?? ''" />
  <EditEmailDlg v-model:visible="dialog.editEmail" :value="email ?? ''" />
  <EditPasswdDlg
    v-model:visible="dialog.editPasswd"
    v-model:new-password="newPassword" />
</template>

<style scoped>
@import "tailwindcss";

section {
  @apply my-4;

  h2 {
    @apply font-bold text-xl;
  }
}
</style>
