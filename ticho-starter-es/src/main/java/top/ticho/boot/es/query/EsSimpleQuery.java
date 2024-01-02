package top.ticho.boot.es.query;

import lombok.Data;

import java.util.List;

/**
 * es 查询条件
 *
 * @author zhajianjun
 * @date 2023-04-15 12:18:22
 */
@Data
public class EsSimpleQuery {
    public static final int DEFAULT_PAGE_SIZE = 20;

    public static final int DEFAULT_MAX_PAGE_SIZE = 10000;

    /** 索引 */
    private List<String> indexs;
    /** 起始行 */
    private Integer from;
    /** 记录大小 */
    private Integer size;
    /** 需要显示的字段，逗号分隔（缺省为全部字段） */
    private List<String> fields;
    /** 排序字段 */
    private List<String> sortFields;
    /** 升序还是降序，和sortField一一对应，默认asc */
    private List<String> sortOrders;
    /** 高亮字段 */
    private String highlightField;
    /** 游标查询 */
    private List<String> searchAfter;


    /**
     * size 必须先正确赋值才能进行setPageNum赋值操作
     *
     * @param pageNum pageNum
     */
    public void setPageNum(Integer pageNum) {
        // 注意第一页是 from = 0 开始的
        if (pageNum == null || pageNum <= 1) {
            this.from = 0;
            return;
        }
        if (size == null || size < 0) {
            // size 必须先正确赋值才能进行setPageNum赋值操作
            throw new IllegalArgumentException("size is not illeagal");
        }
        this.from = (pageNum - 1) * size;
    }

    public void setPageSize(Integer pageSize) {
        if (this.size == null || this.size < 0) {
            this.size = DEFAULT_PAGE_SIZE;
        }
        this.size = pageSize;
    }

    public Integer getPageNum() {
        if (from == null || from <= 0) {
            return 1;
        }
        return this.from / size + 1;
    }

    public Integer getPageSize() {
        if (this.size == null || this.size < 0) {
            this.size = DEFAULT_MAX_PAGE_SIZE;
        }
        return size;
    }

    public void checkPage() {
        if (this.from == null || this.from < 0) {
            this.from = 0;
        }
        if (this.size == null || this.size < 0) {
            this.size = DEFAULT_PAGE_SIZE;
        }
    }

}
