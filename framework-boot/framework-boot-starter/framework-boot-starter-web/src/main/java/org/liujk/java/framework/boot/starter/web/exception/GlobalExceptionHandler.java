package org.liujk.java.framework.boot.starter.web.exception;

import org.liujk.java.framework.base.api.CommonResultCode;
import org.liujk.java.framework.base.api.response.DefaultResponse;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@ControllerAdvice
public class GlobalExceptionHandler implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(value = HttpStatus.OK)
    public Object badRequest(HandlerMethod handlerMethod,
                             HttpServletRequest request,
                             HttpServletResponse response,
                             Exception exception) {
        DefaultResponse<String> defaultResponse = new DefaultResponse<>();
        defaultResponse.setStatus(CommonResultCode.UNKNOWN_EXCEPTION);
        defaultResponse.setData(exception.getMessage());
        return defaultResponse;
    }

}
