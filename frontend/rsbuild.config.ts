import { defineConfig } from '@rsbuild/core';

// Plugins
import { pluginVue } from '@rsbuild/plugin-vue';
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
        pluginVueComponents({ dts: true, resolvers: [PrimeVueResolver()] }),
      ],
    },
  },
});
