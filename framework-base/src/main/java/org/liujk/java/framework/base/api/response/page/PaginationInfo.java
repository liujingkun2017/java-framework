package org.liujk.java.framework.base.api.response.page;


public class PaginationInfo<T> extends Page<T> implements Pageable {

    public void setPageInfo(int pageNumber, int pageSize, long totalCount) {
        setPageNumber(pageNumber);
        setPageSize(pageSize);
        setTotalCount(totalCount);
    }

    public void add(T item) {
        getCurrentPageResult().add(item);
    }

    /**
     * 复制分页信息(不复制数据)
     *
     * @param origin
     */
    public void copyPageInfo(PaginationInfo origin) {
        setTotalCount(origin.getTotalCount());
        setPageNumber(origin.getPageNumber());
        setPageSize(origin.getPageSize());
    }
}
