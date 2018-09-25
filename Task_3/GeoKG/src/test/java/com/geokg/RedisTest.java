package com.geokg;

import org.junit.Test;
import redis.clients.jedis.Jedis;

public class RedisTest {
    @Test
    public void testJedis() throws Exception {
        //创建一个jedis对象，需要指定服务的ip和端口号
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        //直接操作数据库
        jedis.set("jedis", "1234");
        String result = jedis.get("jedis");
        System.out.println(result);
//        jedis.del("jedis");
        //关闭jedis
        jedis.close();
    }
}