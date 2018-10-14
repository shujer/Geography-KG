package com.geokg.redis;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.google.common.collect.Lists;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisUtil {
    private static String ADDR = "127.0.0.1";
    private static int PORT = 6379;
    //private static String AUTH = "admin";

    private static int MAX_ACTIVE = 1024;

    private static int MAX_IDLE = 200;

    private static int MAX_WAIT = 10000;

    private static int TIMEOUT = 10000;

    private static boolean TEST_ON_BORROW = true;

    private static JedisPool jedisPool = null;

    static {
        try{
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxIdle(MAX_IDLE);
            config.setMaxWaitMillis(MAX_WAIT);
            config.setTestOnBorrow(TEST_ON_BORROW);
            jedisPool = new JedisPool(config,ADDR,PORT);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized static Jedis getJedis(){
        try{
            if(jedisPool != null){
                Jedis jedis = jedisPool.getResource();
                return jedis;
            }else{
                return null;
            }
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void returnResource(final Jedis jedis){
        if(jedis != null){
            jedisPool.returnResource(jedis);
        }
    }


    /**
     * 获取缓存
     * @param key 键
     * @return 值
     */
    public static String get(String key) {
        System.out.println("================获取redis的值================");
        String value = null;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            if (jedis.exists(key)) {
                value = jedis.get(key);
                value = StringUtils.isNotBlank(value) && !"nil".equalsIgnoreCase(value) ? value : null;
                //logger.debug("get {} = {}", key, value);
            }
        } catch (Exception e) {
            //logger.warn("get {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }


    /**
     * 设置缓存
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public static String set(String key, String value, int cacheSeconds) {
        System.out.println("================设置redis的值================");
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            result = jedis.set(key, value);
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            //logger.debug("set {} = {}", key, value);
        } catch (Exception e) {
            //logger.warn("set {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }


    /**
     * 获取缓存
     * @param key 键
     * @return 值
     */
    public static Object getObject(String key) {
        Object value = null;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            if (jedis != null && jedis.exists(getBytesKey(key))) {
                value = toObject(jedis.get(getBytesKey(key)));
                //logger.debug("getObject {} = {}", key, value);
            }
        } catch (Exception e) {
            //logger.warn("getObject {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }


    /**
     * 设置缓存
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public static String setObject(String key, Object value, int cacheSeconds) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            if(jedis != null) {
                result = jedis.set(getBytesKey(key), toBytes(value));
                if (cacheSeconds != 0) {
                    jedis.expire(key, cacheSeconds);
                }
            }
            //logger.debug("setObject {} = {}", key, value);
        } catch (Exception e) {
            //logger.warn("setObject {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }


    /**
     * 获取List缓存
     * @param key 键
     * @return 值
     */
    public static List<String> getList(String key) {
        List<String> value = null;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            if (jedis.exists(key)) {
                value = jedis.lrange(key, 0, -1);
                //logger.debug("getList {} = {}", key, value);
            }
        } catch (Exception e) {
            //logger.warn("getList {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 获取List缓存
     * @param key 键
     * @return 值
     */
    public static List<Object> getObjectList(String key) {
        List<Object> value = null;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            if (jedis.exists(getBytesKey(key))) {
                List<byte[]> list = jedis.lrange(getBytesKey(key), 0, -1);
                value = Lists.newArrayList();
                for (byte[] bs : list){
                    value.add(toObject(bs));
                }
                //logger.debug("getObjectList {} = {}", key, value);
            }
        } catch (Exception e) {
            //logger.warn("getObjectList {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 设置List缓存
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public static long setList(String key, List<String> value, int cacheSeconds) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            if (jedis.exists(key)) {
                jedis.del(key);
            }
            result = jedis.rpush(key, (String[])value.toArray());
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            //logger.debug("setList {} = {}", key, value);
        } catch (Exception e) {
            //logger.warn("setList {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 设置List缓存
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public static long setObjectList(String key, List<Object> value, int cacheSeconds) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            if (jedis.exists(getBytesKey(key))) {
                jedis.del(key);
            }
            List<byte[]> list = Lists.newArrayList();
            for (Object o : value){
                list.add(toBytes(o));
            }
            result = jedis.rpush(getBytesKey(key), (byte[][])list.toArray());
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            //logger.debug("setObjectList {} = {}", key, value);
        } catch (Exception e) {
            //logger.warn("setObjectList {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }


    /**
     * 删除缓存
     * @param key 键
     * @return
     */
    public static long del(String key) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            if (jedis.exists(key)){
                result = jedis.del(key);
                //logger.debug("del {}", key);
            }else{
                //logger.debug("del {} not exists", key);
            }
        } catch (Exception e) {
            //logger.warn("del {}", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 删除缓存
     * @param key 键
     * @return
     */
    public static long delObject(Object key) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            if (jedis.exists(getBytesKey(key))){
                result = jedis.del(getBytesKey(key));
                //logger.debug("delObject {}", key);
            }else{
                //logger.debug("delObject {} not exists", key);
            }
        } catch (Exception e) {
            //logger.warn("delObject {}", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 缓存是否存在
     * @param key 键
     * @return
     */
    public static boolean exists(String key) {
        System.out.println("================redis的key是否存在================");
        boolean result = false;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            if(jedis != null)
                result = jedis.exists(key);
            //logger.debug("exists {}", key);
        } catch (Exception e) {
            //logger.warn("exists {}", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 缓存是否存在
     * @param key 键
     * @return
     */
    public static boolean existsObject(Object key) {
        boolean result = false;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            result = jedis.exists(getBytesKey(key));
            //logger.debug("existsObject {}", key);
        } catch (Exception e) {
            //logger.warn("existsObject {}", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 获取byte[]类型Key
     * @param key
     * @return
     */
    public static byte[] getBytesKey(Object object){
        if(object instanceof String){
            return ((String)object).getBytes();
        }else{
            return SerializeUtil.serialize(object);
        }
    }

    /**
     * 获取byte[]类型Key
     * @param key
     * @return
     */
    public static Object getObjectKey(byte[] key){
        try{
            return new String(key);
        }catch(UnsupportedOperationException uoe){
            try{
                return JedisUtil.toObject(key);
            }catch(UnsupportedOperationException uoe2){
                uoe2.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Object转换byte[]类型
     * @param key
     * @return
     */
    public static byte[] toBytes(Object object){
        return SerializeUtil.serialize(object);
    }

    /**
     * byte[]型转换Object
     * @param key
     * @return
     */
    public static Object toObject(byte[] bytes){
        return SerializeUtil.unserialize(bytes);
    }

}

