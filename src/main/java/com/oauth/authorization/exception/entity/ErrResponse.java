package com.oauth.authorization.exception.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class ErrResponse {
    private String message;
    private int status;
    private String errCode;
    private String detail;
    private UUID traceId;

    public ErrResponse(ErrCode code, Exception err) {
        this.message = code.getMessage();
        this.status = code.getStatus();
        this.errCode = code.getCode();
        this.detail = err.getMessage();
        this.traceId = UUID.randomUUID();
    }

    public static ErrResponse of(ErrCode code, Exception err) {
        return new ErrResponse(code, err);
    }
}