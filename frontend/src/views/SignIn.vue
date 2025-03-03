<script setup lang="ts">
import { notify } from "@kyvg/vue3-notification";

// Reactive
const errIdx = ref<number | null>(null);

// Actions
async function trySignIn(ev: Event): Promise<void> {
  const formData = new FormData(ev.target as HTMLFormElement);

  errIdx.value = null;

  const username = formData.get("username")!.toString().trim();
  if (username.length === 0) {
    errIdx.value = 0;
    notify({ text: "Username is required", type: "error" });
    return;
  }

  const passowrd = formData.get("password")!.toString().trim();
  if (passowrd.length === 0) {
    errIdx.value = 1;
    notify({ text: "Password is required", type: "error" });
    return;
  }

  ((ev as SubmitEvent).submitter! as HTMLButtonElement).disabled = true;
  notify({ text: "Signing in" });

  // TODO: Call RESTful API to sign in
  await new Promise((res) => setTimeout(res, 1000));
  notify({ text: "TODO", type: "error" });
  ((ev as SubmitEvent).submitter! as HTMLButtonElement).disabled = false;
  // end TODO
}
</script>

<template>
  <div class="fixed flex flex-col inset-0 items-center">
    <h1 class="mb-6 mt-40 text-center text-xl">Sign In to CertVault</h1>
    <Card class="bg-neutral-50! border border-neutral-200">
      <template #content>
        <form class="flex flex-col gap-4" @submit.prevent="trySignIn">
          <InputText
            :invalid="errIdx === 0"
            name="username"
            placeholder="Username"
            type="text"
            @focus="errIdx = null" />
          <Password
            :feedback="false"
            :invalid="errIdx === 1"
            name="password"
            placeholder="Password"
            toggle-mask
            @focus="errIdx = null" />
          <Button label="Sign In" type="submit"></Button>
        </form>
      </template>
    </Card>
  </div>
</template>
