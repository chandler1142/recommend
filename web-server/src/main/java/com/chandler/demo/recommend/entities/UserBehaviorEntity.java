package com.chandler.demo.recommend.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "user_behavior")
public class UserBehaviorEntity {

    @Id
    @GeneratedValue
    private Integer id;

    private Date time;

    private String userName;

    private Integer movieId;

    /**
     * 不感兴趣
     *  收藏
     *  立即观看
     */
    private String event;

    private String agent;

}
