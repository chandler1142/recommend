package com.chandler.demo.recommend.controller;

import com.chandler.demo.recommend.dao.UserRepository;
import com.chandler.demo.recommend.entities.UserEntity;
import com.chandler.demo.recommend.model.AjaxResult;
import com.chandler.demo.recommend.model.UserLoginDO;
import com.chandler.demo.recommend.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    LoginService loginService;

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @PostMapping("/login")
    public AjaxResult login(@RequestBody UserLoginDO userLoginDO) {
        String userName = userLoginDO.getUsername();
        UserEntity entity = userRepository.findByName(userName);
        if (entity == null) {
            return AjaxResult.error("实体不存在: " + userLoginDO.getUsername());
        }
        String token = loginService.saveLoginInfo(entity);
        return AjaxResult.success("登录成功", token);
    }

}
