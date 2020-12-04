package org.liujk.java.framework.base.api.response;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.collect.Lists;
import org.liujk.java.framework.base.api.response.page.PaginationResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * 说明：
 * <p>
 * 列表类返回结果
 *
 */
public class ListResponse<T> extends DefaultResponse<List<T>> {

    /**
     * 构造一个成功的返回对象
     */
    public ListResponse () {
        this (new ArrayList<T> ());
    }

    /**
     * 构造一个成功的返回对象
     *
     * @param data
     */
    public ListResponse (List<T> data) {
        super ();
        setData (data);
    }

    public ListResponse (ResultCodeable resultCode) {
        this (resultCode, new ArrayList<> ());
    }

    public ListResponse (ResultCodeable resultCode, List<T> data) {
        super (resultCode);
        setData (data);
    }

    public ListResponse (String resultCode, String resultMessage) {
        this (resultCode, resultMessage, new ArrayList<> ());
    }

    public ListResponse (String resultCode, String resultMessage, List<T> data) {
        super (resultCode, resultMessage);
        setData (data);
    }

    /**
     * 建议使用构造函数
     *
     * @param data
     * @param <T>
     *
     * @return
     * @see ListResponse( Object)
     */
    public static <T> ListResponse success (List<T> data) {
        return new ListResponse<T> (data);
    }

    public static ListResponse success () {
        return new ListResponse ();
    }

    public static ListResponse newResponse (String code, String msg) {
        return new ListResponse (code, msg);
    }

    public static ListResponse newResponse (ResultCodeable resultCode) {
        return new ListResponse (resultCode);
    }

    /**
     * 返回列表数量
     *
     * @return
     */
    public int getSize () {
        return getData () == null ? 0 : getData ().size ();
    }

    /**
     * 返回判断数据是否为空
     *
     * @return
     */
    @JSONField(serialize = false)
    public boolean isEmpty () {
        return getSize () == 0;
    }

    @Override
    public ListResponse<T> setToSuccess () {
        super.setToSuccess ();
        return this;
    }

    @Override
    public ListResponse<T> setStatus (ResultCodeable resultCode) {
        super.setStatus (resultCode);
        return this;
    }

    @Override
    public ListResponse<T> setStatus (String resultCode, String resultMessage) {
        super.setStatus (resultCode, resultMessage);
        return this;
    }

    @Override
    public <RESPONSE extends BaseResponse> ListResponse<T> copyResult (RESPONSE response) {
        super.copyResult (response);
        return this;
    }

    @Override
    public ListResponse<T> setData (List<T> data) {
        super.setData (data);
        return this;
    }

    /**
     * 添加一条数据
     *
     * @param item
     */
    public ListResponse<T> add (T item) {
        if (getData () == null) {
            setData (Lists.newArrayList ());
        }
        getData ().add (item);

        return this;
    }


    /**
     * 从一个 PaginationResponse 复制到 另一个 PaginationResponse
     *
     * @param origin   原PaginationResponse
     * @param parser   对象转换器
     * @param <ORIGIN> 原PaginationResponse 的对象类型
     * @param <TARGET> 目标PaginationResponse 的对象类型
     *
     * @return
     */
    public static <ORIGIN, TARGET> ListResponse<TARGET> convertFrom (PaginationResponse<ORIGIN> origin,
                                                                     IResponseParser<ORIGIN, TARGET> parser) {
        ListResponse<TARGET> response = new ListResponse<TARGET> ();
        response.copyResult (origin);

        if (!origin.isEmpty ()) {
            convertFrom (origin.getPaginationInfo ().getCurrentPageResult (), response, parser);
        }
        return response;
    }

    /**
     * 从一个 ListResponse 复制到 另一个 PaginationResponse
     *
     * @param origin   原ListResponse
     * @param parser   对象转换器
     * @param <ORIGIN> 原ListResponse 的对象类型
     * @param <TARGET> 目标PaginationResponse 的对象类型
     *
     * @return
     */
    public static <ORIGIN, TARGET> ListResponse<TARGET> convertFrom (ListResponse<ORIGIN> origin,
                                                                     IResponseParser<ORIGIN, TARGET> parser) {
        ListResponse<TARGET> response = new ListResponse<TARGET> ();
        response.copyResult (origin);

        if (!origin.isEmpty ()) {
            convertFrom (origin.getData (), response, parser);
        }
        return response;
    }

    private static <ORIGIN, TARGET> ListResponse<TARGET> convertFrom (List<ORIGIN> list, ListResponse<TARGET> response,
                                                                      IResponseParser<ORIGIN, TARGET> parser) {
        List<TARGET> data = new ArrayList<TARGET> (list.size ());
        for (int i = 0; i < list.size (); i++) {
            data.add (parser.parse (list.get (i)));
        }
        response.setData (data);
        return response;
    }
}
