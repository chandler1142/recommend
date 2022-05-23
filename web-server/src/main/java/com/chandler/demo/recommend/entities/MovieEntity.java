package com.chandler.demo.recommend.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "movies")
public class MovieEntity {

    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    private String lang;

    private String category;

    private String movieUrl;

    private String picUrl;

    private String director;

    private String actors;

    private String releaseTime;

    private String region;

    private String score;

    private String scoreNum;

    private String time;

}
