package com.dolly.instagramdemo.model;

// Pojo mimicking JSON hierarchy
public class Meta {

    private Integer code;
    private String error_type;
    private String error_message;

    public Integer getCode() {
        return code;
    }

    public String getErrorType() {
        return error_type;
    }

    public String getErrorMessage() {
        return error_message;
    }
}
