package top.ticho.starter.transform.annotation;

/**
 * @author zhajianjun
 * @date 2025-03-21 21:44
 */
public interface TiRendering<T, R> {

    R render(T object, String[] params);

    default void errorHandle(Exception e, T object, String[] params) {

    }

    default R defaultValue() {
        return null;
    }

}
