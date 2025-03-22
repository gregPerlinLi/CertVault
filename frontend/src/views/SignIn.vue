<script setup lang="ts">
import { useUserStore } from "@/stores/user";
import { useToast } from "primevue/usetoast";

// Reactive
const router = useRouter();
const toast = useToast();
const { signIn } = useUserStore();
const errIdx = ref<number | null>(null);

// Actions
async function trySignIn(ev: Event): Promise<void> {
  // Parse form data
  const formData = new FormData(ev.target as HTMLFormElement);

  // Clear error index
  errIdx.value = null;

  // Validate username
  const username = formData.get("username")!.toString().trim();
  if (username.length === 0) {
    errIdx.value = 0;
    toast.add({
      severity: "error",
      summary: "Validation Error",
      detail: "Username is required",
      life: 5000
    });
    return;
  }

  // Validate password
  const passowrd = formData.get("password")!.toString().trim();
  if (passowrd.length === 0) {
    errIdx.value = 1;
    toast.add({
      severity: "error",
      summary: "Validation Error",
      detail: "Password is required",
      life: 5000
    });
    return;
  }

  // Try sign in
  ((ev as SubmitEvent).submitter! as HTMLButtonElement).disabled = true;
  toast.add({
    severity: "info",
    summary: "Info",
    detail: "Signing in",
    life: 3000
  });

  const err = await signIn(username, passowrd, toast);
  if (err === null) {
    router.push("/dashboard");
  } else {
    ((ev as SubmitEvent).submitter! as HTMLButtonElement).disabled = false;
  }
}
</script>

<template>
  <div class="fixed flex flex-col inset-0 items-center pt-40">
    <img class="h-12 mb-4 w-12" draggable="false" src="/favicon.svg" />
    <h1 class="mb-6 text-center text-xl">Sign In to CertVault</h1>
    <Card
      class="bg-neutral-50 border border-neutral-200 dark:bg-neutral-800 dark:border-neutral-500">
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
