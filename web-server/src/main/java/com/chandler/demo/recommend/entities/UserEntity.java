package com.chandler.demo.recommend.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "user")
public class UserEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(unique = true)
    private String name;

    private String password;

    private Integer age;

    /**
     *  0 - 女生
     *  1 - 男生
     */
    private Integer sex;

    private String movieFlags;

}
