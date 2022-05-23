package com.chandler.demo.recommend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class UploadData {

    private String token;

    private String agent;

    private Date time;

    private String event;

    @JsonProperty("movie_link")
    private String movieLink;

    @Override
    public String toString() {
        return "UploadData{" +
                "token='" + token + '\'' +
                ", agent='" + agent + '\'' +
                ", time=" + time +
                ", event='" + event + '\'' +
                ", movieLink='" + movieLink + '\'' +
                '}';
    }
}
