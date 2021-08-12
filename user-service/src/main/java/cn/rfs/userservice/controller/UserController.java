package cn.rfs.userservice.controller;

import cn.rfs.userservice.entity.UserEntity;
import cn.rfs.userservice.util.RedisUtil;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
@RequestMapping("user")
public class UserController {
    @Value("${test}")
    private String test;
    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/get")
    public String get() {
        return test;
    }
    @GetMapping("/getUser")
    public String getUser(long id) {
        String key="user:"+id;
        if (redisUtil.hasKey(key)){
            return redisUtil.get(key);
        }
        UserEntity userEntity=new UserEntity();
        userEntity.setAge(18);
        userEntity.setName("renfushuai");
        userEntity.setId(id);
        redisUtil.set(key, JSON.toJSONString(userEntity));
        return JSON.toJSONString(userEntity);
    }
    @GetMapping("/set")
    public String set() {
        redisUtil.set("name", "renfushuai");
        return redisUtil.get("name");
    }
}
