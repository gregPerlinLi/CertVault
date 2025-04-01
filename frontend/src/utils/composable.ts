import { useUserStore } from "@/stores/user";
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

export const useRole = () => {
  const { role } = useUserStore();

  const isUser = computed(() => role.value === "User");
  const isAdmin = computed(() => role.value === "Admin");
  const isSuperadmin = computed(() => role.value === "Superadmin");
  const aboveUser = computed(() => isAdmin.value || isSuperadmin.value);

  return { role, isUser, isAdmin, isSuperadmin, aboveUser };
};
