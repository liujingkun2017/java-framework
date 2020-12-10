package org.liujk.java.framework.base.api.response;

import com.alibaba.fastjson.annotation.JSONField;
import org.liujk.java.framework.base.api.SerializableObject;
import org.liujk.java.framework.base.utils.lang.StringUtils;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;


public class BaseResponse extends SerializableObject {
    protected static String SUCCESS_CODE = ResponseCode.SUCCESS_CODE;
    protected static String SUCCESS_MESSAGE = ResponseCode.SUCCESS_MESSAGE;

    /**
     * 表示接口返回的某种业务结果
     */
    private String resultCode = SUCCESS_CODE;

    /**
     * 接口返回的消息
     */
    private String resultMessage = SUCCESS_MESSAGE;

    /**
     * 设置 BaseResponse 默认 success 的 resultCode
     * @param successCode
     */
    public static void setSuccessCode (String successCode) {
        SUCCESS_CODE = successCode;
    }

    /**
     *  设置 BaseResponse 默认 success 的 successMessage
     * @param successMessage
     */
    public static void setSuccessMessage (String successMessage) {
        SUCCESS_MESSAGE = successMessage;
    }

    public BaseResponse () {
        setStatus (SUCCESS_CODE, SUCCESS_MESSAGE);
    }

    public BaseResponse (ResultCodeable resultCode) {
        setStatus (resultCode);
    }

    public BaseResponse (String resultCode, String resultMessage) {
        setStatus (resultCode, resultMessage);
    }

    public static BaseResponse success () {
        return new BaseResponse ();
    }

    public static BaseResponse newResponse (String code, String msg) {
        return new BaseResponse (code, msg);
    }

    public static BaseResponse newResponse (ResultCodeable resultCode) {
        return new BaseResponse (resultCode);
    }

    /**
     * 将结果置为成功
     */
    public BaseResponse setToSuccess () {
        setStatus (SUCCESS_CODE, SUCCESS_MESSAGE);
        return this;
    }

    /**
     * 将结果置为失败
     * <p>
     * 不建议使用该方法，因为虽然setToFail，但resultCode可能传入success；将在下一个版本删除
     *
     * @param resultCode
     *
     */
    @Deprecated
    @JSONField(deserialize = false)
    public void setToFail (ResultCodeable resultCode) {
        setStatus (resultCode);
    }

    /**
     * 设置result code
     *
     * @param resultCode
     */
    @JSONField(deserialize = false)
    public BaseResponse setStatus (@NotNull ResultCodeable resultCode) {
        Assert.notNull (resultCode, "入参[resultCode]不能为null");
        setStatus (resultCode.getCode (), resultCode.getMessage ());
        return this;
    }

    /**
     * 设置状态
     *
     * @param resultCode
     * @param resultMessage
     */
    @JSONField(deserialize = false)
    public BaseResponse setStatus (String resultCode, String resultMessage) {
        setResultCode (resultCode);
        setResultMessage (resultMessage);
        return this;
    }

    /**
     * 判断结果是否success (resultCode == 0000)
     *
     * @return
     */
    public boolean isSuccess () {
        return isEqualsCode (SUCCESS_CODE);
    }


    /**
     * 复制另一个Response的resultCode 和 resultMessage
     *
     * @param response
     */
    public <RESPONSE extends BaseResponse> BaseResponse copyResult (RESPONSE response) {
        Assert.notNull (response, "传入[response]不能为空");
        setResultCode (response.getResultCode ());
        setResultMessage (response.getResultMessage ());
        return this;
    }

    /**
     * 判断结果码是否相等
     *
     * @param resultCode
     *
     * @return
     */
    public boolean isEqualsCode (ResultCodeable resultCode) {
        return isEqualsCode (resultCode.getCode ());
    }


    /**
     * 判断resultCode是否存在于指定的列表中
     *
     * @param checkList 检查列表
     *
     * @return
     */
    public boolean isResultCodeIn (String... checkList) {
        if (StringUtils.isBlank (resultCode)) {
            return false;
        }
        for (String code : checkList) {
            if (resultCode.equalsIgnoreCase (code)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断resultCode是否存在于指定的列表中
     * 主要用于判断结果，同{@link BaseResponse#isResultCodeIn(String...)}
     *
     * @param checkList 检查列表
     *
     * @return
     */
    public boolean isResultCodeIn (ResultCodeable... checkList) {
        if (StringUtils.isBlank (resultCode)) {
            return false;
        }
        for (ResultCodeable code : checkList) {
            if (code != null && resultCode.equalsIgnoreCase (code.getCode ())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断结果码是否相等
     *
     * @param resultCode
     *
     * @return
     */
    public boolean isEqualsCode (String resultCode) {
        return this.resultCode == null ? false : this.resultCode.equals (resultCode);
    }

    public String getResultCode () {
        return resultCode;
    }

    public void setResultCode (String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage () {
        return resultMessage;
    }

    public void setResultMessage (String resultMessage) {
        this.resultMessage = resultMessage;
    }


    protected BaseResponse newInstance () {
        return new BaseResponse ();
    }

}
