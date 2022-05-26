package com.chandler.demo.recommend.controller;

import com.chandler.demo.recommend.dao.MovieRepository;
import com.chandler.demo.recommend.dao.UserBehaviorRepository;
import com.chandler.demo.recommend.dao.UserRepository;
import com.chandler.demo.recommend.entities.MovieEntity;
import com.chandler.demo.recommend.entities.UserBehaviorEntity;
import com.chandler.demo.recommend.entities.UserEntity;
import com.chandler.demo.recommend.model.AjaxResult;
import com.chandler.demo.recommend.model.UploadData;
import com.chandler.demo.recommend.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static com.chandler.demo.recommend.model.SessionContainer.userTokenMap;

@RestController
@RequestMapping("/event")
public class EventController {

    @Autowired
    UserBehaviorRepository userBehaviorRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MovieRepository movieRepository;

    @PostMapping("/upload")
    public AjaxResult upload(@RequestBody UploadData uploadData) {
        System.out.println(uploadData.toString());
        UserBehaviorEntity entity = new UserBehaviorEntity();
        if (StringUtils.isEmpty(uploadData.getToken()) || userTokenMap.get(uploadData.getToken()) == null) {
            return AjaxResult.error("token or user is null");
        }

        entity.setUserId(userTokenMap.get(uploadData.getToken()));
        entity.setAgent(uploadData.getAgent());
        entity.setEvent(uploadData.getEvent());
        String[] movieInfos = uploadData.getMovieLink().split("/");
        entity.setMovieId(Integer.parseInt(movieInfos[movieInfos.length - 1]));
        entity.setTime(uploadData.getTime());
        UserBehaviorEntity savedEntity = userBehaviorRepository.save(entity);
        return AjaxResult.success(savedEntity);
    }

    @GetMapping("/mock")
    public AjaxResult mock() {

        //0.获取所有的movie到内存
        Iterable<MovieEntity> iterable = movieRepository.findAll();
        Iterator<MovieEntity> iterator = iterable.iterator();
        ArrayList<MovieEntity> movieEntities = new ArrayList<>();
        while (iterator.hasNext()) {
            movieEntities.add(iterator.next());
        }

        //1. 取出前的800个用户
        for (int i = 0; i < 800; i++) {
            String userName = "test" + i;
            UserEntity userEntity = userRepository.findByName(userName);
            String[] flags = userEntity.getMovieFlags().split(",");
            //2. 选到当前的用户感兴趣的电影
            List<MovieEntity> selectedMovies = movieEntities.stream().filter(e -> {
                String[] movieCategories = e.getCategory().split(",");
                boolean needed = false;
                for (String flag : flags) {
                    for (String movieFlag : movieCategories) {
                        if (movieFlag.equalsIgnoreCase(flag)) {
                            needed = true;
                            break;
                        }
                    }
                }
                return needed;
            }).collect(Collectors.toList());

            //3. 每个用户随机对筛选出来的50部电影发生行为
            List<UserBehaviorEntity> records = new ArrayList<>();
            Set<Integer> handled = new HashSet<>();
            while (handled.size() < Math.min(20, selectedMovies.size()-10)) {
                MovieEntity movieEntity = selectedMovies.get((int) (Math.random() * selectedMovies.size()));
                if (handled.contains(movieEntity.getId())) {
                    continue;
                }

                UserBehaviorEntity entity = new UserBehaviorEntity();
                String[] events = {"click", "NotInterested", "marked", "watch"};
                entity.setEvent(events[(int) (Math.random() * events.length)]);
                entity.setAgent("windows");
                entity.setUserId(userEntity.getId());
                entity.setMovieId(movieEntity.getId());

                LocalDateTime localDateTime = LocalDateTime.now();
                localDateTime.minusDays((int) (Math.random() * 60));
                Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
                entity.setTime(date);

                records.add(entity);
                handled.add(movieEntity.getId());
            }
            userBehaviorRepository.saveAll(records);
            System.out.println("处理完成用户: " + userName);
        }

        return AjaxResult.success("处理成功");
    }


}