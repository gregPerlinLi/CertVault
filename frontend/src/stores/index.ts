// Types
type RoleType = "super" | "admin" | "user";

// Export store
export const useMainStore = createGlobalState(() => {
  // States

  // Getters
  const username = computed((): string => "username");
  const role = computed<RoleType>((): RoleType => "super");

  // Actions

  // Returns
  return { username, role };
});
