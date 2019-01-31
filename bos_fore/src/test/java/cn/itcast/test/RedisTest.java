package cn.itcast.test;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:applicationContext.xml")
public class RedisTest {
	//注入redisTemplate
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	@Test
	public void test() {
		//保存key,value,60秒失效
		redisTemplate.opsForValue().set("test", "11111", 60, TimeUnit.SECONDS);
	}
	@Test
	public void test2() {
		//通过key得到value
		String value = redisTemplate.opsForValue().get("test");
		System.out.println(value);
	}
	@Test
	public void test3() {
		//删除指定key值对
		redisTemplate.delete("test");
	}
}
