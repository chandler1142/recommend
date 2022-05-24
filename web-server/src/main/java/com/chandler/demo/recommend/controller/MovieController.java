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

import java.util.*;

@RestController
@RequestMapping("/movie")
public class MovieController {

    @Autowired
    private MovieRepository movieRepository;

    @GetMapping("/test")
    public AjaxResult test() {
        PageRequest pageRequest = PageRequest.of(1, 10, Sort.Direction.ASC, "id");
        Page<MovieEntity> testEntities = movieRepository.findAll(pageRequest);
        return AjaxResult.success(testEntities);
    }

    @GetMapping("/list")
    public AjaxResult list(@RequestParam("pageSize") int pageSize, @RequestParam("page") int page) {
        System.out.println("pageSize: " + pageSize + " page: " + page);
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.Direction.ASC, "id");
        Page<MovieEntity> testEntities = movieRepository.findAll(pageRequest);
        return AjaxResult.success(testEntities);
    }

    @GetMapping("/getById")
    public AjaxResult getById(@RequestParam("id") int id) {
        Optional<MovieEntity> entity = movieRepository.findById(id);
        return AjaxResult.success(entity.get());
    }

    @GetMapping("/getAllTypes")
    public AjaxResult getAllTypes() {
        Iterable<MovieEntity> allMovies = movieRepository.findAll();
        Iterator<MovieEntity> iterator = allMovies.iterator();
        Set<String> categorySet = new HashSet<>();
        while (iterator.hasNext()) {
            MovieEntity entity = iterator.next();
            String[] categories = entity.getCategory().split(",");
            Arrays.stream(categories).forEach(
                    n -> {
                        String element = n.trim();
                        categorySet.add(element);
                    }
            );
        }
        return AjaxResult.success(categorySet);
    }

}
