package cn.rfs.userservice.service;

import cn.rfs.userservice.entity.UserEntity;
import cn.rfs.userservice.util.RedisUtil;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private RedisUtil redisUtil;
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
}
