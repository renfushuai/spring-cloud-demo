package cn.rfs.userservice.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @description:redis访问类
 **/
@Component
public class RedisUtil {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	/**
	 * 向redis写入key-value【string类型】
	 * 
	 * @param key
	 * @param value
	 */
	public <T> void set(String key, T value) {
		redisTemplate.opsForValue().set(key, value);
	}

	/**
	 * 向redis写入key-value【string类型】
	 * 
	 * @param key
	 * @param value
	 * @param expireTime 毫秒，过期时间
	 */
	public <T> void set(String key, T value, long expireTime) {
		redisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.MILLISECONDS);
	}

	/**
	 * 从redis读取key的值【string类型】
	 * 
	 * @param key
	 * @return Object
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return key == null ? null : (T) redisTemplate.opsForValue().get(key);
	}

	/**
	 * 
	 * Hash类型读取方法
	 * 
	 * @param key  键 不能为null
	 * @param item 项 不能为null
	 * @return T
	 */
	@SuppressWarnings("unchecked")
	public <T> T hget(String key, String item) {
		return (T) redisTemplate.opsForHash().get(key, item);
	}
	/**
	 * 获取hashKey对应的所有键值
	 * @param key 键
	 * @return 对应的多个键值
	 */
	public <HK, HV> Map<HK, HV> hmget(String key){
		return (Map<HK, HV>)redisTemplate.opsForHash().entries(key);
	}
	/**
	 * 获取hashKey对应的所有键值
	 * @param key 键
	 * @return 对应的多个键值
	 */
	public <HK> List<HK> hmgetValues(String key){
		return (List<HK>)redisTemplate.opsForHash().values(key);
	}
	/**
	 * 
	 * 向hash中放入数据,如果不存在将创建
	 * 
	 * @param key      键
	 * @param hashKeys 项
	 * @param value    值
	 */
	public <T> void hset(String key, String hashKeys, T value) {
		redisTemplate.opsForHash().put(key, hashKeys, value);
	}
	/**
	 * HashSet 并设置时间
	 * @param key 键
	 * @param value 对应多个键值
	 * @param time 时间(毫秒)
	 * @return true成功 false失败
	 */
	public <HK, HV> boolean hmset(String key,  Map<HK, HV> value, long time){
		redisTemplate.opsForHash().putAll(key, value);
		if(time>0){
			expire(key, time);
		}
		return true;
	}
	/**
	 * 
	 * 向hash中放入数据,如果不存在将创建
	 * 
	 * @param key      键
	 * @param hashKeys 项
	 * @param value    值
	 * @param expireTime 有效时间，按key设，每次插入元素都更新有效期
	 */
	public <T> void hset(String key, String hashKeys, T value, long expireTime) {
		expire(key, expireTime);
		redisTemplate.opsForHash().put(key, hashKeys, value);
	}
	/**
	 *
	 * Hash类型读取方法
	 *
	 * @param key  键 不能为null
	 * @param item 项 不能为null
	 * @return T
	 */
	public Boolean hhasKey(String key, String item) {
		return  redisTemplate.opsForHash().hasKey(key, item);
	}
	/**
	 * 向队列中放入数据，最大为max个
	 * @param key
	 * @param value
	 * @param max
	 */
	public <T> void lset(String key, T value,Long max) {
		//删除已经存在的
		redisTemplate.opsForList().remove(key,0,value);
		Long count = redisTemplate.opsForList().leftPush(key,value);
		if(max!= null && count > max) {
			redisTemplate.opsForList().rightPop(key);
		}
	}

	public  <T> T lget(String key, Long max) {
		List<Object> list =  redisTemplate.opsForList().range(key,0,max);
		return (T)list;
	}
	/**
	 * 
	 * 批量添加数据
	 * 
	 * @param keyList    键
	 * @param expireTime 有效时间
	 */
	public void batchSet(List<String> keyList, long expireTime) {
		// 开启事务支持
		redisTemplate.setEnableTransactionSupport(true);
		redisTemplate.multi();
		// 遍历存入，值随便定义
		keyList.stream().forEach(key -> {
			set(key, 1, expireTime);
		});
		redisTemplate.exec();
	}
	
	/**
	 * 
	 * 批量删除数据
	 * 
	 * @param keyList
	 */
	public void batchDelete(List<String> keyList) {
		redisTemplate.multi();
		// 遍历存入，值随便定义
		keyList.stream().forEach(key -> {
			del(key);
		});
		redisTemplate.exec();
	}

	/**
	 * 
	 * 批量删除hash中的值
	 * 
	 * @param key  键 不能为null
	 * @param item 项 可以是多个， 不能为null
	 * 
	 */
	public void hdel(String key, Object... hashKeys) {
		redisTemplate.opsForHash().delete(key, hashKeys);
	}
	
	/**
	 * 
	 * 删除
	 * 
	 * @param key  键 ，不能为null
	 * 
	 */
	public void del(String key) {
		redisTemplate.delete(key);
	}

	/**
	 * 设置过期时间
	 * 
	 * @param key
	 * @param expireTime 毫秒
	 */
	public void expire(String key, long expireTime) {
		redisTemplate.expire(key, expireTime, TimeUnit.MILLISECONDS);
	}

	/**
	 * 递增
	 * 
	 * @param key
	 * @return long
	 */
	public Long incr(String key,long expireTime) {
		// 自增，并返回自增后的结果
		Long value = redisTemplate.opsForValue().increment(key);
		// 第一次使用时设置过期时间
		if (value == 1L) {
			expire(key, expireTime);
		}
		return value;
	}
	/**
	 * 递减
	 *
	 * @param key
	 * @return long
	 */
	public Long decr(String key) {
		// 递减
		return redisTemplate.opsForValue().decrement(key);
	}
	/**
	 * 分布式锁，加锁成功返回true，否则false
	 * 
	 * @param key
	 * @param value
	 * @param expireTime 毫秒，过期时间
	 */
	public boolean lock(String key, String value, long expireTime) {
		return redisTemplate.opsForValue().setIfAbsent(key, value, expireTime, TimeUnit.MILLISECONDS);
	}

	/**
	 * 解锁
	 * 
	 * @param key
	 * @param value
	 * @return boolean
	 */
	public boolean releaseLock(String key, String value) {
		// lua脚本：比较加锁值，相等则删除
		String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
		// 实例化DefaultRedisScript对象
		DefaultRedisScript<Boolean> script = new DefaultRedisScript<Boolean>();
		script.setScriptText(luaScript);
		// 设置返回结果类型
		script.setResultType(Boolean.class);
		// 执行lua脚本
		return redisTemplate.execute(script, Arrays.asList(key), value);
	}
	/**
	 * 判断key是否存在
	 * @param key 键
	 * @return true 存在 false不存在
	 */
	public boolean hasKey(String key){
		return redisTemplate.hasKey(key);
	}
}
