package com.chandler.demo.recommend.dao;

import com.chandler.demo.recommend.entities.MovieEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MovieRepository extends PagingAndSortingRepository<MovieEntity, Integer> {
}
