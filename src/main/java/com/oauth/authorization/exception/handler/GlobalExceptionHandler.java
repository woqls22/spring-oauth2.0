package com.oauth.authorization.exception.handler;


import com.oauth.authorization.exception.entity.ErrCode;
import com.oauth.authorization.exception.entity.ErrResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.security.InvalidParameterException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<ErrResponse> handleAuthException(Exception err){
        ErrResponse error = ErrResponse.of(ErrCode.UNAUTHORIZED, err);
        log.error(String.format("Unauthorized Error [%s]", error.getTraceId()), err);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
}