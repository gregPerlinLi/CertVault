package com.gregperlinli.certvault.utils;

import com.gregperlinli.certvault.constant.GeneralConstant;
import com.gregperlinli.certvault.constant.ResultStatusCodeConstant;
import com.gregperlinli.certvault.domain.exception.*;
import com.gregperlinli.certvault.domain.vo.ResultVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;
import java.security.NoSuchAlgorithmException;

/**
 * 全局异常处理器
 *
 * @author gregPerlinLi
 * @version 1.0.0
 * @className GlobalExceptionHandler
 * @date 2024/1/31 16:49
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ResultVO<Void> exceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception e) throws Exception {
        String msg = e.getMessage();
        if ( e instanceof LoginException) {
            // response.setStatus(ResultStatusCodeConstant.FORBIDDEN);
            response.setHeader(GeneralConstant.STATUS_CODE.getValue(), String.valueOf(ResultStatusCodeConstant.UNAUTHORIZED.getResultCode()));
            return new ResultVO<>(((LoginException) e).getCode(), msg);
        }
        if ( e instanceof OAuth2AuthorizationException ) {
            // response.setStatus(ResultStatus
            response.setHeader(GeneralConstant.STATUS_CODE.getValue(), String.valueOf(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode()));
            return new ResultVO<>(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(), msg);
        }
        if ( e instanceof PermissionException) {
            // response.setStatus(ResultStatusCodeConstant.FORBIDDEN);
            response.setHeader(GeneralConstant.STATUS_CODE.getValue(), String.valueOf(ResultStatusCodeConstant.FORBIDDEN.getResultCode()));
            return new ResultVO<>(((PermissionException) e).getCode(), msg);
        }
        if ( e instanceof MissingServletRequestParameterException ) {
            // response.setStatus(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION);
            response.setHeader(GeneralConstant.STATUS_CODE.getValue(), String.valueOf(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode()));
            return new ResultVO<>(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(), msg);
        }
        if ( e instanceof ParamValidateException) {
            // response.setStatus(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION);
            response.setHeader(GeneralConstant.STATUS_CODE.getValue(), String.valueOf(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode()));
            return new ResultVO<>(((ParamValidateException) e).getCode(), msg);
        }
        if ( e instanceof DuplicateKeyException ) {
            // response.setStatus(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION);
            response.setHeader(GeneralConstant.STATUS_CODE.getValue(), String.valueOf(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode()));
            if ( msg.contains("for key 'user.email'") ) {
                return new ResultVO<>(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(), "The email is already existed!");
            } else if ( msg.contains("for key 'user.username'") ) {
                return new ResultVO<>(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(), "The username is already existed!");
            }
            return new ResultVO<>(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(), "Duplicate entry");
        }
        if ( e instanceof NoSuchAlgorithmException) {
            // response.setStatus(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION);
            response.setHeader(GeneralConstant.STATUS_CODE.getValue(), String.valueOf(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode()));
            return new ResultVO<>(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(), msg);
        }
        if ( e instanceof CertGenException ) {
            // response.setStatus(ResultStatusCodeConstant.BUSINESS_EXCEPTION);
            response.setHeader(GeneralConstant.STATUS_CODE.getValue(), String.valueOf(ResultStatusCodeConstant.BUSINESS_EXCEPTION.getResultCode()));
            return new ResultVO<>(((CertGenException) e).getCode(), msg);
        }
        if ( e instanceof BusinessException) {
            // response.setStatus(ResultStatusCodeConstant.BUSINESS_EXCEPTION);
            response.setHeader(GeneralConstant.STATUS_CODE.getValue(), String.valueOf(ResultStatusCodeConstant.BUSINESS_EXCEPTION.getResultCode()));
            return new ResultVO<>(((BusinessException) e).getCode(), msg);
        }
        if ( e instanceof NoResourceFoundException) {
            // response.setStatus(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode());
            if ( !request.getRequestURI().startsWith("/api/") ) {
                request.getRequestDispatcher("/index.html").forward(request, response);
                return null;
            }
            response.setHeader(GeneralConstant.STATUS_CODE.getValue(), String.valueOf(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode()));
            return new ResultVO<>(ResultStatusCodeConstant.PAGE_NOT_FIND.getResultCode(), msg);
        }
        if ( e instanceof MethodNotAllowedException || e instanceof NoSuchMethodException || e instanceof HttpRequestMethodNotSupportedException) {
            // response.setStatus(ResultStatusCodeConstant.FORBIDDEN);
            response.setHeader(GeneralConstant.STATUS_CODE.getValue(), String.valueOf(ResultStatusCodeConstant.METHOD_NOT_ALLOWED.getResultCode()));
            return new ResultVO<>(ResultStatusCodeConstant.METHOD_NOT_ALLOWED.getResultCode(), msg);
        }
        if ( e instanceof HttpMessageNotReadableException ) {
            // response.setStatus(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION);
            response.setHeader(GeneralConstant.STATUS_CODE.getValue(), String.valueOf(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode()));
            return new ResultVO<>(ResultStatusCodeConstant.PARAM_VALIDATE_EXCEPTION.getResultCode(), msg);
        }
        if ( e instanceof ServerException) {
            // response.setStatus(ResultStatusCodeConstant.SERVER_ERROR);
            response.setHeader(GeneralConstant.STATUS_CODE.getValue(), String.valueOf(ResultStatusCodeConstant.SERVER_ERROR.getResultCode()));
            return new ResultVO<>(((ServerException) e).getCode(), msg);
        }
        if ( e instanceof IllegalStateException && msg.contains("Session already invalidated") ) {
            // response.setStatus(ResultStatusCodeConstant.UNAUTHORIZED);
            response.setHeader(GeneralConstant.STATUS_CODE.getValue(), String.valueOf(ResultStatusCodeConstant.UNAUTHORIZED.getResultCode()));
            return new ResultVO<>(ResultStatusCodeConstant.UNAUTHORIZED.getResultCode(), msg);
        }
        // 认证异常需要再往上丢给 SpringSecurity 专门处理
        if ( e instanceof AuthenticationException || e instanceof AccessDeniedException || e instanceof AuthorizationDeniedException) {
            throw e;
        }
        // response.setStatus(ResultStatusCodeConstant.SERVER_ERROR);
        log.error("Uncaught Internal Server Error: {}", e.getMessage());
        log.error("Error Stack Trace: ", e);
        response.setHeader(GeneralConstant.STATUS_CODE.getValue(), String.valueOf(ResultStatusCodeConstant.SERVER_ERROR.getResultCode()));
        return new ResultVO<>(ResultStatusCodeConstant.SERVER_ERROR.getResultCode(), "Uncaught Internal Server Error");
    }

}
