package com.southwind.vo;

import lombok.Data;

import java.util.List;

@Data
public class PageVO {
    private List<?> list;
    private long totalCount;  // 总记录数
    private long currPage;    // 当前页
    private long pageSize;    // 每页记录数
    private long totalPage;   // 总页数
}
