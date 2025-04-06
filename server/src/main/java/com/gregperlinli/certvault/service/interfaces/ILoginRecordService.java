package com.gregperlinli.certvault.service.interfaces;

import com.gregperlinli.certvault.domain.dto.LoginRecordDTO;
import com.gregperlinli.certvault.domain.dto.PageDTO;
import com.gregperlinli.certvault.domain.entities.LoginRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * User Login Record Service
 *
 * @author gregPerlinLi
 * @since 2025-04-05
 */
public interface ILoginRecordService extends IService<LoginRecord> {

    /**
     * Get all login records
     *
     * @param keyword keyword(also username)
     * @param status  online status (-1: all, 0: offline, 1:online)
     * @param page    page
     * @param limit   limit
     * @return user login records
     */
    PageDTO<LoginRecordDTO> getLoginRecords(String keyword, Integer status, Integer page, Integer limit);

    /**
     * Get user login records
     *
     * @param username username
     * @param status   online status (-1: all, 0: offline, 1:online)
     * @param page     page
     * @param limit    limit
     * @return user login records
     */
    PageDTO<LoginRecordDTO> getUserLoginRecords(String username, Integer status, Integer page, Integer limit);

    /**
     * Add user login record
     *
     * @param loginRecordDTO login record
     * @param sessionId      session id
     */
    void addLoginRecord(LoginRecordDTO loginRecordDTO, String sessionId);

    /**
     * Set user login record offline
     *
     * @param sessionId session id
     */
    void setRecordOffline(String sessionId);

    /**
     * Force logout session
     *
     * @param username username
     * @param uuid uuid
     * @return result
     */
    boolean sessionForceLogout(String username, String uuid);

    /**
     * Force logout user's all sessions
     *
     * @param username username
     * @return result
     */
    boolean userForceLogout(String username);

}
