package top.ticho.tool.core.ulid;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * 生成ULID的类。
 * <p>
 * 该生成器可以轻松创建两种类型的ULID，即单调递增和非单调递增。
 * <p>
 * 此外，还可以生成一种"非标准"的基于哈希的ULID，其中随机组件被SHA-256哈希的前10个字节替换。
 */
public final class UlidCreator {

    private UlidCreator() {
    }

    /**
     * 返回一个ULID。
     * <p>
     * 每生成一个新的ULID时，随机组件都会重置。
     *
     * @return 一个ULID
     */
    public static Ulid getUlid() {
        return UlidFactoryHolder.INSTANCE.create();
    }

    /**
     * 返回一个ULID。
     * <p>
     * 每生成一个新的ULID时，随机组件都会重置。
     *
     * @param time 当前时间（毫秒），从1970-01-01T00:00Z（UTC）的UNIX纪元开始计算
     * @return 一个ULID
     */
    public static Ulid getUlid(final long time) {
        return UlidFactoryHolder.INSTANCE.create(time);
    }

    /**
     * 返回一个单调递增ULID。
     * <p>
     * 在同一毫秒内生成的每个新ULID，其随机组件都会递增。
     *
     * @return 一个ULID
     */
    public static Ulid getMonotonicUlid() {
        return MonotonicFactoryHolder.INSTANCE.create();
    }

    /**
     * 返回一个单调递增ULID。
     * <p>
     * 在同一毫秒内生成的每个新ULID，其随机组件都会递增。
     *
     * @param time 当前时间（毫秒），从1970-01-01T00:00Z（UTC）的UNIX纪元开始计算
     * @return 一个ULID
     */
    public static Ulid getMonotonicUlid(final long time) {
        return MonotonicFactoryHolder.INSTANCE.create(time);
    }

    /**
     * 返回一个哈希ULID。
     * <p>
     * 随机组件被SHA-256哈希的前10个字节替换。
     * <p>
     * 对于特定的{time, string}对，它总是返回相同的ULID。
     * <p>
     * 使用示例：
     *
     * <pre>{@code
     * long time = file.getCreatedAt();
     * String name = file.getFileName();
     * Ulid ulid = UlidCreator.getHashUlid(time, name);
     * }</pre>
     *
     * @param time   时间（毫秒），从1970-01-01T00:00Z（UTC）的UNIX纪元开始计算
     * @param string 要使用SHA-256算法进行哈希的字符串
     * @return 一个ULID
     * @since 5.2.0
     */
    public static Ulid getHashUlid(final long time, String string) {
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        return getHashUlid(time, bytes);
    }

    /**
     * 返回一个哈希ULID。
     * <p>
     * 随机组件被SHA-256哈希的前10个字节替换。
     * <p>
     * 对于特定的{time, bytes}对，它总是返回相同的ULID。
     * <p>
     * 使用示例：
     *
     * <pre>{@code
     * long time = file.getCreatedAt();
     * byte[] bytes = file.getFileBinary();
     * Ulid ulid = UlidCreator.getHashUlid(time, bytes);
     * }</pre>
     *
     * @param time  时间（毫秒），从1970-01-01T00:00Z（UTC）的UNIX纪元开始计算
     * @param bytes 要使用SHA-256算法进行哈希的字节数组
     * @return 一个ULID
     * @since 5.2.0
     */
    public static Ulid getHashUlid(final long time, byte[] bytes) {
        // 计算哈希并取前10个字节
        byte[] hash = hasher().digest(bytes);
        byte[] rand = Arrays.copyOf(hash, 10);
        return new Ulid(time, rand);
    }

    private static MessageDigest hasher() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(String.format("%s not supported", "SHA-256"));
        }
    }

    private static class UlidFactoryHolder {
        static final UlidFactory INSTANCE = UlidFactory.newInstance();
    }

    private static class MonotonicFactoryHolder {
        static final UlidFactory INSTANCE = UlidFactory.newMonotonicInstance();
    }

}
