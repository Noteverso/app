package com.noteverso.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtils {
    private static Logger logger = LoggerFactory.getLogger(RedisUtils.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    // key
    /**
     * 实现命令：TTL key，返回key的剩余生存时间（以秒为单位）
     *
     * @param key
     * @return
     */
    public long ttl(String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 实现命令：KEYS pattern，查询所有符合给定模式 pattern 的 key
     */
    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * 实现命令：DEL key，删除一个key
     *
     * @param key
     */
    public void del(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 实现命令：DEL key1,key2，删除多个key
     */
    public void del(Collection<String> keys) {
        redisTemplate.delete(keys);
    }

    /**
     * 实现命令：EXPIRE key time，设置key的过期时间
     *
     * @param key
     * @param timeout
     * @param unit
     * @return
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 实现命令：EXPIRE key seconds，设置key的过期时间,单位为秒
     * @param key
     * @param timeout
     * @return
     */
    public Boolean expire(String key, long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 实现命令：EXPIREAT key timestamp，设置key的过期时间
     *
     * @param key
     * @param date
     * @return
     */
    public Boolean expire(String key, Date date) {
        return redisTemplate.expireAt(key, date);
    }

    /***** String *****/
    /**
     * 实现命令：SET key value，设置key的值
     *
     * @param key
     * @param value
     */
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 实现命令：SET key value EX time，设置key的值
     * @param key
     * @param value
     * @param timeout
     * @param unit
     */
    public void set(String key, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     *
     * 实现命令：SET key value EX seconds，设置key的值，过期时间为 seconds
     *
     * @param key
     * @param value
     * @param timeout
     */
    public void set(String key, String value, long timeout) {
        set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 实现命令：SETNX key value，只有在 key 不存在时才设置key
     * @param key
     * @param value
     * @return
     */
    public Boolean setIfAbsent(String key, String value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * 实现命令：MSET key1 value1 key2 value2 key3 value3，同时设置多个key-value
     * @param maps
     */
    public void multiSet(Map<String, String> maps) {
        redisTemplate.opsForValue().multiSet(maps);
    }

    /**
     * 实现命令：MSETNX key1 value1 key2 value2 key3 value3，只有在所有 key 都不存在时，才同时设置
     *
     * @param maps
     * @return
     */
    public Boolean multiSetIfAbsent(Map<String, String> maps) {
        return redisTemplate.opsForValue().multiSetIfAbsent(maps);
    }

    /**
     * 实现命令：INCR key，将key自增1
     * @param key
     */
    public void incr(String key) {
        redisTemplate.opsForValue().increment(key);
    }

    /**
     * 实现命令：GET key，查询key的值
     * @param key
     * @return value
     */
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 实现命令：MGET key1 key2 key3，查询多个key
     * @param keys
     * @return
     */
    public List<String> multiGet(Collection<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 判断key是否存在
     *
     * @param key
     * @return
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /****** Hash ******/

    /**
     * 实现命令：HSET key field value，将哈希表 key 中的字段 field 的值设为 value
     *
     * @param key
     * @param field
     * @param value
     */
    public void hPut(String key, String field, String value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    /**
     * 实现命令：HMSET key field1 value1 field2 value2 field3 value3，同时将多个 field-value (域-值)对设置到哈希表 key 中
     *
     * @param key
     * @param maps
     */
    public void hPutAll(String key, Map<String, String> maps) {
        redisTemplate.opsForHash().putAll(key, maps);
    }

    /**
     * 实现命令：HGET key field，返回哈希表 key 中给定字段 field 的值
     *
     * @param key
     * @param field
     * @return
     */
    public Object hGet(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    /**
     * 实现命令：HGETALL key，返回哈希表 key 中的所有字段和值
     *
     * @param key
     * @return
     */

    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 实现命令：HMGET key field1 field2 field3，返回哈希表 key 中给定的多个字段的值
     *
     * @param key
     * @param fields
     * @return
     */
    public List<Object> hMultiGet(String key, Collection<Object> fields) {
        return redisTemplate.opsForHash().multiGet(key, fields);
    }

    /**
     * 实现命令：HDEL key field，删除哈希表中一个或多个字段
     *
     * @param key
     * @param fields
     */
    public void hDel(String key, Object... fields) {
        redisTemplate.opsForHash().delete(key, fields);
    }

    /**
     * 实现命令：HSETNX key field value，将哈希表 key 中的字段 field 的值设为 value，只有在字段 field 不存在时才设置
     *
     * @param key
     * @param hashKey
     * @param value
     * @return
     */
    public Boolean hPutIfAbsent(String key, String hashKey, String value) {
        return redisTemplate.opsForHash().putIfAbsent(key, hashKey, value);
    }

    /**
     * 实现命令： HEXISTS key field，判断哈希表 key 中是否存在域 field
     *
     * @param key
     * @param field
     * @return
     */
    public boolean hExists(String key, String field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }
}
