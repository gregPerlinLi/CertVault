import type { UserProfileDTO } from "@api/types";

// Types
export enum UserRole {
  User = 1,
  Admin = 2,
  Superadmin = 3
}

// Export store
export const useUserStore = createGlobalState(() => {
  /* States */
  const username = ref<string | null>(null);
  const displayName = ref<string | null>(null);
  const email = ref<string | null>(null);
  const role = ref<UserRole | null>(null);

  /* Getters */
  const displayRole = computed(() => {
    switch (role.value) {
      case UserRole.User:
        return "User";
      case UserRole.Admin:
        return "Admin";
      case UserRole.Superadmin:
        return "Superadmin";
      default:
        return null;
    }
  });
  const isUser = computed(() => role.value === UserRole.User);
  const isAdmin = computed(() => role.value === UserRole.Admin);
  const isSuperadmin = computed(() => role.value === UserRole.Superadmin);
  const isSignedIn = computed(() => username.value !== null);
  const getRoleClass = computed(() => (r?: UserRole) => {
    switch (r ?? role.value) {
      case UserRole.Admin:
        return "text-blue-500";
      case UserRole.Superadmin:
        return "font-bold text-red-500";
      default:
        return null;
    }
  });

  /* Actions */
  const clear = () => {
    username.value = null;
    displayName.value = null;
    email.value = null;
    role.value = null;
  };
  const update = (dto: UserProfileDTO) => {
    username.value = dto.username;
    displayName.value = dto.displayName;
    email.value = dto.email;
    role.value = dto.role;
  };

  // Returns
  return {
    username,
    displayName,
    email,
    role,

    displayRole,
    isUser,
    isAdmin,
    isSuperadmin,
    isSignedIn,
    getRoleClass,

    clear,
    update
  };
});
