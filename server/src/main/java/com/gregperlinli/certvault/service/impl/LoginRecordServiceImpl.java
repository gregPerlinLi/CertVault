package com.gregperlinli.certvault.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gregperlinli.certvault.constant.AccountTypeConstant;
import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.dto.LoginRecordDTO;
import com.gregperlinli.certvault.domain.dto.PageDTO;
import com.gregperlinli.certvault.domain.entities.LoginRecord;
import com.gregperlinli.certvault.domain.entities.User;
import com.gregperlinli.certvault.domain.exception.ParamValidateException;
import com.gregperlinli.certvault.mapper.LoginRecordMapper;
import com.gregperlinli.certvault.service.interfaces.ILoginRecordService;
import com.gregperlinli.certvault.service.interfaces.IUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 User Login Record Service Implementation
 *
 * @author gregPerlinLi
 * @since 2025-04-05
 */
@Service
@Slf4j
public class LoginRecordServiceImpl extends ServiceImpl<LoginRecordMapper, LoginRecord> implements ILoginRecordService {

    @Resource
    IUserService userService;

    @Resource
    SessionRegistry sessionRegistry;

    @Override
    public PageDTO<LoginRecordDTO> getLoginRecords(String keyword, Integer status, String sessionId, Integer page, Integer limit) {
        if ( status == null ) {
            status = -1;
        }
        Page<LoginRecord> loginRecordPage = new Page<>(page, limit);
        Page<LoginRecord> resultPage;
        QueryWrapper<LoginRecord> loginRecordQueryWrapper = new QueryWrapper<>();
        if ( status == 0 ) {
            loginRecordQueryWrapper.eq("online", false);
        } else if ( status == 1 ) {
            loginRecordQueryWrapper.eq("online", true);
        }
        if ( keyword != null && !keyword.isEmpty() ) {
            List<User> users = userService.list(new QueryWrapper<User>().like("username", keyword).eq("deleted", false));
            if ( users == null || users.isEmpty() ) {
                return new PageDTO<>(0L, null);
            }
            Set<Integer> uids = users.stream().map(User::getId).collect(Collectors.toSet());
            loginRecordQueryWrapper.in("uid", uids);
        }
        resultPage = this.page(loginRecordPage, loginRecordQueryWrapper);
        if ( resultPage.getSize() == 0 || resultPage.getRecords() == null || resultPage.getRecords().isEmpty() ) {
            return new PageDTO<>(resultPage.getTotal(), null);
        }
        Set<Integer> uids = resultPage.getRecords().stream()
                .map(LoginRecord::getUid).collect(Collectors.toSet());
        Map<Integer, String> userMap = userService.listByIds(uids)
                .stream().collect(Collectors.toMap(User::getId, User::getUsername));
        return new PageDTO<>(resultPage.getTotal(),
                resultPage.getRecords().stream().map(loginRecord -> {
                    LoginRecordDTO dto = new LoginRecordDTO();
                    dto.setUuid(loginRecord.getUuid());
                    dto.setUsername(userMap.getOrDefault(loginRecord.getUid(), "Unknown"));
                    dto.setIpAddress(loginRecord.getIp());
                    dto.setRegion(loginRecord.getRegion());
                    dto.setProvince(loginRecord.getProvince());
                    dto.setCity(loginRecord.getCity());
                    dto.setBrowser(loginRecord.getBrowser());
                    dto.setOs(loginRecord.getOs());
                    dto.setPlatform(loginRecord.getPlatform());
                    dto.setLoginTime(loginRecord.getLoginTime());
                    dto.setIsOnline(loginRecord.getOnline());
                    if ( Objects.equals(loginRecord.getSessionId(), sessionId) ) {
                        dto.setIsCurrentSession(true);
                    }
                    return dto;
                }).toList());
    }

