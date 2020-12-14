package org.liujk.java.framework.boot.starter.redis.message;


import org.liujk.java.framework.base.api.SerializableObject;

public class RedisMessage extends SerializableObject {
    public static final String BODY_TYPE_JSON = "json";
    public static final String BODY_TYPE_STRING = "string";
    public static final String BODY_TYPE_OBJECT = "object";

    private String msgId;               //消息ID

    private Long timestamp;             //时间戳

    private Object body;                //消息体

    private String bodyType;            //消息体类型

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public String getBodyType() {
        return bodyType;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }
}
