import { useToast } from "primevue/usetoast";

export const useNotify = () => {
  const toast = useToast();

  const info = (summary: string, detail: string) =>
    toast.add({ severity: "info", summary, detail, life: 3000 });
  const success = (summary: string, detail: string) =>
    toast.add({ severity: "success", summary, detail, life: 3000 });
  const error = (summary: string, detail: string) =>
    toast.add({ severity: "error", summary, detail, life: 5000 });

  return { toast, info, success, error };
};
