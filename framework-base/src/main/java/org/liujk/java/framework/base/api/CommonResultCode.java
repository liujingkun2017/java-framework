package org.liujk.java.framework.base.api;

import org.liujk.java.framework.base.api.response.ResponseCode;
import org.liujk.java.framework.base.api.response.ResultCodeable;

public enum CommonResultCode implements ResultCodeable {


    // 公共类
    /**
     * 执行成功
     */
    EXECUTE_SUCCESS (ResponseCode.SUCCESS_CODE, ResponseCode.SUCCESS_MESSAGE),

    PROMPT_CONFIRMATION_BOX ("0001", "提示信息，前端弹出确认框"),

    // 无效请求，请刷新页面后重试
    EXECUTE_FAIL ("9000", "处理失败"),

    // 9100 验证类错误编码
    // 参数校验错误
    ILLEGAL_ARGUMENT ("9100", "参数校验错误"),

    ARGUMENT_NOT_BE_NULL ("9101", "参数不能为空"),
    // 参数[xxx]格式错误
    ILLEGAL_ARGUMENT_TYPE ("9102", "参数格式错误"),
    // 参数[xxx]内容错误
    ILLEGAL_ARGUMENT_CONTENT ("9102", "参数内容错误"),


    // 无效请求，请刷新页面后重试
    INVALID_AND_RETRY ("9001", "无效请求，请刷新页面后重试"),

    // 主键重复/唯一键重复
    DUPLICATE_KEY ("9900", "主键重复"),

    // 数据不存在
    DATA_NOT_EXISTS ("9901", "数据不存在"),

    // 数据不存在
    NO_DATA_IS_UPDATED ("9902", "没有更新到数据"),

    // 数据不存在
    DATA_HAS_BEEN_MODIFIED ("9903", "数据已被其他用户修改，请刷新后再试"),

    // CSRF 验证异常
    CSRF_DENIED ("9981", "CSRF校验拒绝"),

    // 业务异常(业务逻辑错误，比如参数错误等，但不能细化的错误)
    CANNOT_FOUND_SERVICE ("9993", "服务异常"),

    // 业务异常(业务逻辑错误，比如参数错误等，但不能细化的错误)
    NET_EXCEPTION ("9994", "网络异常"),

    // 业务异常(业务逻辑错误，比如参数错误等，但不能细化的错误)
    BIZ_EXCEPTION ("9995", "业务异常"),

    // 数据库操作异常
    DATEBASE_EXCEPTION ("9996", "数据库操作异常"),

    // 内部异常(系统内部异常，比如空指针等，不能归结于业务的错误)
    NEST_EXCEPTION ("9997", "内部异常"),

    // 系统异常(系统内部异常，比如空指针等，不能归结于业务的错误)
    SYS_EXCEPTION ("9998", "系统异常"),

    // 系统未知异常
    UNKNOWN_EXCEPTION (ResponseCode.UNKNOWN_EXCEPTION, "系统未知异常"),

    ;


    private String code;
    private String message;

    CommonResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return null;
    }

    @Override
    public String getMessage() {
        return null;
    }
}
