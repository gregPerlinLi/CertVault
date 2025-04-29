<script setup lang="ts">
import { login } from "@api/authentication";
import { oidcLogin } from "@api/authentication/oauth";
import { signInSchema } from "@utils/schema";

/* Services */
const router = useRouter();
const { success, info, warn, error, remove } = useNotify();
const { isInvalid, clearInvalid, validate } = useFormValidator(signInSchema);

/* Stores */
const { oidcProviders } = useCommonStore();
const user = useUserStore();

/* Reactive */
const busy = ref(false);

/* Actions */
const trySignIn = async (ev: Event) => {
  // Validate form
  const result = validate(ev.target as HTMLFormElement);
  if (!result.success) {
    warn(result.issues![0].message);
    return;
  }

  // Try to sign in
  busy.value = true;
  const msg = info("Signing in");

  try {
    const profile = await login({
      username: result.output.username,
      password: result.output.password,
      abort: { timeout: 20000 }
    });

    user.update(profile);
    router.push("/dashboard");
    success("Successfully signed in");
  } catch (err: unknown) {
    error((err as Error).message, "Fail to Sign in");
  }

  remove(msg);
  busy.value = false;
};
</script>

<template>
  <div class="fixed flex flex-col inset-0 items-center justify-center">
    <img class="h-12 mb-4 w-12" draggable="false" src="/favicon.svg" />
    <h1 class="mb-6 text-center text-xl">Sign in to CertVault</h1>
    <Card
      class="bg-neutral-50 border border-neutral-200 dark:bg-neutral-800 dark:border-neutral-500">
      <template #content>
        <form class="flex flex-col gap-4" @submit.prevent="trySignIn">
          <!-- Form elements -->
          <InputText
            name="username"
            placeholder="Username"
            :disabled="busy"
            :invalid="isInvalid('username')"
            @focus="clearInvalid('username')" />
          <Password
            input-class="w-full"
            name="password"
            placeholder="Password"
            :disabled="busy"
            :feedback="false"
            :invalid="isInvalid('password')"
            @focus="clearInvalid('password')"
            toggle-mask />
          <Button label="Sign in" type="submit" :loading="busy"></Button>

          <!-- OIDC list -->
          <template v-if="oidcProviders !== null">
            <div class="flex items-center justify-center my-4">
              <div
                class="border-neutral-300 border-t w-full dark:border-neutral-500"></div>
              <div
                class="absolute bg-neutral-50 px-2 text-neutral-300 dark:bg-neutral-800 dark:text-neutral-500">
                or
              </div>
            </div>
            <div class="flex flex-col gap-2">
              <Button
                v-for="item in oidcProviders"
                class="flex items-center"
                :disabled="busy"
                @click="oidcLogin(item.provider)">
                <img class="h-5" :src="item.logo" />
                <span>Continue with {{ item.displayName }}</span>
              </Button>
            </div>
          </template>
        </form>
      </template>
    </Card>
  </div>
</template>
