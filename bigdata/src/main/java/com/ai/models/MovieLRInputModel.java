package com.ai.models;

public class MovieLRInputModel {

    public MovieLRInputModel(Integer age, Integer sex, Integer matchFlag) {
        this.age = age;
        this.sex = sex;
        this.matchFlag = matchFlag;
    }

    private Integer age;

    private Integer sex;

    private Integer matchFlag;

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getMatchFlag() {
        return matchFlag;
    }

    public void setMatchFlag(Integer matchFlag) {
        this.matchFlag = matchFlag;
    }

}
