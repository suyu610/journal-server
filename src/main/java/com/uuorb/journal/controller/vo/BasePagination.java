package com.uuorb.journal.controller.vo;

import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class BasePagination<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 7130845630609910943L;

    int totalCount;

    int totalPage;

    int pageSize;

    Boolean hasNextPage;

    int currentPage;

    T data;

    public static BasePagination createFromPageInfo(PageInfo pageInfo) {
        BasePagination basePagination = new BasePagination();
        basePagination.setCurrentPage(pageInfo.getPageNum());
        basePagination.setPageSize(pageInfo.getPageSize());
        basePagination.setTotalPage(pageInfo.getPages());
        basePagination.setHasNextPage(pageInfo.isHasNextPage());
        basePagination.setTotalCount((int) pageInfo.getTotal());
        basePagination.setData(pageInfo.getList());
        return basePagination;
    }
}
