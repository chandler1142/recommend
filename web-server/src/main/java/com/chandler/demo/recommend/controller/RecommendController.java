package com.chandler.demo.recommend.controller;

import com.chandler.demo.recommend.dao.MovieRepository;
import com.chandler.demo.recommend.entities.MovieEntity;
import com.chandler.demo.recommend.model.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.chandler.demo.recommend.model.SessionContainer.userTokenMap;

@RestController
@RequestMapping("/recommend")
public class RecommendController {

    @Autowired
    private MovieRepository movieRepository;

    @GetMapping("/getByUserToken")
    public AjaxResult getByUserToken(@RequestParam("token") String userToken) {
        System.out.println("recommend for user: " + userTokenMap.get(userToken));
        Integer page = (int) (Math.random() * 100);
        Integer pageSize = 12;
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.Direction.ASC, "id");
        Page<MovieEntity> testEntities = movieRepository.findAll(pageRequest);
        return AjaxResult.success(testEntities);
    }

}
