import { createApp } from 'vue';

// Vue plugins
import PrimeVue from 'primevue/config';
import Aura from '@primeuix/themes/aura';

// Global stylesheets
import '@/index.css';

// Root component
import App from '@/App.vue';

// Create application
createApp(App)
  .use(PrimeVue, { theme: { preset: Aura } })
  .mount('#root');