    @Override
    public PageDTO<LoginRecordDTO> getUserLoginRecords(String username, Integer status, String sessionId, Integer page, Integer limit) {
        if ( status == null ) {
            status = -1;
        }
        Page<LoginRecord> loginRecordPage = new Page<>(page, limit);
        Page<LoginRecord> resultPage;
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", username)
                .eq("deleted", false);
        User user = userService.getOne(userQueryWrapper);
        if ( user == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The user does not exist.");
        }
        QueryWrapper<LoginRecord> loginRecordQueryWrapper = new QueryWrapper<>();
        loginRecordQueryWrapper.eq("uid", user.getId());
        if ( status == 0 ) {
            loginRecordQueryWrapper.eq("online", false);
        } else if ( status == 1 ) {
            loginRecordQueryWrapper.eq("online", true);
        }
        resultPage = this.page(loginRecordPage, loginRecordQueryWrapper);
        if ( resultPage.getSize() == 0 || resultPage.getRecords() == null || resultPage.getRecords().isEmpty() ) {
            return new PageDTO<>(resultPage.getTotal(), null);
        }
        return new PageDTO<>(resultPage.getTotal(), resultPage.getRecords().stream().map(loginRecord -> {
            LoginRecordDTO dto = new LoginRecordDTO();
            dto.setUuid(loginRecord.getUuid());
            dto.setUsername(user.getUsername());
            dto.setIpAddress(loginRecord.getIp());
            dto.setRegion(loginRecord.getRegion());
            dto.setProvince(loginRecord.getProvince());
            dto.setCity(loginRecord.getCity());
            dto.setBrowser(loginRecord.getBrowser());
            dto.setOs(loginRecord.getOs());
            dto.setPlatform(loginRecord.getPlatform());
            dto.setLoginTime(loginRecord.getLoginTime());
            dto.setIsOnline(loginRecord.getOnline());
            if ( Objects.equals(loginRecord.getSessionId(), sessionId) ) {
                dto.setIsCurrentSession(true);
            }
            return dto;
        }).toList());
    }

    @Override
    public void addLoginRecord(LoginRecordDTO loginRecordDTO, String sessionId) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", loginRecordDTO.getUsername())
                .eq("deleted", false);
        User user = userService.getOne(userQueryWrapper);
        if ( user == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The user does not exist.");
        }
        LoginRecord loginRecord = new LoginRecord();
        loginRecord.setUuid(loginRecordDTO.getUuid());
        loginRecord.setUid(user.getId());
        loginRecord.setSessionId(sessionId);
        loginRecord.setIp(loginRecordDTO.getIpAddress());
        loginRecord.setRegion(loginRecordDTO.getRegion());
        loginRecord.setProvince(loginRecordDTO.getProvince());
        loginRecord.setCity(loginRecordDTO.getCity());
        loginRecord.setBrowser(loginRecordDTO.getBrowser());
        loginRecord.setOs(loginRecordDTO.getOs());
        loginRecord.setPlatform(loginRecordDTO.getPlatform());
        loginRecord.setLoginTime(loginRecordDTO.getLoginTime());
        loginRecord.setOnline(loginRecordDTO.getIsOnline());
        this.save(loginRecord);
    }

    @Override
    public void setRecordOffline(String sessionId) {
        UpdateWrapper<LoginRecord> loginRecordUpdateWrapper = new UpdateWrapper<>();
        loginRecordUpdateWrapper.eq("session_id", sessionId)
                .set("online", false);
        this.update(loginRecordUpdateWrapper);
    }

    @Override
    public boolean sessionForceLogout(String username, String uuid) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", username)
                .eq("deleted", false);
        User user = userService.getOne(userQueryWrapper);
        if ( user == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The user does not exist.");
        }
        QueryWrapper<LoginRecord> loginRecordQueryWrapper = new QueryWrapper<>();
        loginRecordQueryWrapper.eq("uuid", uuid);
        LoginRecord loginRecord = this.getOne(loginRecordQueryWrapper);
        if ( loginRecord == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The login record does not exist.");
        }
        if (
                !Objects.equals(loginRecord.getUid(), user.getId()) &&
                user.getRole() != AccountTypeConstant.SUPERADMIN.getAccountType()
        ) {
            throw new ParamValidateException(ResultStatusCodeConstant.FORBIDDEN.getResultCode(), "The login record is not yours.");
        }
        loginRecord.setOnline(false);
        boolean result = this.updateById(loginRecord);
        if ( result ) {
            log.info("Session force logout: {}", loginRecord.getSessionId());
            userService.logout(loginRecord.getSessionId());
            return true;
        }
        return false;
    }

    @Override
    public boolean userForceLogout(String username) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", username)
                .eq("deleted", false);
        User user = userService.getOne(userQueryWrapper);
        if ( user == null ) {
            throw new ParamValidateException(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), "The user does not exist.");
        }
        QueryWrapper<LoginRecord> loginRecordQueryWrapper = new QueryWrapper<>();
        loginRecordQueryWrapper.eq("uid", user.getId())
                .eq("online", true);
        List<LoginRecord> loginRecords = this.list(loginRecordQueryWrapper);
        UpdateWrapper<LoginRecord> loginRecordUpdateWrapper = new UpdateWrapper<>();
        loginRecordUpdateWrapper.eq("uid", user.getId())
                .eq("online", true)
                .set("online", false);
        if ( this.update(loginRecordUpdateWrapper) ) {
            log.info("User force logout: {}", username);
            for ( LoginRecord loginRecord : loginRecords ) {
                userService.logout(loginRecord.getSessionId());
            }
            return true;
        }
        return false;
    }
}
