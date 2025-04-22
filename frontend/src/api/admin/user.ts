import type {
  BaseParams,
  PageParams,
  PageVO,
  UserProfileDTO
} from "@api/types";
import { callRestfulApi } from "@api/index";

export const getAllUsrInfo = (params: BaseParams & PageParams = {}) =>
  callRestfulApi<PageVO<UserProfileDTO>>({
    method: "GET",
    baseUrl: "/api/v1/admin/users",
    searchParams: { ...params, abort: undefined },
    abort: params.abort
  });
