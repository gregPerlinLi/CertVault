export const useAsyncGuard = () => {
  const isActive = ref(true);
  let abort = new AbortController();

  const getSignal = () => abort.signal;
  const reset = () => {
    isActive.value = true;
    abort = new AbortController();
  };
  const cancel = () => {
    isActive.value = false;
    abort.abort("Component deactivated");
  };

  onBeforeUnmount(cancel);

  return { isActive, getSignal, reset, cancel };
};
