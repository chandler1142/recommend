package com.chandler.demo.recommend.service;

import org.apache.commons.math3.distribution.BetaDistribution;

public class BetaParam {

    private Integer userId;

    private BetaDistribution betaDistribution;

    private Integer movie;

    public BetaParam(Integer userId, BetaDistribution betaDistribution, Integer movie) {
        this.betaDistribution = betaDistribution;
        this.movie = movie;
        this.userId = userId;
    }

    public BetaDistribution getBetaDistribution() {
        return betaDistribution;
    }

    public void setBetaDistribution(BetaDistribution betaDistribution) {
        this.betaDistribution = betaDistribution;
    }

    public Integer getMovie() {
        return movie;
    }

    public void setMovie(Integer movie) {
        this.movie = movie;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
