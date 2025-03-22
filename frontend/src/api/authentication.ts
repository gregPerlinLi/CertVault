import type { ResultVO, UserProfileDTO } from "@/api/types";

export const login = async (username: string, password: string) => {
  const req = {
    headers: { "Content-Type": "application/json" },
    method: "POST",
    body: JSON.stringify({ username, password })
  } satisfies RequestInit;

  const resp = await fetch("/api/v1/auth/login", req);
  if (!resp.ok) {
    throw Error(`HTTP: ${resp.statusText} (${resp.status})`);
  }

  const json: ResultVO<UserProfileDTO> = await resp.json();
  if (json.code !== 200) {
    throw Error(`${json.msg} (${json.code})`);
  }

  return json.data!;
};

export const logout = async () => {
  const req = { method: "DELETE" } satisfies RequestInit;

  const resp = await fetch("/api/v1/auth/logout", req);
  if (!resp.ok) {
    throw Error(`${resp.statusText} (${resp.status})`);
  }

  const json: ResultVO = await resp.json();
  if (json.code !== 200) {
    throw Error(`${json.msg} (${json.code})`);
  }
};
