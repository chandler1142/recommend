package com.chandler.demo.recommend.controller;

import com.chandler.demo.recommend.dao.UserRepository;
import com.chandler.demo.recommend.entities.UserEntity;
import com.chandler.demo.recommend.model.AjaxResult;
import com.chandler.demo.recommend.model.UserLoginDO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.chandler.demo.recommend.model.SessionContainer.userTokenMap;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

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
        Object result = UUID.randomUUID().toString();

        userTokenMap.putIfAbsent(result.toString(), entity.getId());
        return AjaxResult.success(result);
    }


    @GetMapping("/mock")
    public AjaxResult mockUsers(@RequestParam("num") Integer num) {
        String namePrefix = "test";
        List<UserEntity> entityList = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            UserEntity entity = new UserEntity();
            entity.setAge((int) (Math.random() * 50 + 10));
            entity.setPassword("123456");
            entity.setName(namePrefix + i);
            entity.setMovieFlags(getCategory());
            entity.setSex(Math.random() > 0.5 ? 1 : 0);
            entityList.add(entity);
            System.out.println("插入成功: " + entity.getName());
            userRepository.save(entity);
        }

        return AjaxResult.success("成功");
    }


    public String getCategory() {
        String[] categories = {"喜剧", "情色", "科幻", "运动", "恐怖", "儿童", "灾难", "同性", "犯罪", "西部", "动画", "传记", "纪录片", "惊悚", "冒险", "奇幻", "歌舞", "历史", "悬疑", "战争", "动作"};
        int len = categories.length;
        Set<String> set = new HashSet<>();
        while (set.size() < 3) {
            String element = categories[(int) (Math.random() * len)];
            set.add(element);
        }
        String result = String.join(",", StringUtils.join(set.toArray(), ","));
        return result;
    }


}
