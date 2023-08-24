package com.ticho.boot.web.util;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 树工具
 *
 * @author zhajianjun
 * @date 2023-01-30 13:36
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TreeUtil {


    /**
     * 获取树节点
     *
     * @param data     所有的菜单集合
     * @param parentId 父id
     * @return {@link List}<{@link T}> 树节点
     */
    public static <T extends TreeNode<T>> List<T> tree(List<T> data, Serializable parentId) {
        if (Objects.isNull(data) || Objects.isNull(parentId)) {
            return new ArrayList<>();
        }
        Map<Boolean, List<T>> group = group(data, parentId);
        // 获取 parentId 下所有的根节点
        List<T> roots = Optional.ofNullable(group.get(Boolean.TRUE)).orElseGet(ArrayList::new);
        // 获取 parentId 下所有的非根节点
        List<T> childs = Optional.ofNullable(group.get(Boolean.FALSE)).orElseGet(ArrayList::new);
        // 遍历 root
        for (T root : roots) {
            if (Objects.isNull(root.getChildren())) {
                root.setChildren(new ArrayList<>());
            }
            // 获取子节点的子节点，递归
            List<T> rootChilds = tree(childs, root.getId());
            if (!rootChilds.isEmpty()) {
                root.setHasChildren(true);
            }
            root.getChildren().addAll(rootChilds);
        }
        return roots;
    }

    /**
     * 获取该id以及子节点id的集合
     *
     * @param data     所有的菜单集合
     * @param parentId 父id
     * @return {@link Set}<{@link Serializable}> 树节点
     */
    public static <T extends TreeNode<T>> Set<Serializable> getAllNodeIds(List<T> data, Serializable parentId) {
        Set<Serializable> ids = new HashSet<>();
        ids.add(parentId);
        if (Objects.isNull(data) || Objects.isNull(parentId)) {
            return ids;
        }
        Map<Boolean, List<T>> group = group(data, parentId);
        // 获取 parentId 下所有的根节点
        List<T> roots = Optional.ofNullable(group.get(Boolean.TRUE)).orElseGet(ArrayList::new);
        // 获取 parentId 下所有的非根节点
        List<T> childs = Optional.ofNullable(group.get(Boolean.FALSE)).orElseGet(ArrayList::new);
        // 遍历 root
        for (T root : roots) {
            // 获取子节点的子节点，递归
            ids.addAll(getAllNodeIds(childs, root.getId()));
        }
        return ids;
    }

    /**
     * 集团
     *
     * @param data     数据
     * @param parentId 父id
     * @return {@link Map}<{@link Boolean}, {@link List}<{@link T}>>
     */
    private static <T extends TreeNode<T>> Map<Boolean, List<T>> group(List<T> data, Serializable parentId) {
        // @formatter:off
        return data
            .stream()
            .collect(Collectors.groupingBy(x-> Objects.equals(x.getParentId(), parentId)));
        // @formatter:on
    }

    /**
     * 树
     *
     * @param data          数据
     * @param root          根节点数据
     * @param filter        子节点过滤
     * @param afterConsumer 父节点收集所有子节点后的处理
     */
    public static <T extends TreeNode<T>> void tree(List<T> data, T root, Predicate<T> filter, Consumer<T> afterConsumer) {
        // @formatter:off
        if (Objects.isNull(data) || Objects.isNull(root)) {
            return;
        }
        Map<Boolean, List<T>> group = group(data, root.getId());
        // 获取 root 下所有的节点
        List<T> nodes = Optional.ofNullable(group.get(Boolean.TRUE)).orElseGet(ArrayList::new);
        // 获取 非root 下所有的节点
        List<T> nodeChilds = Optional.ofNullable(group.get(Boolean.FALSE)).orElseGet(ArrayList::new);
        root.setChildren(nodes);
        filter = Optional.ofNullable(filter).orElseGet(() -> (x) -> true);
        // 遍历 root
        Predicate<T> finalFilter = filter;
        nodes
            .stream()
            .filter(filter)
            .forEach(x -> tree(nodeChilds, x, finalFilter, afterConsumer));
        // @formatter:on
        if (afterConsumer == null) {
            return;
        }
        afterConsumer.accept(root);
    }

}
