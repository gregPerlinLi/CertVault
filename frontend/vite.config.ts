/// <reference types="vitest" />

import { defineConfig, loadEnv } from "vite";

// Vite plugins
import { visualizer } from "rollup-plugin-visualizer";
import autoImport from "unplugin-auto-import";
import tailwindcss from "@tailwindcss/vite";
import tsconfigPaths from "vite-tsconfig-paths";
import vue from "@vitejs/plugin-vue";
import vueComponents from "unplugin-vue-components";

// Resources
import { PrimeVueResolver } from "@primevue/auto-import-resolver";

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd());
  return {
    plugins: [
      tsconfigPaths({ loose: true, projects: ["tsconfig.app.json"] }),
      vue(),
      autoImport.vite({
        dirs: ["./src/composables/**", "./src/stores/**"],
        dts: true,
        imports: ["vue", "vue-router", "@vueuse/core"]
      }),
      vueComponents.vite({ dts: true, resolvers: [PrimeVueResolver()] }),
      tailwindcss(),
      visualizer({
        open: env.VITE_OPEN_VISUALIZER === "1",
        filename: "visualizer.html"
      })
    ],
    server: {
      proxy: {
        "/api": {
          changeOrigin: true,
          target: env.VITE_TEST_SERVER_TARGET
        },
        "/swagger-ui": {
          changeOrigin: true,
          target: env.VITE_TEST_SERVER_TARGET
        }
      }
    }
  };
});
