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
import java.util.Date;
import java.util.Optional;

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
        for (int i = 0; i < 100; i++) {
            UserBehaviorEntity entity = new UserBehaviorEntity();
            String[] events = {"click", "NotInterested", "marked", "watch"};
            entity.setEvent(events[(int) (Math.random() * events.length)]);
            entity.setAgent("windows");
            UserEntity userEntity = userRepository.findByName("test" + (int) (Math.random() * 1000));
            entity.setUserId(userEntity.getId());

            int movieId = (int) (Math.random() * 1200);
            Optional<MovieEntity> movieEntityOptional = movieRepository.findById(movieId);
            if (!movieEntityOptional.isPresent()) {
                System.out.println(movieId + " 不存在");
                continue;
            }
            MovieEntity movieEntity = movieEntityOptional.get();
            entity.setMovieId(movieEntity.getId());

            LocalDateTime localDateTime = LocalDateTime.now();
            localDateTime.minusDays((int) (Math.random() * 60));
            Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            entity.setTime(date);

            userBehaviorRepository.save(entity);
            System.out.println("插入成功: " + entity.toString());
        }
        return AjaxResult.success("处理成功");
    }


}
