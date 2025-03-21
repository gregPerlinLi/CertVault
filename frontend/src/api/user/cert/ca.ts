import type { CAInfoDTO, PaginationVO, ResultVO } from "@/api/types";

export const getAllBindedCAs = async (page: number, limit: number) => {
  const resp = await fetch(`/api/v1/user/cert/ca/${page}/${limit}`);
  if (!resp.ok) {
    throw Error(`${resp.statusText} (${resp.status})`);
  }

  const json: ResultVO<PaginationVO<CAInfoDTO>> = await resp.json();
  if (json.code !== 200) {
    throw Error(`${json.msg} (${json.code})`);
  }

  return json.data!;
};

export const getCACert = async (uuid: string) => {
  const resp = await fetch(`/api/v1/user/cert/ca/cer/${uuid}`);
  if (!resp.ok) {
    throw Error(`${resp.statusText} (${resp.status})`);
  }

  const json: ResultVO<string> = await resp.json();
  if (json.code !== 200) {
    throw Error(`${json.msg} (${json.code})`);
  }

  return json.data!;
};
