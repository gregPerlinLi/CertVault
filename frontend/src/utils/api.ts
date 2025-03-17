export interface ResultVO<T> {
  code: number;
  msg: string;
  data: T | null;
  timestamp: number;
}

// Login
export interface UserProfileDTO {
  username: string;
  displayName: string;
  email: string;
  role: 1 | 2 | 3;
}
export const login = async (
  username: string,
  password: string
): Promise<UserProfileDTO> => {
  const r: ResultVO<UserProfileDTO> = await fetch("/api/auth/login", {
    headers: { "Content-Type": "application/json" },
    method: "POST",
    body: JSON.stringify({ username, password })
  }).then((r) => r.json());

  if (r.code === 200) {
    return r.data!;
  } else {
    throw r;
  }
};

// Logout
export const logout = async (): Promise<void> => {
  const r: ResultVO<null> = await fetch("/api/auth/logout", {
    method: "DELETE"
  }).then((r) => r.json());

  if (r.code !== 200) {
    throw r;
  }
};

// Get user profile
export const getUserProfile = async (): Promise<UserProfileDTO> => {
  const r: ResultVO<UserProfileDTO> = await fetch("/api/user/profile").then(
    (r) => r.json()
  );

  if (r.code === 200) {
    return r.data!;
  } else {
    throw r;
  }
};
