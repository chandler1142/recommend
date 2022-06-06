package com.chandler.demo.recommend.service;

import com.chandler.demo.recommend.dao.MovieRepository;
import com.chandler.demo.recommend.dao.UserBehaviorRepository;
import com.chandler.demo.recommend.dao.UserRepository;
import com.chandler.demo.recommend.entities.MovieEntity;
import com.chandler.demo.recommend.entities.UserEntity;
import org.apache.commons.math3.distribution.BetaDistribution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class RealTimeService implements CommandLineRunner {

    //这里假设所有的历史点击率平均值为5%
    private static final float clickRate = 0.05f;

    public static final List<BetaParam> betaParamList = new ArrayList<>();

    @Autowired
    MovieRepository movieRepository;

    @Autowired
    UserBehaviorRepository userBehaviorRepository;

    @Autowired
    UserRepository userRepository;

    ArrayList<MovieEntity> movieEntities = new ArrayList<>();

    public static Map<Integer, List<MovieEntity>> currentRecommendList = new ConcurrentHashMap();

    @Override
    public void run(String... args) throws Exception {
        Iterable<MovieEntity> movieEntityIterable = movieRepository.findAll();
        Iterator<MovieEntity> iterator1 = movieEntityIterable.iterator();
        while (iterator1.hasNext()) {
            movieEntities.add(iterator1.next());
        }

        List<UserEntity> userEntities = new ArrayList<>();
        Iterable<UserEntity> userEntityIterable = userRepository.findAll();
        Iterator<UserEntity> iterator2 = userEntityIterable.iterator();
        while (iterator2.hasNext()) {
            userEntities.add(iterator2.next());
        }

        userEntities = userEntities.stream().sorted(new Comparator<UserEntity>() {
            @Override
            public int compare(UserEntity o1, UserEntity o2) {
                return o1.getId() > o1.getId() ? 0 : 1;
            }
        }).limit(50).collect(Collectors.toList());

        System.out.println("data prepared completed...");

        //a的初始值为同类型电影的点击值, 不考虑历史行为
        double a = 1;
        for (UserEntity userEntity : userEntities) {
            for (MovieEntity movieEntity : movieEntities) {
                betaParamList.add(new BetaParam(userEntity.getId(), new BetaDistribution(a, getB(a)), movieEntity.getId()));
            }
        }
        System.out.println("param map prepared completed...");


    }

    public static Double getB(Double a) {
        BigDecimal n1 = new BigDecimal((1 - clickRate) * a);
        BigDecimal n2 = new BigDecimal(clickRate);
        return n1.divide(n2, 5, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public List<MovieEntity> recommendForUser(Integer userId, Integer limit) {
        List<MidResult> midResults = betaParamList.stream().filter(betaParam -> {
            return betaParam.getUserId().equals(userId);
        }).map(param -> {
            return new MidResult(param.getMovie(), param.getBetaDistribution().getNumericalMean());
        }).sorted(Comparator.comparing(MidResult::getMean, Comparator.reverseOrder())).collect(Collectors.toList());

        List<Integer> currentMovieIdList = new ArrayList<>();
        List<MovieEntity> recommendedList = RealTimeService.currentRecommendList.get(userId);
        if (recommendedList != null) {
            recommendedList.forEach(element -> {
                currentMovieIdList.add(element.getId());
            });
        }

        List<MovieEntity> collect = midResults.stream().filter(midResult -> {
                    return !currentMovieIdList.contains(midResult.getMovieId());
                }
        ).limit(limit).map(midResult -> {
            System.out.println("推荐movie id: " + midResult.getMovieId() + " mean: " + midResult.getMean());
            return this.movieEntities.stream().filter(m -> midResult.getMovieId().equals(m.getId())).findFirst().get();
        }).collect(Collectors.toList());

        RealTimeService.currentRecommendList.put(userId, collect);
        return collect;
    }

    /**
     * 如果点击了电影，相类似的电影都更新一下
     *
     * @param userId
     * @param movieId
     * @param event
     */
    public void updateParamList(Integer userId, Integer movieId, String event) {

        MovieEntity clickMovie = movieEntities.stream().filter(m -> {
            return m.getId().equals(movieId);
        }).findFirst().get();

        //所有类别跟电影类似的电影都增益
        String[] updateCategories = clickMovie.getCategory().split(",");
        for (String firstCategory : updateCategories) {
            List<MovieEntity> relatedMovies = movieEntities.stream().filter(m -> {
                return m.getCategory().contains(firstCategory);
            }).collect(Collectors.toList());

            for (MovieEntity u : relatedMovies) {
                BetaParam param = betaParamList.stream().filter(betaParam -> {
                    return betaParam.getUserId().equals(userId) && betaParam.getMovie().equals(u.getId());
                }).findFirst().get();

                BetaDistribution betaDistribution = param.getBetaDistribution();
                BetaParam updatedParam = new BetaParam(userId, new BetaDistribution(betaDistribution.getAlpha() + 1, betaDistribution.getBeta()), u.getId());
                betaParamList.remove(param);
                betaParamList.add(updatedParam);
                System.out.println("更新电影: " + u.getName() + " alpha: " + updatedParam.getBetaDistribution().getAlpha() + " beta: " + updatedParam.getBetaDistribution().getBeta() + " mean: " + updatedParam.getBetaDistribution().getNumericalMean() + " original mean: " + param.getBetaDistribution().getNumericalMean());
            }
        }

        //已经推荐的电影没有点击的，进行减益
        if (currentRecommendList.get(userId) != null) {
            for (MovieEntity current : currentRecommendList.get(userId)) {
                if (!current.getId().equals(movieId)) {
                    //推荐了但是没有点
                    BetaParam param = betaParamList.stream().filter(betaParam -> {
                        return betaParam.getUserId().equals(userId) && betaParam.getMovie().equals(current.getId());
                    }).findFirst().get();
                    BetaDistribution betaDistribution = param.getBetaDistribution();
                    //beta 加上当前电影对应的类别的个数
                    BetaParam updatedParam = new BetaParam(userId, new BetaDistribution(betaDistribution.getAlpha(), betaDistribution.getBeta() + current.getCategory().split(",").length), current.getId());
                    betaParamList.remove(param);
                    betaParamList.add(updatedParam);
                    System.out.println("更新电影: " + current.getName() + " alpha: " + updatedParam.getBetaDistribution().getAlpha() + " beta: " + updatedParam.getBetaDistribution().getBeta() + " mean: " + updatedParam.getBetaDistribution().getNumericalMean() + " original mean: " + param.getBetaDistribution().getNumericalMean());
                }
            }
        }
    }

}
