package com.chandler.demo.recommend.controller;

import com.chandler.demo.recommend.dao.UserBehaviorRepository;
import com.chandler.demo.recommend.entities.UserBehaviorEntity;
import com.chandler.demo.recommend.model.AjaxResult;
import com.chandler.demo.recommend.model.UploadData;
import com.chandler.demo.recommend.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.chandler.demo.recommend.model.SessionContainer.userTokenMap;

@RestController
@RequestMapping("/event")
public class EventController {

    @Autowired
    UserBehaviorRepository userBehaviorRepository;

    @PostMapping("/upload")
    public AjaxResult upload(@RequestBody UploadData uploadData) {
        System.out.println(uploadData.toString());
        UserBehaviorEntity entity = new UserBehaviorEntity();
        if(StringUtils.isEmpty(uploadData.getToken()) || userTokenMap.get(uploadData.getToken()) == null) {
           return AjaxResult.error("token or user is null");
        }

        entity.setUserName(userTokenMap.get(uploadData.getToken()));
        entity.setAgent(uploadData.getAgent());
        entity.setEvent(uploadData.getEvent());
        String[] movieInfos = uploadData.getMovieLink().split("/");
        entity.setMovieId(Integer.parseInt(movieInfos[movieInfos.length-1]));
        entity.setTime(uploadData.getTime());
        UserBehaviorEntity savedEntity = userBehaviorRepository.save(entity);
        return AjaxResult.success(savedEntity);
    }

}
