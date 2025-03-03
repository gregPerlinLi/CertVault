package com.gregperlinli.certvault.service.interfaces;

import com.gregperlinli.certvault.domain.dto.UserDTO;
import com.gregperlinli.certvault.domain.entities.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户 服务类
 * </p>
 *
 * @author gregPerlinLi
 * @since 2025-03-03
 */
public interface IUserService extends IService<User> {

    /**
     * Login
     *
     * @param username username
     * @param password password
     * @param sessionId sessionId
     * @return login result
     */
    UserDTO login(String username, String password, String sessionId);

}
