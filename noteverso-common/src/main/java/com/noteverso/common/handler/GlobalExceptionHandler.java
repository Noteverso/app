package com.noteverso.common.handler;

import com.noteverso.common.api.ApiCode;
import com.noteverso.common.api.model.ErrorDetail;
import com.noteverso.common.api.model.ErrorMessageType;
import com.noteverso.common.api.model.ErrorResponse;
import com.noteverso.common.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ErrorResponse> exceptionHandler(Exception exception) {
        return serverErrorResponse(ApiCode.SYSTEM_EXCEPTION, exception);
    }

    private ResponseEntity<ErrorResponse> serverErrorResponse(ApiCode apiCode, Exception exception) {
        String message = apiCode.getMessage();
        // 服务端异常需要记录日志
        log.error(message, exception);
        // 服务端异常使用api code 中的 message，避免敏感信息发送到客户端
        return new ResponseEntity<>(errorResponse(apiCode, ErrorMessageType.API_CODE, exception), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> requestErrorResponse(ApiCode code, Exception exception) {
        return requestErrorResponse(code, exception, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ErrorResponse> requestErrorResponse(ApiCode code, Exception exception, HttpStatus httpStatus) {
        String message = code.getMessage();
        ErrorMessageType errorMessageType = ErrorMessageType.API_CODE;
        // 客户端请求错误只记录debug日志
        if (log.isDebugEnabled()) {
            log.debug(message, exception);
            // 开启调试，客户端异常使用异常中的message
            errorMessageType = ErrorMessageType.EXCEPTION;
        }
        return new ResponseEntity<>(errorResponse(code, errorMessageType, exception), httpStatus);
    }

    private ErrorResponse errorResponse(ApiCode code, ErrorMessageType messageType, Exception exception) {
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setCode(code.getCode());
        if (messageType.equals(ErrorMessageType.API_CODE) || StringUtils.isBlank(exception.getMessage())) {
            errorDetail.setMessage(code.getMessage());
        } else {
            errorDetail.setMessage(exception.getMessage());
        }
        errorDetail.setType(exception.getClass().getSimpleName());

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError(errorDetail);
        return errorResponse;
    }

    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity<ErrorResponse> businessExceptionHandler(BusinessException exception) {
        return requestErrorResponse(ApiCode.BUSINESS_EXCEPTION, exception);
    }

    @ExceptionHandler(value = NoSuchDataException.class)
    public ResponseEntity<ErrorResponse> noSuchDataExceptionHandler(NoSuchDataException exception) {
        return requestErrorResponse(ApiCode.NOT_FOUND, exception, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = DaoException.class)
    public ResponseEntity<ErrorResponse> daoExceptionHandler(DaoException exception) {
        return serverErrorResponse(ApiCode.DAO_EXCEPTION, exception);
    }

    @ExceptionHandler(value = NoPermissionException.class)
    public ResponseEntity<ErrorResponse> noPermissionExceptionHandler(NoPermissionException exception) {
        return requestErrorResponse(ApiCode.FORBIDDEN, exception, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = UnAuthorizedException.class)
    public ResponseEntity<ErrorResponse> unAuthorizedExceptionHandler(NoPermissionException exception) {
        return requestErrorResponse(ApiCode.UNAUTHORIZED, exception, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = RequestVerifyException.class)
    public ResponseEntity<ErrorResponse> requestVerifyExceptionHandler(RequestVerifyException exception) {
        return requestErrorResponse(ApiCode.PARAMETER_EXCEPTION, exception);
    }

    /**
     * handle spring valid exception
     */
    @ExceptionHandler(value = BindException.class)
    public ResponseEntity<ErrorResponse> bindExceptionHandler(BindException exception) {
        return requestErrorResponse(ApiCode.PARAMETER_EXCEPTION, exception);
    }

    @ExceptionHandler(value = ServletRequestBindingException.class)
    public ResponseEntity<ErrorResponse> servletRequestBindingExceptionHandler(ServletRequestBindingException exception) {
        return requestErrorResponse(ApiCode.PARAMETER_EXCEPTION, exception);
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> badCredentialsExceptionHandler(BadCredentialsException exception) {
        return requestErrorResponse(ApiCode.UNAUTHORIZED, exception, HttpStatus.UNAUTHORIZED);
    }


//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<?> handlerValidationExceptions(MethodArgumentNotValidException ex) {
//        List<String> errors = ex.getBindingResult()
//            .getFieldErrors()
//            .stream()
//            .map(FieldError::getDefaultMessage)
//            .toList();
//
//        return ResponseEntity.badRequest().body(errors);
//    }
}
