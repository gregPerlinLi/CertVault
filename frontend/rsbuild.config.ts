import { defineConfig } from '@rsbuild/core';

// Rsbuild plugins
import { pluginVue } from '@rsbuild/plugin-vue';

// Rspack plugins
import pluginAutoImport from 'unplugin-auto-import/rspack';
import pluginVueComponents from 'unplugin-vue-components/rspack';

// Resources
import { PrimeVueResolver } from '@primevue/auto-import-resolver';

// Export configs
export default defineConfig({
  html: {
    title: 'CertVault',
  },
  plugins: [pluginVue()],
  tools: {
    rspack: {
      plugins: [
        pluginAutoImport({
          dts: true,
          imports: ['vue', 'vue-router', '@vueuse/core'],
        }),
        pluginVueComponents({ dts: true, resolvers: [PrimeVueResolver()] }),
      ],
    },
  },
});
