import type { ResultVO, UserProfileDTO } from "@/api/types";

export const getProfile = async () => {
  const resp = await fetch("/api/v1/user/profile");
  if (!resp.ok) {
    throw Error(`${resp.statusText} (${resp.status})`);
  }

  const json: ResultVO<UserProfileDTO> = await resp.json();
  if (json.code !== 200) {
    throw Error(`${json.msg} (${json.code})`);
  }

  return json.data!;
};

export interface UpdateProfileRequestPayload {
  displayName?: string;
  email?: string;
  oldPassword?: string;
  newPassword?: string;
}
export const updateProfile = async (payload: UpdateProfileRequestPayload) => {
  const req = {
    headers: { "Content-Type": "application/json" },
    method: "PATCH",
    body: JSON.stringify(payload)
  } satisfies RequestInit;

  const resp = await fetch("/api/v1/user/profile", req);
  if (!resp.ok) {
    throw Error(`${resp.statusText} (${resp.status})`);
  }

  const json: ResultVO = await resp.json();
  if (json.code !== 200) {
    throw Error(`${json.msg} (${json.code})`);
  }
};
