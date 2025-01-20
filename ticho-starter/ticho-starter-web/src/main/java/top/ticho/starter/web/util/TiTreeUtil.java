package top.ticho.starter.web.util;


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
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 树工具
 *
 * @author zhajianjun
 * @date 2023-01-30 13:36
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TiTreeUtil {

    /**
     * 获取该id以及子节点id的集合
     *
     * @param data     所有的菜单集合
     * @param parentId 父id
     * @return {@link Set}<{@link Serializable}> 树节点
     */
    public static <T extends TiTreeNode<T>> Set<Serializable> getAllNodeIds(List<T> data, Serializable parentId) {
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
    private static <T extends TiTreeNode<T>> Map<Boolean, List<T>> group(List<T> data, Serializable parentId) {
        return data
            .stream()
            .collect(Collectors.groupingBy(x -> Objects.equals(x.getParentId(), parentId)));
    }

    /**
     * 树
     *
     * @param datas         数据
     * @param root          根节点数据
     * @param predicate     子节点过滤，第一个参数是父节点
     * @param consumer      父节点收集所有子节点后的处理
     * @param afterConsumer 父节点收集所有子节点后的处理
     */
    public static <T extends TiTreeNode<T>> void tree(List<T> datas, T root, BiPredicate<T, T> predicate, BiConsumer<T, T> consumer, Consumer<T> afterConsumer) {
        Objects.requireNonNull(datas);
        Objects.requireNonNull(root);
        Objects.requireNonNull(root.getId());
        Map<Boolean, List<T>> group = group(datas, root.getId());
        // 获取 root 下所有的儿子节点
        List<T> sons = Optional.ofNullable(group.get(Boolean.TRUE)).orElseGet(ArrayList::new);
        // 获取 root 下其它的子孙节点
        List<T> elseChilds = Optional.ofNullable(group.get(Boolean.FALSE)).orElseGet(ArrayList::new);
        root.setChildren(sons);
        root.setHasChildren(!sons.isEmpty());
        sons
            .stream()
            .filter(x -> predicate.test(root, x))
            .peek(x -> consumer.accept(root, x))
            .forEach(x -> tree(elseChilds, x, predicate, consumer, afterConsumer));
        if (afterConsumer == null) {
            return;
        }
        afterConsumer.accept(root);
    }

    /**
     * 树
     *
     * @param datas 数据
     * @param root  根节点数据
     */
    public static <T extends TiTreeNode<T>> void tree(List<T> datas, T root) {
        tree(datas, root, (x, y) -> true, (x, y) -> {}, (x) -> {});
    }

}
