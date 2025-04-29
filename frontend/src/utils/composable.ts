export const useAsyncGuard = () => {
  const isActivate = ref(true);
  const abort = new AbortController();

  const cancel = () => {
    isActivate.value = false;
    abort.abort("Component deactivated");
  };
  onBeforeUnmount(cancel);

  return { isActivate, signal: abort.signal, cancel };
};

export const useReloadableAsyncGuard = () => {
  const isActivate = ref(true);
  let abort = new AbortController();

  const reload = () => {
    isActivate.value = true;
    abort = new AbortController();
  };
  const cancel = () => {
    isActivate.value = false;
    abort.abort("Component deactivated");
  };
  const getSignal = () => abort.signal;
  onBeforeUnmount(cancel);

  return { isActivate, reload, cancel, getSignal };
};
