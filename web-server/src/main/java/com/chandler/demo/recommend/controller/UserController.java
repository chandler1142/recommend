package com.chandler.demo.recommend.controller;

import com.chandler.demo.recommend.model.AjaxResult;
import com.chandler.demo.recommend.model.UserLoginDO;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.chandler.demo.recommend.model.SessionContainer.userTokenMap;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @PostMapping("/login")
    public AjaxResult login(@RequestBody UserLoginDO userLoginDO) {
        Object result = UUID.randomUUID().toString();
        userTokenMap.putIfAbsent(result.toString(), userLoginDO.getUsername());
        return AjaxResult.success(result);
    }



}
