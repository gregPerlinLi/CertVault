import ErrorPlaceholer from "@comps/placeholder/ErrorPlaceholer.vue";
import LoadingPlaceholder from "@comps/placeholder/LoadingPlaceholder.vue";

export const useAsyncDataTable = () => {
  const { error } = useNotify();

  return defineAsyncComponent({
    suspensible: false,
    loader: () => import("primevue/datatable"),
    loadingComponent: LoadingPlaceholder,
    errorComponent: ErrorPlaceholer,
    onError: (err, retry, fail, attampts) => {
      if (attampts < 5) {
        retry();
      } else {
        error("Fail to Load Data Table Component", err.message);
        fail();
      }
    }
  });
};
