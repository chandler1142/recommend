package com.chandler.demo.recommend.service;

public class MidResult {

    private Integer movieId;

    private Double mean;

    public MidResult(Integer movieId, Double mean) {
        this.movieId = movieId;
        this.mean = mean;
    }

    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public Double getMean() {
        return mean;
    }

    public void setMean(Double mean) {
        this.mean = mean;
    }


}
