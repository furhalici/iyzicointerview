package com.iyzico.challenge.exception;

public class IyzicoPaymentException extends RuntimeException{
    private String errorCode;
    private String errorGroup;

    public IyzicoPaymentException(String errorCode, String errorMessage, String errorGroup) {
        super(errorCode+" "+errorGroup+" /"+errorMessage);
        this.errorCode = errorCode;
        this.errorGroup = errorGroup;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorGroup() {
        return errorGroup;
    }
}
