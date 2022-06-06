package com.chandler.demo.recommend.service;

import com.chandler.demo.recommend.entities.UserEntity;
import com.chandler.demo.recommend.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LoginService {

    @Autowired
    RedisUtil redisUtil;

    public String saveLoginInfo(UserEntity userEntity) {
        String token = UUID.randomUUID().toString();
        redisUtil.set("login:" + token, userEntity.getId().toString(), 10 * 60);
        return token;
    }

    public Integer getUserByToken(String token) {
        Object result = redisUtil.get("login:"+token);
        if (result == null) {
            return null;
        } else {
            return Integer.parseInt(String.valueOf(result));
        }
    }


}
