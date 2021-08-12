package cn.rfs.userservice.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

	@Bean
	@SuppressWarnings("all")
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {

		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);

		/**
		 * Jackson2JsonRedisSerializer
		 *  可直接序列化对象，序列化之后是json字符串；
		 *  由于没有标识原对象的类型，所以反序列化之后只能转换成LinkedHashMap而非原对象类型，强转会抛异常
		 * GenericJackson2JsonRedisSerializer
		 *  可直接序列化对象，序列化之后的对象是携带对象类型的json字符串
		 *  所以反序列化之后会变为原对象，但是反序列化Java8新增的time相关（如：Instant、LocalDateTime等）类时，会报错
		 *  为解决这个问题可以增一个定制化ObjectMapper来处理 详情请询问 @zhaochao53，18701321850
		 *  	考虑现已有线上业务，未直接进行更改替换成GenericJackson2JsonRedisSerializer，
		 *  	而是依然使用Jackson2JsonRedisSerializer并默认按照json字符串来存储实体对象
		 */
		Jackson2JsonRedisSerializer jackson2JsonRedisSerializer =  new Jackson2JsonRedisSerializer(Object.class);

		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(om);


		StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

		// key采用String的序列化方式
		redisTemplate.setKeySerializer(stringRedisSerializer);
		// hash的key也采用String的序列化方式
		redisTemplate.setHashKeySerializer(stringRedisSerializer);
		// value采用json序列化方式
		redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
		// hash的value采用json序列化方式
		redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}
}
