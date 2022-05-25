package com.chandler.demo.recommend.dao;

import com.chandler.demo.recommend.entities.MovieEntity;
import com.chandler.demo.recommend.entities.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovieRepository extends PagingAndSortingRepository<MovieEntity, Integer> {

    @Query("select m from MovieEntity m where m.category like %?1%")
    List<MovieEntity> findByFlag(@Param("name") String flag);

}
