package org.liujk.java.framework.base.api.response.page;

public interface Pageable {
    /**
     * 默认分页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 返回当前页码，从1开始
     *
     * @return
     */
    public int getPageNumber();

    /**
     * 设置当前页码，从1开始
     */
    public void setPageNumber(int pageNumber);

    /**
     * 返回分页大小
     *
     * @return
     */
    public int getPageSize();

    /**
     * 设置分页大小
     */
    public void setPageSize(int pageSize);
}
