package org.liujk.java.framework.base.enums;

public enum Status implements CodeMessageable {


    SUCCESS("success", "success"),
    FAIL("fail", "fail"),
    PROCESSING("processing", "processing"),

    ;

    private String code;

    private String message;

    Status(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
