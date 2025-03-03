import { defineConfig } from '@rsbuild/core';

// Plugins
import { pluginVue } from '@rsbuild/plugin-vue';

// Export configs
export default defineConfig({
  plugins: [pluginVue()],
});
