package top.ticho.tool.core.ulid;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntFunction;
import java.util.function.LongFunction;
import java.util.function.LongSupplier;

/**
 * 实际生成ULID的类。
 * <p>
 * 此类由 {@link UlidCreator} 使用。
 * <p>
 * 如果您需要使用特定的随机生成器策略，可以使用此类。
 * 但大多数人只需要 {@link UlidCreator}。
 * <p>
 * 此类的实例可以以两种方式之一运行：单调递增或非单调递增（默认）。
 * <p>
 * 如果工厂是单调递增的，则在同一毫秒内生成多个ULID时，随机组件会递增1。
 * <p>
 * 每毫秒可以生成的ULID最大数量为2^80。
 */
public final class UlidFactory {

    private final LongSupplier timeFunction;
    private final LongFunction<Ulid> ulidFunction;
    private final ReentrantLock lock = new ReentrantLock();

    // ******************************
    // 构造函数
    // ******************************

    /**
     * 默认构造函数。
     */
    public UlidFactory() {
        this(new UlidFunction());
    }

    private UlidFactory(LongFunction<Ulid> ulidFunction) {
        this(ulidFunction, System::currentTimeMillis);
    }

    private UlidFactory(LongFunction<Ulid> ulidFunction, LongSupplier timeFunction) {

        Objects.requireNonNull(ulidFunction, "ULID函数不能为空");
        Objects.requireNonNull(timeFunction, "时间函数不能为空");

        this.ulidFunction = ulidFunction;
        this.timeFunction = timeFunction;

        if (this.ulidFunction instanceof MonotonicFunction) {
            // 初始化单调函数的内部状态
            ((MonotonicFunction) this.ulidFunction).initialize(this.timeFunction);
        }
    }

    /**
     * 返回一个新的工厂实例。
     * <p>
     * 等同于默认构造函数 {@code new UlidFactory()}。
     *
     * @return {@link UlidFactory}
     */
    public static UlidFactory newInstance() {
        return new UlidFactory(new UlidFunction());
    }

    /**
     * 返回一个新的工厂实例。
     *
     * @param random 一个 {@link Random} 生成器
     * @return {@link UlidFactory}
     */
    public static UlidFactory newInstance(Random random) {
        return new UlidFactory(new UlidFunction(random));
    }

    /**
     * 返回一个新的工厂实例。
     * <p>
     * 给定的随机函数必须返回一个long值。
     *
     * @param randomFunction 返回long值的随机函数
     * @return {@link UlidFactory}
     */
    public static UlidFactory newInstance(LongSupplier randomFunction) {
        return new UlidFactory(new UlidFunction(randomFunction));
    }

    /**
     * 返回一个新的工厂实例。
     * <p>
     * 给定的随机函数必须返回一个字节数组。
     *
     * @param randomFunction 返回字节数组的随机函数
     * @return {@link UlidFactory}
     */
    public static UlidFactory newInstance(IntFunction<byte[]> randomFunction) {
        return new UlidFactory(new UlidFunction(randomFunction));
    }

    /**
     * 返回一个新的单调递增工厂实例。
     *
     * @return {@link UlidFactory}
     */
    public static UlidFactory newMonotonicInstance() {
        return new UlidFactory(new MonotonicFunction());
    }

    /**
     * 返回一个新的单调递增工厂实例。
     *
     * @param random 一个 {@link Random} 生成器
     * @return {@link UlidFactory}
     */
    public static UlidFactory newMonotonicInstance(Random random) {
        return new UlidFactory(new MonotonicFunction(random));
    }

    /**
     * 返回一个新的单调递增工厂实例。
     * <p>
     * 给定的随机函数必须返回一个long值。
     *
     * @param randomFunction 返回long值的随机函数
     * @return {@link UlidFactory}
     */
    public static UlidFactory newMonotonicInstance(LongSupplier randomFunction) {
        return new UlidFactory(new MonotonicFunction(randomFunction));
    }

    /**
     * 返回一个新的单调递增工厂实例。
     * <p>
     * 给定的随机函数必须返回一个字节数组。
     *
     * @param randomFunction 返回字节数组的随机函数
     * @return {@link UlidFactory}
     */
    public static UlidFactory newMonotonicInstance(IntFunction<byte[]> randomFunction) {
        return new UlidFactory(new MonotonicFunction(randomFunction));
    }

