package org.liujk.java.framework.base.api.response.page;


import com.alibaba.fastjson.annotation.JSONField;
import org.liujk.java.framework.base.api.response.DefaultResponse;
import org.liujk.java.framework.base.api.response.IResponseParser;
import org.liujk.java.framework.base.api.response.ListResponse;
import org.liujk.java.framework.base.api.response.ResultCodeable;

import java.util.ArrayList;
import java.util.List;

public class PaginationResponse<T> extends DefaultResponse<PaginationInfo<T>> {


    {
        //构造函数块，初始化Data
        setData (new PaginationInfo<T> ());
    }

    /**
     * 构造一个成功的返回对象
     */
    public PaginationResponse () {
    }


    /**
     * 构造一个成功的返回对象
     *
     * @param data
     */
    public PaginationResponse (List<T> data) {
        this ();
        setData (data);
    }

    public PaginationResponse (ResultCodeable resultCode) {
        super (resultCode);
    }

    public PaginationResponse (ResultCodeable resultCode, List<T> data) {
        this (resultCode);
        setData (data);
    }


    public PaginationResponse (String resultCode, String resultMessage) {
        super (resultCode, resultMessage);
    }

    public PaginationResponse (String resultCode, String resultMessage, List<T> data) {
        this (resultCode, resultMessage);
        setData (data);
    }

    public PaginationResponse (int pageNumber, int pageSize, long totalCount) {
        setPageInfo (pageNumber, pageSize, totalCount);
    }

    /**
     * 建议使用构造函数
     *
     * @param data
     * @param <T>
     *
     * @return
     * @see PaginationResponse( Object)
     */
    public static <T> PaginationResponse success (List<T> data) {
        return new PaginationResponse<T> (data);
    }

    public static PaginationResponse success () {
        return new PaginationResponse ();
    }

    public static PaginationResponse newResponse (String code, String msg) {
        return new PaginationResponse (code, msg);
    }

    public static PaginationResponse newResponse (ResultCodeable resultCode) {
        return new PaginationResponse (resultCode);
    }

    public void setPageInfo (int pageNumber, int pageSize, long totalCount) {
        getData ().setPageInfo (pageNumber, pageSize, totalCount);
    }

    @JSONField(serialize = false)
    public PaginationInfo<T> getPaginationInfo () {
        return getData ();
    }

    @Override
    public PaginationResponse setData (PaginationInfo<T> data) {
        super.setData (data);
        return this;
    }

    public PaginationResponse setData (List<T> data) {
        if (getData () == null) {
            super.setData (new PaginationInfo<T> ());
        }
        super.getData ().setCurrentPageResult (data);

        return this;
    }

    /**
     * 添加一条数据
     *
     * @param item
     */
    public PaginationResponse add (T item) {
        if (super.getData () == null) {
            super.setData (new PaginationInfo<T> ());
        }
        getData ().add (item);

        return this;
    }

    /**
     * 返回判断数据是否为空
     *
     * @return
     */

    @JSONField(serialize = false)
    public boolean isEmpty () {
        return getPaginationInfo () == null || getPaginationInfo ().isEmpty ();
    }

    /**
     * 返回当前分页结果里是否包含数据
     *
     * @return
     */
    @JSONField(serialize = false)
    public boolean hasData () {
        return !isEmpty ();
    }

    /**
     * 复制 除了Data以外的信息，包括resultCode，分页信息
     *
     * @param response
     */
    public PaginationResponse<T> copyResult (PaginationResponse response) {
        super.copyResult (response);
        PaginationInfo paginationInfo = response.getPaginationInfo ();
        setPageInfo (paginationInfo.getPageNumber (), paginationInfo.getPageSize (), paginationInfo.getTotalCount ());

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
    public static <ORIGIN, TARGET> PaginationResponse<TARGET> convertFrom (PaginationResponse<ORIGIN> origin,
                                                                           IResponseParser<ORIGIN, TARGET> parser) {
        PaginationResponse<TARGET> response = new PaginationResponse<TARGET> ();
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
    public static <ORIGIN, TARGET> PaginationResponse<TARGET> convertFrom (ListResponse<ORIGIN> origin,
                                                                           IResponseParser<ORIGIN, TARGET> parser) {
        PaginationResponse<TARGET> response = new PaginationResponse<TARGET> ();
        response.copyResult (origin);

        if (!origin.isEmpty ()) {
            convertFrom (origin.getData (), response, parser);
            response.getPaginationInfo ().setTotalCount (origin.getData ().size ());
        }
        return response;
    }
    public static <ORIGIN, TARGET> PaginationResponse<TARGET> convertFrom (PaginationInfo<ORIGIN> origin,
                                                                           IResponseParser<ORIGIN, TARGET> parser) {
        PaginationResponse<TARGET> response = new PaginationResponse<TARGET> ();
        response.setToSuccess ();

        if (!origin.isEmpty ()) {
            convertFrom (origin.getCurrentPageResult (), response, parser);
            response.getPaginationInfo ().copyPageInfo (origin);
        }
        return response;
    }

    private static <ORIGIN, TARGET> PaginationResponse<TARGET> convertFrom (List<ORIGIN> list,
                                                                            PaginationResponse<TARGET> response,
                                                                            IResponseParser<ORIGIN, TARGET> parser) {
        List<TARGET> data = new ArrayList<TARGET> (list.size ());
        for (int i = 0; i < list.size (); i++) {
            data.add (parser.parse (list.get (i)));
        }
        response.setData (data);
        return response;
    }
}
