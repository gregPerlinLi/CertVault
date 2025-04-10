<script setup lang="ts">
import { oidcLogin } from "@/api/authentication/oauth";
import { useUserStore } from "@/stores/user";
import { useNotify } from "@/utils/composable";

// Services
const router = useRouter();
const { toast, info, error } = useNotify();

// Stores
const { oidcProvider, oidcLogo, signIn } = useUserStore();

// Reactive
const busy = ref(false);
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
    error("Validation Error", "Username is required");
    return;
  }

  // Validate password
  const passowrd = formData.get("password")!.toString().trim();
  if (passowrd.length === 0) {
    errIdx.value = 1;
    error("Validation Error", "Password is required");
    return;
  }

  // Try sign in
  busy.value = true;
  info("Info", "Signing in");

  const err = await signIn(username, passowrd, toast);
  if (err === null) {
    await router.push("/dashboard");
  }
  busy.value = false;
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
            name="username"
            placeholder="Username"
            type="text"
            :invalid="errIdx === 0"
            @focus="errIdx = null" />
          <Password
            input-class="w-full"
            name="password"
            placeholder="Password"
            :feedback="false"
            :invalid="errIdx === 1"
            @focus="errIdx = null"
            toggle-mask />
          <Button label="Sign In" type="submit" :loading="busy"></Button>
          <template v-if="oidcProvider !== null">
            <div class="flex items-center justify-center my-4">
              <div
                class="border-neutral-300 border-t w-full dark:border-neutral-500"></div>
              <div
                class="absolute bg-neutral-50 px-2 text-neutral-300 dark:bg-neutral-800 dark:text-neutral-500">
                or
              </div>
            </div>
            <Button
              class="flex items-center"
              :disabled="busy"
              @click="oidcLogin">
              <img class="h-5" :src="oidcLogo" />
              <span>Continue with {{ oidcProvider }}</span>
            </Button>
          </template>
        </form>
      </template>
    </Card>
  </div>
</template>
