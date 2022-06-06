package com.chandler.demo.recommend.controller;

import com.chandler.demo.recommend.dao.MovieRepository;
import com.chandler.demo.recommend.entities.MovieEntity;
import com.chandler.demo.recommend.model.AjaxResult;
import com.chandler.demo.recommend.service.RealTimeService;
import com.chandler.demo.recommend.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.chandler.demo.recommend.model.SessionContainer.userTokenMap;

@RestController
@RequestMapping("/recommend")
public class RecommendController {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    RealTimeService realTimeService;

    @GetMapping("/getByUserToken")
    public AjaxResult getByUserToken(@RequestParam("token") String userToken) {
        System.out.println("recommend for user: " + userTokenMap.get(userToken));
        Integer page = (int) (Math.random() * 100);
        Integer pageSize = 12;
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.Direction.ASC, "id");
        Page<MovieEntity> testEntities = movieRepository.findAll(pageRequest);
        return AjaxResult.success(testEntities);
    }

    @GetMapping("/getByItemCF")
    public AjaxResult getByItemCF(@RequestParam("token") String userToken) {
        Integer userId = userTokenMap.get(userToken);
        //方便测试
        if (userId == null) {
            userId = 2313;
        }
        String resultsStr = String.valueOf(redisUtil.hget("itemCF", userId.toString()));
        List<MovieEntity> movieEntities = Arrays.stream(resultsStr.split(",")).limit(10).map(movie_id -> {
            Optional<MovieEntity> entity = movieRepository.findById(Integer.parseInt(movie_id));
            return entity.get();
        }).collect(Collectors.toList());
        return AjaxResult.success(movieEntities);
    }

    @GetMapping("/getByALS")
    public AjaxResult getByALS(@RequestParam("token") String userToken) {
        Integer userId = userTokenMap.get(userToken);
        //方便测试
        if (userId == null) {
            userId = 2313;
        }
        String resultsStr = String.valueOf(redisUtil.hget("als", userId.toString()));
        List<MovieEntity> movieEntities = Arrays.stream(resultsStr.split(",")).limit(10).map(movie_id -> {
            Optional<MovieEntity> entity = movieRepository.findById(Integer.parseInt(movie_id));
            return entity.get();
        }).collect(Collectors.toList());
        return AjaxResult.success(movieEntities);
    }

    @GetMapping("getRealTimeList")
    public AjaxResult getRealTimeList(@RequestParam("token") String userToken) {
        Integer userId = userTokenMap.get(userToken);
        //方便测试
        if (userId == null) {
            userId = 2313;
        }
        System.out.println("realtime recommend for user: " + userId);
        List<MovieEntity> movieEntities = realTimeService.recommendForUser(userId, 20);
        return AjaxResult.success(movieEntities);
    }

}
