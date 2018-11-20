package com.yzsj.neteco.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.*;

@Component
@Configuration
public class RedisManager {

	private static Logger log = LoggerFactory.getLogger(RedisManager.class);

	@Value("${com.yzsj.resources.neteco.redis.host}")
	private String host;

	@Value("${com.yzsj.resources.neteco.redis.port}")
	private int port;

	/**
	 * redis设置值时的超时时间  eg:redis.set(key,expire)
	 */
	private int expire = 0;

	/**
	 * 与服务器建立连接的超时时间
	 */
	@Value("${com.yzsj.resources.neteco.redis.timeout}")
	private int timeout;

	@Value("${com.yzsj.resources.neteco.redis.password}")
	private String password;

	/**
	 * jedis的最大活跃连接数
	 */
	@Value("${com.yzsj.resources.neteco.redis.pool.max-active}")
	private int maxActive;

	/**
	 * jedis最大空闲连接数
	 */
	@Value("${com.yzsj.resources.neteco.redis.pool.max-idle}")
	private int maxIdle;

	/**
	 * jedis池没有连接对象返回时，等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。
	 * 如果超过等待时间，则直接抛出JedisConnectionException
	 */
	@Value("${com.yzsj.resources.neteco.redis.pool.max-wait}")
	private Long maxWait;

	/**
	 * 从池中获取连接的时候，是否进行有效检查
	 */
	@Value("${com.yzsj.resources.neteco.redis.pool.testOnBorrow}")
	private boolean testOnBorrow;

	/**
	 * 归还连接的时候，是否进行有效检查
	 */
	@Value("${com.yzsj.resources.neteco.redis.pool.testOnReturn}")
	private boolean testOnReturn;


	private static JedisPool jedisPool = null;


	public RedisManager() {
	}


	/**
	 * 初始化方法
	 */
	public void init() {
		try {
			// 设置池配置项值
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxTotal(maxActive);
			config.setMaxIdle(maxIdle);
			config.setMaxWaitMillis(maxWait);
			config.setTestOnBorrow(testOnBorrow);
			// 根据配置实例化jedis池
			config.setTestOnReturn(testOnReturn);
			jedisPool = new JedisPool(config, host, port, timeout, password);
			log.warn("线程池被成功初始化");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获得连接
	 *
	 * @return Jedis
	 */
	private synchronized Jedis getJedis() {
		if (jedisPool == null) {
			init();
		}
		//Redis对象
		Jedis jedis = jedisPool.getResource();
		return jedis;
	}


	/**
	 * 新版本用close归还连接
	 */
	public void closeConn(Jedis jedis) {
		if (jedis != null) {
			jedis.close();
		}
	}

	//关闭池
	public void closePool() {
		if (jedisPool != null) {
			jedisPool.close();
		}
	}


	/**
	 * 推入消息到redis消息通道
	 *
	 * @param channel
	 * @param message
	 */
	public void publish(String channel, String message) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			jedis.publish(channel, message);
		} catch (Exception e) {
			log.error("redis推送消息失败 channel:" + channel + " message:" + message);
		} finally {
			closeConn(jedis);
		}
	}


