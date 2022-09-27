package com.ticho.boot.redis.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具对象
 *
 * @author zhajianjun
 * @date 2022-06-27 14:20
 */
public class RedisUtil<K, V> {
    public static final long DEFAULT_EXPIRE_TIME = -1;
    public static final long DEFAULT_COUNT = 0;
    private final Logger log = LoggerFactory.getLogger(RedisUtil.class);
    private final RedisTemplate<K, V> redisTemplate;

    public RedisUtil(RedisTemplate<K, V> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    /* ---------------------------------------------------- key操作 ---------------------------------------------------- */

    /**
     * 设置过期时间
     * @param key 键
     * @param timeout 过期时间
     * @param timeUnit 时间单位
     * @return true-成功，false-失败
     */
    public boolean expire(K key, long timeout, TimeUnit timeUnit) {
        try {
            Boolean expire = redisTemplate.expire(key, timeout, timeUnit);
            return Boolean.TRUE.equals(expire);
        } catch (Exception e) {
            log.error("{}键设置过期时间失败,异常：{}", key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取过期时间
     *
     * @param key 键
     @return long
     */
    public long getExpire(K key) {
        try {
            Long expireTime = redisTemplate.getExpire(key);
            return Optional.ofNullable(expireTime).orElse(DEFAULT_EXPIRE_TIME);
        } catch (Exception e) {
            log.error("{}键获取过期时间失败,异常：{}", key, e.getMessage(), e);
            return DEFAULT_EXPIRE_TIME;
        }
    }

    /**
     * 设置过期时间
     * @param key 键
     * @param timeout 过期时间
     * @return true-成功，false-失败
     */
    public boolean expire(K key, long timeout) {
        return this.expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 删除键
     * @param key 键
     * @return true-成功，false-失败
     */
    public boolean delete(K key) {
        try {
            Boolean delete = redisTemplate.delete(key);
            return Boolean.TRUE.equals(delete);
        } catch (Exception e) {
            log.error("{}键删除失败,异常：{}", key, e.getMessage(), e);
            return false;
        }

    }

    /**
     * 批量删除键
     * @param keys 键 集合
     * @return true-成功，false-失败
     */
    public long delete(Collection<K> keys) {
        try {
            Long count = redisTemplate.delete(keys);
            return Optional.ofNullable(count).orElse(DEFAULT_COUNT);
        } catch (Exception e) {
            log.error("{}键批量删除失败,异常：{}", keys, e.getMessage(), e);
            return DEFAULT_COUNT;
        }
    }

    /**
     * 查询键是否存在
     * @param key 键
     * @return true-成功，false-失败
     */
    public boolean exists(K key) {
        try {
            Boolean hasKey = redisTemplate.hasKey(key);
            return Boolean.TRUE.equals(hasKey);
        } catch (Exception e) {
            log.error("查询{}键是否存在,异常：{}", key, e.getMessage(), e);
            return false;
        }

    }
    /* ---------------------------------------------------- opsForValue ---------------------------------------------------- */

    /**
     * 自增
     * @param key 键
     * @param delta 步长
     */
    public void increment(K key, long delta) {
        try {
            this.opsForValue().increment(key, delta);
        } catch (Exception e) {
            log.error("{}键自增失败,异常：{}", key, e.getMessage(), e);
        }
    }

    /**
     * 自减
     * @param key 键
     * @param delta 步长
     */
    public void decrement(K key, long delta) {
        try {
            this.opsForValue().decrement(key, delta);
        } catch (Exception e) {
            log.error("{}键自减失败,异常：{}", key, e.getMessage(), e);
        }
    }

    /**
     * 存入普通键值对象
     * @param key 键
     * @param value 值
     */
    public void vSet(final K key, final V value) {
        try {
            this.opsForValue().set(key, value);
        } catch (Exception e) {
            log.error("{}键存入普通对象失败,异常：{}", key, e.getMessage(), e);
        }
    }

    /**
     * 存入普通键值对象
     * @param key 键
     * @param value 值
     * @param timeout 过期时间
     * @param timeUnit 时间单位
     */
    public void vSet(final K key, final V value, final long timeout, TimeUnit timeUnit) {
        try {
            this.opsForValue().set(key, value, timeout, timeUnit);
        } catch (Exception e) {
            log.error("{}键存入普通对象失败,异常：{}", key, e.getMessage(), e);
        }
    }

    /**
     * 批量存入普通键值对象
     * @param map 键值对集合
     */
    public void vMultiSet(Map<? extends K, ? extends V> map) {
        try {
            this.opsForValue().multiSet(map);
        } catch (Exception e) {
            log.error("批量存入普通对象失败,异常：{}", e.getMessage(), e);
        }
    }

    /**
     * 获取普通键值对象
     * @param key 键
     * @return 对象
     */
    public V vGet(final K key) {
        try {
            return this.opsForValue().get(key);
        } catch (Exception e) {
            log.error("{}键获取普通对象失败,异常：{}", key, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 批量获取普通键值对象
     * @param keys 键集合
     * @return 对象
     */
    public List<V> vMultiGet(Collection<K> keys) {
        try {
            return this.opsForValue().multiGet(keys);
        } catch (Exception e) {
            log.error("{}键批量获取普通对象失败,异常：{}", keys, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /* ---------------------------------------------------- opsForHash ---------------------------------------------------- */

    /**
     * 查询Hash中的某个对象是否存在
     * @param key 键
     * @param obj true-成功，false-失败
     * @return
     */
    public boolean hExists(K key, Object obj) {
        try {
            return this.opsForHash().hasKey(key, obj);
        } catch (Exception e) {
            log.error("查询{}键的Hash对象是否存在对象失败,异常：{}", key, e.getMessage(), e);
            return false;
        }

    }

    /**
     * Hash中存入数据
     * @param key 键
     * @param hKey Hash键
     * @param value 值
     */
    public <HK, HV> void hPut(final K key, final HK hKey, final HV value) {
        try {
            this.opsForHash().put(key, hKey, value);
        } catch (Exception e) {
            log.error("{}键的Hash对象存入数据失败,异常：{}", key, e.getMessage(), e);
        }
    }

    /**
     * Hash中批量存入数据
     * @param key 键
     * @param values Hash键值对
     */
    public <HK, HV> void hPutAll(final K key, final Map<HK, HV> values) {
        try {
            this.opsForHash().putAll(key, values);
        } catch (Exception e) {
            log.error("{}键的Hash对象批量存入数据失败,异常：{}", key, e.getMessage(), e);
        }
    }

    /**
     * 获取Hash中的数据
     * @param key 键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public <HK, HV> HV hGet(final K key, final HK hKey) {
        try {
            HashOperations<K, HK, HV> opsForHash = this.opsForHash();
            return opsForHash.get(key, hKey);
        } catch (Exception e) {
            log.error("{}键的Hash对象获取单个数据失败,异常：{}", key, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 删除Hash中的数据
     * @param key 键
     * @param hKey Hash键
     * @return <HK>
     */
    public <HK> long hDelete(final K key, final HK hKey) {
        try {
            return this.opsForHash().delete(key, hKey);
        } catch (Exception e) {
            log.error("{}键的Set对象存入数据失败,异常：{}", key, e.getMessage(), e);
            return DEFAULT_COUNT;
        }
    }

    /**
     * 获取多个Hash中的数据
     * @param key 键
     * @param hKeys Hash键集合
     * @return List
     */
    public <HK, HV> List<HV> hMultiGet(final K key, final Collection<HK> hKeys) {
        try {
            HashOperations<K, HK, HV> opsForHash = opsForHash();
            return opsForHash.multiGet(key, hKeys);
        } catch (Exception e) {
            log.error("{}键的Hash对象获取多个数据失败,异常：{}", key, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * 获取Hash数据的个数
     * @param key 键
     * @return 个数
     */
    public long hSize(final K key) {
        try {
            return this.opsForHash().size(key);
        } catch (Exception e) {
            log.error("获取{}键的Hash对象个数失败,异常：{}", key, e.getMessage(), e);
            return DEFAULT_COUNT;
        }
    }

    /**
     * 获取Hash中所有的数据 键值对形式
     * @param key 键
     * @return Hash对象集合 Map<HK, HV>
     */
    public <HK, HV> Map<HK, HV> hGetAll(final K key) {
        try {
            HashOperations<K, HK, HV> opsForHash = this.opsForHash();
            return opsForHash.entries(key);
        } catch (Exception e) {
            log.error("{}键的Hash对象获取所有数据失败,异常：{}", key, e.getMessage(), e);
            return new HashMap<>(0);
        }
    }

    /* ---------------------------------------------------- opsForSet ---------------------------------------------------- */

    /**
     * Set中存入数据
     * @param key 键
     * @param values 值
     * @return 存入个数
     */
    @SafeVarargs
    public final long sAdd(final K key, final V... values) {
        Long count;
        try { count = this.opsForSet().add(key, values); } catch (Exception e) {
            log.error("{}键的Set对象存入数据失败,异常：{}", key, e.getMessage(), e);
            return DEFAULT_COUNT;
        }
        return count == null ? DEFAULT_COUNT : count;
    }

    /**
     * 获取Set对象的大小
     * @param key 键
     * @return 存入个数
     */
    public final long sSize(final K key) {
        try {
            Long count = this.opsForSet().size(key);
            return count == null ? DEFAULT_COUNT : count;
        } catch (Exception e) {
            log.error("获取{}键的Set对象大小失败,异常：{}", key, e.getMessage(), e);
            return DEFAULT_COUNT;
        }
    }

    /**
     * 查询Set中的某个对象是否存在
     * @param key 键
     * @param obj 对象
     * @return true-成功，false-失败
     */
    public boolean isMember(K key, Object obj) {
        try {
            Boolean isMember = this.opsForSet().isMember(key, obj);
            return isMember != null && isMember;
        } catch (Exception e) {
            log.error("查询{}键的Set对象是否存在对象失败,异常：{}", key, e.getMessage(), e);
            return false;
        }

    }

    /**
     * 随机从Set集合查询对象
     * @param key 键
     * @param size 大小
     * @return List<V>
     */
    public List<V> randomMembers(K key, long size) {
        List<V> result;
        try { result = this.opsForSet().randomMembers(key, size); } catch (Exception e) {
            log.error("随机查询{}键的Set对象失败,异常：{}", key, e.getMessage(), e);
            return Collections.emptyList();
        }
        return result;
    }

    /**
     * 随机(不重复)从Set集合查询对象
     * @param key 键
     * @param size 大小
     * @return Set<V>
     */
    public Set<V> distinctRandomMembers(K key, long size) {
        try {
            return this.opsForSet().distinctRandomMembers(key, size);
        } catch (Exception e) {
            log.error("随机(不重复)查询{}键的Set对象失败,异常：{}", key, e.getMessage(), e);
            return Collections.emptySet();
        }
    }

    public Set<V> members(K key) {
        try { return this.opsForSet().members(key); } catch (Exception e) {
            log.error("获取{}键的Set对象失败,异常：{}", key, e.getMessage(), e);
            return new HashSet<>();
        }
    }

    /**
     * 删除Set中的数据
     * @param key 键
     * @param values 值
     * @return 删除个数
     */
    public long sRemove(final K key, final Object... values) {
        Long count;
        try { count = this.opsForSet().remove(key, values); } catch (Exception e) {
            log.error("{}键的Set对象删除数据失败,异常：{}", key, e.getMessage(), e);
            return DEFAULT_COUNT;
        }
        return count == null ? DEFAULT_COUNT : count;
    }

    /**
     *
     * @param key 键
     * @param otherKey
     * @param destKey
     * @return
     */
    public long intersectAndStore(K key, K otherKey, K destKey) {
        try {
            Long count = this.opsForSet().intersectAndStore(key, otherKey, destKey);
            return count == null ? DEFAULT_COUNT : count;
        } catch (Exception e) {
            log.error("{}键 intersectAndStore 操作失败,异常：{}", key, e.getMessage(), e);
            return DEFAULT_COUNT;
        }
    }

    /**
     *
     * @param key 键
     * @param otherKey
     * @param destKey
     * @return
     */
    public long differenceAndStore(K key, K otherKey, K destKey) {
        try {
            Long count = this.opsForSet().differenceAndStore(key, otherKey, destKey);
            return count == null ? DEFAULT_COUNT : count;
        } catch (Exception e) {
            log.error("{}键 differenceAndStore 操作失败,异常：{}", key, e.getMessage(), e);
            return DEFAULT_COUNT;
        }
    }

    /* ---------------------------------------------------- opsForList ---------------------------------------------------- */

    /**
     * List中存入数据
     * @param key 键
     * @param value 值
     * @return 存入个数
     */
    public long lPush(final K key, final V value) {
        try {
            Long count = this.opsForList().rightPush(key, value);
            return count == null ? DEFAULT_COUNT : count;
        } catch (Exception e) {
            log.error("{}键的List对象存入数据失败,异常：{}", key, e.getMessage(), e);
            return DEFAULT_COUNT;
        }
    }

    /**
     * 往List中存入多个数据
     * @param key 键
     * @param values 多个数据
     * @return 存入的个数
     */
    public long lPushAll(final K key, final Collection<V> values) {
        try {
            Long count = this.opsForList().rightPushAll(key, values);
            return count == null ? DEFAULT_COUNT : count;
        } catch (Exception e) {
            log.error("{}键的List对象批量存入数据失败,异常：{}", key, e.getMessage(), e);
            return DEFAULT_COUNT;
        }
    }

    /**
     * 从List中获取begin到end之间的元素
     * @param key 键
     * @param start 开始位置
     * @param end 结束位置（start=DEFAULT_COUNT，end=INT表示获取全部元素）
     * @return List对象
     */
    public List<V> lGet(final K key, final int start, final int end) {
        try {
            return this.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("{}键的List对象获取指定位置数据失败,异常：{}", key, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /* -------------------------------------------------------------------------------------------------------- */

    private ValueOperations<K, V> opsForValue() {
        return redisTemplate.opsForValue();
    }

    private <HK, HV> HashOperations<K, HK, HV> opsForHash() {
        return redisTemplate.opsForHash();
    }

    private ListOperations<K, V> opsForList() {
        return redisTemplate.opsForList();
    }

    private SetOperations<K, V> opsForSet() {
        return redisTemplate.opsForSet();
    }

}
