package top.ticho.boot.view.core;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * es分页对象
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class TiEsPageResult<T> extends TiPageResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 索引列表 */
    private Set<String> indexs;

    public TiEsPageResult(Number pageNum, Number pageSize, Number total, Collection<String> indexs, List<T> rows) {
        super(pageNum, pageSize, total, rows);
        this.indexs = Optional.ofNullable(indexs)
            .map(HashSet::new)
            .orElseGet(HashSet::new);
    }

}