	/**
	 * 监听消息通道
	 *
	 * @param jedisPubSub - 监听任务
	 * @param channels    - 要监听的消息通道
	 */
	public void subscribe(JedisPubSub jedisPubSub, String... channels) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			jedis.subscribe(jedisPubSub, channels);
		} catch (Exception e) {
			log.error(String.format("redis监听通道失败 channel:%s", channels));
		} finally {
			closeConn(jedis);
		}
	}


	public int delete(String key) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			int count = this.del(jedis, key);
			return count;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			closeConn(jedis);
		}
		return 0;
	}


	private int del(Jedis shardJedis, Object key) {
		if (key instanceof String) {
			return shardJedis.del((String) key).intValue();
		} else {
			return shardJedis.del((byte[]) key).intValue();
		}
	}


	public List<String> hmget(String key, String... fields) {
		// 从shard池中获取shardJedis实例
		Jedis jedis = null;
		try {
			jedis = getJedis();
			List<String> result;
			result = jedis.hmget(key, fields);
			return result;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			closeConn(jedis);
		}
		return null;
	}


	public void hset(String key, String field, String value) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			jedis.hset(key, field, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			closeConn(jedis);
		}
	}

	public Map<String, String> hgetAll(String key) {
		Map<String, String> result = new HashMap<>();
		// 从shard池中获取shardJedis实例
		Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.hgetAll(key);
			return result;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return result;
		} finally {
			closeConn(jedis);
		}
	}


	public List<String> mget(String[] keys) {
		List<String> result = new ArrayList<>();
		Jedis jedis = null;
		try {
			jedis = getJedis();
			return jedis.mget(keys);
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		} finally {
			closeConn(jedis);
		}
	}

	public Set<String> hkeys() {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			Set<String> tagsVals = jedis.hkeys("tags_vals");
			return tagsVals;
		} catch (Exception e) {
			log.error("获取tags_vals出错");
			return new HashSet<>();
		} finally {
			closeConn(jedis);
		}
	}

	public List<String> hmget(String[] keys) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			return jedis.hmget("tags_vals", keys);
		} catch (Exception e) {
			log.error("获取tags_vals出错");
			e.printStackTrace();
			return new ArrayList<>();
		} finally {
			closeConn(jedis);
		}
	}

	/**
	 * get value from redis
	 *
	 * @param key
	 * @return
	 */
	public byte[] get(byte[] key) {
		byte[] value = null;
		Jedis jedis = jedisPool.getResource();
		try {
			value = jedis.get(key);
		} catch (Exception e) {
			log.error("redis get出错");
		} finally {
			closeConn(jedis);
		}
		return value;
	}

	/**
	 * set
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public byte[] set(byte[] key, byte[] value) {
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.set(key, value);
			if (this.expire != 0) {
				jedis.expire(key, this.expire);
			}
		} finally {
			closeConn(jedis);
		}
		return value;
	}

	/**
	 * set
	 *
	 * @param key
	 * @param value
	 * @param expire
	 * @return
	 */
	public byte[] set(byte[] key, byte[] value, int expire) {
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.set(key, value);
			if (expire != 0) {
				jedis.expire(key, expire);
			}
		} finally {
			closeConn(jedis);
		}
		return value;
	}

	/**
	 * del
	 *
	 * @param key
	 */
	public void del(byte[] key) {
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.del(key);
		} finally {
			closeConn(jedis);
		}
	}

	/**
	 * flush
	 */
	public void flushDB() {
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.flushDB();
		} finally {
			closeConn(jedis);
		}
	}

	/**
	 * size
	 */
	public Long dbSize() {
		Long dbSize = 0L;
		Jedis jedis = jedisPool.getResource();
		try {
			dbSize = jedis.dbSize();
		} finally {
			closeConn(jedis);
		}
		return dbSize;
	}

	/**
	 * keys
	 *
	 * @param pattern
	 * @return
	 */
	public Set<byte[]> keys(String pattern) {
		Set<byte[]> keys = null;
		Jedis jedis = jedisPool.getResource();
		try {
			keys = jedis.keys(pattern.getBytes());
		} finally {
			closeConn(jedis);
		}
		return keys;
	}


	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getExpire() {
		return expire;
	}

	public void setExpire(int expire) {
		this.expire = expire;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public static Logger getLog() {
		return log;
	}

	public static void setLog(Logger log) {
		RedisManager.log = log;
	}

	public int getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public Long getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(Long maxWait) {
		this.maxWait = maxWait;
	}

	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}

	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	public boolean isTestOnReturn() {
		return testOnReturn;
	}

	public void setTestOnReturn(boolean testOnReturn) {
		this.testOnReturn = testOnReturn;
	}

	public static JedisPool getJedisPool() {
		return jedisPool;
	}

	public static void setJedisPool(JedisPool jedisPool) {
		RedisManager.jedisPool = jedisPool;
	}

}
