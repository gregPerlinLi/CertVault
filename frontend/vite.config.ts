/// <reference types="vitest" />

import { defineConfig } from "vite";

// Vite plugins
import autoImport from "unplugin-auto-import";
import tailwindcss from "@tailwindcss/vite";
import tsconfigPaths from "vite-tsconfig-paths";
import vue from "@vitejs/plugin-vue";
import vueComponents from "unplugin-vue-components";

// Resources
import { PrimeVueResolver } from "@primevue/auto-import-resolver";

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    autoImport.vite({
      dts: true,
      imports: ["vue", "vue-router", "@vueuse/core"]
    }),
    tailwindcss(),
    tsconfigPaths(),
    vue(),
    vueComponents.vite({ dts: true, resolvers: [PrimeVueResolver()] })
  ]
});