    /**
     * 返回一个新的单调递增工厂实例。
     *
     * @param random       一个 {@link Random} 生成器
     * @param timeFunction 返回当前时间（以毫秒为单位）的函数，
     *                     从1970-01-01T00:00Z (UTC) UNIX纪元开始计算
     * @return {@link UlidFactory}
     */
    public static UlidFactory newMonotonicInstance(Random random, LongSupplier timeFunction) {
        return new UlidFactory(new MonotonicFunction(random), timeFunction);
    }

    /**
     * 返回一个新的单调递增工厂实例。
     * <p>
     * 给定的随机函数必须返回一个long值。
     *
     * @param randomFunction 返回long值的随机函数
     * @param timeFunction   返回当前时间（以毫秒为单位）的函数，
     *                       从1970-01-01T00:00Z (UTC) UNIX纪元开始计算
     * @return {@link UlidFactory}
     */
    public static UlidFactory newMonotonicInstance(LongSupplier randomFunction, LongSupplier timeFunction) {
        return new UlidFactory(new MonotonicFunction(randomFunction), timeFunction);
    }

    /**
     * 返回一个新的单调递增工厂实例。
     * <p>
     * 给定的随机函数必须返回一个字节数组。
     *
     * @param randomFunction 返回字节数组的随机函数
     * @param timeFunction   返回当前时间（以毫秒为单位）的函数，
     *                       从1970-01-01T00:00Z (UTC) UNIX纪元开始计算
     * @return {@link UlidFactory}
     */
    public static UlidFactory newMonotonicInstance(IntFunction<byte[]> randomFunction, LongSupplier timeFunction) {
        return new UlidFactory(new MonotonicFunction(randomFunction), timeFunction);
    }

    // ******************************
    // 公共方法
    // ******************************

    /**
     * 返回一个新的ULID。
     *
     * @return 一个ULID
     */
    public Ulid create() {
        return create(timeFunction.getAsLong());
    }

    /**
     * 返回一个新的ULID。
     *
     * @param time 当前时间（以毫秒为单位），从1970-01-01T00:00Z (UTC) UNIX纪元开始计算
     * @return 一个ULID
     */
    public Ulid create(final long time) {
        lock.lock();
        try {
            return this.ulidFunction.apply(time);
        } finally {
            lock.unlock();
        }
    }

    // ******************************
    // 包私有内部类
    // ******************************

    /**
     * 创建ULID的函数。
     */
    record UlidFunction(IRandom random) implements LongFunction<Ulid> {

        public UlidFunction() {
            this(IRandom.newInstance());
        }

        public UlidFunction(Random random) {
            this(IRandom.newInstance(random));
        }

        public UlidFunction(LongSupplier randomFunction) {
            this(IRandom.newInstance(randomFunction));
        }

        public UlidFunction(IntFunction<byte[]> randomFunction) {
            this(IRandom.newInstance(randomFunction));
        }

        @Override
        public Ulid apply(final long time) {
            if (this.random instanceof ByteRandom) {
                return new Ulid(time, this.random.nextBytes(Ulid.RANDOM_BYTES));
            } else {
                final long msb = (time << 16) | (this.random.nextLong() & 0xffffL);
                final long lsb = this.random.nextLong();
                return new Ulid(msb, lsb);
            }
        }
    }

    /**
     * 创建单调递增ULID的函数。
     */
    static final class MonotonicFunction implements LongFunction<Ulid> {

        private Ulid lastUlid;

        private final IRandom random;

        // 用于在NTP调整小的时钟漂移或由于闰秒系统时钟回退1秒后保持单调性。
        static final int CLOCK_DRIFT_TOLERANCE = 10_000;

        private MonotonicFunction(IRandom random) {
            this.random = random;
        }

        public MonotonicFunction() {
            this(IRandom.newInstance());
        }

        public MonotonicFunction(Random random) {
            this(IRandom.newInstance(random));
        }

        public MonotonicFunction(LongSupplier randomFunction) {
            this(IRandom.newInstance(randomFunction));
        }

