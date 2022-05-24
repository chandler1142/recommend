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

    private Integer userId;

    private Integer movieId;

    /**
     *  点击
     *  不感兴趣
     *  收藏
     *  立即观看
     */
    private String event;

    private String agent;

    @Override
    public String toString() {
        return "UserBehaviorEntity{" +
                "id=" + id +
                ", time=" + time +
                ", userId='" + userId + '\'' +
                ", movieId=" + movieId +
                ", event='" + event + '\'' +
                ", agent='" + agent + '\'' +
                '}';
    }
}
