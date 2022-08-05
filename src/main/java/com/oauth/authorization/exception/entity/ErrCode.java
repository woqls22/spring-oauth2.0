package com.oauth.authorization.exception.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrCode implements EnumModel {

    // COMMON
    BAD_REQUEST(4000, "C400", "BAD REQUEST"),
    FROBIDDEN(4003, "C403", "FORBIDDEN"),
    UNAUTHORIZED(4001, "C401", "UNAUTHORIZED"),
    REQUEST_TIME_OUT(4008, "C408", "REQUEST TIME OUT"),
    NOT_FOUND(4004, "C404", "NOT FOUND"),
    METHOD_NOT_ALLOWED(4005, "C405", "METHOD NOT ALLOWED"),
    BUSINESS_ERROR(5001, "C501", "UNKNOWN ERROR"),
    INVALID_KEY_ERROR(4010, "C410", "[CANNOT FIND] : INVALID KEY ERROR"),
    ALREADY_SIGN_UP(4020, "C420", "ALREADY SIGNED UP USER"),
    UNDEFINED_USER(4030, "C430", "[CANNOT FIND] USER NOT FOUND"),
    DUPLICATE_REQUEST(4002, "C4002", "DUPLICATE REQUEST ERROR");

    private final int status;
    private final String code;
    private final String message;

    ErrCode(int status, String code, String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }

    @Override
    public String getKey() {
        return this.code;
    }

    @Override
    public String getValue() {
        return this.message;
    }
}