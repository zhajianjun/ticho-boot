package com.ticho.boot.es.page;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-04-15 12:23:38
 */
@Data
public class PageInfo<T> {
    /** 索引 */
    private Set<String> indexs;
    /** 当前页 */
    private Integer pageNum;
    /** 记录大小 */
    private Integer pageSize;
    /** 总数 */
    private Long total = 0L;
    /** 数据 */
    private List<T> data = new ArrayList<>();
}

