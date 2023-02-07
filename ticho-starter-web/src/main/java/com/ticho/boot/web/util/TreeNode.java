package com.ticho.boot.web.util;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "树节点")
public class TreeNode<T> {

    @ApiModelProperty(value = "编号")
    private Serializable id;

    @ApiModelProperty(value = "父编号")
    private Serializable parentId;

    @ApiModelProperty(value = "是否有子节点")
    private Boolean hasChildren = false;

    @ApiModelProperty(value = "子节点数据")
    private List<T> children;

}
