package com.nowcoder.service;

import com.nowcoder.controller.HomeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/*
InitializingBean接口为bean提供了初始化方法的方式，
它只包括afterPropertiesSet方法，凡是继承该接口的类，
在初始化bean的时候会执行该方法。
由结果可看出，在spring初始化bean的时候，如果该bean是实现了InitializingBean接口，
并且同时在配置文件中指定了init-method，系统则是先调用afterPropertiesSet方法，
然后在调用init-method中指定的方法。
 */
@Service
public class JedisAdapter implements InitializingBean{
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    private JedisPool pool;

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("redis://localhost:6379/2");
    }

    // add a set in jedis
    public long sadd(String key, String value) {
        Jedis jedis = null;
        try {
            // link error
            jedis = pool.getResource();
            jedis.sadd(key, value);
        } catch (Exception e) {
            logger.error("jedis sadd failed" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public long srem(String key, String value) {
        Jedis jedis = null;
        try {
            // link error
            jedis = pool.getResource();
            jedis.srem(key, value);
        } catch (Exception e) {
            logger.error("jedis srem failed" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public long scard(String key) {
        Jedis jedis = null;
        try {
            // link error
            jedis = pool.getResource();
            return jedis.scard(key);
        } catch (Exception e) {
            logger.error("jedis scard failed" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public boolean sismember(String key, String value) {
        Jedis jedis = null;
        try {
            // link error
            jedis = pool.getResource();
            return jedis.sismember(key, value);
        } catch (Exception e) {
            logger.error("jedis sismember failed" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }


}