        public MonotonicFunction(IntFunction<byte[]> randomFunction) {
            this(IRandom.newInstance(randomFunction));
        }

        void initialize(LongSupplier timeFunction) {
            // 使用1970-01-01 00:00:00.000 UTC时间点初始化工厂
            this.lastUlid = new Ulid(0L, this.random.nextBytes(Ulid.RANDOM_BYTES));
        }

        @Override
        public Ulid apply(final long time) {

            final long lastTime = lastUlid.getTime();

            // 检查当前时间是否与前一个时间相同或在小的系统时钟调整或闰秒后向后移动。
            // 漂移容忍度 = (前一个时间 - 10秒) < 当前时间 <= 前一个时间
            if ((time > lastTime - CLOCK_DRIFT_TOLERANCE) && (time <= lastTime)) {
                this.lastUlid = this.lastUlid.increment();
            } else {
                if (this.random instanceof ByteRandom) {
                    this.lastUlid = new Ulid(time, this.random.nextBytes(Ulid.RANDOM_BYTES));
                } else {
                    final long msb = (time << 16) | (this.random.nextLong() & 0xffffL);
                    final long lsb = this.random.nextLong();
                    this.lastUlid = new Ulid(msb, lsb);
                }
            }

            return new Ulid(this.lastUlid);
        }
    }

    static interface IRandom {

        public long nextLong();

        public byte[] nextBytes(int length);

        static IRandom newInstance() {
            return new ByteRandom();
        }

        static IRandom newInstance(Random random) {
            if (random == null) {
                return new ByteRandom();
            } else {
                if (random instanceof SecureRandom) {
                    return new ByteRandom(random);
                } else {
                    return new LongRandom(random);
                }
            }
        }

        static IRandom newInstance(LongSupplier randomFunction) {
            return new LongRandom(randomFunction);
        }

        static IRandom newInstance(IntFunction<byte[]> randomFunction) {
            return new ByteRandom(randomFunction);
        }
    }

    record LongRandom(LongSupplier randomFunction) implements IRandom {

        public LongRandom() {
            this(newRandomFunction(null));
        }

        public LongRandom(Random random) {
            this(newRandomFunction(random));
        }

        LongRandom(LongSupplier randomFunction) {
            this.randomFunction = randomFunction != null ? randomFunction : newRandomFunction(null);
        }

        @Override
        public long nextLong() {
            return randomFunction.getAsLong();
        }

        @Override
        public byte[] nextBytes(int length) {

            int shift = 0;
            long random = 0;
            final byte[] bytes = new byte[length];

            for (int i = 0; i < length; i++) {
                if (shift < Byte.SIZE) {
                    shift = Long.SIZE;
                    random = randomFunction.getAsLong();
                }
                shift -= Byte.SIZE; // 56, 48, 40...
                bytes[i] = (byte) (random >>> shift);
            }

            return bytes;
        }

        static LongSupplier newRandomFunction(Random random) {
            final Random entropy = random != null ? random : new SecureRandom();
            return entropy::nextLong;
        }
    }

    record ByteRandom(IntFunction<byte[]> randomFunction) implements IRandom {

        public ByteRandom() {
            this(newRandomFunction(null));
        }

        public ByteRandom(Random random) {
            this(newRandomFunction(random));
        }

        ByteRandom(IntFunction<byte[]> randomFunction) {
            this.randomFunction = randomFunction != null ? randomFunction : newRandomFunction(null);
        }

        @Override
        public long nextLong() {
            long number = 0;
            byte[] bytes = this.randomFunction.apply(Long.BYTES);
            for (int i = 0; i < Long.BYTES; i++) {
                number = (number << 8) | (bytes[i] & 0xff);
            }
            return number;
        }

        @Override
        public byte[] nextBytes(int length) {
            return this.randomFunction.apply(length);
        }

        static IntFunction<byte[]> newRandomFunction(Random random) {
            final Random entropy = random != null ? random : new SecureRandom();
            return (final int length) -> {
                final byte[] bytes = new byte[length];
                entropy.nextBytes(bytes);
                return bytes;
            };
        }
    }
}
