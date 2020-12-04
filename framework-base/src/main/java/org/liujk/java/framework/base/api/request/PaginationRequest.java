package org.liujk.java.framework.base.api.request;


import org.liujk.java.framework.base.api.response.page.Pageable;

public class PaginationRequest extends BaseRequest implements Pageable {

    private int pageNumber = 1;

    private int pageSize = 10;

    public PaginationRequest () {

    }

    public PaginationRequest(int pageNumber, int pageSize) {
        setPageInfo(pageNumber, pageSize);
    }


    public void setPageInfo(int pageNumber, int pageSize) {
        setPageNumber(pageNumber);
        setPageSize(pageSize);
    }

    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    @Override
    public int getPageSize() {
        return pageNumber;
    }

    @Override
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
