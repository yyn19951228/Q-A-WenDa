package com.nowcoder.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nowcoder.model.User;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

public class JedisAdapter {
    public static void print(int index, Object obj) {
        System.out.println(String.format("%d, %s", index, obj.toString()));
    }

    public static void main(String[] args) {
        // default localhost:6379
        // /1 : choose db 9
        Jedis jedis = new Jedis("redis://localhost:6379/1");
        // empty db
        jedis.flushDB();

        // save data in to db
        jedis.set("hello", "world");
        print(1, jedis.get("hello"));

        jedis.rename("hello", "newHello");


        // save data in expire times (auto delete in 10 sec)
        jedis.setex("hello_expired", 10, "invalid");
        // usage : verification code

        jedis.set("pv", "100");
        print(2, jedis.get("pv"));
        jedis.incr("pv");
        print(3, jedis.get("pv"));
        jedis.incrBy("pv", 5);
        print(4, jedis.get("pv"));
        jedis.decrBy("pv", 10);
        print(5, jedis.get("pv"));

        // show all the keys
        print(6, jedis.keys("*"));


        // list : start with "l"

        String listName = "list";
        jedis.del(listName);
        for (int i = 0; i < 10; i++)  {
            jedis.lpush(listName , "a"+ String.valueOf(i));
        }

        // 7, [ a9,  a8,  a7,  a6,  a5,  a4,  a3,  a2,  a1,  a0]
        print(7, jedis.lrange(listName, 0, 12));
        // 8, [a9, a8, a7, a6]
        print(8, jedis.lrange(listName, 0, 3));
        // length : 10
        print(9, jedis.llen(listName));
        // like stack : a9
        print(10, jedis.lpop(listName));
        // length : 9
        print(11, jedis.llen(listName));
        // choose by index
        print(12, jedis.lindex(listName, 4));
        // insert before and after specific elements
        print(13, jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER, "a4", "xx"));
        print(14, jedis.linsert(listName, BinaryClient.LIST_POSITION.BEFORE, "a4", "yy"));
        // 15, [a8, a7, a6, a5, yy, a4, xx, a3, a2, a1, a0]
        print(15, jedis.lrange(listName, 0, 12));

        // hash : start with "hs"
        String userKey = "userXX";
        // hash set
        jedis.hset(userKey, "name", "jim");
        jedis.hset(userKey, "age", "12");
        jedis.hset(userKey, "phone", "123456789");
        // 16, jim
        print(16, jedis.hget(userKey, "name"));
        // 17, {phone=123456789, name=jim, age=12}
        print(17, jedis.hgetAll(userKey));
        // delete phone
        jedis.hdel(userKey, "phone");
        // false
        jedis.hexists(userKey, "email");
        // get all keys or all values
        jedis.hkeys(userKey);
        jedis.hvals(userKey);
        // set if not exist
        jedis.hsetnx(userKey, "school", "zju");
        jedis.hsetnx(userKey, "name", "yyn");
        // 18, {school=zju, name=jim, age=12}
        print(18, jedis.hgetAll(userKey));


        // set : start with "s"
        String likeKey1 = "commentLike1";
        String likeKey2 = "commentLike2";
        for (int i = 0; i < 10; i++) {
            jedis.sadd(likeKey1, String.valueOf(i));
            jedis.sadd(likeKey2, String.valueOf(i * i));
        }
        // 19, [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
        print(19, jedis.smembers(likeKey1));
        // 20, [0, 1, 4, 9, 16, 25, 36, 49, 64, 81]
        print(20, jedis.smembers(likeKey2));
        // set union
        // 21, [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 16, 25, 36, 49, 64, 81]
        print(21, jedis.sunion(likeKey1, likeKey2));
        // set different
        // 22, [8, 2, 3, 5, 6, 7]
        print(22, jedis.sdiff(likeKey1, likeKey2));
        // 23, [0, 1, 4, 9]
        print(23, jedis.sinter(likeKey1, likeKey2));
        // search
        jedis.sismember(likeKey1, "14");
        // remove
        jedis.srem(likeKey1, "5");
        // move "25" from likeKey2 to likeKey1
        // change nothing if not exist in likeKey2
        jedis.smove(likeKey2, likeKey1, "25");
        // 24, [0, 1, 2, 3, 4, 6, 7, 8, 9, 25]
        print(24, jedis.smembers(likeKey1));
        // length
        jedis.scard(likeKey1);

        // priority queue : start with "z"(sorted set)
        String rankKey = "rankKey";
        jedis.zadd(rankKey, 15, "jim");
        jedis.zadd(rankKey, 20, "ben");
        jedis.zadd(rankKey, 25, "lee");
        jedis.zadd(rankKey, 90, "lucy");
        // 25, 4
        print(25, jedis.zcard(rankKey));
        // 26, 1  (count num betwenn min and max)
        print(26, jedis.zcount(rankKey, 61, 100));
        // 27, 15.0
        print(27, jedis.zscore(rankKey, "jim"));
        // increase by operatior
        jedis.zincrby(rankKey, 20, "lucy");
        // luc : 20
        jedis.zincrby(rankKey, 20, "luc");
        // 28, [jim, ben, luc, lee, lucy]
        print(28, jedis.zrange(rankKey, 0, 100));
        // choose top n : default asc
        // 29, [jim, ben, luc, lee]
        print(29, jedis.zrange(rankKey,0, 3));
        // choose top n : desc
        print(30, jedis.zrevrange(rankKey,0, 3));
        /*
        31, jim:15.0
        31, ben:20.0
        31, luc:20.0
        31, lee:25.0
         */
        for (Tuple tuple : jedis.zrangeByScoreWithScores(rankKey, 0, 40)) {
            print(31, tuple.getElement() + ":" + String.valueOf(tuple.getScore()));
        }
        // get rank aes and desc
        jedis.zrank(rankKey, "ben");
        jedis.zrevrank(rankKey, "ben");

        // rank by alphabet
        String setKey = "zset";
        jedis.zadd(setKey, 1, "a");
        jedis.zadd(setKey, 1, "b");
        jedis.zadd(setKey, 1, "c");
        jedis.zadd(setKey, 1, "d");
        jedis.zadd(setKey, 1, "f");
        // 32, 5 ;
        // - : negative infinitive
        // + : positive infinitive
        print(32, jedis.zlexcount(setKey, "-", "+"));
        // 33, 2
        // (b, d] : > b and <= d
        print(33, jedis.zlexcount(setKey, "(b", "[d"));
        // 34, [a, b, c]
        jedis.zremrangeByLex(setKey, "(c", "+");
        print(34, jedis.zrange(setKey, 0, 100));

        /*
        // link pool
        JedisPool pool = new JedisPool();
        for (int i = 0; i < 100; i++) {
            Jedis j = pool.getResource();
            print(45, j.get("pv"));
            // if don't close, the link will not be returned, just print
            // 8 results
            j.close();
        }
        */

        // redis cache
        User user = new User();
        user.setName("xx");
        user.setPassword("123");
        user.setHeadUrl("a.png");
        user.setSalt("123");
        user.setId(1);
        jedis.set("user1", JSONObject.toJSONString(user));
        // get cache
        String value = jedis.get("user1");
        User user2 = JSON.parseObject(value, User.class);


    }


}
