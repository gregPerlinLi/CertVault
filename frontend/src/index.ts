import { createApp } from 'vue';

// Vue plugins
import pluginRouter from '@/router';
import pluginPrimeVue from 'primevue/config';

// Resources
import Aura from '@primeuix/themes/aura';

// Global stylesheets
import '@/index.css';

// Root component
import App from '@/App.vue';

// Create application
createApp(App)
  .use(pluginRouter)
  .use(pluginPrimeVue, { theme: { preset: Aura } })
  .mount('#root');
