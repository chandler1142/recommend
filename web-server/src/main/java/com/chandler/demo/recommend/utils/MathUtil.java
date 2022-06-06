package com.chandler.demo.recommend.utils;

import org.apache.commons.math3.distribution.BetaDistribution;

public class MathUtil {


    public static void main(String[] args) {
        BetaDistribution distribution = new BetaDistribution(0, 0);

        System.out.println(distribution.getNumericalMean());
    }
}
