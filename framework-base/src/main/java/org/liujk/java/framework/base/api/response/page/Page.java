package org.liujk.java.framework.base.api.response.page;



import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * 说明：
 * <p>
 * 分页对象
 *
 */
public class Page<E> implements Pageable {

    private List<E> currentPageResult;

    /**
     * 当前页码，从1开始
     */
    private int pageNumber = 1;
    /**
     * 分页大小 默认10
     */
    private int pageSize = 10;
    /**
     * 总数据量
     */
    private long totalCount;
    /**
     * 总页数
     */
    private int totalPages = 1;

    public Page () {
        super ();
    }

    public Page (int pageNumber, int pageSize) {
        setPageNumber (pageNumber);
        setPageSize (pageSize);
    }

    public Page (int pageNumber, int pageSize, long totalCount) {
        setPageNumber (pageNumber);
        setPageSize (pageSize);
    }

    public List<E> getCurrentPageResult () {
        if (currentPageResult == null) {
            currentPageResult = Lists.newArrayList ();
        }
        return currentPageResult;
    }

    public void setCurrentPageResult (List<E> currentPageResult) {
        this.currentPageResult = currentPageResult;
    }

    @JSONField(serialize = false)
    public boolean isEmpty () {
        return currentPageResult == null || currentPageResult.isEmpty ();
    }

    @Override
    public int getPageNumber () {
        return pageNumber;
    }

    @Override
    public void setPageNumber (int pageNumber) {
        this.pageNumber = pageNumber <= 0 ? 1 : pageNumber;
    }

    @Override
    public int getPageSize () {
        return pageSize;
    }

    @Override
    public void setPageSize (int pageSize) {
        this.pageSize = pageSize <= 0 ? DEFAULT_PAGE_SIZE : pageSize;
        calculatedTotalPages ();
    }

    public long getTotalCount () {
        return totalCount;
    }

    public void setTotalCount (long totalCount) {
        this.totalCount = totalCount;
        if (totalCount == -1) {
            return;
        }
        calculatedTotalPages ();
    }

    public int getTotalPages () {
        return totalPages;
    }

    /**
     * 不用设置，设置totalCount和pageSize时会触发计算
     *
     * @param totalPages
     */
    public void setTotalPages (int totalPages) {
        this.totalPages = totalPages;
    }

    /**
     * 计算总页数
     */
    private void calculatedTotalPages () {
        int pages = (int) (getTotalCount () / getPageSize ());
        if (pages * getPageSize () < getTotalCount ()) {
            pages++;
        }
        setTotalPages (pages);
    }

}
