package top.ticho.tool.core;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 树节点
 *
 * @author zhajianjun
 * @date 2023-01-30 13:36
 */
@Data
public class TiTreeNode<T> {

    /** 编号 */
    private Serializable id;
    /** 父编号 */
    private Serializable parentId;
    /** 是否有子节点 */
    private Boolean hasChildren;
    /** 子节点数据 */
    private List<T> children;

}
