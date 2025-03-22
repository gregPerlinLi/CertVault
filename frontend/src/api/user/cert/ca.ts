import type { CAInfoDTO, PaginationVO, ResultVO } from "@/api/types";
import { createURLSearchParams } from "@/utils";

export const getAllBindedCAs = async (
  page: number,
  limit: number,
  keyword?: string
) => {
  const params = createURLSearchParams({ page, limit, keyword });
  const resp = await fetch(`/api/v1/user/cert/ca?${params.toString()}`);
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
