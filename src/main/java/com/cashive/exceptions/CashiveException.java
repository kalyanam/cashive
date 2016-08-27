package com.cashive.exceptions;

/**
 * Created by mkalyan on 8/27/16.
 */
public class CashiveException extends RuntimeException {
    private ErrorCode errorCode = ErrorCode.UNKNOWN;

    public CashiveException() {
        super();
    }

    public CashiveException(String message) {
        super(message);
    }

    public CashiveException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return this.errorCode;
    }
}
